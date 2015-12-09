package net.codejava;

import java.util.HashMap;
import java.util.Map;

import com.google.maps.GeocodingApi;
import com.google.maps.model.LatLng;

public class VehicleUnitTest {
	
	static Vehicle[] vehicles;
	static HashMap<String, Map<String, Integer>> vGraph1;
	static Map<String, Integer> vP1;
	static String[] it1;
	static HashMap<String, Map<String, Integer>> vGraph2;
	static Map<String, Integer> vP2;
	static String[] it2;

	protected static void initialize() {
		//vGraph 1
        Map<String, Integer> vG1_Claridge = new HashMap<String, Integer>();
        vG1_Claridge.put("2350 H Street NW, Washington, DC 20052, USA", 3);
        Map<String, Integer> vG1_Amsterdam = new HashMap<String, Integer>();
        vG1_Amsterdam.put("950 25th Street NW, Washington, DC 20037, USA", 4);
        vG1_Amsterdam.put("2135 F Street NW, Washington, DC 20037, USA", 6);
        Map<String, Integer> vG1_South = new HashMap<String, Integer>();
        vG1_South.put("950 25th Street NW, Washington, DC 20037, USA", 11);
        
        vGraph1 = new HashMap<String, Map<String, Integer>>();
        vGraph1.put("950 25th Street NW, Washington, DC 20037, USA", vG1_Claridge);
        vGraph1.put("2350 H Street NW, Washington, DC 20052, USA", vG1_Amsterdam);
        vGraph1.put("2135 F Street NW, Washington, DC 20037, USA", vG1_South);
        
        vP1 = new HashMap<String, Integer>();
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
        
        vGraph2 = new HashMap<String, Map<String, Integer>>();
        vGraph2.put("2400 M Street NW, Washington, DC 20037, USA", vG2_2400M);
        vGraph2.put("1957 E Street NW, Washington, DC 20052, USA", vG2_EStreet);
        vGraph2.put("1837 M Street NW, Washington, DC 20036, USA", vG2_ChipotleOnM);
        
        vP2 = new HashMap<String, Integer>();
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
        vehicles = new Vehicle[2];
        vehicles[0] = new Vehicle(5, 4, "Mr. Smith", new float[]{ 38.900197f, -77.050110f }, vP2, vGraph2 );
        vehicles[1] = new Vehicle(6, 2, "Mr. Dillard", new float[]{ 38.897367f, -77.045475f }, vP3, vGraph3 );
        
        String[] itinerary1 = { "2350 H Street NW, Washington, DC 20052, USA", 
        						"2135 F Street NW, Washington, DC 20037, USA", 
        						"950 25th Street NW, Washington, DC 20037, USA" };
        it1 = itinerary1;
        
        String[] itinerary2 = { "1957 E Street NW, Washington, DC 20052, USA", 
        						"1837 M Street NW, Washington, DC 20036, USA", 
        						"2400 M Street NW, Washington, DC 20037, USA", };
        it2 = itinerary2;
        vehicles[0].setItinerary(itinerary2);
        
        String[] itinerary3 = { "2135 F Street NW, Washington, DC 20037, USA", 
        						"950 25th Street NW, Washington, DC 20037, USA", 
        						"1957 E Street NW, Washington, DC 20052, USA" };
        vehicles[1].setItinerary(itinerary3);
	}

	public static void main(String[] args) {
		
		initialize();
		
		//check that only complete graphs are accepted in vehicle constructor
		try 
		{
			Vehicle v = new Vehicle(6, 4, "Ms. Malone", new float[]{ 38.899354f, -77.048844f }, vP1, vGraph1 );	
			System.out.println("Failed: Vehicle accepts incomplete graph");
		} 
		catch (Exception e) { 
			System.out.println("Passed: Vehicle doesn't accept incomplete graph");
		}
		
		try 
		{
			Vehicle v = new Vehicle(6, 4, "Ms. Malone", new float[]{ 38.899354f, -77.048844f }, vP2, vGraph2 );	
			System.out.println("Passed: Vehicle accepts complete graph");
		} 
		catch (Exception e) { 
			System.out.println("Failed: Vehicle doesn't accept complete graph");
		}
		
		//check that constructor accepts coordinates correctly
		try 
		{
			Vehicle v = new Vehicle(6, 4, "Ms. Malone", new float[]{ 38.899354f, -77.048844f, 0.0f }, vP1, vGraph1 );
			System.out.println("Failed: Vehicle accepts coordinates != size 2");
		} 
		catch (Exception e) { 
			System.out.println("Passed: Vehicle rejects coordinates != size 2");
		}
		
		try 
		{
			Vehicle v = new Vehicle(6, 4, "Ms. Malone", new float[]{ 38.899354f, -77.048844f }, vP2, vGraph2 );	
			System.out.println("Passed: Vehicle accepts coordinates of size 2");
		} 
		catch (Exception e) { 
			System.out.println("Failed: Vehicle rejects coordinates of size 2");
		}
		
		//check that setGraph correctly checks graph
		if(vehicles[0].setGraph(vGraph1) == false)
			System.out.println("Passed: Vehicle setGraph() rejects income graph");
		else
			System.out.println("Failed: Vehicle setGraph() accepts incomplete");
		
		//check that setGraph correctly checks graph
		if(vehicles[0].setGraph(vGraph2) == true)
			System.out.println("Passed: Vehicle setGraph() accepts complete graph");
		else
			System.out.println("Failed: Vehicle setGraph() rejects complete graph");
			

		//check that setLocation correctly checks coordinates
		float[] correctLoc = new float[]{ 38.899354f, -77.048844f };
		float[] incorrectLoc = new float[]{ 38.899354f, -77.048844f, 0.0f };
		
		if(vehicles[0].setLocation(incorrectLoc) == false)
			System.out.println("Passed: Vehicle setLocation() rejects coordinates != size 2");
		else
			System.out.println("Failed: Vehicle setLocation() accepts coordinates != size 2");
		
		//check that setGraph correctly checks graph
		if(vehicles[0].setLocation(correctLoc) == true)
			System.out.println("Passed: Vehicle setLocation() accepts coordinates = size 2");
		else
			System.out.println("Failed: Vehicle setLocation() rejects coordinates = size 2");
		
		//check that setItinerary correctly checks input
		String[] correctIt = {"Boston", "New York", "Philadelphia"};
		String[] emptyIt = new String[3];
		
		if(vehicles[0].setItinerary(emptyIt) == false)
			System.out.println("Passed: Vehicle setItinerary() rejects empty itineraries");
		else
			System.out.println("Failed: Vehicle setItinerary() accepts empty itineraries");
		
		if(vehicles[0].setItinerary(correctIt) == true)
			System.out.println("Passed: Vehicle setItinerary() accepts complete itineraries");
		else
			System.out.println("Failed: Vehicle setItinerary() rejects complete itineraries");
			
	}

}
