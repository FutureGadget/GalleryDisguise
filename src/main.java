import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class main {
	public static void main(String[] args) {
		Encrypt_Passwd ep = new Encrypt_Passwd();
		final String originalPath = "test"; // file path		
		String user_passwd = "";
		String result = "";
		String key = "";
		
		Scanner sc = new Scanner(System.in);
		System.out.print("패스워드 입력 :");
		user_passwd = sc.nextLine();
		result = ep.encryption(user_passwd); // md5 hash value 
		key = ep.key_generate(result);

		/**
		 * Iterate over image files.
		 * 1. Read image file to a byte stream.
		 * 2. Encrypt.
		 * 3. Write out.
		 */
		final File folder = new File(originalPath);
		ArrayList<String> paths = listFiles(folder);
		for (String s : paths) {
			int picture[][] = ImageData.imageData(s);
			Picture.setImage(picture);
			picture = PhotoMagic.transform(picture, key, 8); 	// perfect key : 00111000111100010000110101001			
			ImageData.save(picture, s);
		}
	}

	public static ArrayList<String> listFiles(final File folder) {
		ArrayList<String> files = new ArrayList<>();
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				files.add(fileEntry.getAbsolutePath());
			}
		}
		return files;
	}
}
