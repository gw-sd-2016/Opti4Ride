//
//  Location.swift
//  4Ride Driver
//
//  Created by Randy Fitzmorris on 4/2/16.
//
//

import Foundation
import MapKit

class Location: NSObject, MKAnnotation {
    let title: String!
    let subtitle: String!
    let coordinate: CLLocationCoordinate2D
    
    init(title: String, subtitle: String, coordinate: CLLocationCoordinate2D) {
        self.title = title
        self.subtitle = subtitle + " minutes"
        self.coordinate = coordinate
    }
    
    func pinTintColor() -> UIColor  {
        return UIColor.purpleColor()
    }
}
