public class PhotoMagic {
	public static int[][] transform(int[][] picture, String seed, int tap){
		int Generated_Seed=0;
		//  transform picture using lfsr
		LFSR.init(seed, tap);
		for(int i=0; i<picture.length; i++){ // rows
			for(int j=0; j<picture[0].length; j++){ // columns
				LFSR.generate(2);
				Generated_Seed = Integer.parseInt(LFSR.string(),2);
				picture[i][j] = picture[i][j] ^ Generated_Seed; 
			}
		}
		
		return picture;
	}
	
	public static  void main(String[] args){
		//  read in the name of a picture and the description of an LFSR
        //  from the command-line and encrypt the picture with the LFSR
		int picture[][] = ImageData.imageData("test/cat.jpg");
		transform(picture, "0011100011110001000011010100111", 8);
		ImageData.show(picture);
		ImageData.save(picture, "abc.jpg"); // 
	}
}
