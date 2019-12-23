package app.studio.android.com.android_participant_app;

import java.util.Random;

public class ApplicationParameters {
    static String hostname = "http://tutorial-network.zapto.org";
    static String port = "3000";
    static String auth0hostname = "https://tutorial-network.auth0.com";

    static String passport_auth_url = hostname + ":" + port + "/auth/auth0";
    static String test_api_url = hostname + ":" + port + "/api/system/ping";
    static String explorer_api_url = hostname + ":" + port + "/explorer";
    static String card_get_api_url = hostname + ":" + port + "/api/wallet";
    static String card_import_api_url = hostname + ":" + port + "/api/wallet/import?name=";
    static String asset_Parcel_api_url = hostname + ":" + port + "/api/org.example.mynetwork.Parcel";
    static String asset_Unit_api_url = hostname + ":" + port + "/api/org.example.mynetwork.Unit";
    static String get_trans_orgId_api_url = hostname + ":" + port + "/api/org.example.mynetwork.TransportComp";
    static String get_coop_orgId_api_url = hostname + ":" + port + "/api/org.example.mynetwork.Cooperative";
    static String get_seller_orgId_api_url = hostname + ":" + port + "/api/org.example.mynetwork.Seller";
    static String transaction_AddUnitToParcel_api_url = hostname + ":" + port + "/api/org.example.mynetwork.AddUnitToParcel";
    static String transaction_Trade_api_url = hostname + ":" + port + "/api/org.example.mynetwork.Trade";
    static String transaction_PutParcelIntoStock_api_url = hostname + ":" + port + "/api/org.example.mynetwork.PutParcelIntoStock";
    static String transaction_ForSale_api_url = hostname + ":" + port + "/api/org.example.mynetwork.ForSale";
    static String transaction_Sold_api_url = hostname + ":" + port + "/api/org.example.mynetwork.Sold";
    static String logout_REST_api_url = hostname + ":" + port + "/auth/logout";
    static String logout_Auth0_api_url = auth0hostname + "/v2/logout";

    public static String GetRandomBoundaryString() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = 16;
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}