
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// User Password Encryption using by MD5
public class Encrypt_Passwd {
	// SHA-256 Encryption
	public static String encryption(String str){
		String sha256 = " ";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256"); 
			md.update(str.getBytes());
			byte data[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<data.length; i++){
				sb.append(Integer.toString((data[i]&0xff)+0x100,16).substring(1));
			}
			sha256 = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sha256 = "";
		}
		return sha256;
	}
	
	// Image Encryption Key
	public static String key_generate(String randkey){
		String key = "";
		char[] tmp = null;
		tmp = randkey.toCharArray(); 
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
	
	// making random String
	public static String randKey(){
		String key="";
		char tmp;
		for(int i=0; i<4; i++){
			tmp = (char)((int)(Math.random()*26)+97);
			key += tmp;
		}
		return key;
	}
	
	// Caesar Cipher
	public static char[] Cipher(char[] tmp){
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