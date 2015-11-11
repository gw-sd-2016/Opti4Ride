//
//  Meal.swift
//  FoodTracker
//
//  Created by Jane Appleseed on 5/26/15.
//  Copyright © 2015 Apple Inc. All rights reserved.
//  See LICENSE.txt for this sample’s licensing information.
//

import UIKit
import MapKit

class Meal {
    // MARK: Properties
    
    var photo: UIImage?
    var address: String
    var location: CLLocationCoordinate2D

    // MARK: Initialization
    
    init?(photo: UIImage?, address: String, location: CLLocationCoordinate2D) {
        // Initialize stored properties.
        self.photo = photo
        self.address = address
        self.location = location
        
        // Initialization should fail if there is no address
        if address.isEmpty {
            return nil
        }
    }

}