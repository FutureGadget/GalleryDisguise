import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// user password Encryption using MD5
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
		char[] tmp;
		tmp = str.toCharArray();
		for(int i=0; i<tmp.length; i++){
			key += Integer.toBinaryString(tmp[i]);
			if(key.length() > 31){
				key = key.substring(0, 31);
				break;	
			}
		}
		return key;
	}
}
