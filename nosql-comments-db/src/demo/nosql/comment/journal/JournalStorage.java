package demo.nosql.comment.journal;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.logging.Log;

import demo.nosql.comment.model.FileInfo;
import demo.nosql.comment.page.PageConverter;
import demo.util.NumberUtil;
import static demo.util.Util.*;

public class JournalStorage {
	
	private static final Log log = getLog(JournalStorage.class);
	
	
	public final File root;
	public final JournalConverter c;
	
	private FileInfo journal;
	
	
	public JournalStorage(File root) throws IOException {
		this(root, new PageConverter());
	}

	public JournalStorage(File root, PageConverter pageConverter) throws IOException {
		
		this.root = root;
		this.c = new JournalConverter(pageConverter);
		
		journal = getLastJournal(root);
		if(journal == null){
			journal = newJournal(root);
		}
		
	}
	
	
	
	private static FileInfo newJournal(File root) throws IOException{
		File file = new File(root, "journal-"+NumberUtil.zeroFormattedStr(0, 6)+".db");
		file.createNewFile();
		return new FileInfo(file);
	}
	
	private static FileInfo getLastJournal(File root) throws IOException{
		String[] existsFiles = root.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("journal-");
			}
		});
		if(isEmpty(existsFiles)) return null;
		Arrays.sort(existsFiles);
		File file = new File(root, existsFiles[existsFiles.length-1]);
		return new FileInfo(file);
	}
	

}
