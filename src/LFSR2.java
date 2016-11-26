
public class LFSR2 {
	private static long current_seed;
	private static int tap_index;
	private static long seedlen;
	
	public static boolean init(String seed, int tap){
		seedlen = seed.length();
		//  create LFSR with the given initial seed and tap
		if (seedlen - tap < 0) return false;
		current_seed = Long.parseLong(seed,2);
		tap_index = (int) (seedlen - tap - 1);
		return true;
	}
	
	public static byte[] transform(byte[] picture, String seed, int tap){
		long Generated_Seed=0;
		//  transform picture using lfsr
		LFSR2.init(seed, tap);
		for(int i=0; i<picture.length; i++){
			LFSR2.generate(1);
			Generated_Seed = LFSR2.current_seed;
			picture[i] = (byte) (picture[i] ^ Generated_Seed); 
		}
		return picture;
	}

	public static long generate(int k) {
		//  simulate k steps and return k-bit integer
		long tmp;
		long result = 0;
		for (int i=0; i<k; i++){
			tmp = step();
			current_seed = remove_1digit() + tmp;
			result = result << 1;
			result += tmp;
		}
		return result;
	}
	
	public static long step() {
		return (get_Ndigit(0))^(get_Ndigit(tap_index));
	}

	public static long get_Ndigit(int N){
		long result = current_seed & (2^(seedlen-1-N));
		if(result == 0)
			return 0;
		else 
			return 1;
	}
	
	public static long remove_1digit(){
		long result = current_seed - (2^(seedlen-1));
		return result<<1;
	}
	
	public static long get_current_seed() {
		//  return a string representation of the LFSR
		return current_seed;
	}
//	public static int step() {
//		return (get_Ndigit(seedlen))^(get_Ndigit(seedlen-tap_index));
//		//  simulate one step and return the least significant (rightmost) bit as 0 or 1
//	}

//	public static int get_Ndigit(int N){
//		int i = 0;
//		int tmp = current_seed; 
//		while(tmp>=2){
//			if(N==i) break;
//			tmp = tmp >> 1;
//			i++;
//		}
//		tmp = tmp % 2;
//		return tmp;
//	}
	
//	public static int remove_1digit(){
//		int i=0;
//		int j=0;
//		int tmp = current_seed;
//		while(i >= 2){
//			tmp = tmp / 2;
//			j++;
//		}
//		return current_seed-2^j;
//	}

}
