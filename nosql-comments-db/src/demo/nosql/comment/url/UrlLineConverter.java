package demo.nosql.comment.url;

import java.util.ArrayList;

import demo.nosql.comment.model.CommentsPage;
import demo.nosql.comment.model.UrlLine;
import static demo.model.GlobalConst.*;
import static demo.nosql.comment.model.Url.*;
import static demo.util.ArrayUtil.*;

public class UrlLineConverter {
	
	public static final char SEPARATOR = '|';
	public static final char LINE_END = '\n';
	
	public static final int GLOBAL_ORDER_BYTES_COUNT = 4 + 1;
	public static final int URL_BYTES_COUNT = MAX_URL_SIZE * 2 + 1;
	public static final int UPDATE_TX_APPEND_BYTES = 1 + 8;
	
	//PAGE = offset+limit
	public static final int PAGE_BYTES_COUNT = 8 + 4;
	public static final int ALL_PAGES_BYTES_COUNT = PAGE_BYTES_COUNT * MAX_PAGES_COUNT;
	public static final int LINE_BYTES_COUNT = GLOBAL_ORDER_BYTES_COUNT + URL_BYTES_COUNT + ALL_PAGES_BYTES_COUNT + 1;
	
	
	public static byte[] lineWithFirstPageToBytes(int pageBlockIndex, String url, CommentsPage page){
		
		byte[] globalOrderBytes = new byte[GLOBAL_ORDER_BYTES_COUNT];
		writeIntToArray(pageBlockIndex, globalOrderBytes, 0);
		globalOrderBytes[GLOBAL_ORDER_BYTES_COUNT-1] = SEPARATOR;
		
		byte[] urlBytes = convertStrToBytes(url, URL_BYTES_COUNT);
		urlBytes[URL_BYTES_COUNT-1] = SEPARATOR;
		
		byte[] pageBytes = pageToBytes(page);
		
		byte[] result = new byte[LINE_BYTES_COUNT];
		copyFromSmallToBig(globalOrderBytes, result, 0);
		copyFromSmallToBig(urlBytes, result, GLOBAL_ORDER_BYTES_COUNT);
		copyFromSmallToBig(pageBytes, result, GLOBAL_ORDER_BYTES_COUNT + URL_BYTES_COUNT);
		result[LINE_BYTES_COUNT-1] = LINE_END;
				
		return result;
		
	}
	
	public static byte[] pageToBytes(CommentsPage page){
		
		byte[] result = new byte[PAGE_BYTES_COUNT];
		writeLongToArray(page.virtualOffset, result, 0);
		writeIntToArray(page.limit, result, 8);
		return result;
		
	}
	
	
	public static byte[] getUpdateTxBytes(long offset, CommentsPage page){
		
		byte[] result = new byte[PAGE_BYTES_COUNT+UPDATE_TX_APPEND_BYTES];
		if(page != null){
			writeLongToArray(offset, result, 1);
			writeLongToArray(page.virtualOffset, result, 1+8);
			writeIntToArray(page.limit, result, 1+8+8);
		}
		result[0] = '-';
		return result;
		
	}
	
	public static class GetOldDataFromUpdateTxResp {
		long offset;
		byte[] data;
		public GetOldDataFromUpdateTxResp(long offset, byte[] data) {
			super();
			this.offset = offset;
			this.data = data;
		}
		public String getParsedData(){
			long virtualOffset = getLong(data);
			long limit = getInt(data, 8);
			return "pageOffset="+offset+", virtualOffset="+virtualOffset+", limit="+limit;
		}
	}
	
	public static GetOldDataFromUpdateTxResp getOldDataFromUpdateTx(byte[] updateTxBytes) {
		
		if(updateTxBytes.length != PAGE_BYTES_COUNT+UPDATE_TX_APPEND_BYTES) throw new IllegalStateException("invalid length of updateTxBytes: "+updateTxBytes.length);
		
		long offset = getLong(updateTxBytes, 1);
		byte[] data = new byte[PAGE_BYTES_COUNT];
		for (int i = 0; i < data.length; i++) {
			data[i] = updateTxBytes[i+UPDATE_TX_APPEND_BYTES];
		}
		return new GetOldDataFromUpdateTxResp(offset, data);
	}
	
	public static UrlLine bytesToUrlLine(long offset, byte[] bytes){
		
		if(bytes.length < LINE_BYTES_COUNT) return null;
		if(bytes[GLOBAL_ORDER_BYTES_COUNT-1] != SEPARATOR)return null;
		if(bytes[GLOBAL_ORDER_BYTES_COUNT+URL_BYTES_COUNT-1] != SEPARATOR)return null;
		if(bytes[LINE_BYTES_COUNT-1] != LINE_END)return null;
		
		int pageBlockIndex = getInt(bytes, 0);
		String url = getStr(bytes, GLOBAL_ORDER_BYTES_COUNT, URL_BYTES_COUNT-1);
		
		ArrayList<CommentsPage> pagesList = new ArrayList<>();
		int curOffset = GLOBAL_ORDER_BYTES_COUNT + URL_BYTES_COUNT;
		while(curOffset < LINE_BYTES_COUNT - 1){
			long pageOffset = getLong(bytes, curOffset);
			int pageLimit = getInt(bytes, curOffset+8);
			if(pageOffset == 0 && pageLimit == 0) break;
			pagesList.add(new CommentsPage(pageOffset, pageLimit));
			curOffset += PAGE_BYTES_COUNT;
		}
		
		return new UrlLine(offset, pageBlockIndex, url, pagesList.toArray(new CommentsPage[0]));
	}

}
