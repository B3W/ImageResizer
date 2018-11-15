import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Class for finding various shortest paths in a
 * graph where nodes have two dimensions (x, y).
 * 
 * @author Weston Berg
 */
public class WGraph {
	
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
	} // Edge
	
	/**
	 * Class for representing node with 2D coordinates.
	 * Needs to override hashcode and equals to be able
	 * to be used as key in a HashMap.
	 * @author Weston Berg
	 */
	private class Node {
		private final Integer[] coords;
		// Fields needed for Djikstra's Algorithm
		private int dist;
		private Node parent;
		private boolean visited;
		
		/**
		 * @param coords  Array of size 2 with x coordinate at index 0 and y coordinate at index 1
		 */
		public Node(Integer[] coords) {
			if(coords.length != 2) {
				throw new IllegalArgumentException();
			}
			this.coords = coords;
			this.dist = 0;
			this.parent = null;
			this.visited = false;
		}
		
		/**
		 * Getter for Node coordinates
		 * @return  Array of size 2 representing x, y coordinates of Node
		 */
		public Integer[] getCoords() {
			return coords;
		} // getCoords
			
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
	    public int hashCode () {
	        return Arrays.deepHashCode(this.coords);
	    } // hashCode

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
	    @Override
	    public boolean equals (Object obj) {
	    	Node node = (Node)obj;
	        return Arrays.deepEquals(this.coords, node.getCoords());
	    } // equals
	    
	    /*
	     * (non-Javadoc)
	     * @see java.lang.Object#toString()
	     */
	    @Override
	    public String toString() {
	    	return "(" + coords[0] + "," + coords[1] + ")";
	    } // toString
	} // Node
	
	/**
	 * Class for representing the graph as an adjacency list
	 * @author Weston Berg
	 */
	private class Graph {
		
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
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder retStr = new StringBuilder();
			for (Map.Entry<Node, ArrayList<Edge>> entry : adjList.entrySet()) {
				retStr.append(entry.getKey().toString());
				for (Edge e : entry.getValue()) {
					retStr.append("->");
					retStr.append(e.weight);
					retStr.append(e.node.toString());
				}
				retStr.append("\n");
			}
			return retStr.toString();
		} // toString
	} // Graph
	
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
			String line;
			int ux, uy, vx, vy;
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
				ux = Integer.parseInt(splitLine[0]);
				uy = Integer.parseInt(splitLine[1]);
				vx = Integer.parseInt(splitLine[2]);
				vy = Integer.parseInt(splitLine[3]);
				weight = Integer.parseInt(splitLine[4]);
				
				graph.addEdge(ux, uy, vx, vy, weight);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates the shortest path using Djikstra's algorithm
	 * from specified source vertex to specified destination vertex.
	 * @param ux  Source vertex x-coordinate
	 * @param uy  Source vertex y-coordinate
	 * @param vx  Destination vertex x-coordinate
	 * @param vy  Destination vertex y-coordinate
	 * @return 	ArrayList containing even number of integers,
               	for any even i,	i-th and i+1-th integers in the array represent
       			the x-coordinate and y-coordinate of the i/2-th vertex
      			in the returned path (path is an ordered sequence of vertices)
	 */
	ArrayList<Integer> V2V(int ux, int uy, int vx, int vy) {
		Node src = new Node(new Integer[] {ux, uy});
		Node curNode;
		ArrayList<Integer> minPath = new ArrayList<Integer>();
		PriorityQueue<Node> pq = new PriorityQueue<Node>();
		// Initialize priority queue values
		for (Map.Entry<Node, ArrayList<Edge>> entry : graph.adjList.entrySet()) {
			if(entry.getKey().equals(src)) {
				entry.getKey().dist = 0;
				entry.getKey().parent = null;
			} else {
				entry.getKey().dist = Integer.MAX_VALUE;
			}
			pq.add(entry.getKey());
		}
		
		return null;
	} // V2V
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String wgraphStr = "Nodes: " + graph.numNodes + "\n";
		wgraphStr += "Edges: " + graph.numEdges + "\n";
		wgraphStr += graph.toString();
		return wgraphStr;
	} // toString
	
} // WGraph
