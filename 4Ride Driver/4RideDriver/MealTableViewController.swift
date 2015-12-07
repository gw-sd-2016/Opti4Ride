//
//  MealTableViewController.swift
//  FoodTracker
//
//  Created by Jane Appleseed on 5/27/15.
//  Copyright © 2015 Apple Inc. All rights reserved.
//  See LICENSE.txt for this sample’s licensing information.
//

import UIKit
import CoreLocation
import Alamofire
import SwiftyJSON

class MealTableViewController: UITableViewController, CLLocationManagerDelegate {
    // MARK: Properties
    
    var meals = [Meal]()
    let locationManager = CLLocationManager()
    var currentLocation = CLLocationCoordinate2D()
    @IBOutlet var distanceReading: UITableView!
    

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.locationManager.delegate = self
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        self.locationManager.requestWhenInUseAuthorization()
        self.locationManager.startUpdatingLocation()
        
        loadItinerary()
        var timer = NSTimer.scheduledTimerWithTimeInterval(10, target: self, selector: "itineraryUpdate", userInfo: nil, repeats: true)
    }
    
    func itineraryUpdate() {
        
      loadItinerary()
    }
    
    func loadItinerary() {
        
        self.meals = [Meal]()
    
        let paras = [
            "DeviceType": "Driver",
        ]
        
        Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
            .response { request, response, data, error in
                //print(response)
                //print(data)
                
                let json = JSON(data: data!)
                print(json["itinerary"])
                
                
                let itinerary = json["itinerary"].array
                
                for var index=0; index<3; index++ {
                    
                    let destinationAddress = itinerary![index]
                    var destinationCoords = CLLocationCoordinate2D()
                    
                    CLGeocoder().geocodeAddressString(String(destinationAddress)) { (placemark, error) -> Void in
                        
                        if error != nil
                        {
                            print("Error: " + error!.localizedDescription, terminator: "\n")
                            return
                        }
                        
                        if placemark!.count > 0
                        {
                            let pm = placemark![0] as! CLPlacemark
                            destinationCoords = CLLocationCoordinate2D(latitude: pm.location!.coordinate.latitude, longitude: pm.location!.coordinate.longitude)
                        }
                        
                        let addressArray = String(destinationAddress).componentsSeparatedByString(",")

                        let meal = Meal(photo: UIImage(named: "defaultPhoto")!,
                            address: String(addressArray[0]),
                            location: destinationCoords)
                
                        self.meals.append(meal!)
                        self.tableView.reloadData()
                    }
                }
        }
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return meals.count
    }

    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        // Table view cells are reused and should be dequeued using a cell identifier.
        let cellIdentifier = "MealTableViewCell"
        let cell = tableView.dequeueReusableCellWithIdentifier(cellIdentifier, forIndexPath: indexPath) as! MealTableViewCell
        
        // Fetches the appropriate meal for the data source layout.
        let meal = meals[indexPath.row]
        
        cell.nameLabel.text = meal.address
        cell.photoImageView.image = meal.photo
        cell.distance.text = distanceTo(meal.location)
        
        //cell.backgroundColor = UIColor.greenColor()
        
        return cell
    }
    
    //LocationUpdater
    func locationManager(manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        
        let location = locations.last
        self.currentLocation = CLLocationCoordinate2DMake(location!.coordinate.latitude, location!.coordinate.longitude)
        //print("current location")
        //print(location!.coordinate.latitude)
        //print(location!.coordinate.longitude)
        
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

}
