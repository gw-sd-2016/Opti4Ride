//
//  ItineraryItem.swift
//  4Ride Driver
//
//  Created by Randy Fitzmorris on 10/1/15.
//  Copyright © 2015 Randy Fitzmorris All rights reserved.
//

import UIKit
import MapKit

class ItineraryItem {
    // MARK: Properties
    
    var photo: UIImage?
    var shortAddress: String
    var address: String
    var location: CLLocationCoordinate2D

    // MARK: Initialization
    
    init?(photo: UIImage?, shortAddress: String, address: String, location: CLLocationCoordinate2D) {
        // Initialize stored properties.
        self.photo = photo
        self.shortAddress = shortAddress
        self.address = address
        self.location = location
        
        // Initialization should fail if there is no address
        if address.isEmpty {
            return nil
        }
    }

}