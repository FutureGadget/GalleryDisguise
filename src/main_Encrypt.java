

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class main_Encrypt {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Encrypt_Passwd ep = new Encrypt_Passwd();
		final String originalPath = "test"; // 사진폴더의 절대 경로받고
		final String outpath = "test/abcd/"; // 저장될 폴더 경로 넘겨받고 
		String user_passwd = "";
		String result = "";
		String key = "";
		
		Scanner sc = new Scanner(System.in);
		System.out.print("암호화 패스워드 입력 :");
		user_passwd = sc.nextLine(); // 맨 처음 1회 사용자로부터 password를 입력받음
		
		result = ep.encryption(user_passwd); // md5 hash value 
		key = ep.key_generate(result);
		
		final File folder = new File(originalPath);
		ArrayList<String> paths = get_FilePath.listFiles(folder);
		
		// Encrypt
		for (String img_path : paths) {
			try {
				byte[] byte_file = Processing.byteRead(img_path); // byte 형식으로 읽어옴
				byte_file = PhotoMagic.transform(byte_file, key, 8); // perfect key : 00111000111100010000110101001
				Processing.Encrypt_save(byte_file, outpath, img_path); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
