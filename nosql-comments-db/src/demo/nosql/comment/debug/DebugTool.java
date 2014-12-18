package demo.nosql.comment.debug;

import java.util.ArrayList;
import java.util.List;

import demo.nosql.comment.model.FileInfo;
import demo.util.ExceptionUtil;
import static java.util.Collections.*;
import static demo.util.Util.*;

public class DebugTool {
	
	public static interface DebugListener {
		void onEvent(String name, Object data) throws Exception;
	}
	
	public static boolean ENABLED = false;
	public static boolean THREAD_KILLED_SIMULATION = false;
	
	private static List<DebugListener> listeners = emptyList();
	public static void add(DebugListener l){
		if( ! (listeners instanceof ArrayList)) listeners = new ArrayList<>();
		listeners.add(l);
	}
	public static void clear(){
		listeners = emptyList();
	}

	
	public static void debug_BeforeWriteToPageFile(FileInfo file){
		if(!ENABLED) return;
		for(DebugListener l : listeners) {
			try {
				l.onEvent("debug_BeforeWriteToPageFile", file);
			}catch (Exception e) {
				throw ExceptionUtil.getRuntimeExceptionOrThrowError(e);
			}
		}
	}
	
	public static void debug_AfterWriteToPageFile(FileInfo file){
		if(!ENABLED) return;
		for(DebugListener l : listeners) {
			try {
				l.onEvent("debug_AfterWriteToPageFile", file);
			}catch (Exception e) {
				throw ExceptionUtil.getRuntimeExceptionOrThrowError(e);
			}
		}
	}
	
	
	public static void debug_BeforeUpdatePageFile(FileInfo file){
		if(!ENABLED) return;
		for(DebugListener l : listeners) {
			try {
				l.onEvent("debug_BeforeUpdatePageFile", file);
			}catch (Exception e) {
				throw ExceptionUtil.getRuntimeExceptionOrThrowError(e);
			}
		}
	}
	
	public static void debug_AfterUpdatePageFile(FileInfo file){
		if(!ENABLED) return;
		for(DebugListener l : listeners) {
			try {
				l.onEvent("debug_AfterUpdatePageFile", file);
			}catch (Exception e) {
				throw ExceptionUtil.getRuntimeExceptionOrThrowError(e);
			}
		}
	}
	
	
	
	
	public static void debug_BeforeWriteNewUrlLine(FileInfo file){
		if(!ENABLED) return;
		for(DebugListener l : listeners) {
			try {
				l.onEvent("debug_BeforeWriteNewUrlLine", file);
			}catch (Exception e) {
				throw ExceptionUtil.getRuntimeExceptionOrThrowError(e);
			}
		}
	}
	
	public static void debug_AfterWriteNewUrlLine(FileInfo file){
		if(!ENABLED) return;
		for(DebugListener l : listeners) {
			try {
				l.onEvent("debug_AfterWriteNewUrlLine", file);
			}catch (Exception e) {
				throw ExceptionUtil.getRuntimeExceptionOrThrowError(e);
			}
		}
	}
	
	public static void debug_BeforeUpdateUrlLine(FileInfo file){
		if(!ENABLED) return;
		for(DebugListener l : listeners) {
			try {
				l.onEvent("debug_BeforeUpdateUrlLine", file);
			}catch (Exception e) {
				throw ExceptionUtil.getRuntimeExceptionOrThrowError(e);
			}
		}
	}
	
	public static void debug_AfterUpdateUrlLine(FileInfo file, long offset, byte[] pageBytes){
		if(!ENABLED) return;
		for(DebugListener l : listeners) {
			try {
				l.onEvent("debug_AfterUpdateUrlLine", map("file", file, "offset", offset, "bytes", pageBytes));
			}catch (Exception e) {
				throw ExceptionUtil.getRuntimeExceptionOrThrowError(e);
			}
		}
	}

}
