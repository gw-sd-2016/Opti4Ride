//
//  ViewController.swift
//  4Ride Client
//
//  Created by Randy Fitzmorris on 9/20/15.
//  Copyright © 2015 Randy Fitzmorris. All rights reserved.
//

import UIKit
import MapKit
import Alamofire
import SwiftyJSON

class ViewController: UIViewController, CLLocationManagerDelegate {

    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet weak var inactiveView: UIVisualEffectView!
    @IBOutlet weak var sliderValue: UILabel!
    @IBOutlet weak var destinationBar: UITextField!
    @IBOutlet weak var capacitySlider: UISlider!
    @IBOutlet weak var requestButton: UIButton!
    @IBOutlet weak var activeView: UIVisualEffectView!
    @IBOutlet weak var driverName: UILabel!
    @IBOutlet weak var driverDistance: UILabel!
    
    var sliderValues = [UILabel]()
    let locationManager = CLLocationManager()
    var currentLocation = CLLocationCoordinate2D()
    var origin = CLLocationCoordinate2D()
    var destination = CLLocationCoordinate2D()
    
    func setDefaultRegion() {
        
        let location = CLLocationCoordinate2D(latitude: 38.899571, longitude: -77.048275)
        let region = MKCoordinateRegionMakeWithDistance(location, 1000.0, 1400.0)
        mapView.setRegion(region, animated: true)
        
    }
    
    func clearMap() {
        let annotationsToRemove = self.mapView.annotations.filter { $0 !== mapView.userLocation }
        self.mapView.removeAnnotations( annotationsToRemove )
        
        let overlaysToRemove = self.mapView.overlays
        self.mapView.removeOverlays(overlaysToRemove)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.locationManager.delegate = self
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        self.locationManager.requestWhenInUseAuthorization()
        self.locationManager.startUpdatingLocation()
        
        mapView.delegate = self
        setDefaultRegion();
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
        self.mapView.showsUserLocation = true
        self.mapView.showsTraffic = true
        self.activeView.hidden = true
        self.mapView.showsScale = true
        loadAllVehicles();
    }
    
    func loadAllVehicles() {
        let paras = [
            "DeviceType": "Student",
            "RequestType": "VehicleLocations"
        ]
        
        Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
            .response { request, response, data, error in
                //print(response)
                //print(data)
                
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
        
        for var i=0; i < 6; i++ {
            sliderValues[i].hidden = true
        }
        sliderValues[Int(capacity)-1].hidden = false
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //LocationUpdater
    func locationManager(manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        
        let location = locations.last
        self.currentLocation = CLLocationCoordinate2DMake(location!.coordinate.latitude, location!.coordinate.longitude)
        
        CLGeocoder().reverseGeocodeLocation(manager.location!) { (placemarks, error) -> Void in
        
            if error != nil
            {
                //print("Error: " + error!.localizedDescription, terminator: "\n")
                return
            }
            
            if placemarks!.count > 0
            {
                let pm = placemarks![0] as! CLPlacemark
                self.displayLocationInfo(pm)
            }
        }
        
    }
    
    func displayLocationInfo(placemark: CLPlacemark) {
        
        //self.locationManager.stopUpdatingLocation()
        /*
        print(placemark.locality, terminator: "\n")
        print(placemark.postalCode, terminator: "\n")
        print(placemark.administrativeArea, terminator: "\n")
        print(placemark.country, terminator: "\n")
        */
        
    }
    
    func locationManager(manager: CLLocationManager, didFailWithError error: NSError) {
        print("Error: " + error.localizedDescription, terminator: "\n")
    }
    
    func showDirections( orig: CLLocationCoordinate2D, dest: CLLocationCoordinate2D) {
        
        let currentPlace: MKPlacemark = MKPlacemark(coordinate: orig, addressDictionary: nil)
        let place: MKPlacemark = MKPlacemark(coordinate: dest, addressDictionary: nil)
        
        let start = Vehicle(title: "start",
            capacity: 0,
            currentCapacity: 0,
            type: "Origin",
            coordinate: CLLocationCoordinate2D(latitude: orig.latitude, longitude: orig.longitude))
        
        let end = Vehicle(title: "end",
            capacity: 0,
            currentCapacity: 0,
            type: "Destination",
            coordinate: CLLocationCoordinate2D(latitude: dest.latitude, longitude: dest.longitude))
        
        
        var request = MKDirectionsRequest()
        request.source = MKMapItem(placemark: currentPlace)
        request.destination = MKMapItem(placemark: place)
        request.transportType = MKDirectionsTransportType.Any
        request.requestsAlternateRoutes = true
        var directions: MKDirections = MKDirections(request: request)
        
        directions.calculateDirectionsWithCompletionHandler { [unowned self] response, error in
            guard let unwrappedResponse = response else { return }
            
            for route in unwrappedResponse.routes {
                self.mapView.addAnnotation(start)
                self.mapView.addAnnotation(end)
                self.mapView.addOverlay(route.polyline)
            }
        }
        
    }
    
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
    @IBAction func pickupRequestDelegate(sender: AnyObject) {
        //request to algorithm
        
        origin = CLLocationCoordinate2DMake(currentLocation.latitude, currentLocation.longitude)
        let passengers = capacitySlider.value;
        
        CLGeocoder().geocodeAddressString(self.destinationBar.text!) { (placemark, error) -> Void in
            
            print(self.destinationBar.text)
            
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
            
            let paras = [
                "DeviceType": "Student",
                "RequestType": "Pickup",
                "Origin": String(self.origin.latitude) + " " + String(self.origin.longitude),
                "Destination": String(self.destination.latitude) + " " + String(self.destination.longitude),
                "Passengers": String(passengers)
            ]
            
            print("sent destination:")
            print(String(self.destination.latitude) + " " + String(self.destination.longitude))
            
            Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
                .response { request, response, data, error in
                    //print(response)
                    //print(data)
                    
                    let json = JSON(data: data!)
                    
                    if(data == "max_capacity") {
                        //alert user MAX CAPACITY
                        //UIAlert
                    }
                    else {
                        self.clearMap()
                        self.mapView.delegate = self
                        
                        let vehicleLoc = CLLocationCoordinate2D(latitude: json["location"][0].doubleValue,
                                                                longitude: json["location"][1].doubleValue)
                        
                        let assignment = Vehicle(title: json["driverName"].string!,
                            capacity: json["capacity"].intValue,
                            currentCapacity: json["currentCapacity"].intValue,
                            type: "Vehicle",
                            coordinate: vehicleLoc)
                        
                        self.driverName.text = json["driverName"].string!
                        self.driverDistance.text = String(self.distanceTo(vehicleLoc)) + " miles away"
                        
                        self.mapView.addAnnotation(assignment)
                        self.showDirections(self.origin, dest: self.destination)
                        
                        self.inactiveView.hidden = true
                        self.activeView.hidden = false
                    }
            }
            
        }
    }

    @IBAction func cancelRequest(sender: AnyObject) {
        
        let paras = [
            "DeviceType": "Student",
            "RequestType": "Cancel",
            "DriverName": self.driverName.text!,
            "PassengerCount": String(self.capacitySlider.value),
            "Origin": String(self.origin.latitude) + " " + String(self.origin.longitude),
            "Destination": String(self.destination.latitude) + " " + String(self.destination.longitude)
        ]
        
        Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
            .response { request, response, data, error in
                //print(response)
                print(data)
                
                let json = JSON(data: data!)
                print(json)
                
                if(String(json[0]) == "Cancel Complete") {
                    self.activeView.hidden = true
                    self.inactiveView.hidden = false
                    self.clearMap()
                    self.loadAllVehicles()
                }
                else {
                    let alertView = UIAlertController(title: "Cancellation Error", message: "Our server was unable to service your request at this time.", preferredStyle: .Alert)
                    alertView.addAction(UIAlertAction(title: "Ok", style: .Default, handler: nil))
                    self.presentViewController(alertView, animated: true, completion: nil)
                }
        }
 
    }
}

