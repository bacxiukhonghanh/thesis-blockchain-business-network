package app.studio.android.com.android_participant_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CreateParcelUnitActivity extends AppCompatActivity {

    Button btnSubmit;
    TextView txtOrgId;
    EditText txtParcelMaxQuantity;
    String transaction = "";
    String orgId = "";
    String cookies = "";
    String scannedResult = "";
    ArrayList<String> scannedArrayList = new ArrayList<String>();
    CodeSubmitListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_create_parcel_unit);

        btnSubmit = findViewById(R.id.btnSubmit);
        txtOrgId = findViewById(R.id.txtOrgId);
        txtParcelMaxQuantity = findViewById(R.id.txtParcelMaxQuantity);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        transaction = extras.getString("TRANSACTION");
        orgId = extras.getString("ORG_ID");
        cookies = extras.getString("COOKIES");
        scannedResult = extras.getString("SCANNED");

        txtOrgId.setText("Participant: " + orgId);
        if (transaction.equals("CreateUnit")) {
            txtParcelMaxQuantity.setVisibility(View.INVISIBLE);
        }
        else if (transaction.equals("CreateParcel")){
            txtParcelMaxQuantity.setVisibility(View.VISIBLE);
        }

        if (scannedResult.contains(","))
            scannedArrayList = new ArrayList<String>(Arrays.asList(scannedResult.split(",")));
        else {
            scannedArrayList = new ArrayList<String>();
            scannedArrayList.add(scannedResult);
        }

        //instantiate custom adapter
        adapter = new CodeSubmitListViewAdapter(scannedArrayList, this);

        //handle listview and assign adapter
        ListView lView = findViewById(R.id.listOfItems);
        lView.setAdapter(adapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                CodeScanActivity.fa.finish();
                if (btnSubmit.getText().toString().contains("Submit")
                        || btnSubmit.getText().toString().contains("try again")) {
                    if (adapter.getCount() > 0) {
                        btnSubmit.setText("Submitting ...");
                        btnSubmit.setEnabled(false);
                        switch (transaction) {
                            case "CreateParcel":
                                if (!txtParcelMaxQuantity.getText().toString().trim().equals("")) {
                                    new POSTTransactionDataTask().execute(ApplicationParameters.asset_Parcel_api_url, "Parcel", txtParcelMaxQuantity.getText().toString());
                                }
                                else {
                                    ShowAlertDialog("The maximum quantity of the parcels must be specified");
                                    btnSubmit.setText("Submit");
                                    btnSubmit.setEnabled(true);
                                }
                                break;
                            case "CreateUnit":
                                new POSTTransactionDataTask().execute(ApplicationParameters.asset_Unit_api_url, "Unit");
                                break;
                        }
                    }
                    else ShowAlertDialog("There must be at least one item");
                }
                else if (btnSubmit.getText().toString().contains("Completed successfully")){
                    CreateParcelUnitActivity.this.finish();
                }
            }
        });
    }

    // params[0]: URL
    // params[1]: Parcel/Unit
    // params[2]: Parcel's max quantity
    private class POSTTransactionDataTask extends AsyncTask<String, Integer, String> {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        int numberOfErrors = 0;

        @Override
        protected String doInBackground(String... params){
            String result = "OK";
            try {
                for (int i = 0; i < adapter.getCount(); i++) {
                    publishProgress(i + 1);
                    String transactionData = "";
                    switch (params[1]) {
                        case "Parcel":
                            transactionData += "{ \"$class\": \"org.example.mynetwork.Parcel\", \"parcelId\": \"";
                            transactionData += adapter.getItem(i).toString();
                            transactionData += "\", \"description\": \"Parcel\"";
                            transactionData += ", \"maxQuantity\": ";
                            transactionData += params[2];
                            transactionData += ", \"currQuantity\": \"0\", \"pStatus\": \"PREPARING\", \"units\": [], \"owner\": \"";
                            transactionData += orgId;
                            transactionData += "\" }";
                            break;
                        case "Unit":
                            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                            Date currentLocalTime = cal.getTime();
                            DateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            date.setTimeZone(TimeZone.getTimeZone("GMT"));
                            String localTime = date.format(currentLocalTime);
                            transactionData += "{ \"$class\": \"org.example.mynetwork.Unit\", \"unitId\": \"";
                            transactionData += adapter.getItem(i).toString();
                            transactionData += "\", \"parcelId\": \"N/A\", \"description\": \"Unit\", ";
                            transactionData += "\"createdDate\": \"";
                            transactionData += localTime + "\", ";
                            transactionData += "\"uStatus\": \"DELIVERING\", \"owner\": \"";
                            transactionData += orgId;
                            transactionData += "\" }";
                            break;
                    }
                    URL url = new URL(params[0]);
                    connection = (HttpURLConnection) url.openConnection();

                    connection.setDoInput(true);
                    connection.setUseCaches(false);

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Cookie", cookies);
                    connection.setRequestProperty("Content-Type", "application/json");

                    outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(transactionData);

                    if (connection.getResponseCode() != 200) {      //response should be 200 OK
                        if (connection.getResponseCode() == 401) {
                            result = "401";
                            outputStream.flush();
                            outputStream.close();
                            return result;
                        } else {
                            result += "Failed to submit item " + adapter.getItem(i).toString() + ". Response:" + connection.getResponseCode() + " " + connection.getResponseMessage() + "\n";
                            numberOfErrors++;
                        }
                    }
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                result = e2.toString();
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            btnSubmit.setText("Submitting " + progress[0] + " of " + adapter.getCount() + " item(s)....");
        }

        @Override
        protected void onPostExecute(String result){
            if (result.equals("OK")) {
                btnSubmit.setText("Completed successfully. Tap to go back");
                btnSubmit.setEnabled(true);
            }
            else if (result.equals("401")) {
                ClearCookies(CreateParcelUnitActivity.this);
                Toast.makeText(CreateParcelUnitActivity.this, "Session expired. Please login again",
                        Toast.LENGTH_LONG).show();
                StartLoginActivity();
            }
            else if (result.startsWith("Failed")) {
                ShowAlertDialog(result);
                btnSubmit.setText("Finished with " + numberOfErrors + " errors");
            }
            else {
                btnSubmit.setText("Unable to submit");
                ShowPromptDialog(result);
            }
        }
    }

    private void StartLoginActivity() {
        Intent intent1 = new Intent(this, LoginActivity.class);
        startActivity(intent1);
        finish();
    }

    public void ShowPromptDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg + "\nDo you want to go back or try again?");
        builder.setCancelable(false);
        builder.setNegativeButton("Go back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CreateParcelUnitActivity.this.finish();
            }
        });
        builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                btnSubmit.setText("Tap here to try again");
                btnSubmit.setEnabled(true);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

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
