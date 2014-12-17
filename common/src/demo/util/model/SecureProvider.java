package demo.util.model;

import java.io.IOException;

public interface SecureProvider {
	
	public static final SecureProvider DUMMY_IMPL = new SecureProvider(){

		@Override
		public String encode(String msg) throws IOException {
			return msg;
		}

		@Override
		public String decode(String msg) throws IOException {
			return msg;
		}
		
	};
	
	public String encode(String msg) throws IOException;
	
	public String decode(String msg) throws IOException;

}
