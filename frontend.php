<?php

$ip = "127.0.0.1";
$port = "3001";
$uid = "";

function startsWith ($string, $startString) 
{ 
    $len = strlen($startString); 
    return (substr($string, 0, $len) === $startString); 
} 

ob_start();
if (!empty($_GET)) {
	if (!($_GET["uid"] == "")){
        $responseJsonStr = "";
        
		$uid = $_GET["uid"];
        header('Access-Control-Allow-Origin: *');
        $UnitUrl = "http://" . $ip . ":" . $port . "/api/org.example.mynetwork.Unit/" . $uid;
        $UnitResponse = file_get_contents($UnitUrl);
        $UnitJSON = json_decode($UnitResponse);
        
        $UStatus = $UnitJSON->{'uStatus'};
        $UCreatedDate = $UnitJSON->{'createdDate'};
        $USellingDate = $UnitJSON->{'sellingDate'};
        $USoldDate = $UnitJSON->{'soldDate'};
        $ParcelId = $UnitJSON->{'parcelId'};
        
        if ($ParcelId == 'N/A') {
            $responseJsonStr = $responseJsonStr . '{ "parcelId": "" }';
        }
        else {
            $UCreatedDate = str_replace("-", "/", $UCreatedDate);
            $UCreatedDate = str_replace("T", " ", $UCreatedDate);
            $UCreatedDate = str_replace(substr($UCreatedDate, strrpos($UCreatedDate, ".")), "", $UCreatedDate);
            $responseJsonStr = $responseJsonStr . '{ ';
            $responseJsonStr = $responseJsonStr . '"createdDate": "' . $UCreatedDate . '", ';
            
            if ($UStatus == 'DELIVERING' || $UStatus == 'INSTOCK' || $UStatus == 'SELLING' || $UStatus == 'SOLD') {
                $responseJsonStr = $responseJsonStr . '"parcelId": "' . $ParcelId . '" , ';
                $responseJsonStr = $responseJsonStr . '"uStatus": "' . $UStatus . '", ';
                $AddUnitToParcelUrl = "http://" . $ip . ":" . $port . "/api/org.example.mynetwork.AddUnitToParcel?filter=%7B%22where%22%3A%7B%22parcel%22%3A%22resource%3Aorg.example.mynetwork.Parcel%23" . $ParcelId . "%22%7D%7D";
                $AddUnitToParcelResponse = file_get_contents($AddUnitToParcelUrl);
                $AddUnitToParcelJSON = json_decode($AddUnitToParcelResponse);
                $AddUnitToParcelSubmittedBy = $AddUnitToParcelJSON[0]->{'submittedBy'};
                $AddUnitToParcelSubmittedBy = str_replace("resource:org.example.mynetwork.Organization#", "", $AddUnitToParcelSubmittedBy);
                $AddUnitToParcelTimeStamp = $AddUnitToParcelJSON[0]->{'timestamp'};
                $AddUnitToParcelTimeStamp = str_replace("-", "/", $AddUnitToParcelTimeStamp);
                $AddUnitToParcelTimeStamp = str_replace("T", " ", $AddUnitToParcelTimeStamp);
                $AddUnitToParcelTimeStamp = str_replace(substr($AddUnitToParcelTimeStamp, strrpos($AddUnitToParcelTimeStamp, ".")), "", $AddUnitToParcelTimeStamp);

                $COOPInfoUrl = "http://" . $ip . ":" . $port . "/api/org.example.mynetwork.Cooperative/" . $AddUnitToParcelSubmittedBy;
                $COOPInfoResponse = file_get_contents($COOPInfoUrl);
                $COOPInfoJSON = json_decode($COOPInfoResponse);
                $COOPInfoDescription = $COOPInfoJSON->{'description'};

                $responseJsonStr = $responseJsonStr . '"AddUnitToParcelPlace" : "' . $COOPInfoDescription . '", ';
                $responseJsonStr = $responseJsonStr . '"AddUnitToParcelTime" : "' . $AddUnitToParcelTimeStamp . '", ';

                $TradeUrl = "http://" . $ip . ":" . $port . "/api/queries/QryTrade?qryParcelId=resource%3Aorg.example.mynetwork.Parcel%23" . $ParcelId;
                $responseJsonStr = $responseJsonStr . '"Trade" : ';
                $TradeResponse = file_get_contents($TradeUrl);
                if ($TradeResponse == "[]") {
                    $responseJsonStr = $responseJsonStr . '["N/A"]';
                }
                else {
                    $responseJsonStr = $responseJsonStr . '[ ';
                    $TradeJSONArr = json_decode($TradeResponse);
                    for ($index = 0; $index < count($TradeJSONArr); $index++){
                        $responseJsonStr = $responseJsonStr . '{ ';
                        
                        $TradeSubmittedBy = $TradeJSONArr[$index]->{'submittedBy'};
                        $TradeSubmittedBy = str_replace("resource:org.example.mynetwork.Organization#", "", $TradeSubmittedBy);
                        if (startsWith($TradeSubmittedBy, "COOP")) {
                            $COOPInfoUrl2 = "http://" . $ip . ":" . $port . "/api/org.example.mynetwork.Cooperative/" . $TradeSubmittedBy;
                            $COOPInfoResponse2 = file_get_contents($COOPInfoUrl2);
                            $COOPInfoJSON2 = json_decode($COOPInfoResponse2);
                            $COOPInfoDescription2 = $COOPInfoJSON2->{'description'};
                            $TradeSubmittedBy = $COOPInfoDescription2;
                        }
                        else if (startsWith($TradeSubmittedBy, "TRANS")) {
                            $TRANSInfoUrl2 = "http://" . $ip . ":" . $port . "/api/org.example.mynetwork.TransportComp/" . $TradeSubmittedBy;
                            $TRANSInfoResponse2 = file_get_contents($TRANSInfoUrl2);
                            $TRANSInfoJSON2 = json_decode($TRANSInfoResponse2);
                            $TRANSInfoDescription2 = $TRANSInfoJSON2->{'description'};
                            $TradeSubmittedBy = $TRANSInfoDescription2;
                        }
                        else if (startsWith($TradeSubmittedBy, "SELLER")) {
                            $SELLERInfoUrl2 = "http://" . $ip . ":" . $port . "/api/org.example.mynetwork.Seller/" . $TradeSubmittedBy;
                            $SELLERInfoResponse2 = file_get_contents($SELLERInfoUrl2);
                            $SELLERInfoJSON2 = json_decode($SELLERInfoResponse2);
                            $SELLERInfoDescription2 = $SELLERInfoJSON2->{'description'};
                            $TradeSubmittedBy = $SELLERInfoDescription2;
                        }
                        
                        $TradeNewOwner = $TradeJSONArr[$index]->{'newOwner'};
                        $TradeNewOwner = str_replace("resource:org.example.mynetwork.Organization#", "", $TradeNewOwner);
                        if (startsWith($TradeNewOwner, "COOP")) {
                            $COOPInfoUrl2 = "http://" . $ip . ":" . $port . "/api/org.example.mynetwork.Cooperative/" . $TradeNewOwner;
                            $COOPInfoResponse2 = file_get_contents($COOPInfoUrl2);
                            $COOPInfoJSON2 = json_decode($COOPInfoResponse2);
                            $COOPInfoDescription2 = $COOPInfoJSON2->{'description'};
                            $TradeNewOwner = $COOPInfoDescription2;
                        }
                        else if (startsWith($TradeNewOwner, "TRANS")) {
                            $TRANSInfoUrl2 = "http://" . $ip . ":" . $port . "/api/org.example.mynetwork.TransportComp/" . $TradeNewOwner;
                            $TRANSInfoResponse2 = file_get_contents($TRANSInfoUrl2);
                            $TRANSInfoJSON2 = json_decode($TRANSInfoResponse2);
                            $TRANSInfoDescription2 = $TRANSInfoJSON2->{'description'};
                            $TradeNewOwner = $TRANSInfoDescription2;
                        }
                        else if (startsWith($TradeNewOwner, "SELLER")) {
                            $SELLERInfoUrl2 = "http://" . $ip . ":" . $port . "/api/org.example.mynetwork.Seller/" . $TradeNewOwner;
                            $SELLERInfoResponse2 = file_get_contents($SELLERInfoUrl2);
                            $SELLERInfoJSON2 = json_decode($SELLERInfoResponse2);
                            $SELLERInfoDescription2 = $SELLERInfoJSON2->{'description'};
                            $TradeNewOwner = $SELLERInfoDescription2;
                        }
                        
                        $TradeTimestamp = $TradeJSONArr[$index]->{'timestamp'};
                        $TradeTimestamp = str_replace("-", "/", $TradeTimestamp);
                        $TradeTimestamp = str_replace("T", " ", $TradeTimestamp);
                        $TradeTimestamp = str_replace(substr($TradeTimestamp, strrpos($TradeTimestamp, ".")), "", $TradeTimestamp);
                        $responseJsonStr = $responseJsonStr . '"submittedBy": "' . $TradeSubmittedBy . '", ';
                        $responseJsonStr = $responseJsonStr . '"newOwner": "' . $TradeNewOwner . '", ';
                        $responseJsonStr = $responseJsonStr . '"timestamp": "' . $TradeTimestamp . '" ';
                        $responseJsonStr = $responseJsonStr . ' }';
                        if ($index != count($TradeJSONArr) - 1) {
                            $responseJsonStr = $responseJsonStr . ', ';
                        }
                    }
                    $responseJsonStr = $responseJsonStr . ' ]';
                }
                
                if ($UStatus == 'INSTOCK' || $UStatus == 'SELLING' || $UStatus == 'SOLD') {
                    $PutParcelIntoStockUrl = "http://" . $ip . ":" . $port . "/api/queries/QryPutParcelIntoStock?qryParcelId=resource%3Aorg.example.mynetwork.Parcel%23" . $ParcelId;
                    $PutParcelIntoStockResponse = file_get_contents($PutParcelIntoStockUrl);
                    $PutParcelIntoStockJSON = json_decode($PutParcelIntoStockResponse);
                    $PutParcelIntoStockSubmittedBy = $PutParcelIntoStockJSON[0]->{'submittedBy'};
                    $PutParcelIntoStockSubmittedBy = str_replace("resource:org.example.mynetwork.Organization#", "", $PutParcelIntoStockSubmittedBy);
                    $PutParcelIntoStockTimeStamp = $PutParcelIntoStockJSON[0]->{'timestamp'};
                    $PutParcelIntoStockTimeStamp = str_replace("-", "/", $PutParcelIntoStockTimeStamp);
                    $PutParcelIntoStockTimeStamp = str_replace("T", " ", $PutParcelIntoStockTimeStamp);
                    $PutParcelIntoStockTimeStamp = str_replace(substr($PutParcelIntoStockTimeStamp, strrpos($PutParcelIntoStockTimeStamp, ".")), "", $PutParcelIntoStockTimeStamp);

                    $SELLERInfoUrl = "http://" . $ip . ":" . $port . "/api/org.example.mynetwork.Seller/" . $PutParcelIntoStockSubmittedBy;
                    $SELLERInfoResponse = file_get_contents($SELLERInfoUrl);
                    $SELLERInfoJSON = json_decode($SELLERInfoResponse);
                    $SELLERInfoDescription = $SELLERInfoJSON->{'description'};

                    $responseJsonStr = $responseJsonStr . ', "PutParcelIntoStockPlace" : "' . $SELLERInfoDescription . '", ';
                    $responseJsonStr = $responseJsonStr . '"PutParcelIntoStockTime" : "' . $PutParcelIntoStockTimeStamp . '" ';
                    
                    if ($UStatus == 'SELLING' || $UStatus == 'SOLD') {
                        $ForSaleUrl = "http://" . $ip . ":" . $port . "/api/queries/QryForSale?qryUnitId=resource%3Aorg.example.mynetwork.Unit%23" . $uid;
                        $ForSaleResponse = file_get_contents($ForSaleUrl);
                        $ForSaleJSON = json_decode($ForSaleResponse);
                        $ForSaleSubmittedBy = $ForSaleJSON[0]->{'submittedBy'};
                        $ForSaleSubmittedBy = str_replace("resource:org.example.mynetwork.Organization#", "", $ForSaleSubmittedBy);
                        $ForSaleTimeStamp = $ForSaleJSON[0]->{'timestamp'};
                        $ForSaleTimeStamp = str_replace("-", "/", $ForSaleTimeStamp);
                        $ForSaleTimeStamp = str_replace("T", " ", $ForSaleTimeStamp);
                        $ForSaleTimeStamp = str_replace(substr($ForSaleTimeStamp, strrpos($ForSaleTimeStamp, ".")), "", $ForSaleTimeStamp);

                        $SELLERInfoUrl = "http://" . $ip . ":" . $port . "/api/org.example.mynetwork.Seller/" . $ForSaleSubmittedBy;
                        $SELLERInfoResponse = file_get_contents($SELLERInfoUrl);
                        $SELLERInfoJSON = json_decode($SELLERInfoResponse);
                        $SELLERInfoDescription = $SELLERInfoJSON->{'description'};

                        $responseJsonStr = $responseJsonStr . ', "sellingDate" : "' . $USellingDate . '", ';
                        $responseJsonStr = $responseJsonStr . '"ForSalePlace" : "' . $SELLERInfoDescription . '", ';
                        $responseJsonStr = $responseJsonStr . '"ForSaleTime" : "' . $ForSaleTimeStamp . '" ';
                        
                        if ($UStatus == 'SOLD') {
                            $SoldUrl = "http://" . $ip . ":" . $port . "/api/queries/QrySold?qryUnitId=resource%3Aorg.example.mynetwork.Unit%23" . $uid;
                            $SoldResponse = file_get_contents($SoldUrl);
                            $SoldJSON = json_decode($SoldResponse);
                            $SoldSubmittedBy = $SoldJSON[0]->{'submittedBy'};
                            $SoldSubmittedBy = str_replace("resource:org.example.mynetwork.Organization#", "", $SoldSubmittedBy);
                            $SoldTimeStamp = $SoldJSON[0]->{'timestamp'};
                            $SoldTimeStamp = str_replace("-", "/", $SoldTimeStamp);
                            $SoldTimeStamp = str_replace("T", " ", $SoldTimeStamp);
                            $SoldTimeStamp = str_replace(substr($SoldTimeStamp, strrpos($SoldTimeStamp, ".")), "", $SoldTimeStamp);

                            $SELLERInfoUrl = "http://" . $ip . ":" . $port . "/api/org.example.mynetwork.Seller/" . $SoldSubmittedBy;
                            $SELLERInfoResponse = file_get_contents($SELLERInfoUrl);
                            $SELLERInfoJSON = json_decode($SELLERInfoResponse);
                            $SELLERInfoDescription = $SELLERInfoJSON->{'description'};

                            $responseJsonStr = $responseJsonStr . ', "soldDate" : "' . $USoldDate . '", ';
                            $responseJsonStr = $responseJsonStr . '"SoldPlace" : "' . $SELLERInfoDescription . '", ';
                            $responseJsonStr = $responseJsonStr . '"SoldTime" : "' . $SoldTimeStamp . '" ';
                        }
                    }
                }
            }
            $responseJsonStr = $responseJsonStr . ' }';
        }
        
        //echo $responseJsonStr;
	}
	else {
		ob_clean();
		die();
	}
}
else {
	ob_clean();
	die();
}

?>

<!doctype html>
<html>
    <head>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
            table {
				border-collapse: collapse;
			}
			th, td, .tdAddUnitToParcel, .tdTrade, .tdPutParcelIntoStock, .tdForSale, .tdSold {
				border: 1px solid #ccc;
				padding: 5px;
				text-align: left;
			}
			@media only screen and (max-width: 640px)  {
				table, thead, tbody, th, td, .tdAddUnitToParcel, .tdTrade, .tdPutParcelIntoStock, .tdForSale, .tdSold, tr { 
					display: block; 
				}
				thead tr { 
					position: absolute;
					top: -9999px;
					left: -9999px;
				}
				
				tr { border: 1px solid #ccc; }
				
				td, .tdAddUnitToParcel, .tdTrade, .tdPutParcelIntoStock, .tdForSale, .tdSold { 
					/* Behave  like a "row" */
					border: none;
					border-bottom: 1px solid #eee; 
					position: relative;
					padding-left: 50%; 
				}
				
				td:before, .tdAddUnitToParcel:before, .tdTrade:before, .tdPutParcelIntoStock:before, .tdForSale:before, .tdSold:before { 
					/* Now like a table header */
					position: absolute;
					/* Top/left values mimic padding */
					left: 6px;
					width: 45%; 
					padding-right: 10px; 
					white-space: nowrap;
					font-weight: bold;
				}
				
				.tdAddUnitToParcel:nth-of-type(1):before { content: "Created on"; }
				.tdAddUnitToParcel:nth-of-type(2):before { content: "Added to parcel no."; }
				.tdAddUnitToParcel:nth-of-type(3):before { content: "Added to parcel on"; }
				.tdAddUnitToParcel:nth-of-type(4):before { content: "At"; }
                .tdTrade:nth-of-type(1):before { content: "Transported from"; }
				.tdTrade:nth-of-type(2):before { content: "To"; }
                .tdTrade:nth-of-type(3):before { content: "Transported on"; }
                .tdPutParcelIntoStock:nth-of-type(1):before { content: "Put into stock on"; }
				.tdPutParcelIntoStock:nth-of-type(2):before { content: "At"; }
                .tdForSale:nth-of-type(1):before { content: "Put on sale on"; }
				.tdForSale:nth-of-type(2):before { content: "At"; }
                .tdSold:nth-of-type(1):before { content: "Sold on"; }
				.tdSold:nth-of-type(2):before { content: "At"; }
			}
        </style>
        <script>
			window.onload = function(){
				ExtractData();
			}
            String.prototype.replaceAll = function (stringToFind, stringToReplace) {
                if (stringToFind === stringToReplace) return this;
                var temp = this;
                var index = temp.indexOf(stringToFind);
                while (index != -1) {
                    temp = temp.replace(stringToFind, stringToReplace);
                    index = temp.indexOf(stringToFind);
                }
                return temp;
            };
            function ExtractData(){
                document.getElementById("tbl").innerHTML = "";
				document.getElementById("stt").innerHTML = "";
                
				var response = '<?php echo $responseJsonStr; ?>';
				
                if (response == '{ "createdDate": "",  }') document.getElementById("stt").innerHTML = "ERROR: Unit does not exist, or there might be a problem with the network connection";
                else {
                    var data = JSON.parse(response);
                    if (data.uStatus != "SELLING" && data.uStatus != "SOLD") {
                        document.getElementById("stt").innerHTML = "This unit has not been available on sale yet";
                    }
                    else {
                        document.getElementById("stt").innerHTML = "Status: " + data.uStatus + "<br>Selling Date: " + data.sellingDate;
                        if (data.uStatus == "SOLD") document.getElementById("stt").innerHTML += "<br>Sold Date: " + data.soldDate;
                        document.getElementById("tbl").innerHTML += '<thead><tr>'
                            + '<th>Created on</th>'
                            + '<th>Added to parcel no.</th>'
                            + '<th>Added to parcel on</th>'
                            + '<th>At</th></tr></thead>'

                            + '<tbody><tr><td class="tdAddUnitToParcel">' + data.createdDate + '</td>'
                            + '<td class="tdAddUnitToParcel">' + data.parcelId + '</td>'
                            + '<td class="tdAddUnitToParcel">' + data.AddUnitToParcelTime + '</td>'
                            + '<td class="tdAddUnitToParcel">' + data.AddUnitToParcelPlace + '</td></tr></tbody><br><br>'

                            + '<thead><tr><th>Transported from</th>'
                            + '<th>To</th>'
                            + '<th>Transported on</th></tr></thead><tbody>';

                        var TradeDates = [];
                        for (var i = 0; i < data.Trade.length; i++) {
                            var tradedate = data.Trade[i].timestamp.replaceAll(' ', '').toString();
                            tradedate = tradedate.replaceAll('/', '').toString();
                            tradedate = tradedate.replaceAll(':', '').toString();
                            TradeDates.push(tradedate);
                        }
                        TradeDates = TradeDates.sort((a, b) => a - b);
                        for (var j = 0; j < TradeDates.length; j++) {
                            for (var i = 0; i < data.Trade.length; i++) {
                                var tradedate = data.Trade[i].timestamp.replaceAll(' ', '').replaceAll('/', '').replaceAll(':', '');
                                if (TradeDates[j] == tradedate) {
                                    document.getElementById("tbl").innerHTML += '<tr>'
                                        + '<td class="tdTrade">' + data.Trade[i].submittedBy + '</td>'
                                        + '<td class="tdTrade">' + data.Trade[i].newOwner + '</td>'
                                        + '<td class="tdTrade">' + data.Trade[i].timestamp + '</td></tr>';
                                }
                            }
                        }
                        document.getElementById("tbl").innerHTML += '</tbody><br><br>'

                            + '<thead><tr><th>Put into stock on</th>'
                            + '<th>At</th></tr></thead><tbody>'

                            + '<tbody><tr><td class="tdPutParcelIntoStock">' + data.PutParcelIntoStockTime + '</td>'
                            + '<td class="tdPutParcelIntoStock">' + data.PutParcelIntoStockPlace + '</td></tr></tbody><br><br>'

                            + '<thead><tr><th>Put on sale on</th>'
                            + '<th>At</th></tr></thead><tbody>'

                            + '<tbody><tr><td class="tdForSale">' + data.ForSaleTime + '</td>'
                            + '<td class="tdForSale">' + data.ForSalePlace + '</td></tr></tbody><br><br>';

                        if (data.uStatus == "SOLD") {
                            document.getElementById("tbl").innerHTML += ''
                            + '<thead><tr><th>Sold on</th>'
                            + '<th>At</th></tr></thead><tbody>'
                            + '<tbody><tr><td class="tdSold">' + data.SoldTime + '</td>'
                            + '<td class="tdSold">' + data.SoldPlace + '</td></tr></tbody><br><br>';
                        }
                    }
                }
            }
        </script>
    </head>
    <body>
		Information for unit ID: <?php echo $uid; ?><br><br>
        <span id="stt"></span><br><br>
        <table id="tbl">
        </table>
    </body>
</html>