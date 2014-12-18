package demo.nosql.comment.url;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;

import demo.exception.InvalidInputException;
import demo.nosql.comment.debug.DebugTool;
import demo.nosql.comment.exception.UrlLineIsFullException;
import demo.nosql.comment.log.SessionLog;
import demo.nosql.comment.model.CommentsPage;
import demo.nosql.comment.model.FileInfo;
import demo.nosql.comment.url.UrlLineConverter.GetOldDataFromUpdateTxResp;
import demo.util.FileUtil;
import demo.util.concurrent.ForSingleThread;
import static demo.model.GlobalConst.*;
import static demo.nosql.comment.debug.DebugTool.*;
import static demo.nosql.comment.log.LogEvent.*;
import static demo.nosql.comment.url.UrlLineConverter.*;
import static demo.util.ExceptionUtil.*;
import static demo.util.FileUtil.*;
import static demo.util.MathUtil.*;
import static demo.util.Util.*;

public class UrlLineStorage {


	public static final String FILE_NAME = "urls.db";
	public static final String CUR_TX = "urls-cur-tx.log";
	
	
	private Log log = getLog(getClass());
	private SessionLog sessionLog;
	private final File root;
	private final FileInfo file;
	
	private final FileInfo curTxFile;
	
	public UrlLineStorage(File root) throws IOException {
		this(root, null);
	}
	
	public UrlLineStorage(File root, SessionLog sessionLog) throws IOException {

		this.root = root;
		this.sessionLog = sessionLog;
		
		root.mkdirs();
		
		File f = new File(root, FILE_NAME);
		if( ! f.exists()) f.createNewFile();
		file = new FileInfo(f);
		
		File txF = new File(root, CUR_TX);
		if( ! txF.exists()) txF.createNewFile();
		curTxFile = new FileInfo(txF);
		
		processUnfinishedTx();
	}

	private void processUnfinishedTx() throws IOException {
		
		byte[] oldTxBytes = FileUtil.readFile(curTxFile.f);
		if(oldTxBytes.length == 0) return;
		if(oldTxBytes[0] != '-') return;
		GetOldDataFromUpdateTxResp revertData = null;
		try {
			revertData = getOldDataFromUpdateTx(oldTxBytes);
		}catch (Exception e) {
			//skip
		}
		if(revertData == null) return;
		
		//try revert
		String msg = "Revert old tx: "+revertData.getParsedData();
		if(sessionLog != null) sessionLog.warn(msg);
		log.warn(msg);
		
		file.raf.seek(revertData.offset);
		file.raf.write(revertData.data);
		
		curTxFile.raf.seek(0);
		curTxFile.raf.writeByte('R');
		
		
	}

	/**
	 * Create new url's page record in 'url.db'
	 */
	@ForSingleThread
	public long createLine(int pageBlockIndex, String url, CommentsPage page) throws IOException {
		
		byte[] lineBytes = lineWithFirstPageToBytes(pageBlockIndex, url, page);
		
		debug_BeforeWriteNewUrlLine(file);
		
		//line always has same size
		long fileEndOffset = roundDown(file.length, lineBytes.length);
		file.raf.seek(fileEndOffset);
		file.raf.write(lineBytes);
		file.length = file.raf.getFilePointer();
		
		debug_AfterWriteNewUrlLine(file);
		
		return fileEndOffset;
	}
	
	@ForSingleThread
	public void updateLine(long lineOffset, int pageIndex, CommentsPage page) throws IOException {
		updateLine(lineOffset, pageIndex, page, null);
	}
	
	/**
	 * Write into exists line new page
	 */
	@ForSingleThread
	public void updateLine(long lineOffset, int pageIndex, CommentsPage page, CommentsPage oldData) throws IOException {
		
		if(pageIndex < 0) throw new IllegalStateException("index must be >= 0");
		if(isFullLine(pageIndex)) throw new UrlLineIsFullException("can't write more then "+MAX_PAGES_COUNT+" pages per line");
		
		long offset = lineOffset + GLOBAL_ORDER_BYTES_COUNT + URL_BYTES_COUNT + pageIndex * PAGE_BYTES_COUNT;
		byte[] pageBytes = pageToBytes(page);
		byte[] updateTxBytes = getUpdateTxBytes(offset, oldData);
		
		//prepare update tx
		curTxFile.raf.seek(0);
		curTxFile.raf.write(updateTxBytes);
		
		//do update
		try {
			
			debug_BeforeUpdateUrlLine(file);
			
			file.raf.seek(offset);
			file.raf.write(pageBytes);
			
			debug_AfterUpdateUrlLine(file, offset, pageBytes);
			
			finishUpdateTx('+', lineOffset, pageIndex, page, oldData);
		}
		//exception while update line
		catch (Throwable t) {
			
			//only for tests!
			if(t instanceof ThreadDeath && DebugTool.THREAD_KILLED_SIMULATION){
				throw getRuntimeExceptionOrThrowError(t);
			}
			
			//revert tx
			GetOldDataFromUpdateTxResp revertData = getOldDataFromUpdateTx(updateTxBytes);
			try {
				file.raf.seek(revertData.offset);
				file.raf.write(revertData.data);
				finishUpdateTx('R', lineOffset, pageIndex, page, oldData);
			}
			//can't revert - try save info about it
			catch (Throwable revertTxError) {
				try {
					File waitRevert = new File(root, "unreverted-tx-"+randomSimpleId()+".log");
					writeFile(waitRevert, updateTxBytes);
				}catch (Throwable moveTxError) {
					String msg = "can't move unreverted tx to external file: "+getParamsStr(lineOffset, pageIndex, page, oldData);
					if(sessionLog != null) sessionLog.error(MOVE_UNREVERTED_FILE_ERROR, msg, moveTxError);
					log.error(msg, moveTxError);
				}
				finishUpdateTx('E', lineOffset, pageIndex, page, oldData);
			}
			
			if(t instanceof IOException) throw (IOException) t;
			else throw getRuntimeExceptionOrThrowError(t);
		}
	}

	private void finishUpdateTx(char result, long lineOffset, int pageIndex, CommentsPage page, CommentsPage oldData) {
		try {
			curTxFile.raf.seek(0);
			curTxFile.raf.writeByte(result);
		}
		//no exceptions to outside
		catch (Throwable txError) {
			String msg = "can't finish 'update url line tx' with result '"+result+"' : "+getParamsStr(lineOffset, pageIndex, page, oldData);
			if(sessionLog != null) sessionLog.error(CLOSE_TX_FILE_ERROR, msg, txError);
			log.error(msg, txError);
		}
	}
	
	
	
	
	public UrlLineReader readLines() throws IOException{
		return new UrlLineReader(file.f);
	}
	
	public boolean isFullLine(int index){
		return index > MAX_PAGES_COUNT-1;
	}
	
	
	public void close() throws IOException{
		file.closeRAF();
		curTxFile.closeRAF();
	}
	
	private static String getParamsStr(long lineOffset, int pageIndex, CommentsPage page, CommentsPage oldData){
		return "lineOffset="+lineOffset+", pageIndex="+pageIndex+", page="+page+", oldData="+oldData;
	}

	
	@ForSingleThread
	public void createSnapshot(String resultFilePath) throws IOException {
		if(resultFilePath == null) resultFilePath = new File(root, FILE_NAME+"-snapshot-"+randomSimpleId()+".BAK").getPath();
		File dest = new File(resultFilePath);
		if(dest.exists()) throw new InvalidInputException("can't copy to exists file: "+resultFilePath);
		FileUtil.copyFile(file.f, dest);
	}


}
