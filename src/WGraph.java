import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
	private class Node implements Comparable<Node>{
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
	     * @see java.lang.Comparable#compareTo(java.lang.Object)
	     */
	    @Override
		public int compareTo(Node node) {
			return Integer.compare(dist, node.dist);
		} // compareTo
	    
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
	 * Tracks cost of minimum path from most recent call of V2V, V2S, or S2S
	 */
	private int minPathCost;
	
	/**
	 * Constructs a graph which will then be analyzed for
	 * shortest paths of different kinds. The graph is stored
	 * as an adjacency list.
	 * 
	 * @param FName  Name of file containing edge info
	 */
	public WGraph(String FName) {
		adjList = new ArrayList<Node>();
		minPathCost = Integer.MAX_VALUE;
		
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
				} else {
					adjList.add(edgeNode);
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
	 * Implementation of Djikstra's shortest path algorithm
	 * @param ux  Source vertex x-coordinate
	 * @param uy  Source vertex y-coordinate
	 * @param vx  Destination vertex x-coordinate
	 * @param vy  Destination vertex y-coordinate
	 */
	private void CalculateShortestPaths(Node src) {
		Node curNode;
		PriorityQueue<Node> pq = new PriorityQueue<Node>();
		
		// Initialize priority queue values
		for (Node n : adjList) {
			if(n.equals(src)) {
				n.dist = 0;
			} else {
				n.dist = Integer.MAX_VALUE;
			}
			n.parent = null;
			n.visited = false;
			pq.add(n);
		}
		// Perform Djikstra's
		while (!pq.isEmpty()) {
			curNode = pq.poll();
			curNode.visited = true;
			for (Edge e : curNode.edges) {
				if (!e.node.visited) {
					if (e.node.dist > (curNode.dist + e.weight)) {
						pq.remove(e.node);
						e.node.dist = curNode.dist + e.weight;
						e.node.parent = curNode;
						pq.add(e.node);	
					}
				}
			}
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
	public ArrayList<Integer> V2V(int ux, int uy, int vx, int vy) {
		Node src = new Node(ux, uy);
		int destIndex;
		Node curNode;
		ArrayList<Integer> minPath = new ArrayList<Integer>();
		
		// Error check given coordinates
		if (adjList.indexOf(src) < 0) {
			throw new IllegalArgumentException("Given source node with coordinates " + ux + ", " + uy + " not in graph.");
		}
		destIndex = adjList.indexOf(new Node(vx, vy));
		if (destIndex < 0) {
			throw new IllegalArgumentException("Given destination node with coordinates " + vx + ", " + vy + " not in graph.");
		}
		// Check if source and destination are equal
		if (src.equals(new Node(vx, vy))) {
			minPathCost = 0;
			minPath.add(src.x);
			minPath.add(src.y);
			return minPath;
		}
		// Calculate the shortest paths
		CalculateShortestPaths(src);
		// Trace back shortest path from destination
		curNode = adjList.get(destIndex);
		minPathCost = curNode.dist;
		boolean pathToSrc = false;
		do {
			minPath.add(0, curNode.y);
			minPath.add(0, curNode.x);
			curNode = curNode.parent;
			if (curNode != null) {
				pathToSrc = curNode.equals(src);
			}
		} while(curNode != null);
		
		if (!pathToSrc) {
			minPathCost = Integer.MAX_VALUE;
			minPath.clear();
		}
		return minPath;
	} // V2V
	
	/**
	 * Calculates the shortest path between the given source vertex
	 * and set of destination vertices. ArrayList containing the destination
	 * vertices is of the following format:
	 * Even number of integers - for any even i, i-th and i+1-th integers in
	 * the array represent the x-coordinate and y-coordinate of the i/2-th
	 * vertex in the ArrayList.
	 * Only one minimal path is returned. If there are multiple minimal paths
	 * the returned path is picked arbitrarily.
	 * @param ux  Source vertex x-coordinate
	 * @param uy  Source vertex y-coordinate
	 * @param S  Represents a set of destination vertices (Assuming correct formatting)
	 * @return  ArrayList containing even number of integers,
   				for any even i,	i-th and i+1-th integers in the array represent
				the x-coordinate and y-coordinate of the i/2-th vertex
				in the returned path (path is an ordered sequence of vertices)
	 */
	public ArrayList<Integer> V2S(int ux, int uy, ArrayList<Integer> S) {
		Node src = new Node(ux, uy);
		Node curNode;
		ArrayList<Integer> minPath = new ArrayList<Integer>();
		
		// Error check given coordinates
		if (adjList.indexOf(src) < 0) {
			throw new IllegalArgumentException("Given source node with coordinates " + ux + ", " + uy + " not in graph.");
		}
		// Calculate shortest paths
		CalculateShortestPaths(src);
		// Only trace back shortest paths with costs which are less than current minimal cost
		ArrayList<Integer> tmpPath = new ArrayList<Integer>();
		int curDist;
		boolean pathToSrc;
		for (int i = 0; i < S.size(); i+=2) {
			curNode = adjList.get(adjList.indexOf(new Node(S.get(i), S.get(i+1))));
			if (curNode.dist < minPathCost) {
				curDist = curNode.dist;
				pathToSrc = false;
				do {
					tmpPath.add(0, curNode.y);
					tmpPath.add(0, curNode.x);
					pathToSrc = curNode.equals(src);
					curNode = curNode.parent;
				} while(curNode != null);
				
				if (pathToSrc) {
					minPath = new ArrayList<Integer>(tmpPath);
					minPathCost = curDist;
				}
				tmpPath.clear();
			}
		}
		return minPath;
   	} // V2S
	
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
