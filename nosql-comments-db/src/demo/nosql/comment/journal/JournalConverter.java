package demo.nosql.comment.journal;

import demo.nosql.comment.page.PageConverter;
import static demo.nosql.comment.model.Url.*;
import static demo.util.ArrayUtil.*;

public class JournalConverter {
	
	public static final int URL_BYTES_COUNT = MAX_URL_SIZE * 2;
	
	public final PageConverter pageConverter;
	public final int recordBytes;

	public JournalConverter(PageConverter pageConverter) {
		super();
		this.pageConverter = pageConverter;
		this.recordBytes = 4 + URL_BYTES_COUNT + pageConverter.commentBytesCount;
	}

	public byte[] recordToBytes(JournalRecord record) {
		
		byte[] out = new byte[recordBytes];
		
		//writeIntToArray(record.pageBlockIndexForNew, out, 0);
		writeStrToArray(record.url, out, 4);
		copyFromSmallToBig(record.commentBytes, out, 4 + URL_BYTES_COUNT);
		
		return out;
	}
	
	

}
