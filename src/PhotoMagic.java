
public class PhotoMagic {
	public static byte[] transform(byte[] picture, String seed, int tap){
		int Generated_Seed=0;
		//  transform picture using lfsr
		LFSR.init(seed, tap);
		for(int i=0; i<picture.length; i++){
			LFSR.generate(5);
			Generated_Seed = Integer.parseInt(LFSR.string(),2);
			picture[i] = (byte) (picture[i] ^ Generated_Seed); 
		}
		return picture;
	}
}
