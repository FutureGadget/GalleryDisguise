public class ImageData {
  /* Load an image from filename, and return it
   * as a 2D array of integers in row-major order
   *
   * and empty, 0x0 array is returned on errors
   */
  public static int[][] imageData(String filename) {
    try {
      Picture p = new Picture(filename);

      int[][] img = new int[p.height()][p.width()];
      for (int row = 0; row < p.height(); row++) {
        for (int col = 0; col < p.width(); col++) {
          img[row][col] = p.get(col, row).getRGB();
        }
      }

      return img;
    } catch (RuntimeException e) {
      return new int[0][0];
    }
  }

  /* Create a Picture object from image data
   * in a 2D array of ints.  This is primarily
   * a helper method for show() and save(),
   * but is declared public because it is
   * useful in its own right.
   */
  public static Picture getPicture(int[][] img) {
    if (img == null) return null;
    if (img.length == 0) return null;
    if (img[0].length == 0) return null;

    Picture p = new Picture(img[0].length, img.length);

    for (int row = 0; row < img.length; row++) {
      for (int col = 0; col < img[0].length; col++) {
        p.set(col, row, new java.awt.Color(img[row][col]));
      }
    }

    return p;
  }
  
  public static void save(int[][] img, String filename) {
	    Picture p = getPicture(img);
	    if (p != null) p.save(filename);
	  }
  
}