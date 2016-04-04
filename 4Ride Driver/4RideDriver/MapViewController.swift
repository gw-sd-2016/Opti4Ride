//
//  MapViewController.swift
//  4Ride Driver
//
//  Created by Randy Fitzmorris on 3/20/16.
//
//

import UIKit
import MapKit
import CoreLocation
import Alamofire
import SwiftyJSON
import Contacts

class MapController: UIViewController, CLLocationManagerDelegate, UITextFieldDelegate {
    
    @IBOutlet weak var mapView: MKMapView!
    
    var itinerary: [JSON]?
    var driverName = String()
    let locationManager = CLLocationManager()
    var currentLocation = CLLocationCoordinate2D()
    var origin = CLLocationCoordinate2D()
    var destination = CLLocationCoordinate2D()
    var itineraryAddresses: [JSON]?
    var itineraryCoords: [JSON]?
    
    //set default region displayed on map (GWU Foggy Bottom Campus)
    func setDefaultRegion() {
        let location = CLLocationCoordinate2D(latitude: 38.899599, longitude: -77.041708)
        let region = MKCoordinateRegionMakeWithDistance(location, 2000.0, 2800.0)
        mapView.setRegion(region, animated: true)
    }
    
    //clear all pins and overlays on map
    func clearMap() {
        let annotationsToRemove = self.mapView.annotations.filter { $0 !== mapView.userLocation }
        self.mapView.removeAnnotations( annotationsToRemove )
        
        let overlaysToRemove = self.mapView.overlays
        self.mapView.removeOverlays(overlaysToRemove)
    }
    
    //inital app setup
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //set driver
        self.driverName = "Mr. Smith"
        
        //enable CoreLocation services
        self.locationManager.delegate = self
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        self.locationManager.requestWhenInUseAuthorization()
        self.locationManager.startUpdatingLocation()
        
        //attach map to code
        mapView.delegate = self
        setDefaultRegion();
        
        //enable map features
        self.mapView.showsUserLocation = true
        self.mapView.showsTraffic = true
        self.mapView.showsScale = true
        
        //load vehicle's current itinerary
        loadItinerary()
        
        //then load vehicle's current itinerary every 10 seconds
        var timer = NSTimer.scheduledTimerWithTimeInterval(10, target: self, selector: "itineraryUpdate", userInfo: nil, repeats: true)
    }
    
    func itineraryUpdate() {
        loadItinerary()
    }
    
    func loadItinerary() {
        
        //paramaters that adhere to itinerary load request protocol
        let paras = [
            "DeviceType": "Driver",
            "RequestType": "LoadItineraryAddresses",
            "DriverName": self.driverName,
            "DriverLocationLat": String(currentLocation.latitude),
            "DriverLocationLon": String(currentLocation.longitude)
        ]
        
        Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
            .response { request, response, data, error in
                
                let json = JSON(data: data!)
                print(json)
                self.itineraryAddresses = json.array
                
                if(self.itineraryAddresses != nil)
                {
                    
                    //paramaters that adhere to itinerary load request protocol
                    let paras = [
                        "DeviceType": "Driver",
                        "RequestType": "LoadItineraryCoords",
                        "DriverName": self.driverName
                    ]
                    
                    Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
                        .response { request, response, data, error in
                            
                            let json = JSON(data: data!)
                            print(json)
                            
                            self.itineraryCoords = json.array
                            
                            if(self.itineraryCoords != nil)
                            {
                                
                                //for each itinerary item, save as itinerary item
                                //pass this itinerary item to the graph and reload
                                for var index=0; index<self.itineraryCoords!.count-1; index++ {
                                    
                                    let originAddress = self.itineraryAddresses![index]
                                    var originCoords = CLLocationCoordinate2D()
                                    
                                    originCoords = CLLocationCoordinate2D(latitude: Double(self.itineraryCoords![index].array![0].string!)!, longitude: Double(self.itineraryCoords![index].array![1].string!)!)
                                    
                                    let destinationAddress = self.itineraryAddresses![index+1]
                                    var destinationCoords = CLLocationCoordinate2D()
                                    
                                    destinationCoords = CLLocationCoordinate2D(latitude: Double(self.itineraryCoords![index+1].array![0].string!)!, longitude: Double(self.itineraryCoords![index+1].array![1].string!)!)
                    
                                    self.showDirections(originAddress.string!, origCoords: originCoords, destAddress: destinationAddress.string!, destCoords: destinationCoords)
                                }
                            }
                    }
                }
        }

    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //LocationUpdater
    func locationManager(manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        
        //set new location to current
        let location = locations.last
        self.currentLocation = CLLocationCoordinate2DMake(location!.coordinate.latitude, location!.coordinate.longitude)
        
        //test method for current location
        CLGeocoder().reverseGeocodeLocation(manager.location!) { (placemarks, error) -> Void in
            
            if error != nil
            {
                //print("Error: " + error!.localizedDescription, terminator: "\n")
                return
            }
            
            if placemarks!.count > 0
            {
                let pm = placemarks![0] as! CLPlacemark
                //self.displayLocationInfo(pm)
            }
        }
        
    }
    
    func displayLocationInfo(placemark: CLPlacemark) {
        
        print(placemark.locality, terminator: "\n")
        print(placemark.postalCode, terminator: "\n")
        print(placemark.administrativeArea, terminator: "\n")
        print(placemark.country, terminator: "\n")
        
    }
    
    func locationManager(manager: CLLocationManager, didFailWithError error: NSError) {
        print("Error: " + error.localizedDescription, terminator: "\n")
    }
    
    //calculate route between two points, output as map overlay
    func showDirections( origAddress: String, origCoords: CLLocationCoordinate2D, destAddress: String, destCoords: CLLocationCoordinate2D) {
        
        let origAddressArray = String(origAddress).componentsSeparatedByString(",")
        let destAddressArray = String(destAddress).componentsSeparatedByString(",")
        
        let request = MKDirectionsRequest()
        request.source = MKMapItem(placemark: MKPlacemark(coordinate: (origCoords), addressDictionary: [String(CNPostalAddressStreetKey): origAddressArray[0]]))
        request.destination = MKMapItem(placemark: MKPlacemark(coordinate: (destCoords), addressDictionary: [String(CNPostalAddressStreetKey): destAddressArray[0]]))
        request.transportType = .Automobile
        request.requestsAlternateRoutes = false
        
        let directions = MKDirections(request: request)
        
        directions.calculateDirectionsWithCompletionHandler
            {
                (response, error) -> Void in
                
                if let routes = response?.routes where response?.routes.count > 0 && error == nil
                {
                    self.showRoute(response!)
                    let route : MKRoute = routes[0]
                    
                    //distance calculated from the request
                    print(route.distance)
                    
                    //travel time calculated from the request
                    print(route.expectedTravelTime)
                    
                    let formatter = NSNumberFormatter()
                    formatter.minimumFractionDigits = 2
                    formatter.roundingMode = .RoundDown
                    formatter.maximumFractionDigits = 2
                    
                    let destMarker = Location(
                        title: origAddressArray[0],
                        subtitle: String(formatter.stringFromNumber(route.expectedTravelTime/60)!),
                        coordinate: origCoords)
                    
                    self.mapView.addAnnotation(destMarker)
                }
        }
    }
    
    func showRoute(response: MKDirectionsResponse) {
        
        for route in response.routes as! [MKRoute] {
            
            mapView.addOverlay(route.polyline,
                level: MKOverlayLevel.AboveRoads)
        }
    }
    
}
