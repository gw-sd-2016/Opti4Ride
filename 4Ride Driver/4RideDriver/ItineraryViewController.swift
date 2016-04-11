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
    @IBOutlet var distanceReading: UITableView!
    
    var itItems = [ItineraryItem]()
    let locationManager = CLLocationManager()
    var currentLocation = CLLocationCoordinate2D()
    var driverName = String()
    var itineraryAddresses: [JSON]?
    var itineraryCoords: [JSON]?
    

    override func viewDidLoad() {
        super.viewDidLoad()
        
        //set driver
        self.driverName = "Mr. Dillard"
        
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
      loadItinerary()
    }
    
    func loadItinerary() {
        
        //empty itinerary items array for each load
        self.itItems = [ItineraryItem]()
        print(self.driverName)
    
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
                                for var index=0; index<self.itineraryCoords!.count; index++ {
                                    
                                    let destinationAddress = self.itineraryAddresses![index]
                                    var destinationCoords = CLLocationCoordinate2D()
                                    
                                    destinationCoords = CLLocationCoordinate2D(latitude: Double(self.itineraryCoords![index].array![0].string!)!, longitude: Double(self.itineraryCoords![index].array![1].string!)!)
                                    
                                    let addressArray = String(destinationAddress).componentsSeparatedByString(",")
                                        
                                    let it = ItineraryItem(photo: UIImage(named: "defaultPhoto")!,
                                        shortAddress: String(addressArray[0]),
                                        address: String(destinationAddress),
                                        location: destinationCoords)
                                        
                                    self.itItems.append(it!)
                                    self.tableView.reloadData()
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
        tableView.allowsSelection = true;
        
        // Table view cells are reused and should be dequeued using a cell identifier.
        let cellIdentifier = "ItineraryTableViewCell"
        let cell = tableView.dequeueReusableCellWithIdentifier(cellIdentifier, forIndexPath: indexPath) as! ItineraryTableViewCell
        cell.accessoryView = nil;
        
        // Fetches the appropriate itinerary for the data source layout.
        let itItem = itItems[indexPath.row]
        print(String(indexPath.row) + "\n")
        print(itItem.shortAddress + "\n")
        
        cell.nameLabel.text = itItem.shortAddress
        cell.photoImageView.image = itItem.photo
        cell.distance.text = distanceTo(itItem.location)
        
        //cell.backgroundColor = UIColor.greenColor()
        
        return cell
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let alertView = UIAlertController(title: "Service Complete!", message: "Proceed to next destination.", preferredStyle: .Alert)
        alertView.addAction(UIAlertAction(title: "Ok", style: .Default, handler: nil))
        self.presentViewController(alertView, animated: true, completion: nil)
        
        //get destination coordinates from address input, issue request
        CLGeocoder().geocodeAddressString(itItems[indexPath.row].address) { (placemark, error) -> Void in
            
            print(self.itItems[indexPath.row].address)
            var destination = CLLocationCoordinate2D()
            
            if error != nil
            {
                print("Error: " + error!.localizedDescription, terminator: "\n")
                return
            }
            
            if placemark!.count > 0
            {
                let pm = placemark![0] as! CLPlacemark
                destination = CLLocationCoordinate2DMake(pm.location!.coordinate.latitude, pm.location!.coordinate.longitude)
            }
            
            //parameters needed for completion request protocol
            let paras = [
                "DeviceType": "Driver",
                "RequestType": "CompletionRequest",
                "DriverName": self.driverName,
                "Destination": String(self.itItems[indexPath.row].address)
            ]
            
            //issue completion request
            Alamofire.request(.POST, "http://localhost:8080/4RideServlet/Servlet", parameters: paras)
                .response { request, response, data, error in
                    //print(response)
                    print(data)
                    
                    let json = JSON(data: data!)
                    print(json)
                    
                    //if completion request successful, return to inactive state
                    if(String(json[0]) == "Request Completion Successful") {
                        self.loadItinerary()
                    }
                        //if competion request unsuccessful, alert user
                    else {
                        let alertView = UIAlertController(title: "Completion Request Error", message: "Our server was unable to service your request at this time.", preferredStyle: .Alert)
                        alertView.addAction(UIAlertAction(title: "Ok", style: .Default, handler: nil))
                        self.presentViewController(alertView, animated: true, completion: nil)
                    }
            }
            
        }

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
