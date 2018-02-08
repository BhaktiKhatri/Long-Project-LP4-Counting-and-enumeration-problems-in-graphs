/*Class having methods for topological sort */
package cs6301.g45;


import cs6301.g00.Graph;
import cs6301.g00.Graph.Vertex;
import cs6301.g00.Graph.Edge;
import cs6301.g00.GraphAlgorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author Lopamudra
 * 		   Gautam Gunda
 * 		   Bhakti Khatri
 * 	       Sangeeta 				
 *
 */
public class TopologicalOrder extends GraphAlgorithm<TopologicalOrder.TopologicalOrderVertex> {
	
    // Class to store information about a vertex in this algorithm
    static class TopologicalOrderVertex {
		boolean seen;
		Graph.Vertex parent;
		int inDegree; // indegree for a vertex
		int top; 
		TopologicalOrderVertex(Graph.Vertex u) {
		    seen = false;
		    parent = null;
		    inDegree = 0;
		    top = 0;
		}
    }
    
    //Constructor for this class
    /**
     * @param g : Graph
     */
    public TopologicalOrder(Graph g) {
    	super(g);
    	node = new TopologicalOrderVertex[g.size()];
    	// Create array for storing vertex properties
    	for(Graph.Vertex u: g) {
    	    node[u.getName()] = new TopologicalOrderVertex(u);
    	}
    }
    
    /** Algorithm 1. Remove vertices with no incoming edges, one at a
     *  time, along with their incident edges, and add them to a list.
     */
    /**
     * @param g : Graph for which topological order will be calculated.
     * @return List : List<Graph.Vertex> : will have the output for the topological sort
     * Return null if the graph is not a DAG.
     */
    List<Graph.Vertex> toplogicalOrder1(Graph g) {
    	int topNum = 0; // count of the total number vertices we have done the assignment.
    	Queue<Graph.Vertex> queue = new LinkedList<>(); // Queue used to process/store the vertices. 
    	List<Graph.Vertex> topList = new ArrayList<>(); //output list having vertices in topological order.
    	for(Graph.Vertex u: g){
    		getVertex(u).inDegree = u.revAdj.size();
    		if(getVertex(u).inDegree == 0){
    			queue.add(u);
    			//System.out.println("entered queue : "+ u.name);
    		}
    	}
    	while(!queue.isEmpty()){
    		Graph.Vertex u = queue.poll();
    		//System.out.println("exit queue : "+ u.name);
    		getVertex(u).top = ++topNum;
    		topList.add(u);
    		for(Graph.Edge e: u){
    			Graph.Vertex v = e.otherEnd(u);
    			getVertex(v).inDegree--;
    			if(getVertex(v).inDegree == 0)
    				queue.add(v);
    		}
    	}
    	if(topNum != g.size())
    		return null; 
    	else
    		return topList;
    	
    }
    
    /** Algorithm 2. Run DFS on g and add nodes to the front of the output list,
     *  in the order in which they finish.  Try to write code without using global variables.
     */
    /**
     * @param g : Graph for which topological order will be calculated.
     * @return List : List<Graph.Vertex> : will have the output for the topological sort in decreasing finish time.
     * Return null if the graph is not a DAG.
     */
    public List<Graph.Vertex> toplogicalOrder2(Graph g) {
    	Iterator<Graph.Vertex> itr = g.iterator();
    	List<Graph.Vertex> decFinList = DFS(g, itr);
    	return decFinList; //decreasing finishing time list
    }
    
    
    /**
     * Helper function for toplogicalOrder2 algorithm
     * @param g 	: Graph to be processed for calculating topological sort.
     * @param itr 	: Iterator for vertices of the Graph.
     * @return List<Graph.Vertex> : will have the output for the topological sort
     */
    private List<Graph.Vertex> DFS(Graph g, Iterator<Graph.Vertex> itr){
    	int topNum = g.size(); //total number of vertices
    	List<Graph.Vertex> decFinList = new LinkedList<>();
    	reinitialize(g);
    	while(itr.hasNext()){
    		Graph.Vertex v = itr.next();
    		if(!getVertex(v).seen){
    			DFSVisit(v, decFinList, topNum);
    		}
    			
    	}
    	return decFinList;
    	
    	
    }
    
    /**
     * Helper function for DFS algorithm
     * @param v          : Vertex - DFS root vertex
     * @param decFinList : List<Graph.Vertex> - List containing final order of the vertices
     * @param topNum	 : int - count required for the total number vertices processed.
     */
    private void DFSVisit(Graph.Vertex v, List<Graph.Vertex> decFinList, int topNum){
    	TopologicalOrderVertex tVertex = getVertex(v);
    	tVertex.seen = true;
        for(Graph.Edge e: v){
			Graph.Vertex u = e.otherEnd(v);
			if(!getVertex(u).seen){
				tVertex.parent = v;
				DFSVisit(u, decFinList, topNum);
			}
		}
        tVertex.top = topNum--;
        decFinList.add(v);
    }
    
    /**
     * @param u : Graph.Vertex : the vertex for which "seen" will be checked.
     * @return boolean : Return true if vertex is visited during topological sort and vice versa.
     */
    boolean seen(Graph.Vertex u) {
    	return getVertex(u).seen;
    }
    
 // reinitialize the "seen" boolean value.Mark all vertices as not visited.
    /**
     * @param g : Graph
     */
    void reinitialize(Graph g) {
		for(Graph.Vertex u: g) {
		    TopologicalOrderVertex tu = getVertex(u);
		    tu.seen = false;
		}
    }

    
}
