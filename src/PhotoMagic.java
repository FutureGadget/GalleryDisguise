public class PhotoMagic {
	public static int[][] transform(int[][] picture, String seed, int tap){
		int Generated_Seed=0;
		//  transform picture using lfsr
		LFSR.init(seed, tap);
		for(int i=0; i<picture.length; i++){ // rows
			for(int j=0; j<picture[0].length; j++){ // columns
				LFSR.generate(5);
				Generated_Seed = Integer.parseInt(LFSR.string(),2);
				picture[i][j] = picture[i][j] ^ Generated_Seed; 
			}
		}
		return picture;
	}
}
