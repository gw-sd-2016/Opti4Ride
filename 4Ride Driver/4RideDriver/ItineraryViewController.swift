//
//  ItineraryViewController.swift
//  4Ride Driver
//
//  Created by Randy Fitzmorris on 10/1/15.
//  Copyright © 2015 Randy Fitzmorris All rights reserved.
//

import UIKit
import CoreLocation
import Alamofire
import SwiftyJSON

class ItineraryController: UITableViewController, CLLocationManagerDelegate {
    
    //initialize globals
    var itItems = [ItineraryItem]()
    let locationManager = CLLocationManager()
    var currentLocation = CLLocationCoordinate2D()
    @IBOutlet var distanceReading: UITableView!
    

    override func viewDidLoad() {
        super.viewDidLoad()
        
        //enable CoreLocation services
        self.locationManager.delegate = self
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        self.locationManager.requestWhenInUseAuthorization()
        self.locationManager.startUpdatingLocation()
        
        //load vehicle's current itinerary
        loadItinerary()
        
        //then load vehicle's current itinerary every 10 seconds
        var timer = NSTimer.scheduledTimerWithTimeInterval(10, target: self, selector: "itineraryUpdate", userInfo: nil, repeats: true)
    }
    
    func itineraryUpdate() {
      //loadItinerary()
    }
    
    func loadItinerary() {
        
        //empty itinerary items array for each load
        self.itItems = [ItineraryItem]()
    
        //paramaters that adhere to itinerary load request protocol
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
                
                //for each itinerary item, save as itinerary item
                //pass this itinerary item to the graph and reload
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

                        let meal = ItineraryItem(photo: UIImage(named: "defaultPhoto")!,
                            address: String(addressArray[0]),
                            location: destinationCoords)
                
                        self.itItems.append(meal!)
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

    //set # of table rows to # of itinerary items
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return itItems.count
    }

    //convert itinerary item to table cell and reload
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        // Table view cells are reused and should be dequeued using a cell identifier.
        let cellIdentifier = "MealTableViewCell"
        let cell = tableView.dequeueReusableCellWithIdentifier(cellIdentifier, forIndexPath: indexPath) as! ItineraryTableViewCell
        
        // Fetches the appropriate meal for the data source layout.
        let itItem = itItems[indexPath.row]
        
        cell.nameLabel.text = itItem.address
        cell.photoImageView.image = itItem.photo
        cell.distance.text = distanceTo(itItem.location)
        
        //cell.backgroundColor = UIColor.greenColor()
        
        return cell
    }
    
    //set current location to last updated location
    func locationManager(manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        
        let location = locations.last
        self.currentLocation = CLLocationCoordinate2DMake(location!.coordinate.latitude, location!.coordinate.longitude)
        
    }
    
    //determine distance from one point to another in miles
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
