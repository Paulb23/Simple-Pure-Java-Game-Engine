package src;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class Art {
	
	private transient static final Logger LOG = Logger.getLogger(GameClass.class.getName());

	public static Images Load(String string) {		
		Images result;
		
		 try {
	            final BufferedImage bi = ImageIO.read(new File(string));

	            final int w = bi.getWidth();
	            final int h = bi.getHeight();

	            result = new Images(w, h);
	            bi.getRGB(0, 0, w, h, result.pixels, 0, w);

	           
	        } catch (IOException e) {
	        	 LOG.log(null, "Cant Load Image!", e);
	        	 result = null;
	        }
		 
		 return result;
	}
	
	public static int generatRandomPositiveNegitiveValue(int max , int min) {
	    //Random rand = new Random();
	    int ii = -min + (int) (Math.random() * ((max - (-min)) + 1));
	    return ii;
	}
	
}
