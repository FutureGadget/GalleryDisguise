
public class LFSR {
	private static String initial_seed;
	private static String current_seed;
	private static int tap_index;
	public static boolean init(String seed, int tap){
		//  create LFSR with the given initial seed and tap
		if (seed.length() - tap < 0) return false;
		initial_seed = seed;
		current_seed = seed;
		tap_index = seed.length() - tap - 1;
		return true;
	}
	public static int step() {
		return (current_seed.charAt(0) - '0') ^ (current_seed.charAt(tap_index) - '0');
		//  simulate one step and return the least significant (rightmost) bit as 0 or 1
	}
	public static int generate(int k) {
		int key = 0;
		for (int i = 0; i < k; ++i) {
			key = key << 1;
			key += step();
		}
		return key;
		//  simulate k steps and return k-bit integer
		int tmp;
		int result = 0;
		for (int i=0; i<k; i++){
			tmp=step();
			current_seed = current_seed.substring(1, current_seed.length()) + tmp;
			result = result << 1;
			result += tmp;
		}
		return result;
	}
	public static String string() {
		//  return a string representation of the LFSR
		return current_seed;
	}
	public static void main(String[] args) {
		//  test all of the methods in LFSR
		LFSR.init("01101000010", 8);
		for (int i = 0; i < 10; i++) {
		    int r = LFSR.generate(5);
		    System.out.println(LFSR.string() + " " + r);
		}
	}
}
