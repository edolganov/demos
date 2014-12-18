package demo.nosql.comment.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import demo.util.Const;
import demo.util.StreamUtil;
import demo.util.StringUtil;
import demo.util.json.GsonUtil;
import demo.util.json.JsonUtil;
import static demo.util.DateUtil.*;

import static demo.util.Util.*;

import static demo.util.json.GsonUtil.*;

public class Comment {

	public static final int CONTENT_MAX_SIZE = 300;
	public static final String DATE_FORMAT = Const.FULL_DATE_FORMAT;
	
	public Date created;
	public long author;
	private String content;
	
	

	public Comment() {
		super();
	}
	

	public Comment(Date created, long author, String content) {
		this(author, content);
		this.created = created;
	}
	
	public Comment(long author, String content) {
		super();
		this.author = author;
		setContent(content);
	}

	public Comment(String content) {
		super();
		setContent(content);
	}
	
	public void setContent(String content) {
		content = convertContent(content);
		this.content = content;
	}

	public Date getCreated() {
		return created;
	}
	
	public void setCreated(String createdStr) {
		setCreated(parseDate(createdStr, DATE_FORMAT));
	}

	public void setCreated(long created) {
		setCreated(new Date(created));
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}

	public long getAuthor() {
		return author;
	}

	public void setAuthor(long author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}


	public static String convertContent(String content) {
		
		if( ! hasText(content)) return "";
		
		content = StringUtil.removeAll(content, "\r\0");
			
		//check every char to json escaping
		int realIndex;
		int virtualIndex = 0;
		int length = content.length();
		for (realIndex = 0; realIndex < length; realIndex++) {
			
			
			char c = content.charAt(realIndex);
			String replacement = JsonUtil.getReplacement(c, true);
			if(replacement != null) virtualIndex += replacement.length();
			else virtualIndex++;
			
			if(virtualIndex >= CONTENT_MAX_SIZE){
				if(replacement == null) realIndex++;
				break;
			}
		}
		//cut str if need
		if(virtualIndex >= CONTENT_MAX_SIZE){
			content = content.substring(0, realIndex);
		}
		
		return content;
	}
	
	public static List<Comment> fromJsonStream(InputStream is) throws IOException{
		if(is == null) return null;
		String json = StreamUtil.streamToStr(is);
		if( ! json.startsWith("[")) json = "[" + json;
		if( ! json.endsWith("]")) json = json + "]";
		return GsonUtil.getList(json, Comment.class, defaultGson);
	}


	@Override
	public String toString() {
		return "Comment [created=" + created + ", author=" + author
				+ ", content=" + content + "]";
	}
	
	
	
	

}
