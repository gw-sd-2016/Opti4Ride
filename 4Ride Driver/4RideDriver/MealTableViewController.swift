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
    var locationManager: CLLocationManager!
    var currentLocation: CLLocationCoordinate2D!
    @IBOutlet var distanceReading: UITableView!
    

    override func viewDidLoad() {
        super.viewDidLoad()
        
        locationManager = CLLocationManager()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()

        //default location
        currentLocation = CLLocationCoordinate2D(latitude: 38.899591, longitude: -77.049276)
        
        // Load the sample data.
        loadItinerary()
    }
    
    func loadItinerary() {
        
        var message = "driver"
        
        Alamofire.request(.GET, "http://localhost:8080/4RideServlet/Servlet")
            .response { request, response, data, error in
                print(response)
                print(data)
                
                let json = JSON(data: data!)
                print(json)
                
                
                for (key, subJson) in json[0]["itinerary"] {
                    let meal = Meal(photo: UIImage(named: "defaultPhoto")!,
                        address: key,
                        location: CLLocationCoordinate2D(latitude: subJson[0].doubleValue, longitude: subJson[1].doubleValue))!
                    
                    
                    self.meals.append(meal)
                }
                
                self.tableView.reloadData()
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
        
        return cell
    }
    
    func locationManager(manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let location = locations.last as CLLocation!
        let currentLocation = CLLocationCoordinate2D(latitude: location.coordinate.latitude, longitude: location.coordinate.longitude)
        //print(location.coordinate.latitude)
        //print(location.coordinate.longitude)
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
