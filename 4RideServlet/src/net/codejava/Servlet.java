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
	private static final int requestMax = 1000;
	private Map<String, String[]> geocodeCache;
	private Map<String[], String> reverseGeocodeCache;
	private Vehicle[] vehicles;  
	ArrayList<String> vehicleLocs;
	private GeoApiContext context;
	private GeocodingResult[] results;
	private HashMap<String, String[]> requestCache;
	private int requestID;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
        
        //request handling
        requestID = 0;
        requestCache = new HashMap<String, String[]>();
        
        //connect to Google Maps API
        context = new GeoApiContext().setApiKey("AIzaSyCCi0xwG4nVccFDw5vTzkO_402Lg8CyGW4");
        results = null;    
        
        //INITIALIZING geocode caches
        geocodeCache = new HashMap<String, String[]>();
        reverseGeocodeCache = new HashMap<String[], String>();
      
        geocodeCache.put("2350 H St NW, Washington, DC 20052, USA", new String[]{"38.899328", "-77.051051"});
        geocodeCache.put("2135 F St NW, Washington, DC 20037, USA", new String[]{"38.897805", "-77.047896"});
        geocodeCache.put("950 25th St NW, Washington, DC 20037, USA", new String[]{"38.902133", "-77.053464"});
        geocodeCache.put("1957 E St NW, Washington, DC 20052, USA", new String[]{"38.896110", "-77.043707"});
        geocodeCache.put("1203 19th St NW, Washington, DC 20036, USA", new String[]{"38.905817", "-77.043129"});
        geocodeCache.put("2400 M St NW, Washington, DC 20037, USA", new String[]{"38.904673", "-77.052028"});
        geocodeCache.put("912 New Hampshire Ave NW, Washington, DC 20037, USA", new String[]{"38.901128", "-77.051727"});
        geocodeCache.put("2008 G St NW, Washington, DC 20052, USA", new String[]{"38.898068", "-77.045432"});
        geocodeCache.put("University Yard, Washington, DC 20052, USA", new String[]{"38.899018", "-77.045959"});
        
        reverseGeocodeCache.put(new String[]{"38.899328", "-77.051051"}, "2350 H St NW, Washington, DC 20052, USA");
        reverseGeocodeCache.put(new String[]{"38.897805", "-77.047896"}, "2135 F St NW, Washington, DC 20037, USA");
        reverseGeocodeCache.put(new String[]{"38.902133", "-77.053464"}, "950 25th St NW, Washington, DC 20037, USA");
        reverseGeocodeCache.put(new String[]{"38.896110", "-77.043707"}, "1957 E St NW, Washington, DC 20052, USA");
        reverseGeocodeCache.put(new String[]{"38.905817", "-77.043129"}, "1203 19th St NW, Washington, DC 20036, USA");
        reverseGeocodeCache.put(new String[]{"38.904673", "-77.052028"}, "2400 M St NW, Washington, DC 20037, USA"); 
        reverseGeocodeCache.put(new String[]{"38.901128", "-77.051727"}, "912 New Hampshire Ave NW, Washington, DC 20037, USA");
        reverseGeocodeCache.put(new String[]{"38.898068", "-77.045432"}, "2008 G St NW, Washington, DC 20052, USA");
        reverseGeocodeCache.put(new String[]{"38.899018", "-77.045959"}, "University Yard, Washington, DC 20052, USA");
        
        //INITIALIZING driver vehicle data (static until transportation services provides GPS data)
        
        //vGraph 1
        Map<String, Integer> vG1_Claridge = new HashMap<String, Integer>();
        vG1_Claridge.put("2350 H St NW, Washington, DC 20052, USA", 3);
        vG1_Claridge.put("2135 F St NW, Washington, DC 20037, USA", 10);
        Map<String, Integer> vG1_Amsterdam = new HashMap<String, Integer>();
        vG1_Amsterdam.put("950 25th St NW, Washington, DC 20037, USA", 4);
        vG1_Amsterdam.put("2135 F St NW, Washington, DC 20037, USA", 6);
        Map<String, Integer> vG1_South = new HashMap<String, Integer>();
        vG1_South.put("2350 H St NW, Washington, DC 20052, USA", 5);
        vG1_South.put("950 25th St NW, Washington, DC 20037, USA", 11);
        
        HashMap<String, Map<String, Integer>> vGraph1 = new HashMap<String, Map<String, Integer>>();
        vGraph1.put("950 25th St NW, Washington, DC 20037, USA", vG1_Claridge);
        vGraph1.put("2350 H St NW, Washington, DC 20052, USA", vG1_Amsterdam);
        vGraph1.put("2135 F St NW, Washington, DC 20037, USA", vG1_South);
        
        Map<String, Integer> vP1 = new HashMap<String, Integer>();
        vP1.put("950 25th St NW, Washington, DC 20037, USA", 1);
        vP1.put("2350 H St NW, Washington, DC 20052, USA", 2);
        vP1.put("2135 F St NW, Washington, DC 20037, USA", 2);
        
        //vGraph 2
        Map<String, Integer> vG2_2400M = new HashMap<String, Integer>();
        vG2_2400M.put("1957 E St NW, Washington, DC 20052, USA", 20);
        vG2_2400M.put("1203 19th St NW, Washington, DC 20036, USA", 8);
        Map<String, Integer> vG2_EStreet = new HashMap<String, Integer>();
        vG2_EStreet.put("2400 M St NW, Washington, DC 20037, USA", 19);
        vG2_EStreet.put("1203 19th St NW, Washington, DC 20036, USA", 18);
        Map<String, Integer> vG2_ChipotleOnM = new HashMap<String, Integer>();
        vG2_ChipotleOnM.put("2400 M St NW, Washington, DC 20037, USA", 9);
        vG2_ChipotleOnM.put("1957 E St NW, Washington, DC 20052, USA", 19);
        
        HashMap<String, Map<String, Integer>> vGraph2 = new HashMap<String, Map<String, Integer>>();
        vGraph2.put("2400 M St NW, Washington, DC 20037, USA", vG2_2400M);
        vGraph2.put("1957 E St NW, Washington, DC 20052, USA", vG2_EStreet);
        vGraph2.put("1203 19th St NW, Washington, DC 20036, USA", vG2_ChipotleOnM);
        
        Map<String, Integer> vP2 = new HashMap<String, Integer>();
        vP2.put("2400 M St NW, Washington, DC 20037, USA", 1);
        vP2.put("1957 E St NW, Washington, DC 20052, USA", 2);
        vP2.put("1203 19th St NW, Washington, DC 20036, USA", 2);
        
        //vGraph 3
        Map<String, Integer> vG3_Claridge = new HashMap<String, Integer>();
        vG3_Claridge.put("1957 E St NW, Washington, DC 20052, USA", 18);
        vG3_Claridge.put("2135 F St NW, Washington, DC 20037, USA", 10);
        Map<String, Integer> vG3_EStreet = new HashMap<String, Integer>();
        vG3_EStreet.put("950 25th St NW, Washington, DC 20037, USA", 19);
        vG3_EStreet.put("2135 F St NW, Washington, DC 20037, USA", 4);
        Map<String, Integer> vG3_South = new HashMap<String, Integer>();
        vG3_South.put("1957 E St NW, Washington, DC 20052, USA", 5);
        vG3_South.put("950 25th St NW, Washington, DC 20037, USA", 11);
        
        HashMap<String, Map<String, Integer>> vGraph3 = new HashMap<String, Map<String, Integer>>();
        vGraph3.put("1957 E St NW, Washington, DC 20052, USA", vG3_EStreet);
        vGraph3.put("950 25th St NW, Washington, DC 20037, USA", vG3_Claridge);
        vGraph3.put("2135 F St NW, Washington, DC 20037, USA", vG3_South);
        
        Map<String, Integer> vP3 = new HashMap<String, Integer>();
        vP3.put("950 25th St NW, Washington, DC 20037, USA", 1);
        vP3.put("1957 E St NW, Washington, DC 20052, USA", 2);
        vP3.put("2135 F St NW, Washington, DC 20037, USA", 2);
        
        //Vehicles
        vehicles = new Vehicle[3];
        vehicles[0] = new Vehicle(6, 4, "Ms. Malone", new float[]{ 38.899354f, -77.048844f }, vP1, vGraph1 );
        vehicles[1] = new Vehicle(5, 4, "Mr. Smith", new float[]{ 38.900197f, -77.050110f }, vP2, vGraph2 );
        vehicles[2] = new Vehicle(6, 2, "Mr. Dillard", new float[]{ 38.897367f, -77.045475f }, vP3, vGraph3 );
        
        //set vehicle itineraries
        String[] itinerary1 = { "2350 H St NW, Washington, DC 20052, USA", 
        						"2135 F St NW, Washington, DC 20037, USA", 
        						"950 25th St NW, Washington, DC 20037, USA" };
        vehicles[0].setItinerary(itinerary1);
        
        String[] itinerary2 = { "1957 E St NW, Washington, DC 20052, USA", 
        						"1203 19th St NW, Washington, DC 20036, USA", 
        						"2400 M St NW, Washington, DC 20037, USA", };
        vehicles[1].setItinerary(itinerary2);
        
        String[] itinerary3 = { "2135 F St NW, Washington, DC 20037, USA", 
        						"950 25th St NW, Washington, DC 20037, USA", 
        						"1957 E St NW, Washington, DC 20052, USA" };
        vehicles[2].setItinerary(itinerary3);
        
        //set vehicle request data
        setRequestData("2350 H St NW, Washington, DC 20052, USA", "213 F St NW, Washington, DC 20037, USA", vehicles[0].getDriverName(), Integer.toString(1));
        setRequestData("2135 F St NW, Washington, DC 20037, USA", "950 25th St NW, Washington, DC 20037, USA", vehicles[0].getDriverName(), Integer.toString(1));
        
        setRequestData("1957 E St NW, Washington, DC 20052, USA", "1203 19th St NW, Washington, DC 20036, USA", vehicles[1].getDriverName(), Integer.toString(1));
        setRequestData("123 19th St NW, Washington, DC 20036, USA", "2400 M St NW, Washington, DC 20037, USA", vehicles[1].getDriverName(), Integer.toString(1));
        
        setRequestData("2135 F St NW, Washington, DC 20037, USA", "950 25th St NW, Washington, DC 20037, USA", vehicles[2].getDriverName(), Integer.toString(1));
        setRequestData("9500 25th St NW, Washington, DC 20037, USA", "1957 E St NW, Washington, DC 20052, USA", vehicles[2].getDriverName(), Integer.toString(1));
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected String setRequestData(String origin, String destination, String driver, String passengerCount) {
		if(origin != null && destination != null) {
			requestID = (requestID++)%requestMax;
			this.requestCache.put(Integer.toString(requestID), new String[]{origin, destination, driver, passengerCount});
			return Integer.toString(requestID);
		}
		else 
			return null;		
	}

	protected boolean changeRequestData(String requestID, String newLoc, String type) {
		if(requestID != null && newLoc != null && requestCache.containsKey(requestID)) {
			if(type.equals("origin")) {
				String driver = requestCache.get(requestID)[2];
				String oldDest = requestCache.get(requestID)[1];
				requestCache.remove(requestID);
				requestCache.put(requestID, new String[]{newLoc, oldDest, driver});
				return true;
			}
			else if(type.equals("destination")) {
				String driver = requestCache.get(requestID)[2];
				String oldOrig = requestCache.get(requestID)[0];
				requestCache.remove(requestID);
				requestCache.put(requestID, new String[]{oldOrig, newLoc, driver});
				return true;
			}
			else
				return false;
		}
		else 
			return false;		
	}
    
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
        	
        	//determine request student app is making
            String requestType = request.getParameterValues("RequestType")[0];
            System.out.println(deviceType + " " + requestType + "request");
            
        	if(requestType.equals("Pickup")) { 		
        		//parse remaining request values
        		if(request.getParameterMap().containsKey("Origin") &&
        		   request.getParameterMap().containsKey("Destination") &&
        		   request.getParameterMap().containsKey("Passengers")
        		   )
        		{
	        		String[] originCoords = request.getParameterValues("Origin")[0].split("\\s+");
	        		String[] destinationCoords = request.getParameterValues("Destination")[0].split("\\s+");
	        		int passengers;
	        		
	        		try {
		        		passengers = (int) Float.parseFloat(request.getParameterValues("Passengers")[0]);	
	        		}
	        		catch(NumberFormatException e) {
	        			out.print("Invalid passenger count");
	        			return;
	        		}
	        		
	        		//get vehicle assignment
	        		String[] handlerReturn = pickupRequestHandler(originCoords, destinationCoords, passengers);
	        		if(handlerReturn != null) {
		        		String id = handlerReturn[0];
		        		int assignment = Integer.parseInt(handlerReturn[1]);
		        		
		        		//if vehicle was assigned, send assignment to client app
		        		if(assignment >= 0) {
		        			//String vAssignment = mapper.writeValueAsString(vehicles[assignment]);
		        			String pickupResponse = mapper.writeValueAsString(new Object[]{id, vehicles[assignment].getLocation(), vehicles[assignment].getDriverName()});
		        			out.print(pickupResponse); 
		        		}
		        		//otherwise notify them that all vehicles are full
		        		else {
		        			out.print("max_capacity");
		        		}
	        		}
	        		else
	        			out.print("pickup_request_failed");
        		}
        		
        	}
        	if(requestType.equals("VehicleHasArrived")) {
        		//parse remaining request parameters
        		String[] hasArrivedResponse = new String[1];
        		if(request.getParameterMap().containsKey("RequestID"))
        		{
        			String requestID = request.getParameterValues("RequestID")[0];
            		String[] requestInfo = requestCache.get(requestID);
            		
            		if(requestInfo != null)	
            			hasArrivedResponse = VehicleHasArrivedRequestHandler(requestInfo[0], requestInfo[2]);
            		else
            			hasArrivedResponse[0] = "'Has Arrived' request failed. Student 4-RIDE request not found.";	
        		}
        		else 
        		{
        			//if parameters do not conform to cancel protocol, notify client app
        			hasArrivedResponse[0] = "'Has Arrived' request failed. Invalid request parameters.";
        		}

        		out.print(mapper.writeValueAsString(hasArrivedResponse));
        	}
        	else if(requestType.equals("Cancel")) {
        		
        		//parse remaining request parameters
        		String[] cancelResponse = new String[1];
        		if(request.getParameterMap().containsKey("RequestID"))
        		{
        			String id = request.getParameterValues("RequestID")[0];
        			String[] requestInfo = requestCache.get(id);
            		
            		if(requestInfo != null) {
                		String origin =  requestInfo[0];
                		String destination =  requestInfo[1];
            			String driverName = requestInfo[2];
            			String passengerCount =  requestInfo[3];
                		cancelResponse = cancelRequestHandler(origin, destination, driverName, passengerCount);
                		
                		requestCache.remove(id);
            		}
            		else
            			cancelResponse[0] = "Cancel failed. Student 4-RIDE request not found.";
        		}
        		else 
        		{
        			//if parameters do not conform to cancel protocol, notify client app
        			cancelResponse[0] = "Cancel failed. Invalid request parameters.";
        		}

        		out.print(mapper.writeValueAsString(cancelResponse));
        	}
        	else if(requestType.equals("EarlyExit")) {
        		
        		//parse remaining request parameters
        		String[] earlyExitResponse = new String[1];
        		if(request.getParameterMap().containsKey("RequestID"))
        		{
        			String id = request.getParameterValues("RequestID")[0];
        			String[] requestInfo = requestCache.get(id);
        			if(requestInfo != null) {
                		String destination =  requestInfo[1];
            			String driverName = requestInfo[2];
            			String passengerCount =  requestInfo[3];
            			earlyExitResponse = requestCompletionHandler(destination, driverName, passengerCount);
            			
            			requestCache.remove(id);
            		}
            		else
            			earlyExitResponse[0] = "Early Exit failed. Student 4-RIDE request not found.";
        		}
        		else 
        		{
        			//if parameters do not conform to cancel protocol, notify client app
        			earlyExitResponse[0] = "Early Exit failed. Invalid request parameters.";
        		}

        		out.print(mapper.writeValueAsString(earlyExitResponse));
        	}
        	else if(requestType.equals("DestinationChange")) {
        		
        		//parse remaining request parameters
        		String[] destinationChangeResponse = new String[1];
        		if(request.getParameterMap().containsKey("RequestID") &&
        		   request.getParameterMap().containsKey("NewDestination"))
        		{
        			String id = request.getParameterValues("RequestID")[0];
        			String[] requestInfo = requestCache.get(id);
        			if(requestInfo != null) {
                		String oldDestination =  requestInfo[1];
            			String driverName = requestInfo[2];
            			String newDestination = request.getParameterValues("NewDestination")[0];
            			destinationChangeResponse = destinationChangeHandler(oldDestination, newDestination, driverName);
            			
            			changeRequestData(id, newDestination, "destination");
            		}
            		else
            			destinationChangeResponse[0] = "Destination change failed. Student 4-RIDE request not found.";
        		}
        		else 
        		{
        			//if parameters do not conform to cancel protocol, notify client app
        			destinationChangeResponse[0] = "Destination Change failed. Invalid request parameters.";
        		}

        		out.print(mapper.writeValueAsString(destinationChangeResponse));
        	}
        	else if(requestType.equals("VehicleLocations")) { 
        		//send all current vehicle locations to client app
        		String vAssignment = mapper.writeValueAsString(vehicles);
        		out.print(vAssignment);  
        	}
        }
        else if (deviceType.equals("Driver")) {
        	
        	//determine request driver app is making
            String requestType = request.getParameterValues("RequestType")[0];
            System.out.println(deviceType + " " + requestType + "request");
            
            System.out.println("RequestType: " + requestType);
            
        	if(requestType.equals("LoadItineraryAddresses")) {
        		
        		String[] loadItAddressResponse = new String[1];
        		if(		request.getParameterMap().containsKey("DriverName") &&
        				request.getParameterMap().containsKey("DriverLocationLat") &&
        				request.getParameterMap().containsKey("DriverLocationLon")
        		  )
             		{             			
         				String driverName = request.getParameterValues("DriverName")[0];
        				float driverLocationLat;
        				float driverLocationLon;
             			
    	        		try {
    	        			driverLocationLat = Float.parseFloat(request.getParameterValues("DriverLocationLat")[0]);
    	        			driverLocationLon = Float.parseFloat(request.getParameterValues("DriverLocationLon")[0]);
    	        		}
    	        		catch(NumberFormatException e) {
    	        			out.print("Get Itinerary (Addresses) failed. Invalid Vehicle Location");
    	        			return;
    	        		}
                 		
                		//send all current vehicle info to its driver
                		System.out.println(deviceType + " request");
                		
                		String[] it = {};
                		for(Vehicle v : vehicles)
                		{
                			if(v.getDriverName().equals(driverName))
                			{
                				it = v.getItinerary();
                				//v.setLocation(new float[]{driverLocationLat, driverLocationLon});
                			}
                		}
                		
                		String vAssignment = mapper.writeValueAsString(it);
                		System.out.println(vAssignment);
                		out.print(vAssignment);
             		}
             		else 
             		{
             			//if parameters do not conform to cancel protocol, notify client app
             			loadItAddressResponse[0] = "Get Itinerary (Addresses) failed. Invalid request parameters.";
                 		out.print(mapper.writeValueAsString(loadItAddressResponse));
             		}
        	}
        	else if(requestType.equals("LoadItineraryCoords")) {
        		
        		String[] loadItCoordsResponse = new String[1];
        		if(request.getParameterMap().containsKey("DriverName"))
             		{
             			String driverName = request.getParameterValues("DriverName")[0];
                 		
             			//send all current vehicle info to its driver
                		System.out.println(deviceType + " request");
                		
                		ArrayList<String[]> it = new ArrayList<String[]>();
                		for(int i=0; i<vehicles.length; i++) 
                		{
                			if(vehicles[i].getDriverName().equals(driverName))
                			{
                				for(String address : vehicles[i].getItinerary()) 
                					it.add(geocodeCache.get(address));
                			}
                		}
                		
                		String vAssignment = mapper.writeValueAsString(it);
                		out.print(vAssignment);
             		}
             		else 
             		{
             			//if parameters do not conform to cancel protocol, notify client app
             			loadItCoordsResponse[0] = "Get Itinerary (Coordinates) failed. Invalid request parameters.";
                 		out.print(mapper.writeValueAsString(loadItCoordsResponse));
             		}
        	}
        	else if(requestType.equals("CompletionRequest")) {
        		//parse remaining request parameters
        		String[] completionRequestResponse = new String[1];
        		if(request.getParameterMap().containsKey("DriverName") &&
        		   request.getParameterMap().containsKey("Destination")
        		   )
        		{
        			String driverName = request.getParameterValues("DriverName")[0];
            		String destination = request.getParameterValues("Destination")[0];
            		
            		completionRequestResponse = requestCompletionHandler(destination, driverName, Integer.toString(-1));
        		}
        		else 
        		{
        			//if parameters do not conform to cancel protocol, notify client app
        			completionRequestResponse[0] = "Completion Request failed. Invalid request parameters.";
        		}

        		out.print(mapper.writeValueAsString(completionRequestResponse));
        	}
        }
        else if (deviceType.equals("Dispatch")) {
        	//determine request dispatcher is making
            String requestType = request.getParameterValues("RequestType")[0];
        	if(requestType.equals("LoadItineraryAddresses")) {
        		//send all current vehicle info to its driver
        		System.out.println(deviceType + " request");
        		
        		ArrayList<String[]> its = new ArrayList<String[]>();
        		for(Vehicle v : vehicles)
        				its.add(v.getItinerary());
        		
        		String vAssignment = mapper.writeValueAsString(its);
        		out.print(vAssignment);
        	}
        	else if(requestType.equals("LoadItineraryCoords")) {
        		//send all current vehicle info to its driver
        		System.out.println(deviceType + " request");
        		
        		ArrayList<ArrayList<String[]>> its = new ArrayList<ArrayList<String[]>>();
        		for(int i=0; i<vehicles.length; i++) {
        			its.add(new ArrayList<String[]>());
        			for(String it : vehicles[i].getItinerary()) 
        				its.get(i).add(geocodeCache.get(it));
        		}
        		
        		String vAssignment = mapper.writeValueAsString(its);
        		System.out.println(vAssignment);
        		out.print(vAssignment);
        	}
        	else if(requestType.equals("DriverLocations")) {
        		//send all current vehicle info to its driver
        		System.out.println(deviceType + " request");
        		
        		ArrayList<String[]> locs = new ArrayList<String[]>();
        		for(Vehicle v : vehicles)
        				locs.add(new String[]{"" + v.getLocation()[0], "" + v.getLocation()[1]});
        		
        		String vAssignment = mapper.writeValueAsString(locs);
        		out.print(vAssignment);
        	}
        }
       
    }
	
	protected String[] pickupRequestHandler(String[] originCoords, String[] destinationCoords, int passengers) {
		
		//set default vehicle assignment to none
		int vehicleAssignment = -1;
		
		System.out.println(originCoords[0] + "," + originCoords[1]);
		
		String originAddress;
		if((originAddress = reverseGeocodeCache.get(originCoords)) == null) {
			//reverse geocode origin and destination coordinates to address key
			GeocodingResult[] results3 = null;
			try 
			{
				results3 = GeocodingApi.reverseGeocode(context, new LatLng(Float.parseFloat(originCoords[0]), Float.parseFloat(originCoords[1]))).await();
			} 
			catch (Exception e) { e.printStackTrace(); }
			
			originAddress = results3[0].formattedAddress;
			geocodeCache.put(originAddress, originCoords);	
			reverseGeocodeCache.put(originCoords, originAddress);	
		}
		
		String destinationAddress;
		if((destinationAddress = reverseGeocodeCache.get(destinationCoords)) == null) {		
			GeocodingResult[] results4 = null;	
			try 
			{
				results4 = GeocodingApi.reverseGeocode(context, new LatLng(Float.parseFloat(destinationCoords[0]), Float.parseFloat(destinationCoords[1]))).await();
			} 
			catch (Exception e) { e.printStackTrace(); }
			destinationAddress = results4[0].formattedAddress;
			geocodeCache.put(destinationAddress, destinationCoords);
			reverseGeocodeCache.put(destinationCoords, destinationAddress);
		}
		System.out.println(destinationAddress);
		
		ArrayList<Map<String, Map<String, Integer>>> updatedGraphs = new ArrayList<Map<String, Map<String, Integer>>>();
		vehicleLocs = new ArrayList<String>();
		
		//for all vehicles, add origin and destination points to graph
		//compute edge weights between all existing nodes to origin/destination node based on weighting factors
		//edge weights represent how difficult it is to travel from any node A to B, relative to other edges
		for(Vehicle v : vehicles) {		
			//get all current itinerary items
			Map<String, Map<String, Integer>> graph = new HashMap<String, Map<String, Integer>>();

			for(String key : v.getGraph().keySet()) {
				HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
				tempMap.putAll(v.getGraph().get(key));
				graph.put(key, tempMap);
			}	
			addLocation(graph, v, originAddress);
			addLocation(graph, v, destinationAddress); 
			
			String vehicleLocAddress;
			if((vehicleLocAddress = reverseGeocodeCache.get(v.getLocation())) == null) {		
				GeocodingResult[] results5 = null;	
				try 
				{
					results5 = GeocodingApi.reverseGeocode(context, new LatLng(v.getLocation()[0], v.getLocation()[1])).await();
				} 
				catch (Exception e) { e.printStackTrace(); }
				vehicleLocAddress = results5[0].formattedAddress;
				geocodeCache.put(vehicleLocAddress, new String[]{Float.toString(v.getLocation()[0]), Float.toString(v.getLocation()[1])});
				reverseGeocodeCache.put(new String[]{Float.toString(v.getLocation()[0]), Float.toString(v.getLocation()[1])}, vehicleLocAddress);
			}
			System.out.println(vehicleLocAddress);
			vehicleLocs.add(vehicleLocAddress);
			
			addLocation(graph, v, vehicleLocAddress); 

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
    		//print current graph
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
    		int newTour = optimalTour(updatedGraphs.get(i).size(), updatedGraphs.get(i), it, vehicleLocs.get(i));
    		int currentTour = computeTour(vehicles[i].getGraph(), vehicles[i].getItinerary());
    		if((newTour-currentTour) < minTourDifference) 
    		{
    			if(vehicles[i].getCurrentCapacity() > 0 && (vehicles[i].getCapacity()-vehicles[i].getCurrentCapacity() >= passengers)) {
	    			minTourDifference = newTour - currentTour;
	    			System.out.println("Current:" + currentTour + ", New: " + newTour);
	    			itinerary = it;
	    			vehicleAssignment = i;
    			}
    			else
    				return null;
    		}
    		if(maxTour < newTour) {
    			//maxTour = newTour;
    		}
		}
		
		System.out.println("Assigned Vehicle: " + vehicles[vehicleAssignment].getDriverName());
		System.out.println("Original capacity: 4");
		System.out.println("New capacity: " + (vehicles[vehicleAssignment].getCapacity()-vehicles[vehicleAssignment].getCurrentCapacity()));
		
		vehicles[vehicleAssignment].addPassengers(passengers);
		System.out.println("Change in min tour = " + minTourDifference);
		System.out.println("Saved " + maxTour + "!");
		System.out.println("Updated Itinerary:");
		
		//calculate optimal tour and adjust for precedence constraints
		ArrayList<String> rawOptimalIt = new ArrayList<String>( Arrays.asList(printOptimalTour(itinerary, vehicleLocs.get(vehicleAssignment))));
		ArrayList<String> adjustedOptimalIt = new ArrayList<String>();
		
		
		//print RAW IT
		System.out.println("RAW ITINERARY:");
		for(String s : rawOptimalIt) {
			System.out.println(s);
		}
		System.out.println();
		
		HashMap<String, Boolean> idTracker = new HashMap<String, Boolean>();
		String[] currentRequestIDs = requestCache.keySet().toArray(new String[requestCache.keySet().size()]);
		for(int i=0; i<rawOptimalIt.size(); i++) {
			for(int j=0; j<currentRequestIDs.length; j++)
			{
				if(requestCache.get(currentRequestIDs[j])[2].equals(vehicles[vehicleAssignment].getDriverName()))
				{
					String[] requestInfo = requestCache.get(currentRequestIDs[j]);
					if(requestInfo[0].equals(rawOptimalIt.get(i)))
					{
						adjustedOptimalIt.add(rawOptimalIt.get(i));
						rawOptimalIt.remove(i);
						idTracker.put(requestInfo[0], true);
					}
					else if(requestInfo[1].equals(rawOptimalIt.get(i)) && idTracker.containsKey(requestInfo[1]))
					{
						adjustedOptimalIt.add(rawOptimalIt.get(i));
						rawOptimalIt.remove(i);
					}
				}
			}
		}
		
		adjustedOptimalIt.addAll(rawOptimalIt);
		
		//print ADJUSTED IT
		System.out.println("ADJUSTED ITINERARY:");
		for(String s : adjustedOptimalIt) {
			System.out.println(s);
		}
		System.out.println();
		
		
		vehicles[vehicleAssignment].setItinerary(adjustedOptimalIt.toArray(new String[adjustedOptimalIt.size()]));
		vehicles[vehicleAssignment].setGraph(updatedGraphs.get(vehicleAssignment));
		
		//remove current vehicle loc from graph
		removeLocation(vehicles[vehicleAssignment], vehicleLocs.get(vehicleAssignment), false);
		
		//set node's priority for assigned vehicle
		int newPriority = -1;
		Entry<String, Integer> maxPriority = null;
		for(Entry<String,Integer> entry : vehicles[vehicleAssignment].getPriorities().entrySet()) {
		    if (maxPriority == null || entry.getValue() > maxPriority.getValue())
		    	maxPriority = entry;
		}
		newPriority = maxPriority.getValue() + 1;
		System.out.println("Priority: " + newPriority);
		vehicles[vehicleAssignment].setPriority(originAddress, newPriority);
		vehicles[vehicleAssignment].setPriority(destinationAddress, newPriority);
		
		//add origin and destination to request cache
		String requestID = setRequestData(originAddress, destinationAddress, vehicles[vehicleAssignment].getDriverName(), Integer.toString(passengers));
		
		return new String[]{requestID, Integer.toString(vehicleAssignment)};

	}
	
	protected String[] VehicleHasArrivedRequestHandler(String originAddress, String driverName) {
		String[] hasArrivedResponse = new String[1];
		hasArrivedResponse[0] = "True";
		
		for(Vehicle v : vehicles) {
			if(v.getDriverName().equals(driverName)) {	
				
				String[] itinerary = v.getItinerary();
				for(String loc : itinerary) {
					if(loc.equals(originAddress))
					{
						hasArrivedResponse[0] = "False";
						break;
					}
				}
				
			}
		}
		
		return hasArrivedResponse;
	}
	
	protected String[] cancelRequestHandler(String originAddress, String destinationAddress, String driverName, String passengerCount) {
		
		//reverse geocode origin and destination coordinates to address key
		String[] cancelResponse = new String[1];
		
		//find vehicle that cancellation request affects
		for(Vehicle v : vehicles) {
			if(v.getDriverName().equals(driverName)) {
				
				removeLocation(v, originAddress, true);
				removeLocation(v, destinationAddress, true);
				
				//add capacity back to vehicle
				int passengers;
			    try{
			        passengers = (int)Float.parseFloat(passengerCount);
			    }catch(NumberFormatException e){
			    	cancelResponse[0] = "Cancel failed: Passenger count must be an integer.";
			    	break;
			    }
			    v.setCurrentCapacity(v.getCurrentCapacity() + passengers);
			    
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
	
	protected String[] requestCompletionHandler(String destinationAddress, String driverName, String passengerCount) {

		String[] completionResponse = new String[1];
		
		//find vehicle with request completion
		for(Vehicle v : vehicles) {
			if(v.getDriverName().equals(driverName)) {
				
				System.out.println("THIS VEHICLE: " + v);
				removeLocation(v, destinationAddress, true);
				
				//add capacity back to vehicle (for early drop off only)
				if(Integer.parseInt(passengerCount) > 0) {
					v.setCurrentCapacity(v.getCurrentCapacity() + Integer.parseInt(passengerCount));	
				}
				//else add capacity back to vehicle as net sum of pickups and drop offs
				else {
					int passengerChange = 0;
					String[] currentRequestIDs = requestCache.keySet().toArray(new String[requestCache.keySet().size()]);
					for(String id : currentRequestIDs)
					{
						String[] requestInfo = requestCache.get(id);
						if(requestInfo[2].equals(driverName))
						{
							if(destinationAddress.equals(requestInfo[0]))
							{
								passengerChange += Integer.parseInt(requestInfo[3]);
								changeRequestData(id, "", "origin");
							}
							else if(destinationAddress.equals(requestInfo[1]))
							{
								passengerChange -= Integer.parseInt(requestInfo[3]);
								requestCache.remove(id);
							}
						}
					}
					v.setCurrentCapacity(v.getCurrentCapacity() + passengerChange);
				}
			    
				completionResponse[0] = "Request Completion Successful";	
				break;
			}
		}
		//if request not in system, notify client app
		if(completionResponse[0] == null) {
			completionResponse[0] = "Request Completion Failed: No active request";
		}	
		
		return completionResponse;	
	}
	
	protected String[] destinationChangeHandler(String oldDestinationAddress, String newDestinationAddress, String driverName) {
		//reverse geocode coordinates to address key
		String[] destinationChangeResponse = new String[1];
		
		//find vehicle with request completion
		for(Vehicle v : vehicles) {
			if(v.getDriverName().equals(driverName)) {	
				removeLocation(v, oldDestinationAddress, true);
				addLocation(v.getGraph(), v, newDestinationAddress);
				destinationChangeResponse[0] = "Destination Change Successful";	
				break;
			}
		}
		//if request not in system, notify client app
		if(destinationChangeResponse[0] == null) {
			destinationChangeResponse[0] = "Destination Change Failed: No active request";
		}	
		
		return destinationChangeResponse;	
	}
	
	protected void addLocation(Map<String, Map<String, Integer>> graph, Vehicle v, String address) {
		String[] itineraryLocs = graph.keySet().toArray(new String[graph.keySet().size()]);

		//get new itinerary items from request
		String[] newLocs = {address};
		
		String[] allLocs = graph.keySet().toArray(new String[graph.keySet().size()+newLocs.length]);
		allLocs[allLocs.length-1] = address;
		
		//get priority value of new request
		//generic addLocation does not update vehicle priority list
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
		//current weighting factors: (1) time to travel between two locations (considering traffic), (2) First in, first out
		rows = distancesFromMatrix.rows;
		for(int i=0; i<rows.length; i++) {	
			newNodes.add(new HashMap<String, Integer>());
			elements = rows[i].elements;
			for(int j=0; j<elements.length; j++) {
				if(!newLocs[i].equals(allLocs[j])) {
					if(v.getPriorities().get(allLocs[j]) == null)
						newNodes.get(i).put(allLocs[j], (int)(0.5*(elements[j].distance.inMeters/100) + 0.5*(newPriority*10)));
					else
						newNodes.get(i).put(allLocs[j], (int)(0.5*(elements[j].distance.inMeters/100) + 0.5*(v.getPriorities().get(allLocs[j])*10)));
				}
			}
			graph.put(newLocs[i], newNodes.get(i));
		}
		
		rows = distancesToMatrix.rows;
		for(int i=0; i<rows.length; i++) {	
			newNodes.add(new HashMap<String, Integer>());
			elements = rows[i].elements;
			for(int j=0; j<elements.length; j++)	
				graph.get(itineraryLocs[i]).put(newLocs[j], (int)(0.5*(elements[j].distance.inMeters/100) + 0.5*(newPriority*10)));
		}
	}
	
	protected void removeLocation(Vehicle v, String a, boolean recomputeIt) {
		
		//set address to be removed
		String address = a;
		
		//if a is only a substring of address in graph, update address accordingly 
		for(String s : v.getGraph().keySet()) {
			String[] addressSplit = s.split(",");
			if(Arrays.asList(addressSplit).contains(a))
				address = s;
		}
		
		//remove address node from graph (and all related edges)
		for(String s : v.getGraph().keySet()) {
			if(!s.equals(address))
				v.getGraph().get(s).remove(address);
		}
		v.getGraph().remove(address);
		
		//recompute itinerary without origin/destination
		if(recomputeIt) {
			Map<String, Map<String, String>> it = new HashMap<String, Map<String, String>>();
			String[] itineraryLocs = v.getGraph().keySet().toArray(new String[v.getGraph().keySet().size()]);	
			for(int j=0; j<v.getGraph().size(); j++)
				it.put(itineraryLocs[j], new HashMap<String, String>());
		
			optimalTour(v.getGraph().size(), v.getGraph(), it, vehicleLocs.get(Arrays.asList(vehicles).indexOf(v)));
			v.setItinerary(printOptimalTour(it, vehicleLocs.get(Arrays.asList(vehicles).indexOf(v))));
		}
		
	    //remove location's priority from vehicle object
	    v.removePriority(address);
	}
	
	protected int computeTour(Map<String, Map<String, Integer>> weights, String[] it) {
		if(it != null && weights != null) {
			int tour = 0;
			for(int i=0; i<it.length-1; i++)
				tour += weights.get(it[i]).get(it[i+1]);
			return tour;
		}
		else
			return -1;
	}
	
	protected int optimalTour(int n, Map<String, Map<String, Integer>> weights, Map<String, Map<String, String>> paths, String startLoc) {
		
		//check for all nodes in WEIGHTS, there is only ONE key value pair
		//check for all nodes in PATH, there is 2^n key value pairs
	
		ArrayList<String> nodesList = new ArrayList<String>(weights.keySet()); 
		Collections.sort(nodesList);
		
		nodesList.remove(startLoc);
		nodesList.add(0, startLoc);
		
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
			for( String[] A : vMinusV1AsList ) {
				ArrayList<String> aAsList = new ArrayList<String>(Arrays.asList(A));
				
				if(A.length == k && !aAsList.contains(nodes[0])) {		
					for(int i=1; i<nodes.length;i++) {
						if(!aAsList.contains(nodes[i])) {
							
							int dVal = Integer.MAX_VALUE;
							String dValIndex = "";
							
							for(int j=0; j<nodes.length;j++) {
								if(aAsList.contains(nodes[j])) {
									aAsList.remove(nodes[j]);
									String[] aMinusJ = aAsList.toArray(new String[aAsList.size()]);
						
									int cachedDVal;
									if((cachedDVal = weights.get(nodes[i]).get(nodes[j]) + D.get(nodes[j]).get(Arrays.toString(aMinusJ))) <= dVal)
									{
										dVal = cachedDVal;
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
		
		//calculate full optimal tour from D
		int dVal = Integer.MAX_VALUE;
		String dValIndex = "";
		ArrayList<String> nodesAsList = new ArrayList<String>(Arrays.asList(nodes));
		nodesAsList.remove(nodes[0]);
		
		for(int j=1; j<nodes.length;j++) 
		{
			nodesAsList.remove(nodes[j]);
			String[] nodesMinus1J = (String[])nodesAsList.toArray(new String[nodes.length-2]);
	
			int cachedDVal;
			if((cachedDVal = (weights.get(nodes[0]).get(nodes[j]) + D.get(nodes[j]).get(Arrays.toString(nodesMinus1J)))) < dVal)
			{
				dVal = cachedDVal;
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
		//System.out.println("P[" + nodes[0] + "][" + Arrays.toString(nodesMinusV1) +"] = " + dValIndex);
		
		//return min tour
		return D.get(nodes[0]).get(Arrays.toString(nodesMinusV1));
		
	}

	protected String[] printOptimalTour(Map<String, Map<String, String>> paths, String startLoc) {
	
		//print optimal tour from P
		ArrayList<String> nodesList = new ArrayList<String>(paths.keySet()); 
		ArrayList<String> itinerary = new ArrayList<String>();
		Collections.sort(nodesList);
		
		nodesList.remove(startLoc);
		nodesList.add(0, startLoc);
		
		String v1 = nodesList.get(0);
		itinerary.add(v1);
		System.out.println(v1);
		
		String currentNode = v1;
		nodesList.remove(currentNode);
		
		while(nodesList.isEmpty() != true) {
			String nextNode = paths.get(currentNode).get(Arrays.toString(nodesList.toArray(new String[nodesList.size()])));
			itinerary.add(nextNode);
			System.out.println(nextNode);
			nodesList.remove(nextNode);
			Collections.sort(nodesList);
			currentNode = nextNode;
		}
		
		//itinerary.add(v1);
		//System.out.println(v1);
		
		//return itinerary
		return itinerary.toArray(new String[itinerary.size()]);
	}
	

}
