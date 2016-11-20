

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class Processing {
	// image to byte style
	public static byte[] byteRead(String filepath) throws IOException{
		byte[] img = null;
		Path path = Paths.get(filepath);
		img = Files.readAllBytes(path);
		return img;
	}
	
	// File Encryption using by LFSR Algorithm &  Save 
	public static void Encrypt_save (byte[] file, String folder_path, String img_path){
		String outName = fileName_Parse(img_path, 1);
		Path path = Paths.get(folder_path + outName);
		try {
			Files.write(path, file); // 저장 성공
			System.out.println("암호화 저장 성공!!!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// File Decryption using by LFSR Algorithm &  Save 
	public static void Decrypt_save(byte[] file, String folder_path, String img_path){
		String outName = fileName_Parse(img_path, 2);
		Path path = Paths.get(folder_path + outName);
		try {
			Files.write(path, file);
			System.out.println("복호화 저장 성공!!!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public static String fileName_Parse(String img_path, int select){
		String outName = img_path.substring(img_path.lastIndexOf("\\")+1); // 절대경로에서 abc.jpg 만 떼어냄.
		if(select == 1)
			return fileName_Hide(outName);
		else
			return fileName_Unhide(outName);
	}
	
	// Hiding original file name
	public static String fileName_Hide(String str){
		String result = "";
		// 원본 filename 을 숨김.
		byte[] encode = str.getBytes();
		result = Base64.getEncoder().encodeToString(encode);
		return result;
	}
	
	public static String fileName_Unhide(String str){
		String result = "";	
		byte[] decode = Base64.getDecoder().decode(str);
		result = new String(decode);
		return result;
	}
	
}