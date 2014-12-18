package demo.nosql.comment.model.ban;


public class BannedCommentsPageJson {
	
	public long id;
	public String bannedIndexes;
	
	
	public void setId(long id) {
		this.id = id;
	}
	public void setBannedIndexes(String bannedIndexes) {
		this.bannedIndexes = bannedIndexes;
	}

}
