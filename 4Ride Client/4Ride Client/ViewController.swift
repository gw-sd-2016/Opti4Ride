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

class ViewController: UIViewController{

    @IBOutlet weak var mapView: MKMapView!
    
    func setDefaultRegion() {
        
        let location = CLLocationCoordinate2D(latitude: 38.899571, longitude: -77.048275)
        let region = MKCoordinateRegionMakeWithDistance(location, 1000.0, 1400.0)
        mapView.setRegion(region, animated: true)
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        var message = "student"
        mapView.delegate = self
        setDefaultRegion();
        
        Alamofire.request(.GET, "http://localhost:8080/4RideServlet/Servlet")
            .response { request, response, data, error in
                print(response)
                print(data)
                
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

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //Actions
    @IBAction func requestDelegate(sender: AnyObject) {
        //request to algorithm
    }
    


}

