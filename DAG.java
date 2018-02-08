package cs6301.g45;

import java.util.List;
import cs6301.g00.Graph;
import cs6301.g00.Graph.Vertex;
import cs6301.g00.Graph.Edge;
import cs6301.g00.GraphAlgorithm;
import cs6301.g45.TopologicalOrder;

public class DAG extends GraphAlgorithm<DAG.DAGVertex>{
	
		// Class to store information about a vertex in this algorithm
		static class DAGVertex {

			public boolean seen;			//To check if the vertex is seen or not
			public Graph.Vertex parent;		//To keep track of parent of given vertex
			public int inDegree;			//To keep track of in-degree of each vertex
			public int distance;			//To keep track of distance of each vertex
			public int path;
			
			public DAGVertex(Graph.Vertex u) {
				seen = false;
			    parent = null;
			    inDegree = 0;
			    distance = 0;
			    path = 0;
			}
		} 
		
		public DAG(Graph g) {
			super(g);
			node = new DAGVertex[g.size()];
				
		   	for(Graph.Vertex u: g) {
		   	   node[u.getName()] = new DAGVertex(u);
		   	}
		}
	
	public static final int INFINITE = Integer.MAX_VALUE;
	public long totalPaths = 0;						//To keep total shortest paths value
	public Graph.Vertex source;						//To keep source vertex
	public List<Graph.Vertex> topOrder = null;		//To keep a list of topological orders
	public static long countTopOrder = 0;			//To count topological orders
	public boolean printFlag = false;				//Flag used for printing
	
	/**
	 * Method to set the distance property of graph 
	 * @param g: Graph - Given graph g
	 * @param s: Vertex - source vertex
	 * @return g: Graph with updated vertex distance 
	 */
    public Graph setDAG(Graph g, Vertex s) {

    	for (Graph.Vertex v : g.getVertexArray()) {
			getVertex(v).distance = INFINITE;
			getVertex(v).parent = null;
		}
		getVertex(s).distance = 0;

		for (Graph.Vertex u : g) {
			for (Edge e : u.adj) {
				Graph.Vertex v = e.otherEnd(u);
				if (getVertex(v).distance > e.getWeight() + getVertex(u).distance && getVertex(u).distance != INFINITE) {
					getVertex(v).distance = e.getWeight() + getVertex(u).distance;
					getVertex(v).parent = u;
				}
			}
		}
		return g;
	}
	
    /**
     * Method to find a subgraph of Graph g that contains only the tight edges
     * @param g: Graph - Given graph g
     * @param s: Vertex - Source Vertex
     * @return h: Graph - Subgraph h of g that has tight edges
     */
	public Graph getTightEdgeSubGraph(Graph g, Vertex s) {
		getVertex(s).distance = 0;
		Graph h = new Graph(g.size());
		h.setDirected(true);
		
		for(Graph.Vertex v: h) {
			v.adj.clear();
			v.revAdj.clear();
		}
		
		for (Vertex u : g) {
			if (getVertex(u).distance != Integer.MAX_VALUE) {
				for (Edge e : u.adj) {
					Vertex v = e.otherEnd(u);
					if (getVertex(v).distance == getVertex(u).distance + e.getWeight()) {		//check tight edges
						getVertex(v).parent = u;
						h.addEdge(h.getVertex(u.getName()+1), h.getVertex(v.getName()+1), e.getWeight());
					}
				}
			}
		}
		return h;
	}

	/**
	 * Method to count the number of shortest paths in Graph g from source to target vertex
	 * @param g: Graph - Given graph g
	 * @param source: Vertex - Source vertex 
	 * @param target: Vertex - Target vertex
	 * @return totalPaths: long - Returns the number of shortest paths from source to target vertex 
	 */
	public long countNumberofShortestPaths(Graph g, Graph.Vertex source, Graph.Vertex target) {
		this.source = source;
		Graph g1 = setDAG(g, source);
		Graph h = getTightEdgeSubGraph(g1, source);
		
		TopologicalOrder top = new TopologicalOrder(h);
		topOrder = top.toplogicalOrder1(h);

		for(Graph.Vertex u: topOrder) {
			if(u.equals(source)) {
				getVertex(u).path = 1;
			}
			
			for(Graph.Edge e: u.adj) {
				Graph.Vertex n = e.otherEnd(u);
				getVertex(n).path = getVertex(u).path + getVertex(n).path;
				totalPaths = getVertex(n).path;
			}
		}
		return totalPaths;
	}
	
	/**
	 * Method to enumerate all the shortest paths in Graph g from source to target vertex
	 * @param g: Graph - Given graph g
	 * @param source: Vertex - Source vertex 
	 * @param target: Vertex - Target vertex
	 * @return totalPaths: long - Returns the number of shortest paths and prints the shortest paths from source to target vertex 
	 */
	 public long enumerateShortestPaths(Graph g, Graph.Vertex source, Graph.Vertex target) {
		totalPaths = countNumberofShortestPaths(g, source, target);
		
		this.source = source;
		Graph g1 = setDAG(g, source);
		Graph h = getTightEdgeSubGraph(g1, source);
		
		TopologicalOrder top = new TopologicalOrder(h);
		topOrder = top.toplogicalOrder1(h);
		
		TopologicalOrder t = new TopologicalOrder(h);
		topOrder = t.toplogicalOrder1(h);

		for(Graph.Vertex u: topOrder) {
			System.out.println(u.adj);
			if(u.equals(source)) {
				enumerateAllPaths(u, target);
			}
		}
		return totalPaths;
	 }
	
	 /**
	  * Helper method to print the shortest paths from source to target vertex
	  * @param u: Vertex - Source vertex
	  * @param t: Vertex - Target vertex
	  */
	public void enumerateAllPaths(Graph.Vertex u, Graph.Vertex t) {
		if(u.equals(t)) {
			if(printFlag == true)
				System.out.println();
		}
		
		for(Graph.Edge e: u.adj) {
			if(printFlag == true)
				if(u.equals(source))
					System.out.print(u+" ");
			

			Graph.Vertex n = e.otherEnd(u);
			
			if(printFlag == true) {
				System.out.print(n+" ");
			}
			enumerateAllPaths(n, t);
		}
	}
	
	/**
	 * Method to set the in-degree of each vertex
	 * @param g: Graph - Given graph 
	 */
	public void setInDegree(Graph g) {
		for(Graph.Vertex u: g) {
    		getVertex(u).inDegree = u.revAdj.size();
		}
	}

	/**
	 * Method to count and print all the topological orders of graph g
	 * @param g: Graph - Given graph 
	 * @param topList: List<Graph.Vertex> - List to add vertices in topological order
	 * @return countTopOrder: long - Returns the number of topological orders of graph and prints the order
	 */
	public long permuteAllToplogicalOrders(Graph g, List<Graph.Vertex> topOrderList) {
		
		for(Graph.Vertex u: g.getVertexArray()) {
			if(getVertex(u).inDegree == 0 && getVertex(u).seen == false) {
				for (Graph.Edge e : u.adj) {
					Graph.Vertex v = e.toVertex();
					getVertex(v).inDegree--;
				}
				getVertex(u).seen = true;
				topOrderList.add(u);
				permuteAllToplogicalOrders(g,topOrderList);
				topOrderList.remove(u);
				getVertex(u).seen = false;
				for (Graph.Edge e : u.adj) {
					Graph.Vertex v = e.toVertex();
					getVertex(v).inDegree++;
				}
			}
		}
		
		if(topOrderList.size() == g.size()) {
			if(printFlag == true) {
				printTopologicalOrders(topOrderList,g);
			} else {
				countTopOrder++;
			}
		}
		return countTopOrder;
	}

	/**
	 * Helper method to print all the topological orders of graph
	 * @param topOrderList: List<Graph.Vertex> - List containing vertices in topological order
	 * @param g: Graph - Given graph
	 */
	public static void printTopologicalOrders(List<Graph.Vertex> topOrderList, Graph g) {
		if(topOrderList.size() == g.size()) {
			for (Vertex v : topOrderList) {
				System.out.print(v+" ");
			}
			System.out.println();
		}
		countTopOrder++;
	}
	
}