import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Class for finding various shortest paths in a
 * graph where nodes have two dimensions (x, y).
 * 
 * @author Weston Berg
 */
public class WGraph {
	
	/**
	 * Class for representing the graph as an adjacency list
	 * @author Weston Berg
	 */
	private class Graph {
		
		/**
		 * Class for representing node with 2D coordinates.
		 * Needs to override hashcode and equals to be able
		 * to be used as key in a HashMap.
		 * @author Weston Berg
		 */
		private class Node {
			private final Integer[] coords;
			
			/**
			 * @param coords  Array of size 2 with x coordinate at index 0 and y coordinate at index 1
			 */
			public Node(Integer[] coords) {
				if(coords.length != 2) {
					throw new IllegalArgumentException();
				}
				this.coords = coords;
			}
			
			/**
			 * Getter for Node coordinates
			 * @return  Array of size 2 representing x, y coordinates of Node
			 */
			public Integer[] getCoords() {
				return coords;
			}
			
			/*
			 * (non-Javadoc)
			 * @see java.lang.Object#hashCode()
			 */
			@Override
		    public int hashCode () {
		        return Arrays.deepHashCode(this.coords);
		    }

			/*
			 * (non-Javadoc)
			 * @see java.lang.Object#equals(java.lang.Object)
			 */
		    @Override
		    public boolean equals (Object obj) {
		    	Node node = (Node)obj;
		        return Arrays.deepEquals(this.coords, node.getCoords());
		    }
		}
		
		/**
		 * Class for representing a directed, weighted edge in the graph
		 * @author Weston Berg
		 */
		private class Edge {
			private Node node;
			private int weight;
			
			/**
			 * @param node  Node at which edge ends
			 * @param weight  Weight of the edge
			 */
			public Edge(Node node, int weight) {
				this.node = node;
				this.weight = weight;
			}
		}
		
		/**
		 * Adjacency list represented as a HashMap
		 */
		private HashMap<Node, ArrayList<Edge>> adjList;
		private int numNodes;
		private int numEdges;
		
		/**
		 * Constructs an empty graph
		 */
		public Graph() {
			adjList = new HashMap<Node, ArrayList<Edge>>();
			numNodes = 0;
			numEdges = 0;
		}
		
		/**
		 * Add an edge to the graph. If the the source vertex is not already
		 * in the adjacency list then a new entry is created. Assume there are
		 * no duplicate edges so no error checking is needed.
		 * 
		 * @param src  Source node
		 * @param dest  Destination node
		 * @param weight  Weight of the directed edge between nodes
		 */
		public void addEdge(int srcX, int srcY, int destX, int destY, int weight) {
			Node u, v;
			
			u = new Node(new Integer[] {srcX, srcY});
			v = new Node(new Integer[] {destX, destY});
			
			if(!adjList.containsKey(u)) {
				adjList.put(u, new ArrayList<Edge>());
			}
			adjList.get(u).add(new Edge(v, weight));
		} // addEdge
		
		@Override
		public String toString() {
			// TODO
			return null;
		}
	}
	
	/**
	 * Graph represented as adjacency list
	 */
	private Graph graph;
	
	/**
	 * Constructs a graph which will then be analyzed for
	 * shortest paths of different kinds. The graph is stored
	 * as an adjacency list.
	 * 
	 * @param FName  Name of file containing edge info
	 */
	public WGraph(String FName) {
		graph = new Graph();
		
		try(BufferedReader br = new BufferedReader(new FileReader(FName))) {  // Open file for reading
			String line, ux, uy, vx, vy;
			String splitLine[];
			int weight;
			
			if((line = br.readLine()) != null) {  // Read in number of nodes in the graph
				graph.numNodes = Integer.parseUnsignedInt(line);
			}
			if((line = br.readLine()) != null) {  // Read in number of edges in the graph
				graph.numEdges = Integer.parseUnsignedInt(line);
			}
			
			while((line = br.readLine()) != null) {  // Read in the edge information
				splitLine = line.split(" ");
				ux = splitLine[0];
				uy = splitLine[1];
				vx = splitLine[2];
				vy = splitLine[3];
				weight = Integer.parseInt(splitLine[4]);
				
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // WGraph
	
}
