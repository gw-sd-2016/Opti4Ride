//
//  VehicleMapView.swift
//  4Ride Student
//
//  Created by Randy Fitzmorris on 11/9/15.
//  Copyright © 2015 Randy Fitzmorris. All rights reserved.
//

import Foundation
import MapKit

extension ViewController: MKMapViewDelegate {

    //manage adding pins to map, recycling pins when possible
    func mapView(mapView: MKMapView!, viewForAnnotation annotation: MKAnnotation!) -> MKAnnotationView! {
        if let annotation = annotation as? Vehicle {
            let identifier = "pin"
            var view: MKPinAnnotationView
            if let dequeuedView = mapView.dequeueReusableAnnotationViewWithIdentifier(identifier)
                as? MKPinAnnotationView {
                    dequeuedView.annotation = annotation
                    view = dequeuedView
                    view.pinTintColor = annotation.pinTintColor()
            } else {
                view = MKPinAnnotationView(annotation: annotation, reuseIdentifier: identifier)
                view.pinTintColor = annotation.pinTintColor()
                view.canShowCallout = true
                view.calloutOffset = CGPoint(x: -5, y: 5)
                view.rightCalloutAccessoryView = UIButton(type: .DetailDisclosure) as UIView
            }
            return view
        }
        return nil
    }
    
    //manage adding overlays to map
    func mapView(mapView: MKMapView, rendererForOverlay overlay: MKOverlay) -> MKOverlayRenderer {
        let renderer = MKPolylineRenderer(polyline: overlay as! MKPolyline)
        renderer.strokeColor = UIColor.blueColor()
        return renderer
    }

}
