package demo.nosql.comment.model.ban;

import demo.util.model.HasIntCode;

public enum CommentBanReason implements HasIntCode {
	
	SPAM(1),
	FILTHY_LANGUAGE(2),
	NUDE_CONTENT(3),
	HUMILIATION(4),
	DECEPTION(5),
	VIOLENCE(6),
	STOLEN_ACCOUNT(7),
	CLAIM(8)
	
	
	;
	public final int code;

	private CommentBanReason(int code) {
		this.code = code;
	}

	@Override
	public int getCode() {
		return code;
	}
	
	

}
