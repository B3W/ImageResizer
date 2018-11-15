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
	 * Class representing a pixel in an image. Pixel's
	 * identifying info is its RGB format values.
	 * @author Weston Berg
	 */
	private class Pixel {
		private int r, g, b;
		
		/**
		 * Constructs a new pixel instance
		 * @param R  Red value
		 * @param G  Green value
		 * @param B  Blue value
		 */
		public Pixel(int R, int G, int B) {
			r = R;
			g = G;
			b = B;
		} // Pixel
		
		@Override
		public String toString() {
			return "[" + r + "," + g + "," + b + "]";
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
			int i, j, r, g, b;
			
			if ((line = br.readLine()) != null) {  // Read image height
				imgH = Integer.parseUnsignedInt(line);
			}
			if ((line = br.readLine()) != null) {  // Read image width
				imgW = Integer.parseUnsignedInt(line);
			}
			imgMatrix = new Pixel[imgH][imgW];
			
			i = j = 0;
			while ((line = br.readLine()) != null) {  // Read in pixel info
				spltLine = line.split(" ");
				for (int index = 0; index < spltLine.length; index+=3) {
					r = Integer.parseInt(spltLine[index]);
					g = Integer.parseInt(spltLine[index+1]);
					b = Integer.parseInt(spltLine[index+2]);
					imgMatrix[i][j] = new Pixel(r, g, b);
					j++;
				}
				i++;
				j = 0;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // ImageProcessor
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder ipStr = new StringBuilder();
		for (int i = 0; i < imgH; i++) {
			for (int j = 0; j < imgW; j++) {
				ipStr.append(imgMatrix[i][j].toString());
				ipStr.append(" ");
			}
			ipStr.append('\n');
		}
		return ipStr.toString();
	} // toString
}
