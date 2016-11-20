import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class Picture {
    private BufferedImage image;               // the rasterized image
    private String filename;                   // name of file
    private int width, height;                 // width and height
    private static int[][] picture;				   // image to transform
    private boolean isOriginUpperLeft = true;  // location of origin
   /**
	* Create a blank w-by-h picture, where each pixel is black.
   */
    public Picture(int w, int h) {
        width = w;
        height = h;
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        // set to TYPE_INT_ARGB to support transparency
        filename = w + "-by-" + h;
    }
    
    public static void setImage(int[][] image) {
    	picture = new int[image.length][image[0].length];
    	for (int i = 0; i < image.length; ++i) {
    		for (int j = 0; j < image[0].length; ++j) {
    			picture[i][j] = image[i][j];
    		}
    	}
    }

   /**
     * Create a picture by reading in a .png, .gif, or .jpg from
     * the given filename or URL name.
     */
    public Picture(String filename) {
        this.filename = filename;
        try {
            // try to read from file in working directory
            File file = new File(filename);
            if (file.isFile()) {
                image = ImageIO.read(file);
            }

            // now try to read from file in same directory as this .class file
            else {
                URL url = getClass().getResource(filename);
                if (url == null) { url = new URL(filename); }
                image = ImageIO.read(url);
            }
            width  = image.getWidth(null);
            height = image.getHeight(null);
        }
        catch (IOException e) {
            // e.printStackTrace();
            throw new RuntimeException("Could not open file: " + filename);
        }
    }
    /**
     * Return the color of pixel (i, j).
     */
    public Color get(int i, int j) {
        if (isOriginUpperLeft) return new Color(image.getRGB(i, j));
        else                   return new Color(image.getRGB(i, height - j - 1));
    }

   /**
     * Set the color of pixel (i, j) to c.
     */
    public void set(int i, int j, Color c) {
        if (c == null) { throw new RuntimeException("can't set Color to null"); }
        if (isOriginUpperLeft) image.setRGB(i, j, c.getRGB());
        else                   image.setRGB(i, height - j - 1, c.getRGB());
    }
    
    /**
      * Save the picture to a file in a standard image format.
      * The filetype must be .png or .jpg.
      */
     public void save(String name) {
         save(new File(name));
     }

    /**
      * Save the picture to a file in a standard image format.
      */
     public void save(File file) {
         this.filename = file.getName();
         String suffix = filename.substring(filename.lastIndexOf('.') + 1);
         suffix = suffix.toLowerCase();
         if (suffix.equals("jpg") || suffix.equals("png")) {
             try { 
            	 ImageIO.write(image, suffix, new File("test/abcd")); 
            	 System.out.println("저장 성공!!!");
             } // 원래 ImageIO.write(image, suffix, file);
             catch (IOException e) { e.printStackTrace(); }
         }
         else {
             System.out.println("Error: filename must end in .jpg or .png");
         }
     }
    
   /**
     * Create a picture by reading in a .png, .gif, or .jpg from a File.
     */
    public Picture(File file) {
        try { image = ImageIO.read(file); }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not open file: " + file);
        }
        if (image == null) {
            throw new RuntimeException("Invalid image file: " + file);
        }
    }
  
   /**
     * Return the height of the picture in pixels.
     */
    public int height() {
        return height;
    }

   /**
     * Return the width of the picture in pixels.
     */
    public int width() {
        return width;
    }

}