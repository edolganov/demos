package demo.nosql.comment.journal;

public class JournalRecord {
	
	public byte status;
	public long id;
	public String url;
	public long prevPageOffset;
	public long prevPageLimit;

	public byte[] commentBytes;

}
