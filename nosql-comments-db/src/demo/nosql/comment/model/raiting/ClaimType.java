package demo.nosql.comment.model.raiting;

import demo.util.model.HasIntCode;

public enum ClaimType implements HasIntCode {
	
	SPAM(1),
	FILTHY_LANGUAGE(2),
	NUDE_CONTENT(3),
	HUMILIATION(4),
	DECEPTION(5),
	VIOLENCE(6),
	STOLEN_ACCOUNT(7),
	OTHER(8),
	
	;
	public final int code;

	private ClaimType(int code) {
		this.code = code;
	}

	@Override
	public int getCode() {
		return code;
	}

}
