package net.codejava;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Vehicle {

	private static int capacity;
	private int currentCapacity;
	private String driverName;
	private float[] location;
	private Map<String, Integer> priorities;
	private Map<String, Map<String, Integer>> adjacencyList;
	private String[] itinerary;
	
	public Vehicle(int cap, int currentCap, String dName, float[] loc, Map<String, Integer> priors, Map<String, Map<String, Integer>> adjList) {
		this.capacity = cap;
		this.currentCapacity = currentCap;
		this.driverName = dName;
		
	    if (loc.length != 2)
	        throw new IllegalArgumentException("Location argument must be of size 2.");
	    else
	    	this.location = loc;
	    
	    this.priorities = priors;
	    
	    if(!isComplete(adjList))
	    	throw new IllegalArgumentException("Adjacency list must represent a complete graph.");
	    else
	    	this.adjacencyList = adjList;
	}
	/*
	public Vehicle(int cap, int currentCap, String dName) {
		this(cap, currentCap, dName, new float[2], new HashMap<String, Map<String, Integer>>());
	}
	*/
	
	//is complete graph
	private boolean isComplete(Map<String, Map<String, Integer>> graph) {
		
		Set<String> keySet = graph.keySet();
		Iterator<String> it = keySet.iterator();
		
		while (it.hasNext()) {
	        String node1 = it.next(); 
	        Set<String> keySet2 = graph.keySet();
			Iterator<String> it2 = keySet2.iterator();
	        
	        while(it2.hasNext()) {
	        	String node2 = it2.next();
	        	if(!graph.get(node1).containsKey(node2) && !node1.equals(node2))
	        		return false;
	        	//it2.remove(); // avoids a ConcurrentModificationException
	        }   
	        
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
		
		return true;
	}
	
	public boolean setGraph(Map<String, Map<String, Integer>> adjList) {
	    if(adjList.isEmpty() || !isComplete(adjList))
	    	return false;
	    else {
	    	this.adjacencyList = adjList;
	    	return true;
	    }
	}
	
	public Map<String, Map<String, Integer>> getGraph() {
		return this.adjacencyList;
	}
	
	public boolean setItinerary(String[] it) {
	    if(it == null || (it != null && it[0] == null))
	    	return false;
	    else {
	    	this.itinerary = it;
	    	return true;
	    }
	}
	
	public String[] getItinerary() {
		return this.itinerary;
	}
	
	public boolean setPriority(String key, Integer value) {
	    if(key == null || value < 0)
	    	return false;
	    else {
	    	this.priorities.put(key, value);
	    	return true;
	    }
	}
	
	public Map<String, Integer> getPriorities() {
		return this.priorities;
	}
	
	public boolean setLocation(float[] loc) {
		if (loc.length != 2)
	        return false;
	    else {
	    	this.location = loc;
			return true;
	    }
	}
	
	public float[] getLocation() {
		return this.location;
	}
	
	public int getCapacity() {
		return this.capacity;
	}
	
	public int getCurrentCapacity() {
		return this.currentCapacity;
	}
	
	public void addPassengers(int c) {
		this.currentCapacity -= c;
	}
	
	public void removePassengers(int c) {
		this.currentCapacity += c;
	}
	
	public String getDriverName() {
		return this.driverName;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
