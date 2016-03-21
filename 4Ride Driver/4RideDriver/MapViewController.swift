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
import AddressBookUI

class MapController: UIViewController, CLLocationManagerDelegate, UITextFieldDelegate {
    
    @IBOutlet weak var mapView: MKMapView!
    
    var itinerary: [JSON]?
    let locationManager = CLLocationManager()
    var currentLocation = CLLocationCoordinate2D()
    var origin = CLLocationCoordinate2D()
    var destination = CLLocationCoordinate2D()
    
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
            "RequestType": "LoadItinerary"
        ]
        
        Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
            .response { request, response, data, error in
                
                let json = JSON(data: data!)
                print(json["itinerary"])
                
                self.itinerary = json["itinerary"].array
                
                if(self.itinerary != nil)
                {
                    self.clearMap()
                    //for each itinerary item, save as itinerary item
                    //pass this itinerary item to the graph and reload
                    for var index=0; index<self.itinerary!.count-1; index++ {
                        
                        let longOriginAddress = String(self.itinerary![index])
                        let longDestinationAddress = String(self.itinerary![index+1])

                        CLGeocoder().geocodeAddressString(longOriginAddress) { (placemark, error) -> Void in
                            
                            if error != nil
                            {
                                print("Error: " + error!.localizedDescription, terminator: "\n")
                                return
                            }
                            
                            if placemark!.count > 0
                            {
                                let pmOrigin = placemark![0] as! CLPlacemark
                                
                                CLGeocoder().geocodeAddressString(longDestinationAddress) { (placemark, error) -> Void in
                                    
                                    if error != nil
                                    {
                                        print("Error: " + error!.localizedDescription, terminator: "\n")
                                        return
                                    }
                                    
                                    if placemark!.count > 0
                                    {
                                        let pmDestination = placemark![0] as! CLPlacemark
                                        self.showDirections(pmOrigin, dest: pmDestination)
                                    }
                                    
                                }
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
    func showDirections( orig: CLPlacemark, dest: CLPlacemark) {
        
        let request = MKDirectionsRequest()
        request.source = MKMapItem(placemark: MKPlacemark(coordinate: (orig.location?.coordinate)!, addressDictionary: orig.addressDictionary as! [String:AnyObject]?))
        request.destination = MKMapItem(placemark: MKPlacemark(coordinate: (dest.location?.coordinate)!, addressDictionary: dest.addressDictionary as! [String:AnyObject]?))
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
                        title: ABCreateStringWithAddressDictionary(dest.addressDictionary!, false),
                        subtitle: String(formatter.stringFromNumber(route.expectedTravelTime/60)!),
                        coordinate: (dest.location?.coordinate)!)
                    
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
    
    //Actions
    @IBAction func unwindToMealList(sender: UIStoryboardSegue) {
        if let sourceViewController = sender.sourceViewController as? ItineraryController, it = sourceViewController.itinerary {
            // Add a new meal.

        }
    }
    
}
