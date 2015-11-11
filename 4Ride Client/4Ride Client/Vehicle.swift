//
//  Vehicle.swift
//  4Ride Client
//
//  Created by Randy Fitzmorris on 11/9/15.
//  Copyright © 2015 Randy Fitzmorris. All rights reserved.
//

import Foundation
import MapKit

class Vehicle: NSObject, MKAnnotation {
    let title: String!
    let subtitle: String!
    let coordinate: CLLocationCoordinate2D
    
    init(title: String, capacity: Int, currentCapacity: Int, coordinate: CLLocationCoordinate2D) {
        self.title = title
        self.subtitle = String(currentCapacity) + " seats available"
        self.coordinate = coordinate
    }
}