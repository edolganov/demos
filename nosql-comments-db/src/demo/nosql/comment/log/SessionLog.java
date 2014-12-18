package demo.nosql.comment.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import org.apache.commons.logging.Log;

import demo.util.DateUtil;
import demo.util.Util;
import static demo.nosql.comment.log.LogEvent.*;
import static demo.util.Util.*;

public class SessionLog {
	
	private Log log = getLog(getClass());
	private String sessionId;
	private File logRoot;
	private File logFile;
	private PrintWriter writer;
	

	public SessionLog(File root) {
		
		this.logRoot = new File(root, "work-logs");
		sessionId = Util.randomSimpleId();
		
		
	}
	
	public String getId(){
		return sessionId;
	}
	
	public synchronized void info(String msg) {
		try {
			createLogFile();
			printPrefix(INFO);
			writer.println(msg);
			writer.println();
			writer.flush();
		}catch (Throwable t) {
			log.error("can't write msg", t);
		}
		
	}
	
	
	public synchronized void warn(String msg) {
		try {
			createLogFile();
			printPrefix(WARN);
			writer.println(msg);
			writer.println();
			writer.flush();
		}catch (Throwable t) {
			log.error("can't write msg", t);
		}
		
	}


	public synchronized void error(LogEvent code, String msg) {
		try {
			createLogFile();
			printPrefix(code);
			writer.println(msg);
			writer.println();
			writer.flush();
		}catch (Throwable t) {
			log.error("can't write msg", t);
		}
		
	}
	
	public synchronized void error(LogEvent code, Throwable error) {
		error(code, null, error);
	}

	public synchronized void error(LogEvent code, String msg, Throwable error) {
		try {
			createLogFile();
			printPrefix(code);
			if(msg != null) writer.println(msg);
			error.printStackTrace(writer);
			writer.println();
			writer.flush();
		}catch (Throwable t) {
			log.error("can't write msg", t);
		}
	}
	
	public void printPrefix(LogEvent code) {
		if(code == null) code = LogEvent.NO_CODE;
		writer.print("["+DateUtil.formatDate(new Date())+"] "+code+" ");
	}
	
	private void createLogFile() throws IOException {
		if(writer == null) {
			logRoot.mkdirs();
			logFile = new File(logRoot, "session-"+sessionId+".log");
			logFile.createNewFile();
			writer = new PrintWriter(new FileWriter(logFile, true));
		}
	}
	
	
	

}
