package demo.util;

import java.sql.Timestamp;
import java.util.Date;

public class SqlUtil {
	
	public static Timestamp toSqlTimestamp(Date date){
		return date == null? null : new Timestamp(date.getTime());
	}
	
	public static java.sql.Date toSqlDate(Date date){
		return date == null? null : new java.sql.Date(date.getTime());
	}

}
