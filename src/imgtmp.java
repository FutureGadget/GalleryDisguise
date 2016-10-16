import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class imgtmp {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		final String originalPath = "test";
//		byte[] encrypt = null;
		
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
			ImageData.show(picture);
//			PhotoMagic.transform(picture, "00111000111100010000110101001", 8);
		}
	}
	/**
	 * Get absolute paths of files under a given folder.
	 * @param folder
	 * @return ArrayList of file paths under the given folder.
	 */
	public static ArrayList<String> listFiles(final File folder) {
		ArrayList<String> files = new ArrayList<>();
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				files.add(fileEntry.getAbsolutePath());
			}
		}
		return files;
	}
	
	public static String byteArrayToBinaryString(byte[] b){
	    StringBuilder sb=new StringBuilder();
	    for(int i=0; i<b.length; ++i){
	        sb.append(byteToBinaryString(b[i]));
	    }
	    return sb.toString();
	}

	public static String byteToBinaryString(byte n) {
	    StringBuilder sb = new StringBuilder("00000000");
	    for (int bit = 0; bit < 8; bit++) {
	        if (((n >> bit) & 1) > 0) {
	            sb.setCharAt(7 - bit, '1');
	        }
	    }
	    return sb.toString();
	}

	public static byte[] binaryStringToByteArray(String s){
	    int count=s.length()/8;
	    byte[] b=new byte[count];
	    for(int i=1; i<count; ++i){
	        String t=s.substring((i-1)*8, i*8);
	        b[i-1]=binaryStringToByte(t);
	    }
	    return b;
	}

	public static byte binaryStringToByte(String s){
	    byte ret=0, total=0;
	    for(int i=0; i<8; ++i){         
	        ret = (s.charAt(7-i)=='1') ? (byte)(1 << i) : 0;
	        total = (byte) (ret|total);
	    }
	    return total;
	}
	
}


