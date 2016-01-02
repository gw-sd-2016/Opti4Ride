package net.codejava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Servlet implementation class Servlet
 */
@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<String, double[]> locationKeyMap;
	private Vehicle[] vehicles;        
	private GeoApiContext context;
	private GeocodingResult[] results;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
        // TODO Auto-generated constructor stub
        
        //connect to Google Maps API
        context = new GeoApiContext().setApiKey("AIzaSyCCi0xwG4nVccFDw5vTzkO_402Lg8CyGW4");
        results = null;     
        
        //INITIALIZING driver vehicle data (static until transportation services provides GPS data)
        
        //vGraph 1
        Map<String, Integer> vG1_Claridge = new HashMap<String, Integer>();
        vG1_Claridge.put("2350 H Street NW, Washington, DC 20052, USA", 3);
        vG1_Claridge.put("2135 F Street NW, Washington, DC 20037, USA", 10);
        Map<String, Integer> vG1_Amsterdam = new HashMap<String, Integer>();
        vG1_Amsterdam.put("950 25th Street NW, Washington, DC 20037, USA", 4);
        vG1_Amsterdam.put("2135 F Street NW, Washington, DC 20037, USA", 6);
        Map<String, Integer> vG1_South = new HashMap<String, Integer>();
        vG1_South.put("2350 H Street NW, Washington, DC 20052, USA", 5);
        vG1_South.put("950 25th Street NW, Washington, DC 20037, USA", 11);
        
        HashMap<String, Map<String, Integer>> vGraph1 = new HashMap<String, Map<String, Integer>>();
        vGraph1.put("950 25th Street NW, Washington, DC 20037, USA", vG1_Claridge);
        vGraph1.put("2350 H Street NW, Washington, DC 20052, USA", vG1_Amsterdam);
        vGraph1.put("2135 F Street NW, Washington, DC 20037, USA", vG1_South);
        
        Map<String, Integer> vP1 = new HashMap<String, Integer>();
        vP1.put("950 25th Street NW, Washington, DC 20037, USA", 1);
        vP1.put("2350 H Street NW, Washington, DC 20052, USA", 2);
        vP1.put("2135 F Street NW, Washington, DC 20037, USA", 2);
        
        //vGraph 2
        Map<String, Integer> vG2_2400M = new HashMap<String, Integer>();
        vG2_2400M.put("1957 E Street NW, Washington, DC 20052, USA", 20);
        vG2_2400M.put("1837 M Street NW, Washington, DC 20036, USA", 8);
        Map<String, Integer> vG2_EStreet = new HashMap<String, Integer>();
        vG2_EStreet.put("2400 M Street NW, Washington, DC 20037, USA", 19);
        vG2_EStreet.put("1837 M Street NW, Washington, DC 20036, USA", 18);
        Map<String, Integer> vG2_ChipotleOnM = new HashMap<String, Integer>();
        vG2_ChipotleOnM.put("2400 M Street NW, Washington, DC 20037, USA", 9);
        vG2_ChipotleOnM.put("1957 E Street NW, Washington, DC 20052, USA", 19);
        
        HashMap<String, Map<String, Integer>> vGraph2 = new HashMap<String, Map<String, Integer>>();
        vGraph2.put("2400 M Street NW, Washington, DC 20037, USA", vG2_2400M);
        vGraph2.put("1957 E Street NW, Washington, DC 20052, USA", vG2_EStreet);
        vGraph2.put("1837 M Street NW, Washington, DC 20036, USA", vG2_ChipotleOnM);
        
        Map<String, Integer> vP2 = new HashMap<String, Integer>();
        vP2.put("2400 M Street NW, Washington, DC 20037, USA", 1);
        vP2.put("1957 E Street NW, Washington, DC 20052, USA", 2);
        vP2.put("1837 M Street NW, Washington, DC 20036, USA", 2);
        
        //vGraph 3
        Map<String, Integer> vG3_Claridge = new HashMap<String, Integer>();
        vG3_Claridge.put("1957 E Street NW, Washington, DC 20052, USA", 18);
        vG3_Claridge.put("2135 F Street NW, Washington, DC 20037, USA", 10);
        Map<String, Integer> vG3_EStreet = new HashMap<String, Integer>();
        vG3_EStreet.put("950 25th Street NW, Washington, DC 20037, USA", 19);
        vG3_EStreet.put("2135 F Street NW, Washington, DC 20037, USA", 4);
        Map<String, Integer> vG3_South = new HashMap<String, Integer>();
        vG3_South.put("1957 E Street NW, Washington, DC 20052, USA", 5);
        vG3_South.put("950 25th Street NW, Washington, DC 20037, USA", 11);
        
        HashMap<String, Map<String, Integer>> vGraph3 = new HashMap<String, Map<String, Integer>>();
        vGraph3.put("950 25th Street NW, Washington, DC 20037, USA", vG3_Claridge);
        vGraph3.put("1957 E Street NW, Washington, DC 20052, USA", vG3_EStreet);
        vGraph3.put("2135 F Street NW, Washington, DC 20037, USA", vG3_South);
        
        Map<String, Integer> vP3 = new HashMap<String, Integer>();
        vP3.put("950 25th Street NW, Washington, DC 20037, USA", 1);
        vP3.put("1957 E Street NW, Washington, DC 20052, USA", 2);
        vP3.put("2135 F Street NW, Washington, DC 20037, USA", 2);
        
        //Vehicles
        vehicles = new Vehicle[3];
        vehicles[0] = new Vehicle(6, 4, "Ms. Malone", new float[]{ 38.899354f, -77.048844f }, vP1, vGraph1 );
        vehicles[1] = new Vehicle(5, 4, "Mr. Smith", new float[]{ 38.900197f, -77.050110f }, vP2, vGraph2 );
        vehicles[2] = new Vehicle(6, 2, "Mr. Dillard", new float[]{ 38.897367f, -77.045475f }, vP3, vGraph3 );
        
        String[] itinerary1 = { "2350 H Street NW, Washington, DC 20052, USA", 
        						"2135 F Street NW, Washington, DC 20037, USA", 
        						"950 25th Street NW, Washington, DC 20037, USA" };
        vehicles[0].setItinerary(itinerary1);
        
        String[] itinerary2 = { "1957 E Street NW, Washington, DC 20052, USA", 
        						"1837 M Street NW, Washington, DC 20036, USA", 
        						"2400 M Street NW, Washington, DC 20037, USA", };
        vehicles[1].setItinerary(itinerary2);
        
        String[] itinerary3 = { "2135 F Street NW, Washington, DC 20037, USA", 
        						"950 25th Street NW, Washington, DC 20037, USA", 
        						"1957 E Street NW, Washington, DC 20052, USA" };
        vehicles[2].setItinerary(itinerary3);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //return serialized snapshot of server (vehicles array)
        PrintWriter out = response.getWriter();
		
        ObjectMapper mapper = new ObjectMapper();
        String vAssignment = mapper.writeValueAsString(vehicles);
        out.print(vAssignment);
        
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
		//initialize out steam
        PrintWriter out = response.getWriter();
        //initialize serializer
        ObjectMapper mapper = new ObjectMapper();
        
        //get device type (student/client or driver) trying to connect
        String deviceType = request.getParameterValues("DeviceType")[0];
        
        if(deviceType.equals("Student")) {
        	
        	//determine request student app is making (pickup, cancel, all vehicles in service)
            String requestType = request.getParameterValues("RequestType")[0];
            System.out.println(deviceType + " " + requestType + "request");
            
        	if(requestType.equals("Pickup")) {
        		
        		//parse remaining request values
        		String[] originCoords = request.getParameterValues("Origin")[0].split("\\s+");
        		String[] destinationCoords = request.getParameterValues("Destination")[0].split("\\s+");
        		int passengers = (int) Float.parseFloat(request.getParameterValues("Passengers")[0]);
        		
        		//get vehicle assignment
        		int assignment = pickupRequestHandler(originCoords, destinationCoords, passengers);
        		//int assignment = 0;
        		
        		//if vehicle was assigned, send assignment to client app
        		if(assignment >= 0) {
        			String vAssignment = mapper.writeValueAsString(vehicles[assignment]);
        			out.print(vAssignment); 
        		}
        		//otherwise notify them that all vehicles are full
        		else {
        			out.print("max_capacity");
        		}
        		
        	}
        	else if(requestType.equals("Cancel")) {
        		
        		//parse remaining request parameters
        		String[] cancelResponse = new String[1];
        		if(request.getParameterMap().containsKey("DriverName") &&
        		   request.getParameterMap().containsKey("PassengerCount") &&
        		   request.getParameterMap().containsKey("Origin") &&
        		   request.getParameterMap().containsKey("Destination")
        		   )
        		{
        			String driverName = request.getParameterValues("DriverName")[0];
        			String passengerCount = request.getParameterValues("PassengerCount")[0];
            		String[] originCoords = request.getParameterValues("Origin")[0].split("\\s+");
            		String[] destinationCoords = request.getParameterValues("Destination")[0].split("\\s+");
            		
            		cancelResponse = cancelRequestHandler(originCoords, destinationCoords, driverName, passengerCount);
        		}
        		else 
        		{
        			//if parameters do not conform to cancel protocol, notify client app
        			cancelResponse[0] = "Cancel failed. Invalid request parameters.";
        		}

        		out.print(mapper.writeValueAsString(cancelResponse));
        	}
        	else if(requestType.equals("VehicleLocations")) { 
        		//send all current vehicle locations to client app
        		String vAssignment = mapper.writeValueAsString(vehicles);
        		out.print(vAssignment);  
        	}
        }
        else if (deviceType.equals("Driver")) {
        	//send all current vehicle info to its driver
        	System.out.println(deviceType + " request");
            String vAssignment = mapper.writeValueAsString(vehicles[0]);
            out.print(vAssignment);
        }
       
    }
	
	protected String[] cancelRequestHandler(String[] originCoords, String[] destinationCoords, String driverName, String passengerCount) {
		
		//reverse geocode origin and destination coordinates to address key
		String[] cancelResponse = new String[1];
		GeocodingResult[] results = null;
		try 
		{
			results = GeocodingApi.reverseGeocode(context, new LatLng(Float.parseFloat(originCoords[0]), Float.parseFloat(originCoords[1]))).await();
		} 
		catch (Exception e) { e.printStackTrace(); }
		
		String originAddress = results[0].formattedAddress;
		GeocodingResult[] results2 = null;
		
		try 
		{
			results2 = GeocodingApi.reverseGeocode(context, new LatLng(Float.parseFloat(destinationCoords[0]), Float.parseFloat(destinationCoords[1]))).await();
		} 
		catch (Exception e) { e.printStackTrace(); }
		
		String destinationAddress = results2[0].formattedAddress;
		
		//find vehicle that cancellation request affects
		for(Vehicle v : vehicles) {
			if(v.getDriverName().equals(driverName)) {
				
				//remove origin/destination node of cancelled request from graph (and all related edges)
				Map<String, Map<String, Integer>> graph = v.getGraph();
				for(String s : graph.keySet()) {
					if(!s.equals(originAddress) && !s.equals(destinationAddress)) {
						graph.get(s).remove(originAddress);
						graph.get(s).remove(destinationAddress);
					}
				}
				graph.remove(originAddress);
				graph.remove(destinationAddress);
				
				//remove cancelled request origin/destination from itinerary
				ArrayList<String> it = new ArrayList<String>(Arrays.asList(v.getItinerary()));
				it.remove(originAddress);
				it.remove(destinationAddress);
				v.setItinerary(it.toArray(new String[it.size()]));
				
				//add capacity back to vehicle
				int passengers;
			    try{
			        passengers = (int)Float.parseFloat(passengerCount);
			    }catch(NumberFormatException e){
			    	cancelResponse[0] = "Cancel failed: Passenger count must be an integer.";
			    	break;
			    }
			    
			    //remove cancelled request's priority from vehicle object
			    v.setCurrentCapacity(v.getCurrentCapacity() + passengers);
			    v.removePriority(originAddress);
			    v.removePriority(destinationAddress);
			    
				cancelResponse[0] = "Cancel Complete";	
				break;
			}
		}
		//if request not in system, notify client app
		if(cancelResponse[0] == null) {
			cancelResponse[0] = "Cancel failed: No active request";
		}	
		
		return cancelResponse;	
	}
	
	protected int pickupRequestHandler(String[] originCoords, String[] destinationCoords, int passengers) {
		
		//set default vehicle assignment to none
		int vehicleAssignment = -1;
		
		System.out.println(originCoords[0] + "," + originCoords[1]);
		
		//reverse geocode origin and destination coordinates to address key
		GeocodingResult[] results3 = null;
		try 
		{
			results3 = GeocodingApi.reverseGeocode(context, new LatLng(Float.parseFloat(originCoords[0]), Float.parseFloat(originCoords[1]))).await();
		} 
		catch (Exception e) { e.printStackTrace(); }
		
		String originAddress = results3[0].formattedAddress;
		GeocodingResult[] results4 = null;
		
		try 
		{
			results4 = GeocodingApi.reverseGeocode(context, new LatLng(Float.parseFloat(destinationCoords[0]), Float.parseFloat(destinationCoords[1]))).await();
		} 
		catch (Exception e) { e.printStackTrace(); }
		
		String destinationAddress = results4[0].formattedAddress;
		System.out.println(destinationAddress);
		
		ArrayList<Map<String, Map<String, Integer>>> updatedGraphs = new ArrayList<Map<String, Map<String, Integer>>>();
		
		//for all vehicles, add origin and destination points to graph
		//compute edge weights between all existing nodes to origin/destination node based on weighting factors
		//edge weights represent how difficult it is to travel from any node A to B, relative to other edges
		for(Vehicle v : vehicles) {
			
			//get all current itinerary items
			Map<String, Map<String, Integer>> graph = new HashMap<String, Map<String, Integer>>();
			graph.putAll(v.getGraph());		
			String[] itineraryLocs = graph.keySet().toArray(new String[graph.keySet().size()]);

			//get new itinerary items from request
			String[] newLocs = {originAddress, destinationAddress};
			
			String[] allLocs = graph.keySet().toArray(new String[graph.keySet().size()+newLocs.length]);
			allLocs[allLocs.length-2] = originAddress;
			allLocs[allLocs.length-1] = destinationAddress;
			
			//get priority value of new request
			int newPriority = -1;
			Entry<String, Integer> maxPriority = null;
			for(Entry<String,Integer> entry : v.getPriorities().entrySet()) {
			    if (maxPriority == null || entry.getValue() > maxPriority.getValue())
			    	maxPriority = entry;
			}
			newPriority = maxPriority.getValue() + 1;
			
			//get distances of request's origin/destination points to all other itinerary locations
			DistanceMatrix distancesFromMatrix = null;
			DistanceMatrix distancesToMatrix = null;

    		try 
    		{
    	        distancesFromMatrix = DistanceMatrixApi.getDistanceMatrix(context, newLocs, allLocs).await();
    	        distancesToMatrix = DistanceMatrixApi.getDistanceMatrix(context, itineraryLocs, newLocs).await();
    		} 
    		catch (Exception e) { e.printStackTrace(); }
    		
    		ArrayList<Map<String, Integer>> newNodes = new ArrayList<Map<String, Integer>>();
    		DistanceMatrixRow[] rows;
    		DistanceMatrixElement[] elements;
	
    		//calculate weighting for all new edges (connecting request's origin/destination points to all existing locations in itinerary)
    		//current weighting factors: (1) time to travel between two locations, (2) First in, first out
    		rows = distancesFromMatrix.rows;
    		for(int i=0; i<rows.length; i++) {	
    			newNodes.add(new HashMap<String, Integer>());
    			elements = rows[i].elements;
    			for(int j=0; j<elements.length; j++) {
    				if(!newLocs[i].equals(allLocs[j])) {
    					if(v.getPriorities().get(allLocs[j]) == null)
    						newNodes.get(i).put(allLocs[j], (int)(0.25*(elements[j].distance.inMeters/1000) + 0.75*(10-newPriority)));
    					else
    						newNodes.get(i).put(allLocs[j], (int)(0.25*(elements[j].distance.inMeters/1000) + 0.75*(10-v.getPriorities().get(allLocs[j]))));
    				}
    			}
    			graph.put(newLocs[i], newNodes.get(i));
    		}
    		
    		rows = distancesToMatrix.rows;
    		for(int i=0; i<rows.length; i++) {	
    			newNodes.add(new HashMap<String, Integer>());
    			elements = rows[i].elements;
    			for(int j=0; j<elements.length; j++)	
    				graph.get(itineraryLocs[i]).put(newLocs[j], (int)(0.25*(elements[j].distance.inMeters/1000) + 0.75*(10-newPriority)));

    		}
    		
    		updatedGraphs.add(graph);	
		}
		
		//for each vehicle, compute optimal tour with request locations added to the graph
		
		int minTourDifference = Integer.MAX_VALUE;
		int maxTour = 5;
		Map<String, Map<String, String>> itinerary = null;
		
		for(int i=0; i<updatedGraphs.size(); i++) {
			
			Map<String, Map<String, String>> it = new HashMap<String, Map<String, String>>();
			String[] itineraryLocs = updatedGraphs.get(i).keySet().toArray(new String[updatedGraphs.get(i).keySet().size()]);	
			
			for(int j=0; j<updatedGraphs.get(i).size(); j++)
				it.put(itineraryLocs[j], new HashMap<String, String>());
			
    		String[] itLocs = updatedGraphs.get(i).keySet().toArray(new String[updatedGraphs.get(i).keySet().size()]);
    		
    		/*
    		System.out.println("New Graph:");
    		for(String s : itLocs) {
    			System.out.println(s);
    			Map<String, Integer> it2 = updatedGraphs.get(i).get(s);
    			String[] it2Locs = it2.keySet().toArray(new String[it2.keySet().size()]);
    			for(String s2 : it2Locs) {
    				System.out.println("* " + s2 + ": " + updatedGraphs.get(i).get(s).get(s2));
    			}
    		}
    		*/
    		
    		//the vehicle with the least change in optimal tour length will service the new request
    		int newTour;
    		int currentTour;
    		if(((newTour = optimalTour(updatedGraphs.get(i).size(), updatedGraphs.get(i), it)) - 
    			(currentTour = optimalTour(vehicles[i].getGraph().size(), vehicles[i].getGraph(), it))) < minTourDifference) 
    		{
    			if(vehicles[i].getCurrentCapacity() > 0 && (vehicles[i].getCapacity()-vehicles[i].getCurrentCapacity() >= passengers)) {
    				System.out.println("Assigned Vehicle: " + vehicles[i].getDriverName());
    				System.out.println("Original capacity: 4");
	    			System.out.println("New capacity: " + (vehicles[i].getCapacity()-vehicles[i].getCurrentCapacity()));
	    			minTourDifference = newTour - currentTour;
	    			itinerary = it;
	    			vehicleAssignment = i;
	    			vehicles[i].addPassengers(passengers);
	    			System.out.println("Change in min tour = " + minTourDifference);
	    			System.out.println("Saved " + maxTour + "!");
	    			System.out.println("New Itinerary:");
	    			vehicles[i].setItinerary(printOptimalTour(itinerary));
	    			
	    			//set node's priority for vehicle
	    			int newPriority = -1;
	    			Entry<String, Integer> maxPriority = null;
	    			for(Entry<String,Integer> entry : vehicles[i].getPriorities().entrySet()) {
	    			    if (maxPriority == null || entry.getValue() > maxPriority.getValue())
	    			    	maxPriority = entry;
	    			}
	    			newPriority = maxPriority.getValue() + 1;
	    			System.out.println("Priority: " + newPriority);
	    			vehicles[i].setPriority(originAddress, newPriority);
	    			vehicles[i].setPriority(destinationAddress, newPriority);
	    			break;
    			}
    			else {
    				return -1;
    			}
    		}
    		if(maxTour < newTour) {
    			//maxTour = newTour;
    		}
		}
		
		return vehicleAssignment;

	}
	
	protected int optimalTour(int n, Map<String, Map<String, Integer>> weights, Map<String, Map<String, String>> paths) {
		
		//check for all nodes in WEIGHTS, there is only ONE key value pair
		//check for all nodes in PATH, there is 2^n key value pairs
	
		ArrayList<String> nodesList = new ArrayList<String>(weights.keySet()); 
		Collections.sort(nodesList);
		String[] nodes = nodesList.toArray(new String[weights.keySet().size()]);
		
		//vMinusV1AsList initialized as subsets V - {v(1)} as String[]
		//vMinusV1AsString initialized as subsets V - {v(1)} as string	
		
		ArrayList<String[]> vMinusV1AsList = new ArrayList<String[]>();
		Map<String, Integer> vMinusV1AsString = new HashMap<String, Integer>();
		Map<String, Map<String, Integer>> D = new HashMap<String, Map<String, Integer>>();
		
		for(int i=(int)(Math.pow(2, nodes.length)-1); i>=0; i--) {
			int binaryNum = i;
			ArrayList<String> subset = new ArrayList<String>();
			int counter = 0;
			while(binaryNum > 0 || counter < nodes.length) {
				if(binaryNum % 2 == 0)
					subset.add(nodes[(nodes.length-1)-counter]);
				binaryNum = binaryNum >> 1;
				counter++;
			}
			if(!(subset.contains(nodes[0]) && subset.size() == 1)) {
				Collections.sort(subset);
				vMinusV1AsList.add(subset.toArray(new String[subset.size()]));
				vMinusV1AsString.put(Arrays.toString(subset.toArray(new String[subset.size()])), -1);
			}
		}
		
		//D initialized with v(1)-v(n) and subsets V - {v(1)}	
		for(int i=1; i<n; i++) 
		{		
			Map<String, Integer> vSet = new HashMap<String, Integer>();
			vSet.putAll(vMinusV1AsString);
			D.put(nodes[i], vSet);
		}	
		
		//D[i][emptySet] = W[i][0]
		for(int i=1; i<n; i++) 			
			D.get(nodes[i]).put(Arrays.toString(new String[0]), weights.get(nodes[i]).get(nodes[0]));
		
		//populate D with optimal tours of all subsets
		for(int k=1; k<=(n-2); k++) 
		{
			for( String[] A : vMinusV1AsList ) 
			{
				ArrayList<String> aAsList = new ArrayList<String>(Arrays.asList(A));
				if(A.length == k && !aAsList.contains(nodes[0])) 
				{	
					for(int i=0; i<nodes.length;i++) 
					{
						//can just get rid of i=0 in loop...
						if(i != 0 && !aAsList.contains(nodes[i])) 
						{
							int dVal = Integer.MAX_VALUE;
							String dValIndex = "";
							for(int j=0; j<nodes.length;j++) 
							{
								if(aAsList.contains(nodes[j]))
								{
									aAsList.remove(nodes[j]);
									String[] aMinusJ = aAsList.toArray(new String[aAsList.size()]);
								
									int tempVal;
									if((tempVal = weights.get(nodes[i]).get(nodes[j]) + D.get(nodes[j]).get(Arrays.toString(aMinusJ))) <= dVal)
									{
										dVal = tempVal;
										dValIndex = nodes[j];
									}
									aAsList.add(nodes[j]);
									Collections.sort(aAsList);
								}
							}
							D.get(nodes[i]).put(Arrays.toString(A), dVal);
							paths.get(nodes[i]).put(Arrays.toString(A), dValIndex);
						}
					}
				}
			}
		}
		
		//calculate full optimal tour from D
		int dVal = Integer.MAX_VALUE;
		String dValIndex = "";
		ArrayList<String> nodesAsList = new ArrayList<String>(Arrays.asList(nodes));
		nodesAsList.remove(nodes[0]);
		for(int j=1; j<nodes.length;j++) 
		{
			nodesAsList.remove(nodes[j]);
			String[] nodesMinus1J = (String[])nodesAsList.toArray(new String[nodes.length-2]);

			
			int tempVal;
			if((tempVal = (weights.get(nodes[0]).get(nodes[j]) + D.get(nodes[j]).get(Arrays.toString(nodesMinus1J)))) < dVal)
			{
				dVal = tempVal;
				dValIndex = nodes[j];
			}
			nodesAsList.add(nodes[j]);
			Collections.sort(nodesAsList);
		}
		nodesList.remove(nodes[0]);
		String[] nodesMinusV1 = nodesList.toArray(new String[nodesList.size()]);
		D.put(nodes[0], new HashMap<String, Integer>());
		D.get(nodes[0]).put(Arrays.toString(nodesMinusV1), dVal);
		
		paths.get(nodes[0]).put(Arrays.toString(nodesMinusV1), dValIndex);
		System.out.println("P[" + nodes[0] + "][" + Arrays.toString(nodesMinusV1) +"] = " + dValIndex);
		
		//return min tour
		return D.get(nodes[0]).get(Arrays.toString(nodesMinusV1));
		
	}

	protected String[] printOptimalTour(Map<String, Map<String, String>> paths) {
	
		//print optimal tour from P
		ArrayList<String> nodesList = new ArrayList<String>(paths.keySet()); 
		ArrayList<String> itinerary = new ArrayList<String>();
		Collections.sort(nodesList);
		
		String currentNode = nodesList.get(0);
		String v1 = currentNode;
		itinerary.add(v1);
		System.out.println(v1);
		nodesList.remove(currentNode);
		while(nodesList.isEmpty() != true) {
			String nextNode = paths.get(currentNode).get(Arrays.toString(nodesList.toArray(new String[nodesList.size()])));
			itinerary.add(nextNode);
			System.out.println(nextNode);
			nodesList.remove(nextNode);
			Collections.sort(nodesList);
			currentNode = nextNode;
		}
		itinerary.add(v1);
		System.out.println(v1);
		
		//return itinerary
		return itinerary.toArray(new String[itinerary.size()]);
	}
	

}
