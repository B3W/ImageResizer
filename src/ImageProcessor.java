import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private List<ArrayList<Pixel>> imgMatrix;
	
	/**
	 * Construct matrix representing each pixel in
	 * an image and the RGB values of those pixels.
	 * @param FName  Filename of file containing pixel info
	 */
	public ImageProcessor(String FName) {
		try (BufferedReader br = new BufferedReader(new FileReader(FName))) {  // Open file for reading
			String line;
			String[] spltLine;
			int i, r, g, b;
			
			if ((line = br.readLine()) != null) {  // Read image height
				imgH = Integer.parseUnsignedInt(line);
			}
			if ((line = br.readLine()) != null) {  // Read image width
				imgW = Integer.parseUnsignedInt(line);
			}
			imgMatrix = new ArrayList<ArrayList<Pixel>>(imgH);
			
			i = 0;
			while ((line = br.readLine()) != null) {  // Read in pixel info
				imgMatrix.add(new ArrayList<Pixel>(imgW));
				spltLine = line.trim().split(" +");
				for (int index = 0; index < spltLine.length; index+=3) {
					r = Integer.parseInt(spltLine[index]);
					g = Integer.parseInt(spltLine[index+1]);
					b = Integer.parseInt(spltLine[index+2]);
					imgMatrix.get(i).add(new Pixel(r, g, b));
				}
				i++;
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
			return computePDist(imgMatrix.get(i).get(imgW-1), imgMatrix.get(i).get(j+1));
		} else if (j == (imgW-1)) {
			return computePDist(imgMatrix.get(i).get(j-1), imgMatrix.get(i).get(0));
		} else {
			return computePDist(imgMatrix.get(i).get(j-1), imgMatrix.get(i).get(j+1));
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
			return computePDist(imgMatrix.get(imgH-1).get(j), imgMatrix.get(i+1).get(j));
		} else if (i == (imgH-1)) {
			return computePDist(imgMatrix.get(i-1).get(j), imgMatrix.get(0).get(j));
		} else {
			return computePDist(imgMatrix.get(i-1).get(j), imgMatrix.get(i+1).get(j));
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
		// Compute importance of each pixel
		int curImportance;
		for (int i = 0; i < imgH; i++) {
			impMatrix.add(new ArrayList<Integer>(imgW));
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
		/*
		if ((imgW - k) < 2) {
			throw new IllegalArgumentException("Invalid reduction amount. Image must have pixel width greater than 1 after reduction.");
		}
		*/
		ArrayList<ArrayList<Integer>> importance;
		ArrayList<Integer> s1, s2, minCut;
		WGraph pixelG;
		int lastRow = imgH - 1;
		// Copy image matrix to prevent changing original
		int originalW = imgW;
		List<ArrayList<Pixel>> originalImgMatrix = new ArrayList<ArrayList<Pixel>>(imgH);
		ArrayList<Pixel> listRow;
		for (int i = 0; i < imgMatrix.size(); i++) {
			listRow = imgMatrix.get(i);
			originalImgMatrix.add(new ArrayList<Pixel>(imgW));
			for (int j = 0; j < listRow.size(); j++) {
				originalImgMatrix.get(i).add(listRow.get(j));
			}
		}
		// Begin width reduction
		s1 = new ArrayList<Integer>();
		s2 = new ArrayList<Integer>();
		for (int cnt = 0; cnt < k; cnt++) {
			// Compute importance
			importance = this.getImportance();
			// Construct WGraph with Pixel info
			pixelG = new WGraph(importance);
			// Find minimum cost vertical cut
			s1.clear();
			s2.clear();
			for (int j = 0; j < imgW; j++) { // Construct sets for S2S shortest path search
				s1.add(j);
				s1.add(0);
				s2.add(j);
				s2.add(lastRow);
			}
			minCut = pixelG.S2S(s1, s2);
			// Remove pixels in min cut from image
			for (int x = 0; x < minCut.size(); x+=2) {
				imgMatrix.get(minCut.get(x+1)).remove(minCut.get(x).intValue());
			}
			imgW--;
		}
		// Write result
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FName))) {  // Open file for writing
			ArrayList<Pixel> pixelRow;
			bw.write(imgH + "\n");
			bw.write(imgW + "\n");
			for (int i = 0; i < imgMatrix.size(); i++) {
				pixelRow = imgMatrix.get(i);
				for (Pixel p : pixelRow) {
					bw.write(p.r + " " + p.g + " " + p.b + " ");
				}
				bw.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Restore ImageProcessor object back to original state before reduction
		imgW = originalW;
		imgMatrix.clear();
		for (int i = 0; i < originalImgMatrix.size(); i++) {
			listRow = originalImgMatrix.get(i);
			imgMatrix.add(new ArrayList<Pixel>(imgW));
			for (int j = 0; j < listRow.size(); j++) {
				imgMatrix.get(i).add(listRow.get(j));
			}
		}
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
				ipStr.append(imgMatrix.get(i).get(j).toString());
				ipStr.append(" ");
			}
			ipStr.append('\n');
		}
		return ipStr.toString();
	} // toString
}
