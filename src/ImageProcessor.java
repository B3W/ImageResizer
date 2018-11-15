import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
	
	/**
	 * Computes Euclidean-like distance between two
	 * pixels based on their RGB values.
	 * @param p  A pixel
	 * @param q  A pixel
	 * @return  Return 'distance' between given pixels
	 */
	private int computePDist(Pixel p, Pixel q) {
		int res;
		res = (int)Math.pow((p.r - q.r), 2);
		res += (int)Math.pow((p.g - q.g), 2);
		res += (int)Math.pow((p.b - q.b), 2);
		return res;
	} // computePDist
	
	/**
	 * Computes the XImportance of the given pixel
	 * @param i  Row index of pixel in image matrix to compute importance for
	 * @param j  Column index of pixel in image matrix to compute importance for
	 * @return  XImportance of pixel at given position in matrix
	 */
	private int computeXImportance(int i, int j) {
		if (j == 0) {
			return computePDist(imgMatrix[i][imgW-1], imgMatrix[i][j+1]);
		} else if (j == (imgW-1)) {
			return computePDist(imgMatrix[i][j-1], imgMatrix[i][0]);
		} else {
			return computePDist(imgMatrix[i][j-1], imgMatrix[i][j+1]);
		}
	} // computeXImportance
	
	/**
	 * Computes the YImportance of the given pixel
	 * @param i  Row index of pixel in image matrix to compute importance for
	 * @param j  Column index of pixel in image matrix to compute importance for
	 * @return  YImportance of pixel at given position in matrix
	 */
	private int computeYImportance(int i, int j) {
		if (i == 0) {
			return computePDist(imgMatrix[imgH-1][j], imgMatrix[i+1][j]);
		} else if (i == (imgH-1)) {
			return computePDist(imgMatrix[i-1][j], imgMatrix[0][j]);
		} else {
			return computePDist(imgMatrix[i-1][j], imgMatrix[i+1][j]);
		}
	} // computeYImportance

	/**
	 * Compute the importance matrix 'I'
	 * @return  2D matrix where where each entry at indices i, j in I
	 			corresponds to the importance of pixel at indices i, j
	 			in imgMatrix.
	 */
	public ArrayList<ArrayList<Integer>> getImportance() {
		// Instantiate importance matrix
		ArrayList<ArrayList<Integer>> impMatrix = new ArrayList<ArrayList<Integer>>(imgH);
		for (int i = 0; i < imgH; i++) {
			impMatrix.add(new ArrayList<Integer>(imgW));
		}
		// Compute importance of each pixel
		int curImportance;
		for (int i = 0; i < imgH; i++) {
			for (int j = 0; j < imgW; j++) {
				curImportance = computeXImportance(i, j) + computeYImportance(i, j);
				impMatrix.get(i).add(j, curImportance);
			}
		}
		return impMatrix;
	} // getImportance
	
	/**
	 * Compute the new image matrix after reducing the width by k
     * Result written to file named FName in the same format as
     * the input image matrix.
	 * @param k  Number of pixels to reduce image width by
	 * @param FName  Filename to write modified image pixel data to
	 */
	public void writeReduced(int k, String FName) {
		// TODO
	} // writeReduced
	
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
