
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class main_Decrypt {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Encrypt_Passwd ep = new Encrypt_Passwd();
		String decrypt_path ="test/abcd/"; // decrypt할 폴더경로를 받아와야함.
		String outpath = "test/abc/"; // 내보낼 경로
		String user_passwd = "";
		String upw_Encrypt = "";
		Scanner sc = new Scanner(System.in);
		System.out.print("복호화 패스워드 입력 :");
		user_passwd = sc.nextLine();
		upw_Encrypt = ep.encryption(user_passwd);
		
		// 만약 복호화 패스워드의 md5값이 저장되어있는 값과 일치한다면, 복호화 수행
		// if(upw_Encrypt == local md5 value)
		String key = ep.key_generate("gubt");
		final File folder = new File(decrypt_path);
		ArrayList<String> paths = get_FilePath.listFiles(folder);
		for(String img_path : paths){
			try {
				byte[] byte_file = Processing.byteRead(img_path);
				byte_file = PhotoMagic.transform(byte_file, key, 8);
				Processing.Decrypt_save(byte_file, outpath, img_path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
