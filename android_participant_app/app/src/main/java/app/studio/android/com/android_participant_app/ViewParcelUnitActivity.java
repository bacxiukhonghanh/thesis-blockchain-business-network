package app.studio.android.com.android_participant_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ViewParcelUnitActivity extends AppCompatActivity {
    String transaction = "";
    String orgId = "";
    String cookies = "";
    ViewParcelUnitListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_view_parcel_unit);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        transaction = extras.getString("TRANSACTION");
        orgId = extras.getString("ORG_ID");
        cookies = extras.getString("COOKIES");

        switch (transaction) {
            case "ViewParcel":
                DoHttpGetRequest(ApplicationParameters.asset_Parcel_api_url, transaction);
                break;
            case "ViewUnit":
                DoHttpGetRequest(ApplicationParameters.asset_Unit_api_url, transaction);
                break;
        }
    }

    public void DoHttpGetRequest(String url, final String operation) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Cookie", cookies);
        client.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == 401) {
                    ClearCookies(ViewParcelUnitActivity.this);
                    Toast.makeText(ViewParcelUnitActivity.this, "Session expired. Please login again",
                            Toast.LENGTH_LONG).show();
                    StartLoginActivity();
                }
                else {
                    Toast.makeText(ViewParcelUnitActivity.this, "Something's wrong. Response status code: " + statusCode + ". Response is: " + responseString,
                            Toast.LENGTH_LONG).show();
                    ShowAlertDialog("Something's wrong. Response status code: " + statusCode + ". Response is: " + responseString);
                    finish();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONArray jsonArr = new JSONArray(responseString);
                    if (jsonArr.length() > 0) {
                        ArrayList<String> result = new ArrayList<String>();
                        if (operation.equals("ViewParcel")) {
                            for (int i = 0; i < jsonArr.length(); i++) {
                                JSONObject jsonObj = jsonArr.getJSONObject(i);
                                String str = "Parcel ID: " + jsonObj.getString("parcelId") + "\n";
                                str += "Maximum quantity: " + jsonObj.getInt("maxQuantity") + "\n";
                                str += "Current quantity: " + jsonObj.getInt("currQuantity") + "\n";
                                str += "Status: " + jsonObj.getString("pStatus") + "\n";
                                str += "Units: ";
                                JSONArray units = jsonObj.getJSONArray("units");
                                if (units.length() > 0) {
                                    for (int j = 0; j < units.length(); j++) {
                                        String unit = units.getString(j).substring(units.getString(j).lastIndexOf("#") + 1, units.getString(j).length());
                                        if (j == 0) str += "\n\t\t" + unit + "\n";
                                        else str += "\t\t" + unit + "\n";
                                    }
                                }
                                else str += "(this parcel is empty)\n";
                                result.add(str);
                            }
                        }
                        else if (operation.equals("ViewUnit")) {
                            for (int i = 0; i < jsonArr.length(); i++) {
                                JSONObject jsonObj = jsonArr.getJSONObject(i);
                                String str = "Unit ID: " + jsonObj.getString("unitId") + "\n";
                                str += "In parcel ID: " + jsonObj.getString("parcelId") + "\n";
                                str += "Status: " + jsonObj.getString("uStatus") + "\n";
                                if (jsonObj.getString("uStatus") == "SELLING") {
                                    str += "On sale since: " + jsonObj.getString("sellingDate") + "\n";
                                }
                                else if (jsonObj.getString("uStatus") == "SOLD") {
                                    str += "On sale since: " + jsonObj.getString("sellingDate") + "\n";
                                    str += "Sold on: " + jsonObj.getString("soldDate") + "\n";
                                }
                                result.add(str);
                            }
                        }
                        adapter = new ViewParcelUnitListViewAdapter(result, ViewParcelUnitActivity.this);
                        ListView lView = findViewById(R.id.listOfItems);
                        lView.setAdapter(adapter);
                    }
                    else {
                        if (operation.equals("ViewParcel"))
                            Toast.makeText(ViewParcelUnitActivity.this, "There's currently no parcel to display",
                                Toast.LENGTH_LONG).show();
                        else if (operation.equals("ViewUnit"))
                            Toast.makeText(ViewParcelUnitActivity.this, "There's currently no unit to display",
                                    Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (Exception e0) {
                    ShowAlertDialog("Something went wrong: " + e0.toString());
                }
            }
        });
    }

    private void StartLoginActivity() {
        Intent intent1 = new Intent(this, LoginActivity.class);
        startActivity(intent1);
        finish();
    }

    public void ShowAlertDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // (AdamVe)
    // https://stackoverflow.com/questions/28998241/how-to-clear-cookies-and-cache-of-webview-on-android-when-not-in-webview
    @SuppressWarnings("deprecation")
    public static void ClearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

}
