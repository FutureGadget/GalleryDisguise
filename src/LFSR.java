
public class LFSR {
	private static String initial_seed;
	private static int tap_index;
	public static boolean init(String seed, int tap){
		//  create LFSR with the given initial seed and tap
		if (seed.length() - tap < 0) return false;
		initial_seed = seed;
		tap_index = seed.length() - tap;
		return true;
	}
	public static int step() {
		return (initial_seed.charAt(0) - '0') ^ (initial_seed.charAt(tap_index) - '0');
		//  simulate one step and return the least significant (rightmost) bit as 0 or 1
	}
	public static int generate(int k) {
		//  simulate k steps and return k-bit integer
	}
	public static String string() {
		//  return a string representation of the LFSR
	}
	public static void main(String[] args) {
		//  test all of the methods in LFSR
	}
}
