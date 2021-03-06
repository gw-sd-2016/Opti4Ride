//
//  ViewController.swift
//  4Ride Student
//
//  Created by Randy Fitzmorris on 9/20/15.
//  Copyright © 2015 Randy Fitzmorris. All rights reserved.
//

import UIKit
import MapKit
import Alamofire
import SwiftyJSON
import Contacts

class ViewController: UIViewController, CLLocationManagerDelegate, UITextFieldDelegate {

    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet weak var inactiveView: UIVisualEffectView!
    @IBOutlet weak var waitView: UIVisualEffectView!
    @IBOutlet weak var activeView: UIVisualEffectView!
    @IBOutlet weak var capacitySlider: UISlider!
    @IBOutlet weak var sliderValue: UILabel!
    @IBOutlet weak var destinationBar: UITextField!
    @IBOutlet weak var newDestinationBar: UITextField!
    @IBOutlet weak var requestButton: UIButton!
    @IBOutlet weak var driverName: UILabel!
    @IBOutlet weak var driverNameActive: UILabel!
    @IBOutlet weak var driverDistance: UILabel!
    
    var sliderValues = [UILabel]()
    let locationManager = CLLocationManager()
    var currentLocation = CLLocationCoordinate2D()
    var origin = CLLocationCoordinate2D()
    var destination = CLLocationCoordinate2D()
    var originAddress = String()
    var destinationAddress = String()
    var requestID = String()
    var timer = NSTimer()
    
    //set default region displayed on map (GWU Foggy Bottom Campus)
    func setDefaultRegion() {
        let location = CLLocationCoordinate2D(latitude: 38.898322, longitude: -77.048451)
        let region = MKCoordinateRegionMakeWithDistance(location, 1000.0, 1400.0)
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
        
        newDestinationBar.delegate = self
        
        //add numeric value to slider, working but disabled for cleaner UI
        //must connect idleView to code (in inactiveView)
       /*
        let labelSpacing = CGFloat((UIScreen.mainScreen().bounds.width-30)/5)
        var xPosition = CGFloat(CGRectGetMinX(UIScreen.mainScreen().bounds)+15)
        let labelWidth = CGFloat(10)
        let yPosition = CGFloat(CGRectGetMidY(capacitySlider.frame))
        
        for var i = 0; i < 6; i++ {
            
            var label = UILabel(frame: CGRectMake(xPosition - labelWidth/2 - 8, (yPosition-10), labelWidth, 20))
            label.textAlignment = NSTextAlignment.Center
            
            label.adjustsFontSizeToFitWidth = true
            let index = i
            label.text = String(index+1)
            if(index+1 != Int(round(capacitySlider.value))) {
                label.hidden = true
            }
            
            sliderValues.append(label)
            idleView.addSubview(label)
            
            xPosition += labelSpacing;
        }
        */
        
        //enable map features
        self.mapView.showsUserLocation = true
        self.mapView.showsTraffic = true
        self.mapView.showsScale = true
        
        //hide active and wait views, initialize with inactive view (no current request)
        self.activeView.hidden = true
        self.waitView.hidden = true
        
        //load current in-service vehicles
        loadAllVehicles();
    }
    
    func loadAllVehicles() {
        
        //loadAllVehicles request params (protocol accepted by server)
        let paras = [
            "DeviceType": "Student",
            "RequestType": "VehicleLocations"
        ]
        
        //request all vehicles in service
        Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
            .response { request, response, data, error in
                
                let json = JSON(data: data!)
                //print(json)
                
                for (key, subJson) in json {
                    //show active vehicles on map
                    let tempVehicle = Vehicle(title: subJson["driverName"].string!,
                        capacity: subJson["capacity"].intValue,
                        currentCapacity: subJson["currentCapacity"].intValue,
                        type: "Vehicle",
                        coordinate: CLLocationCoordinate2D(latitude: subJson["location"][0].doubleValue, longitude: subJson["location"][1].doubleValue))
                    
                    self.mapView.addAnnotation(tempVehicle)
                }
                
        }
        
    }
    
    @IBAction func sliderChangeAction(sender: UISlider) {
        let capacity = round(sender.value)
        capacitySlider.value = capacity
        
        /*
        for var i=0; i < 6; i++ {
            sliderValues[i].hidden = true
        }
        
        sliderValues[Int(capacity)-1].hidden = false
        */
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
    //calculate route between two points, output as map overlay
    func showDirections( origAddress: String, origCoords: CLLocationCoordinate2D, destAddress: String, destCoords: CLLocationCoordinate2D, showRoute: Bool) {
        
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
                    if(showRoute == true) {
                        self.showRoute(response!)
                    }
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
                        title: destAddressArray[0],
                        subtitle: String(formatter.stringFromNumber(route.expectedTravelTime/60)!),
                        coordinate: destCoords)
                    
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
    
    //calculate distance between two points
    func distanceTo(location: CLLocationCoordinate2D) -> String {
        let oldLocation = CLLocation(latitude: currentLocation.latitude, longitude: currentLocation.longitude)
        let newLocation = CLLocation(latitude: location.latitude, longitude: location.longitude)
        
        let miles = oldLocation.distanceFromLocation(newLocation)*(0.000621371)
        
        let formatter = NSNumberFormatter()
        formatter.minimumFractionDigits = 2
        formatter.roundingMode = .RoundDown
        formatter.maximumFractionDigits = 2
        let formattedAmountString = formatter.stringFromNumber(miles)
        
        return formattedAmountString!
    }

    
    //Actions
    
    //request a pickup
    @IBAction func pickupRequestDelegate(sender: AnyObject) {
        
        //set origin location to current location when request is issued
        origin = CLLocationCoordinate2DMake(currentLocation.latitude, currentLocation.longitude)
        let passengers = capacitySlider.value;
        
        //get destination coordinates from address input, issue request
        CLGeocoder().geocodeAddressString(self.destinationBar.text!) { (placemark, error) -> Void in
            
            print(self.destinationBar.text)
            self.destinationAddress = self.destinationBar.text!
            if error != nil
            {
                print("Error: " + error!.localizedDescription, terminator: "\n")
                return
            }
            
            if placemark!.count > 0
            {
                let pm = placemark![0] as! CLPlacemark
                self.destination = CLLocationCoordinate2DMake(pm.location!.coordinate.latitude, pm.location!.coordinate.longitude)
            }
            
            //protocol parameters accepted by server
            let paras = [
                "DeviceType": "Student",
                "RequestType": "Pickup",
                "Origin": String(self.origin.latitude) + " " + String(self.origin.longitude),
                "Destination": String(self.destination.latitude) + " " + String(self.destination.longitude),
                "Passengers": String(passengers)
            ]
            
            //issue pickup request
            Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
                .response { request, response, data, error in
                    
                    let json = JSON(data: data!)
                    print(json)
                    
                    //alert user if server's pickup request handler fails
                    if(data == "pickup_request_failed") {
                        let alertView = UIAlertController(title: "Pickup Request Error", message: "Our server was unable to service your request at this time. Critical system error.", preferredStyle: .Alert)
                        alertView.addAction(UIAlertAction(title: "Ok", style: .Default, handler: nil))
                        self.presentViewController(alertView, animated: true, completion: nil)
                    }
                    //alert user if system saturated
                    if(data == "max_capacity") {
                        let alertView = UIAlertController(title: "Pickup Request Error", message: "Our server was unable to service your request at this time. Max student capacity reached.", preferredStyle: .Alert)
                        alertView.addAction(UIAlertAction(title: "Ok", style: .Default, handler: nil))
                        self.presentViewController(alertView, animated: true, completion: nil)
                    }
                    else {
                        //clear map, construct new itinerary
                        self.clearMap()
                        self.mapView.delegate = self
                        
                        //get request ID
                        self.requestID = json[0].stringValue
                        
                        //get assigned vehicle location
                        let vehicleLoc = CLLocationCoordinate2D(latitude: json[1][0].doubleValue,
                                                                longitude: json[1][1].doubleValue)
                        
                        //display assignment info in active view
                        self.driverName.text = json[2].string!
                        self.driverNameActive.text = json[2].string!
                        self.driverDistance.text = String(self.distanceTo(vehicleLoc)) + " miles away"
                        
                        //show route from request's origin to destination 
                        //origin represented as green pin
                        //destination represented as red pin
                        self.showDirections("Current Location", origCoords: self.origin, destAddress: self.destinationAddress, destCoords: self.destination, showRoute: true)
                        
                        self.showDirections("Current Location", origCoords: self.origin, destAddress: "4-RIDE Location", destCoords: vehicleLoc, showRoute: false)
                        
                        //switch to wait view
                        self.inactiveView.hidden = true
                        self.waitView.hidden = false
                        
                        self.timer = NSTimer.scheduledTimerWithTimeInterval(10, target: self, selector: "waitForVehicleArrival", userInfo: nil, repeats: true)
                    }
            }
            
        }
    }
    
    func waitForVehicleArrival() {
        
        if waitView.hidden == true
        {
            self.timer.invalidate()
        }
        else
        {
            //protocol parameters accepted by server
            let paras = [
                "DeviceType": "Student",
                "RequestType": "VehicleHasArrived",
                "RequestID": self.requestID
            ]
            //issue 'has arrived' request
            Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
                .response { request, response, data, error in
                    
                    let json = JSON(data: data!)
                    print(json)
                    
                    //if vehicle has arrived, return to inactive state
                    if(String(json[0]) == "True") {
                        //switch to active view
                        self.waitView.hidden = true
                        self.activeView.hidden = false
                        self.timer.invalidate()
                    }
                        //if 'has arrived' request fails, alert user
                    else if(String(json[0]) == "Request failed") {
                        let alertView = UIAlertController(title: "Vehical Arrival Check Error", message: "Our server was unable to check the status of your vehicle.", preferredStyle: .Alert)
                        alertView.addAction(UIAlertAction(title: "Ok", style: .Default, handler: nil))
                        self.presentViewController(alertView, animated: true, completion: nil)
                    }
            }
        }
    }
    

    //issue cancellation request to server
    @IBAction func cancelRequest(sender: AnyObject) {
        
        //parameters needed for cancellation protocol
        let paras = [
            "DeviceType": "Student",
            "RequestType": "Cancel",
            "RequestID": self.requestID
        ]
        
        //issue cancellation request
        Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
            .response { request, response, data, error in
                
                let json = JSON(data: data!)
                print(json)
                
                //if cancellation successful, return to inactive state
                if(String(json[0]) == "Cancel Complete") {
                    self.waitView.hidden = true
                    self.inactiveView.hidden = false
                    self.clearMap()
                    self.loadAllVehicles()
                }
                //if cancellation unsuccessful, alert user
                else {
                    let alertView = UIAlertController(title: "Cancellation Error", message: "Our server was unable to service your request at this time.", preferredStyle: .Alert)
                    alertView.addAction(UIAlertAction(title: "Ok", style: .Default, handler: nil))
                    self.presentViewController(alertView, animated: true, completion: nil)
                }
        }
 
    }
    
    @IBAction func earlyExitHandler(sender: AnyObject) {
        
        //parameters needed for early exit protocol
        let paras = [
            "DeviceType": "Student",
            "RequestType": "EarlyExit",
            "RequestID": self.requestID
        ]
        
        //issue early exit request
        Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
            .response { request, response, data, error in
                
                let json = JSON(data: data!)
                print(json)
                
                //if early drop off successful, return to inactive state
                if(String(json[0]) == "Request Completion Successful") {
                    self.activeView.hidden = true
                    self.inactiveView.hidden = false
                    self.clearMap()
                    self.loadAllVehicles()
                }
                    //if early drop off unsuccessful, alert user
                else {
                    let alertView = UIAlertController(title: "Early Drop Off Error", message: "Our server was unable to service your request at this time.", preferredStyle: .Alert)
                    alertView.addAction(UIAlertAction(title: "Ok", style: .Default, handler: nil))
                    self.presentViewController(alertView, animated: true, completion: nil)
                }
        }

    }
    
    func textFieldShouldReturn(textField: UITextField) -> Bool {
            textField.resignFirstResponder()
            return false
    }
    
    @IBAction func destinationChangeHandler(sender: AnyObject) {
        
        var oldDestination = CLLocationCoordinate2D()
        
        //get destination coordinates from address input, issue request
        CLGeocoder().geocodeAddressString(self.newDestinationBar.text!) { (placemark, error) -> Void in
            
            print(self.newDestinationBar.text)
            self.destinationAddress = self.newDestinationBar.text!
            
            if error != nil
            {
                print("Error: " + error!.localizedDescription, terminator: "\n")
                return
            }
            
            if placemark!.count > 0
            {
                let pm = placemark![0] as! CLPlacemark
                oldDestination = self.destination
                self.destination = CLLocationCoordinate2DMake(pm.location!.coordinate.latitude, pm.location!.coordinate.longitude)
            }
            
            //parameters needed for destination change protocol
            let paras = [
                "DeviceType": "Student",
                "RequestType": "DestinationChange",
                "RequestID": self.requestID,
                "NewDestination": self.newDestinationBar.text!
            ]
            
            //issue destination change request
            Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
                .response { request, response, data, error in
                    
                    let json = JSON(data: data!)
                    print(json)
                    
                    //if destination successful, load new itinerary on map
                    if(String(json[0]) == "Destination Change Successful") {
                        //clear map, add purple pin for assigned vehicle
                        self.clearMap()
                        self.mapView.delegate = self
                        
                        let vehicleLoc = CLLocationCoordinate2D(latitude: json["location"][0].doubleValue, longitude: json["location"][1].doubleValue)
                        
                        //show route from request's origin to destination
                        //origin represented as green pin
                        //destination represented as red pin
                        self.showDirections("Current Location", origCoords: self.origin, destAddress: self.destinationAddress, destCoords: self.destination, showRoute: true)
                        
                        self.showDirections("Current Location", origCoords: self.origin, destAddress: "4-RIDE Location", destCoords: vehicleLoc, showRoute: true)
                    }
                    //if destination change unsuccessful, alert user
                    else {
                        let alertView = UIAlertController(title: "Destination Change Error", message: "Our server was unable to service your request at this time.", preferredStyle: .Alert)
                        alertView.addAction(UIAlertAction(title: "Ok", style: .Default, handler: nil))
                        self.presentViewController(alertView, animated: true, completion: nil)
                    }
            }
            
        }
        
    }
    
}

