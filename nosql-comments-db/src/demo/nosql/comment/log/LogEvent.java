package demo.nosql.comment.log;

public enum LogEvent {
	
	READ_URLS_FILE_ERROR,
	ADD_COMMENT_ERROR,
	CLOSE_TX_FILE_ERROR,
	MOVE_UNREVERTED_FILE_ERROR,
	
	INFO,
	WARN,
	NO_CODE
	
}
