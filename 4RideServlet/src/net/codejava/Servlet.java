package net.codejava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

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
	
	/*
	protected void optimalTour(int n, Map<String, Map<String, Integer>> weights, Map<String, Map<String, Integer>> path) {
		Map<Integer, Map<String, Integer>> distances = new HashMap<Integer, Map<String, Integer>>();
		
		for(int i=0; i<=n; i++) {
			D.put(i, null) = weights.get(weights.keySet()[i]).get(weights.keySet()[i]);
			for(int k=0; k <=(n-2); k++) {
				for(int j=0; j <=n; j++) {
						D.put(i, weights.keySet()) = weights.get(weights.keySet()[i]).get(weights.get(weights.keySet()[i]).keySet()[j]);
						P.put(i, weights.keySet()) = j);
				}		
			}
		}
		
	}
	*/

}
