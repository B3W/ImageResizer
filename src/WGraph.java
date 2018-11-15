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
		private int x, y;
		ArrayList<Edge> edges;
		// Fields needed for Djikstra's Algorithm
		private int dist;
		private Node parent;
		private boolean visited;
		
		/**
		 * @param coords  Array of size 2 with x coordinate at index 0 and y coordinate at index 1
		 */
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
			this.edges = new ArrayList<Edge>();
			this.dist = 0;
			this.parent = null;
			this.visited = false;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
	    @Override
	    public boolean equals (Object obj) {
	    	Node node;
	    	try {
	    		node = (Node)obj;
	    	} catch(ClassCastException cce) {
	    		return false;
	    	}
	        return (this.x == node.x) && (this.y == node.y);
	    } // equals
	    
	    /*
	     * (non-Javadoc)
	     * @see java.lang.Object#toString()
	     */
	    @Override
	    public String toString() {
	    	return "(" + x + "," + y + ")";
	    } // toString
	} // Node

	/**
	 * Graph represented as adjacency list
	 */
	private ArrayList<Node> adjList;
	private int numNodes;
	private int numEdges;
	
	/**
	 * Constructs a graph which will then be analyzed for
	 * shortest paths of different kinds. The graph is stored
	 * as an adjacency list.
	 * 
	 * @param FName  Name of file containing edge info
	 */
	public WGraph(String FName) {
		adjList = new ArrayList<Node>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(FName))) {  // Open file for reading
			String line;
			int ux, uy, vx, vy, srcIndex, edgeIndex;
			int weight;
			String splitLine[];
			Node srcNode, edgeNode;
			
			if((line = br.readLine()) != null) {  // Read in number of nodes in the graph
				numNodes = Integer.parseUnsignedInt(line);
			}
			if((line = br.readLine()) != null) {  // Read in number of edges in the graph
				numEdges = Integer.parseUnsignedInt(line);
			}
			
			while((line = br.readLine()) != null) {  // Read in the edge information
				splitLine = line.split(" ");
				ux = Integer.parseInt(splitLine[0]);
				uy = Integer.parseInt(splitLine[1]);
				vx = Integer.parseInt(splitLine[2]);
				vy = Integer.parseInt(splitLine[3]);
				weight = Integer.parseInt(splitLine[4]);				
				// Add new node or update existing
				edgeNode = new Node(vx, vy);
				edgeIndex = adjList.indexOf(edgeNode);
				if(!(edgeIndex < 0)) { // Check if destination vertex already exists
					edgeNode = adjList.get(edgeIndex);
				}
				srcNode = new Node(ux, uy);
				srcIndex = adjList.indexOf(srcNode);
				if(srcIndex < 0) { // Check if src node already exists
					srcNode.edges.add(new Edge(edgeNode, weight));
					adjList.add(srcNode);
				} else {
					adjList.get(srcIndex).edges.add(new Edge(edgeNode, weight));
				}
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
		Node src = new Node(ux, uy);
		Node curNode;
		ArrayList<Integer> minPath = new ArrayList<Integer>();
		PriorityQueue<Node> pq = new PriorityQueue<Node>();
		// Initialize priority queue values
		for (Node n : adjList) {
			if(n.equals(src)) {
				n.dist = 0;
				n.parent = null;
			} else {
				n.dist = Integer.MAX_VALUE;
			}
			n.visited = false;
			pq.add(n);
		}
		// TODO Perform Djikstra's
		
		return minPath;
	} // V2V
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Node curNode;
		StringBuilder wGraphStr = new StringBuilder();
		wGraphStr.append("Nodes: " + numNodes + "\n");
		wGraphStr.append("Edges: " + numEdges + "\n");
		for (int i = 0; i < adjList.size(); i++) {
			curNode = adjList.get(i);
			wGraphStr.append(curNode.toString());
			for (Edge e : curNode.edges) {
				wGraphStr.append("->");
				wGraphStr.append(e.weight);
				wGraphStr.append(e.node.toString());
			}
			wGraphStr.append("\n");
		}
		return wGraphStr.toString();
	} // toString
	
} // WGraph
