import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class imgtmp {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		byte[] data = null;
		byte[] encrypt = null;
		
		File f=new File("./cat.jpg");
		BufferedImage image = ImageIO.read(f);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", out);
		
		data = out.toByteArray();
		String str = "";
		for (int i = 0; i < data.length; i++) {
			str = byteArrayToBinaryString(data);
			System.out.print(str+" ");
			//System.out.print(data[i]+" ");
		}
		for(int i=0; i<str.length(); i++){
			encrypt=binaryStringToByteArray(str);
		}
		ImageIO.read(new ByteArrayInputStream(encrypt));
		ImageIO.write(image, "jpg", new File("./result"));
		//System.out.println(str);
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


