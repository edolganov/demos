package demo.nosql.comment.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import static demo.util.Util.*;

public class FileInfo {
	
	public final File f;
	public RandomAccessFile raf;
	public long length;
	
	
	
	public FileInfo(File f) throws FileNotFoundException {
		super();
		this.f = f;
		this.length = f.length();
		
		raf = new RandomAccessFile(f, "rw");
	}
	

	
	public void closeRAF() throws IOException {
		if(raf == null) return;
		RandomAccessFile curRAF = raf;
		raf = null;
		curRAF.close();
	}


	@Override
	public String toString() {
		return "FileInfo [file=" + f + ", raf=" + raf + ", length=" + length
				+ "]";
	}
	
	
	public static void closeRAFs(List<FileInfo> files) throws IOException {
		if(isEmpty(files)) return;
		for (FileInfo file : files) {
			file.closeRAF();
		}
	}
	
	



}
