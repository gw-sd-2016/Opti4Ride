//
//  ViewController.swift
//  4Ride Client
//
//  Created by Randy Fitzmorris on 9/20/15.
//  Copyright © 2015 Randy Fitzmorris. All rights reserved.
//

import UIKit
import CoreLocation
import MapKit
import Alamofire
import SwiftyJSON

class ViewController: UIViewController, CLLocationManagerDelegate {

    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet weak var requestButton: UIButton!
    @IBOutlet weak var idleView: UIStackView!
    @IBOutlet weak var activeView: UIStackView!
    @IBOutlet weak var sliderValue: UILabel!
    @IBOutlet weak var capacitySlider: UISlider!
    @IBOutlet weak var addressBar: UITextField!
    
    var sliderValues = [UILabel]()
    var paras = [String: String]()
    
    var locationManager = CLLocationManager()
    var currentLocation: CLLocationCoordinate2D!
    
    func setDefaultRegion() {
        
        let location = CLLocationCoordinate2D(latitude: 38.899571, longitude: -77.048275)
        let region = MKCoordinateRegionMakeWithDistance(location, 1000.0, 1400.0)
        mapView.setRegion(region, animated: true)
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        let carCapacity = CGFloat(6);
        activeView.hidden = true
        mapView.delegate = self
        mapView.showsUserLocation = true
        setDefaultRegion();
        
        //initalize location manager
        self.locationManager.requestAlwaysAuthorization() 
        self.locationManager.requestWhenInUseAuthorization()
        
        if CLLocationManager.locationServicesEnabled() {
            locationManager.delegate = self
            locationManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
            locationManager.startUpdatingLocation()
        }
        
        let labelSpacing = CGFloat((UIScreen.mainScreen().bounds.width-30)/5)
        var xPosition = CGFloat(CGRectGetMinX(UIScreen.mainScreen().bounds)+15)
        let labelWidth = CGFloat(10)
        let yPosition = CGFloat(CGRectGetMidY(capacitySlider.frame))
        
        for var i = 0; i < 6; i++ {
            
            var label = UILabel(frame: CGRectMake(xPosition - labelWidth/2, (yPosition-10), labelWidth, 20))
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
        
        //default location
        currentLocation = CLLocationCoordinate2D(latitude: 38.899591, longitude: -77.049276)
        
        paras["DeviceType"] = "Student"
        paras["RequestType"] = "VehicleLocations"
        
        Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
            .response { request, response, data, error in
                //print(response)
                //print(data)
                
                let json = JSON(data: data!)
                print(json)
               
                for (key, subJson) in json {
                    //show active vehicles on map
                    let tempVehicle = Vehicle(title: subJson["driverName"].string!,
                        capacity: subJson["capacity"].intValue,
                        currentCapacity: subJson["currentCapacity"].intValue,
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
    
    //Gets current device location
    func locationManager(manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        var locValue:CLLocationCoordinate2D = manager.location!.coordinate
        print("locations = \(locValue.latitude) \(locValue.longitude)")
    }
    
    //Vehicle Request Action
    @IBAction func requestDelegate(sender: AnyObject) {
        paras["RequestType"] = "Pickup"
        paras["Address"] = addressBar.text
        paras["Capacity"] = String(capacitySlider.value)
        
        Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
            .response { request, response, data, error in
                //print(response)
                //print(data)
        }
        
    }

}

