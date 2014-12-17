package demo.util.crypto;

import demo.junit.AssertExt;

import java.security.SecureRandom;




import org.junit.Test;

import static demo.util.StringUtil.*;
import static demo.util.crypto.AES128.*;

public class AES128Test extends AssertExt {
	
	@Test
	public void test_code_encode(){
		
		String input = "my str";
		byte[] inputBytes = getBytesUTF8(input);
		
		//<128bit key generation
		{
			byte[] key = new byte[5];
			new SecureRandom().nextBytes(key);
			byte[] encoded = encode(inputBytes, key);
			String decoded = getStrUTF8(decode(encoded, key));
			assertEquals(input, decoded);
			assertEquals(16, encoded.length);
		}
		
		//128bit key generation
		{
			byte[] key = new byte[16];
			new SecureRandom().nextBytes(key);
			byte[] encoded = encode(inputBytes, key);
			String decoded = getStrUTF8(decode(encoded, key));
			assertEquals(input, decoded);
			assertEquals(16, encoded.length);
		}
		
		//>128bit key generation
		{
			byte[] key = new byte[17];
			new SecureRandom().nextBytes(key);
			try {
				encode(inputBytes, key);
				fail_exception_expected();
			}catch (IllegalArgumentException e) {
				//ok
			}
		}
	}

}
