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
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import java.util.Map;
import java.util.Set;

/**
 * Servlet implementation class Servlet
 */
@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<String, double[]> locationKeyMap;
	private Vehicle[] vehicles;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
        // TODO Auto-generated constructor stub
        
        locationKeyMap = new HashMap<String, double[]>();
        locationKeyMap.put("2350 H Street NW", new double[]{38.899291, -77.051000});
        locationKeyMap.put("2135 F Street NW", new double[]{38.899291, -77.051000});
        locationKeyMap.put("950 25th Street NW", new double[]{38.902105, -77.053296});
        locationKeyMap.put("1957 E Street NW", new double[]{38.896026, -77.044187});
        locationKeyMap.put("1837 M Street NW", new double[]{38.905662, -77.043140});
        locationKeyMap.put("2400 M Street NW", new double[]{38.905203, -77.051465});    
        
      //vGraph 1
        Map<String, Integer> vG1_Claridge = new HashMap<String, Integer>();
        vG1_Claridge.put("2350 H Street NW", 3);
        vG1_Claridge.put("2135 F Street NW", 10);
        Map<String, Integer> vG1_Amsterdam = new HashMap<String, Integer>();
        vG1_Amsterdam.put("950 25th Street NW", 4);
        vG1_Amsterdam.put("2135 F Street NW", 6);
        Map<String, Integer> vG1_South = new HashMap<String, Integer>();
        vG1_South.put("2350 H Street NW", 5);
        vG1_South.put("950 25th Street NW", 11);
        
        HashMap<String, Map<String, Integer>> vGraph1 = new HashMap<String, Map<String, Integer>>();
        vGraph1.put("950 25th Street NW", vG1_Claridge);
        vGraph1.put("2350 H Street NW", vG1_Amsterdam);
        vGraph1.put("2135 F Street NW", vG1_South);
        
        //vGraph 2
        Map<String, Integer> vG2_2400M = new HashMap<String, Integer>();
        vG2_2400M.put("1957 E Street NW", 20);
        vG2_2400M.put("1837 M Street NW", 8);
        Map<String, Integer> vG2_EStreet = new HashMap<String, Integer>();
        vG2_EStreet.put("2400 M Street NW", 19);
        vG2_EStreet.put("1837 M Street NW", 18);
        Map<String, Integer> vG2_ChipotleOnM = new HashMap<String, Integer>();
        vG2_ChipotleOnM.put("2400 M Street NW", 9);
        vG2_ChipotleOnM.put("1957 E Street NW", 19);
        
        HashMap<String, Map<String, Integer>> vGraph2 = new HashMap<String, Map<String, Integer>>();
        vGraph2.put("2400 M Street NW", vG2_2400M);
        vGraph2.put("1957 E Street NW", vG2_EStreet);
        vGraph2.put("1837 M Street NW", vG2_ChipotleOnM);
        
        //vGraph 3
        Map<String, Integer> vG3_Claridge = new HashMap<String, Integer>();
        vG3_Claridge.put("1957 E Street NW", 18);
        vG3_Claridge.put("2135 F Street NW", 10);
        Map<String, Integer> vG3_EStreet = new HashMap<String, Integer>();
        vG3_EStreet.put("950 25th Street NW", 19);
        vG3_EStreet.put("2135 F Street NW", 4);
        Map<String, Integer> vG3_South = new HashMap<String, Integer>();
        vG3_South.put("1957 E Street NW", 5);
        vG3_South.put("950 25th Street NW", 11);
        
        HashMap<String, Map<String, Integer>> vGraph3 = new HashMap<String, Map<String, Integer>>();
        vGraph3.put("950 25th Street NW", vG3_Claridge);
        vGraph3.put("1957 E Street NW", vG3_EStreet);
        vGraph3.put("2135 F Street NW", vG3_South);
        
        //Vehicles
        vehicles = new Vehicle[3];
        vehicles[0] = new Vehicle(6, 4, "Ms. Malone", new float[]{ 38.899354f, -77.048844f }, vGraph1 );
        vehicles[1] = new Vehicle(5, 4, "Mr. Smith", new float[]{ 38.900197f, -77.050110f }, vGraph2 );
        vehicles[2] = new Vehicle(6, 2, "Mr. Dillard", new float[]{ 38.897367f, -77.045475f }, vGraph3 );
        
        Map<String, double[]> itinerary1 = new HashMap<String, double[]>();
        itinerary1.put("2350 H Street NW", locationKeyMap.get("2350 H Street NW"));
        itinerary1.put("2135 F Street NW", locationKeyMap.get("2135 F Street NW"));
        itinerary1.put("950 25th Street NW", locationKeyMap.get("950 25th Street NW"));
        vehicles[0].setItinerary(itinerary1);
        
        Map<String, double[]> itinerary2 = new HashMap<String, double[]>();
        itinerary2.put("1957 E Street NW", locationKeyMap.get("1957 E Street NW"));
        itinerary2.put("1837 M Street NW", locationKeyMap.get("1837 M Street NW"));
        itinerary2.put("2400 M Street NW", locationKeyMap.get("2400 M Street NW")); 
        vehicles[1].setItinerary(itinerary2);
        
        Map<String, double[]> itinerary3 = new HashMap<String, double[]>();
        itinerary3.put("2135 F Street NW", locationKeyMap.get("2135 F Street NW"));
        itinerary3.put("950 25th Street NW", locationKeyMap.get("950 25th Street NW"));
        itinerary3.put("1957 E Street NW", locationKeyMap.get("1957 E Street NW"));
        vehicles[2].setItinerary(itinerary3);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub  
		
		System.out.println(request);
        
        PrintWriter out = response.getWriter();
        
        //String objectToReturn = "{ key1: 'value1', key2: 'value2' }";
        //request.setCharacterEncoding("utf8");
        //response.setContentType("application/json");
        //out = response.getWriter();
        
     // Replace the API key below with a valid API key.
        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyCCi0xwG4nVccFDw5vTzkO_402Lg8CyGW4");
        GeocodingResult[] results = null;
		try {
			LatLng CA = new LatLng(37.4220355, -122.0841244);
			results = GeocodingApi.reverseGeocode(context, CA).await();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //System.out.println("(" + results[0].geometry.location.lat + "," + results[0].geometry.location.lng + ")");
		System.out.println(results[0].formattedAddress);
		
        ObjectMapper mapper = new ObjectMapper();
        String vAssignment = mapper.writeValueAsString(vehicles);
        out.print(vAssignment);
        
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println(request);
        
        PrintWriter out = response.getWriter();
        
        String deviceType = request.getParameterValues("DeviceType")[0];
        System.out.println(deviceType);
        
        if(deviceType.equals("Student")) {
        	
            String requestType = request.getParameterValues("RequestType")[0];
            System.out.println(requestType);
            
        	if(requestType.equals("Pickup")) {
        		
        		String Start = request.getParameterValues("Start")[0];
        		String Destination = request.getParameterValues("Destination")[0];
        		String Passangers = request.getParameterValues("Passangers")[0];
        		
        		//for each vehicle, add current location to graph (driver app sends location as parameter in pickup request)
        		
        		//for each vehicle, determine weights to each node (complete graph)
        		
        		//for each vehicle, insert node (with weights) into graph 
        		
        		//for each vehicle, compute optimal tour given new request       		
        		Map<String, double[]> itinerary;
        		//optimalTour(vehicles[0].getGraph().size(), vehecles[0].getGraph(), itinerary);
        		
        	}
        	else if(requestType.equals("VehicleLocations")) {   	
        		ObjectMapper mapper = new ObjectMapper();
        		String vAssignment = mapper.writeValueAsString(vehicles);
        		out.print(vAssignment);  
        	}
        }
        else if (deviceType.equals("Driver")) {
            ObjectMapper mapper = new ObjectMapper();
            String vAssignment = mapper.writeValueAsString(vehicles);
            out.print(vAssignment);
        }
       
    }
	
	
protected static void optimalTour(int n, Map<String, Map<String, Integer>> weights, Map<String, Map<String, String>> paths) {
		
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
		
		for(int k=1; k<=(n-2); k++) 
		{
			System.out.println("k="+k);
			for( String[] A : vMinusV1AsList ) 
			{
				ArrayList<String> aAsList = new ArrayList<String>(Arrays.asList(A));
				if(A.length == k && !aAsList.contains(nodes[0])) 
				{	
					System.out.println(new ArrayList<String>(Arrays.asList(A)));
					for(int i=0; i<nodes.length;i++) 
					{
						//can just get rid of i=0 in loop...
						if(i != 0 && !aAsList.contains(nodes[i])) 
						{
							System.out.println("i=" + i);
							int dVal = Integer.MAX_VALUE;
							String dValIndex = "";
							System.out.println(aAsList);
							for(int j=0; j<nodes.length;j++) 
							{
								if(aAsList.contains(nodes[j]))
								{
									System.out.println("j=" + j);
									System.out.println("current A: " + aAsList);
									aAsList.remove(nodes[j]);
									String[] aMinusJ = aAsList.toArray(new String[aAsList.size()]);
								
									int tempVal;
									System.out.println("current A-vj: " + aAsList);
									System.out.println(weights.get(nodes[i]).get(nodes[j]));
									System.out.println(D.get(nodes[j]).get(Arrays.toString(aMinusJ)));
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
							System.out.println("P[" + nodes[i] + "][" + Arrays.toString(A) +"] = " + dValIndex);
							System.out.println(paths.get(nodes[i]).get(Arrays.toString(A)));
						}
					}
				}
			}
		}
		
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
		System.out.println(D.get(nodes[0]).get(Arrays.toString(nodesMinusV1)));
		
		
		
	}

	protected static void printOptimalTour(Map<String, Map<String, String>> paths) {
	
		ArrayList<String> nodesList = new ArrayList<String>(paths.keySet()); 
		Collections.sort(nodesList);
		
		String currentNode = nodesList.get(0);
		String v1 = currentNode;
		System.out.println(v1);
		nodesList.remove(currentNode);
		while(nodesList.isEmpty() != true) {
			String nextNode = paths.get(currentNode).get(Arrays.toString(nodesList.toArray(new String[nodesList.size()])));
			System.out.println(nextNode);
			nodesList.remove(nextNode);
			Collections.sort(nodesList);
			currentNode = nextNode;
		}
		System.out.println(v1);
	}
	

}
