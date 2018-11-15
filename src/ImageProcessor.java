import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class for shrinking images based on pixel
 * importance. 
 * @author Weston Berg
 */
public class ImageProcessor {
	
	/**
	 * Class representing a pixel in an image
	 * @author Weston Berg
	 */
	private class Pixel {
		private int r, g, b;
		
		public Pixel(int R, int G, int B) {
			r = R;
			g = G;
			b = B;
		}
	}
	
	private int imgH;
	private int imgW;
	private Pixel[][] imgMatrix;
	
	/**
	 * Construct matrix representing each pixel in
	 * an image and the RGB values of those pixels.
	 * @param FName  Filename of file containing pixel info
	 */
	public ImageProcessor(String FName) {
		try (BufferedReader br = new BufferedReader(new FileReader(FName))) {  // Open file for reading
			String line;
			String[] spltLine;
			int i, j;
			
			if ((line = br.readLine()) != null) {  // Read image height
				imgH = Integer.parseUnsignedInt(line);
			}
			if ((line = br.readLine()) != null) {  // Read image width
				imgW = Integer.parseUnsignedInt(line);
			}
			imgMatrix = new Pixel[imgH][imgW];
			
			while ((line = br.readLine()) != null) {
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
