package net.codejava;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServletUnitTest {
	
	private static Vehicle[] vehicles;
	
	protected static void init() {
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
	
	protected static String postCall(String params) throws IOException {
		
		byte[] postData       = params.getBytes( StandardCharsets.UTF_8 );
		int    postDataLength = postData.length;
		String request        = "http://localhost:8080/4RideServlet/Servlet";
		URL    url            = new URL( request );
		HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
		conn.setDoOutput( true );
		conn.setInstanceFollowRedirects( false );
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
		conn.setRequestProperty( "charset", "utf-8");
		conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
		conn.setUseCaches( false );
		try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
		   wr.write( postData );
		}
		
		Reader in = new InputStreamReader(conn.getInputStream());

		String response = "";
		char[] c = new char[10];
		while((in.read(c)) != -1) {
			for(int i=0; i<c.length; i++) {
				response += c[i];
			}
			c = new char[10];
		}
		
		return response;
	}

	
	public static void main(String[] args) throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		init();
		
		//check that driver POST method serializes vehicles correctly
		String urlParameters  = "DeviceType=Driver";
		if((mapper.writeValueAsString(vehicles[0])).equals(postCall(urlParameters)))
			System.out.println("Passed: Driver POST returns expected JSON for Vehicle 0");
		else
			System.out.println("Failed: Driver POST failed to return expected JSON for Vehicle 0");
		
        urlParameters = "DeviceType=Student&RequestType=Pickup&Origin=38.0%20-77.0&Destination=38.0%20-77.1&Passengers=2";
        System.out.println(postCall(urlParameters));
        
        String pickupTestResponse = "{\"currentCapacity\":2,\"driverName\":\"Ms. Malone\",\"location\":[38.899353,-77.04884],\"priorities\":" +
									"{\"Unnamed Road, Milford, VA 22514, USA\":3,\"2350 H Street NW, Washington, DC 20052, USA\":2,\"Unnamed Road," +
									"Champlain, VA 22438, USA\":3,\"950 25th Street NW, Washington, DC 20037, USA\":1,\"2135 F Street NW, Washington, " + 
									"DC 20037, USA\":2},\"itinerary\":[\"2350 H Street NW, Washington, DC 20052, USA\",\"2135 F Street NW, Washington, " +
									"DC 20037, USA\",\"950 25th Street NW, Washington, DC 20037, USA\"],\"graph\":{\"2350 H Street NW, Washington, DC " +
									"20052, USA\":{\"Unnamed Road, Milford, VA 22514, USA\":42,\"Unnamed Road, Champlain, VA 22438, USA\":43,\"950 25th " +
									"Street NW, Washington, DC 20037, USA\":4,\"2135 F Street NW, Washington, DC 20037, USA\":6},\"950 25th Street NW, " +
									"Washington, DC 20037, USA\":{\"Unnamed Road, Milford, VA 22514, USA\":42,\"2350 H Street NW, Washington, DC 20052, " +
									"USA\":3,\"Unnamed Road, Champlain, VA 22438, USA\":43,\"2135 F Street NW, Washington, DC 20037, USA\":10},\"2135 F " +
									"Street NW, Washington, DC 20037, USA\":{\"Unnamed Road, Milford, VA 22514, USA\":42,\"2350 H Street NW, Washington, " +
									"DC 20052, USA\":5,\"Unnamed Road, Champlain, VA 22438, USA\":43,\"950 25th Street NW, Washington, DC 20037, " + 
									"USA\":11}},\"capacity\":6}";
        
        if(pickupTestResponse.equals(postCall(urlParameters)))
        	System.out.println("Passed: Student Pickup POST returns expected JSON");
        else
        	System.out.println("Failed: Student Pickup POST failed to return expected JSON");

	}

}
