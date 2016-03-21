//
//  ItineraryViewCell.swift
//  4Ride Driver
//
//  Created by Randy Fitzmorris on 10/1/15.
//  Copyright © 2015 Randy Fitzmorris All rights reserved.
//

import UIKit

class ItineraryTableViewCell: UITableViewCell {
    // MARK: Properties

    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var photoImageView: UIImageView!
    @IBOutlet weak var distance: UILabel!

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func prepareForReuse() {
        self.nameLabel.text = ""
        self.distance.text = ""
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
