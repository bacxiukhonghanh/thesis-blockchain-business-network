package app.studio.android.com.android_participant_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CoopHomeActivity extends AppCompatActivity {
    String cookies, cardname;
    String orgId = "";
    Button btnLogout, btnAddUnitToParcel, btnViewInfo, btnCreateParcel, btnTrade, btnViewParcels, btnCreateUnit, btnViewUnits, btnPutParcelIntoStock, btnSold, btnForSale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("");

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        cookies = extras.getString("COOKIES");
        cardname = extras.getString("CARDNAME");

        btnLogout = findViewById(R.id.btnLogout);
        btnAddUnitToParcel = findViewById(R.id.btnAddUnitToParcel);
        btnViewInfo = findViewById(R.id.btnViewInfo);
        btnCreateParcel = findViewById(R.id.btnCreateParcel);
        btnTrade = findViewById(R.id.btnTrade);
        btnViewParcels = findViewById(R.id.btnViewParcels);
        btnCreateUnit = findViewById(R.id.btnCreateUnit);
        btnViewUnits = findViewById(R.id.btnViewUnits);
        btnPutParcelIntoStock = findViewById(R.id.btnPutParcelIntoStock);
        btnSold = findViewById(R.id.btnSold);
        btnForSale = findViewById(R.id.btnForSale);

        if (cardname.startsWith("coop")){
            btnCreateParcel.setVisibility(View.VISIBLE);
            btnCreateUnit.setVisibility(View.VISIBLE);
            btnAddUnitToParcel.setVisibility(View.VISIBLE);
            btnPutParcelIntoStock.setVisibility(View.INVISIBLE);
            btnForSale.setVisibility(View.INVISIBLE);
            btnSold.setVisibility(View.INVISIBLE);
            DoHttpGetRequest(ApplicationParameters.get_coop_orgId_api_url, "getOrgId");
        }
        else if (cardname.startsWith("trans")) {
            btnCreateParcel.setVisibility(View.INVISIBLE);
            btnCreateUnit.setVisibility(View.INVISIBLE);
            btnAddUnitToParcel.setVisibility(View.INVISIBLE);
            btnPutParcelIntoStock.setVisibility(View.INVISIBLE);
            btnForSale.setVisibility(View.INVISIBLE);
            btnSold.setVisibility(View.INVISIBLE);
            DoHttpGetRequest(ApplicationParameters.get_trans_orgId_api_url, "getOrgId");
        }
        else if (cardname.startsWith("seller")){
            btnCreateParcel.setVisibility(View.INVISIBLE);
            btnCreateUnit.setVisibility(View.INVISIBLE);
            btnAddUnitToParcel.setVisibility(View.INVISIBLE);
            btnPutParcelIntoStock.setVisibility(View.VISIBLE);
            btnForSale.setVisibility(View.VISIBLE);
            btnSold.setVisibility(View.VISIBLE);
            DoHttpGetRequest(ApplicationParameters.get_seller_orgId_api_url, "getOrgId");
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoHttpGetRequest(ApplicationParameters.logout_REST_api_url, "logout_REST");
                DoHttpGetRequest(ApplicationParameters.logout_Auth0_api_url, "logout_Auth0");
            }
        });
        btnCreateParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CoopHomeActivity.this, CodeScanActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TRANSACTION","CreateParcel");
                extras.putString("ORG_ID", orgId);
                extras.putString("COOKIES", cookies);
                intent1.putExtras(extras);
                startActivity(intent1);
            }
        });
        btnCreateUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CoopHomeActivity.this, CodeScanActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TRANSACTION","CreateUnit");
                extras.putString("ORG_ID", orgId);
                extras.putString("COOKIES", cookies);
                intent1.putExtras(extras);
                startActivity(intent1);
            }
        });
        btnViewParcels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CoopHomeActivity.this, ViewParcelUnitActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TRANSACTION","ViewParcel");
                extras.putString("ORG_ID", orgId);
                extras.putString("COOKIES", cookies);
                intent1.putExtras(extras);
                startActivity(intent1);
            }
        });
        btnViewUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CoopHomeActivity.this, ViewParcelUnitActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TRANSACTION","ViewUnit");
                extras.putString("ORG_ID", orgId);
                extras.putString("COOKIES", cookies);
                intent1.putExtras(extras);
                startActivity(intent1);
            }
        });
        btnAddUnitToParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CoopHomeActivity.this, CodeScanActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TRANSACTION","AddUnitToParcel");
                extras.putString("ORG_ID", orgId);
                extras.putString("COOKIES", cookies);
                intent1.putExtras(extras);
                startActivity(intent1);
            }
        });
        btnTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CoopHomeActivity.this, CodeScanActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TRANSACTION","Trade");
                extras.putString("ORG_ID", orgId);
                extras.putString("COOKIES", cookies);
                intent1.putExtras(extras);
                startActivity(intent1);
            }
        });
        btnPutParcelIntoStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CoopHomeActivity.this, CodeScanActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TRANSACTION","PutParcelIntoStock");
                extras.putString("ORG_ID", orgId);
                extras.putString("COOKIES", cookies);
                intent1.putExtras(extras);
                startActivity(intent1);
            }
        });
        btnForSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CoopHomeActivity.this, CodeScanActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TRANSACTION","ForSale");
                extras.putString("ORG_ID", orgId);
                extras.putString("COOKIES", cookies);
                intent1.putExtras(extras);
                startActivity(intent1);
            }
        });
        btnSold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CoopHomeActivity.this, CodeScanActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TRANSACTION","Sold");
                extras.putString("ORG_ID", orgId);
                extras.putString("COOKIES", cookies);
                intent1.putExtras(extras);
                startActivity(intent1);
            }
        });
    }

    String currentOperation = "";
    public void DoHttpGetRequest(String url, String operation) {
        currentOperation = operation;
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Cookie", cookies);
        client.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == 401) {
                    ClearCookies(CoopHomeActivity.this);
                    Toast.makeText(CoopHomeActivity.this, "Session expired. Please login again",
                            Toast.LENGTH_LONG).show();
                    StartLoginActivity();
                }
                else {
                    Toast.makeText(CoopHomeActivity.this, "Something's wrong. Response status code: " + statusCode + ". Response is: " + responseString,
                            Toast.LENGTH_LONG).show();
                    ShowAlertDialog("Something's wrong. Response status code: " + statusCode + ". Response is: " + responseString);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (currentOperation.equals("logout_Auth0")) {
                    ClearCookies(CoopHomeActivity.this);
                    StartLoginActivity();
                }
                else if (!currentOperation.equals("logout_REST")) {
                    if (currentOperation.equals("getOrgId")){
                        try {
                            JSONArray jsonArr = new JSONArray(responseString);
                            JSONObject jsonObj = jsonArr.getJSONObject(0);
                            orgId = jsonObj.getString("orgId");
                            setTitle("Welcome, " + cardname.replace("@tutorial-network", "") + " (" + orgId + ")");
                        } catch (Exception e0) {
                            ShowAlertDialog("ERROR: Cannot get participant ID");
                        }
                    }
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
