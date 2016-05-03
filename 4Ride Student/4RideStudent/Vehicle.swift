//
//  Vehicle.swift
//  4Ride Student
//
//  Created by Randy Fitzmorris on 11/9/15.
//  Copyright © 2015 Randy Fitzmorris. All rights reserved.
//

import Foundation
import MapKit

class Vehicle: NSObject, MKAnnotation {
    let title: String!
    let subtitle: String!
    let type: String!
    let coordinate: CLLocationCoordinate2D
    
    init(title: String, capacity: Int, currentCapacity: Int, type: String, coordinate: CLLocationCoordinate2D) {
        self.title = title
        self.subtitle = String(currentCapacity) + " seats available"
        self.type = type
        self.coordinate = coordinate
    }
    
    func pinTintColor() -> UIColor  {
        
        print(self.type)
        
        switch self.type {
        case "Vehicle":
            return UIColor.purpleColor()
        case "Origin":
            return UIColor.greenColor()
        case "Destination":
            return UIColor.redColor()
        default:
            return UIColor.redColor()
        }
    }
}