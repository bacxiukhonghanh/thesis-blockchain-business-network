/**
 * Track the add unit to parcel transaction
 * @param {org.example.mynetwork.AddUnitToParcel} u2p - the unit-to-parcel transaction need to be processed
 * @transaction
 */
async function addUnit2Parcel(u2p) {
    if (u2p.parcel.currQuantity < u2p.parcel.maxQuantity) {
        if (typeof u2p.parcel.units == 'undefined') u2p.parcel.units = new Array();
        for (var i = 0; i < u2p.unit.length; i++) {
            // set unit's parcelId = parcelId
            u2p.unit[i].parcelId = u2p.parcel.parcelId;
            // push this unit to the parcel it belongs to
            u2p.parcel.units.push(u2p.unit[i]);
            // update current units number
            u2p.parcel.currQuantity += 1;

            /** Update Unit asset then Parcel asset */
            // get the asset registry for the Unit asset
            let unitRegistry = await getAssetRegistry('org.example.mynetwork.Unit');
            // update the unit asset in the asset registry
            await unitRegistry.update(u2p.unit[i]);
            // get the asset registry for the Parcel asset
            let parcelRegistry = await getAssetRegistry('org.example.mynetwork.Parcel');
            // update the parcel asset in the asset registry
            await parcelRegistry.update(u2p.parcel);
        }
    } else {
        throw new Error("The number of units must not exceed the maximum quantity per parcel");
    }
}

/**
 * Track the trade of a parcel from one organization to another
 * @param {org.example.mynetwork.Trade} trade - the trade to be processed
 * @transaction
 */
async function tradeParcel(trade) {
  	for (var j = 0; j < trade.parcel.length; j++){
      	// update parcel owner to the new one
    	trade.parcel[j].owner = trade.newOwner;
      	// loop to update new owner for all units belongs to this parcel
        for (var i = 0; i < trade.parcel[j].units.length; i++) {
            trade.parcel[j].units[i].owner = trade.parcel[j].owner;
            // get the asset registry for the Unit asset
            let unitRegistry = await getAssetRegistry('org.example.mynetwork.Unit');
            // update the asset in the asset registry
            await unitRegistry.update(trade.parcel[j].units[i]);
        }
      	// change parcel's status to DELIVERING
        trade.parcel[j].pStatus = "DELIVERING";
        // get the asset registry for the Parcel asset
        let parcelRegistry = await getAssetRegistry('org.example.mynetwork.Parcel');
        // update the asset in the asset registry
        await parcelRegistry.update(trade.parcel[j]);
    }
}

/**
 * Track the put parcel into stock transaction of a unit product
 * @param {org.example.mynetwork.PutParcelIntoStock} pis - the pis transaction to be processed
 * @transaction
 */
async function putParcelIntoStock(pis) {
  	for (var j = 0; j < pis.parcel.length; j++){
     	// loop to update new status for all units belongs to this parcel
        for (var i = 0; i < pis.parcel[j].units.length; i++) {
          pis.parcel[j].units[i].uStatus = "INSTOCK";
            // get the asset registry for the Unit asset
            let unitRegistry = await getAssetRegistry('org.example.mynetwork.Unit');
            // update the asset in the asset registry
            await unitRegistry.update(pis.parcel[j].units[i]);
        }
        // change parcel's status to INSTOCK
        pis.parcel[j].pStatus = "INSTOCK";
        // get the asset registry for the Parcel asset
        let parcelRegistry = await getAssetRegistry('org.example.mynetwork.Parcel');
        // update the asset in the asset registry
        await parcelRegistry.update(pis.parcel[j]); 	
    }
}

/**
 * Track the for-sale transaction of a unit product
 * @param {org.example.mynetwork.ForSale} fs - the for-sale transaction to be processed
 * @transaction
 */
async function forSale(fs) {
  	for (var i = 0; i < fs.unit.length; i++) {
    	// update unit status to SELLING
        fs.unit[i].uStatus = "SELLING";
        // update unit sellingDate to current date
        var today = new Date();
        var dd = String(today.getDate()).padStart(2, '0');
        var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
        var yyyy = today.getFullYear();
        fs.unit[i].sellingDate = dd + '/' + mm + '/' + yyyy;
        // get the asset registry for the Unit asset
        let unitRegistry = await getAssetRegistry('org.example.mynetwork.Unit');
        // update the asset in the asset registry
        await unitRegistry.update(fs.unit[i]);
  	}
}

/**
 * Track the sold transaction of a unit product
 * @param {org.example.mynetwork.Sold} s - the sold transaction to be processed
 * @transaction
 */
async function sold(s) {
    for (var i = 0; i < s.unit.length; i++) {
        // update unit status to SOLD
        s.unit[i].uStatus = "SOLD";
        // update unit soldDate to current date
        var today = new Date();
        var dd = String(today.getDate()).padStart(2, '0');
        var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
        var yyyy = today.getFullYear();
        s.unit[i].soldDate = dd + '/' + mm + '/' + yyyy;
        // get the asset registry for the Unit asset
        let unitRegistry = await getAssetRegistry('org.example.mynetwork.Unit');
        // update the unit asset in the asset registry
        await unitRegistry.update(s.unit[i]);
    }
}