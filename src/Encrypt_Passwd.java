
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// User Password Encryption using by MD5
public class Encrypt_Passwd {
	// MD5 Encryption
	protected String encryption(String str){
		String md5 = " ";
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			md.update(str.getBytes());
			byte data[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<data.length; i++){
				sb.append(Integer.toString((data[i]&0xff)+0x100,16).substring(1));
			}
			md5 = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			md5 = "";
		}
		return md5;
	}
	
	// MD5 value to Binary 
	protected String key_generate(String str){
		String key = "";
		char[] tmp = null;
		str = str.replaceAll("[0-9]", ""); // Remove numeric values from string
		tmp = str.toCharArray(); 
		tmp = Cipher(tmp); // Caesar Cipher
		for(int i=0; i<tmp.length; i++){
			key += Integer.toBinaryString(tmp[i]);
			if(key.length() > 31){
				key = key.substring(0, 31);
				break;	
			}
		}
		return key;
	}
	
	protected char[] Cipher(char[] tmp){
		for(int i=0; i<tmp.length; i++){
			char letter = tmp[i];
			letter = (char)(letter + 3);
			if (letter > 'z') {
				letter = (char) (letter - 26);
		    } else if (letter < 'a') {
				letter = (char) (letter + 26);
			}
		    tmp[i] = letter;
		}
		return tmp;
	}
}