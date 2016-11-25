package com.jikheejo.ku.gallarydisguise.Encryption;

public class LFSR {
    private static int current_seed;
    private static int tap_index;
    private static int seedlen;

    public static boolean init(String seed, int tap){
        seedlen = seed.length();
        //  create LFSR with the given initial seed and tap
        if (seedlen - tap < 0) return false;
        current_seed = Integer.parseInt(seed,2);
        tap_index = seedlen - tap - 1;
        return true;
    }

    public static byte[] transform(byte[] picture, String seed, int tap){
        int Generated_Seed=0;
        //  transform picture using lfsr
       init(seed, tap);
        for(int i=0; i<picture.length; i++){
            generate(1);
            Generated_Seed = current_seed;
            picture[i] = (byte) (picture[i] ^ Generated_Seed);
        }
        return picture;
    }

    public static int generate(int k) {
        //  simulate k steps and return k-bit integer
        int tmp;
        int result = 0;
        for (int i=0; i<k; i++){
            tmp = step();
            current_seed = remove_1digit() + tmp;
            result = result << 1;
            result += tmp;
        }
        return result;
    }

    public static int step() {
        return (get_Ndigit(0))^(get_Ndigit(tap_index));
    }

    public static int get_Ndigit(int N){
        int result = current_seed & (2^(seedlen-1-N));
        if(result == 0)
            return 0;
        else
            return 1;
    }

    public static int remove_1digit(){
        int result = current_seed - (2^(seedlen-1));
        return result<<1;
    }

    public static int get_current_seed() {
        //  return a string representation of the LFSR
        return current_seed;
    }
}