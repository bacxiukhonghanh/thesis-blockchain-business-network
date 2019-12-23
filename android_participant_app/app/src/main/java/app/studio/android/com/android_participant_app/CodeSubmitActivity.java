package app.studio.android.com.android_participant_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class CodeSubmitActivity extends AppCompatActivity {

    private static final int CODESCAN_ACTIVITY_REQUEST_CODE = 0;
    Button btnScanEmployeeID, btnSubmit, btnScanParcelIdOrNewOwnerID;
    TextView txtEmployeeId, txtEmployeeName, txtEmployeeGender, txtOrgId, txtParcelIdOrNewOwnerId;
    //EditText txtParcelMaxQuantity;
    String transaction = "";
    String orgId = "";
    String cookies = "";
    String scannedResult = "";
    String currentOperation = "";
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
        setContentView(R.layout.activity_code_submit);

        btnScanEmployeeID = findViewById(R.id.btnScanEmployeeID);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnScanParcelIdOrNewOwnerID = findViewById(R.id.btnScanParcelIdOrNewOwnerID);
        txtEmployeeId = findViewById(R.id.txtEmployeeId);
        txtEmployeeName = findViewById(R.id.txtEmployeeName);
        txtEmployeeGender = findViewById(R.id.txtEmployeeGender);
        txtOrgId = findViewById(R.id.txtOrgId);
        txtParcelIdOrNewOwnerId = findViewById(R.id.txtParcelIdOrNewOwnerId);
        //txtParcelMaxQuantity = findViewById(R.id.txtParcelMaxQuantity);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        transaction = extras.getString("TRANSACTION");
        orgId = extras.getString("ORG_ID");
        cookies = extras.getString("COOKIES");
        scannedResult = extras.getString("SCANNED");

        txtOrgId.setText("Participant: " + orgId);
        if (transaction.equals("Trade")) {
            txtParcelIdOrNewOwnerId.setVisibility(View.VISIBLE);
            btnScanParcelIdOrNewOwnerID.setVisibility(View.VISIBLE);
            txtEmployeeId.setVisibility(View.VISIBLE);
            txtEmployeeGender.setVisibility(View.VISIBLE);
            txtEmployeeName.setVisibility(View.VISIBLE);
            txtEmployeeGender.setVisibility(View.VISIBLE);
            btnScanEmployeeID.setVisibility(View.VISIBLE);
            //txtParcelMaxQuantity.setVisibility(View.INVISIBLE);
            txtParcelIdOrNewOwnerId.setText("Receiver ID: ");
            btnScanParcelIdOrNewOwnerID.setText("Scan for receiver info");
        }
        else if (transaction.equals("AddUnitToParcel")){
            txtParcelIdOrNewOwnerId.setVisibility(View.VISIBLE);
            btnScanParcelIdOrNewOwnerID.setVisibility(View.VISIBLE);
            txtEmployeeId.setVisibility(View.INVISIBLE);
            txtEmployeeGender.setVisibility(View.INVISIBLE);
            txtEmployeeName.setVisibility(View.INVISIBLE);
            txtEmployeeGender.setVisibility(View.INVISIBLE);
            btnScanEmployeeID.setVisibility(View.INVISIBLE);
            //txtParcelMaxQuantity.setVisibility(View.INVISIBLE);
            txtParcelIdOrNewOwnerId.setText("Parcel ID: ");
            btnScanParcelIdOrNewOwnerID.setText("Scan for parcel's ID");
        }
        else if (transaction.equals("CreateParcel")) {
            txtParcelIdOrNewOwnerId.setVisibility(View.VISIBLE);
            btnScanParcelIdOrNewOwnerID.setVisibility(View.VISIBLE);
            txtEmployeeId.setVisibility(View.INVISIBLE);
            txtEmployeeGender.setVisibility(View.INVISIBLE);
            txtEmployeeName.setVisibility(View.INVISIBLE);
            txtEmployeeGender.setVisibility(View.INVISIBLE);
            btnScanEmployeeID.setVisibility(View.INVISIBLE);
//            txtParcelMaxQuantity.setVisibility(View.VISIBLE);
//            txtParcelMaxQuantity.setText("");
        }
        else {
            txtParcelIdOrNewOwnerId.setVisibility(View.INVISIBLE);
            btnScanParcelIdOrNewOwnerID.setVisibility(View.INVISIBLE);
            txtEmployeeId.setVisibility(View.INVISIBLE);
            txtEmployeeGender.setVisibility(View.INVISIBLE);
            txtEmployeeName.setVisibility(View.INVISIBLE);
            txtEmployeeGender.setVisibility(View.INVISIBLE);
            btnScanEmployeeID.setVisibility(View.INVISIBLE);
//            txtParcelMaxQuantity.setVisibility(View.INVISIBLE);
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

        btnScanParcelIdOrNewOwnerID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentOperation = "ScanParcelIdOrNewOwnerID";
                Intent intent = new Intent(getApplicationContext(), CodeScanActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TRANSACTION", currentOperation);
                intent.putExtras(extras);
                startActivityForResult(intent, CODESCAN_ACTIVITY_REQUEST_CODE);
            }
        });
        btnScanEmployeeID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentOperation = "ScanEmployeeID";
                Intent intent = new Intent(getApplicationContext(), CodeScanActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TRANSACTION", currentOperation);
                intent.putExtras(extras);
                startActivityForResult(intent, CODESCAN_ACTIVITY_REQUEST_CODE);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                CodeScanActivity.fa.finish();
                if (transaction.equals("Trade")
                        && (txtEmployeeId.getText().toString().trim().equals("Employee ID:")
                        || txtEmployeeName.getText().toString().trim().equals("Employee name:")
                        || txtEmployeeGender.getText().toString().trim().equals("Gender:")
                        || txtParcelIdOrNewOwnerId.getText().toString().trim().equals("Receiver ID:")))
                    ShowAlertDialog("Missing employee or receiver information");
                else if (transaction.equals("AddUnitToParcel")
                        && txtParcelIdOrNewOwnerId.getText().toString().trim().equals("Parcel ID:"))
                    ShowAlertDialog("Missing employee or parcel information");
                else {
                    if (btnSubmit.getText().toString().contains("Submit")
                            || btnSubmit.getText().toString().contains("try again")) {
                        if (adapter.getCount() > 0) {
                            btnSubmit.setText("Submitting ...");
                            btnSubmit.setEnabled(false);
                            String transactionData = "";
                            switch (transaction){
                                case "AddUnitToParcel":
                                    transactionData = "{ \"$class\": \"org.example.mynetwork.AddUnitToParcel\", \"parcel\": \"";
                                    transactionData += txtParcelIdOrNewOwnerId.getText().toString().replace("Parcel ID: ", "").replace("Receiver ID: ","");
                                    transactionData += "\", \"unit\": [";
                                    for (int i = 0; i < adapter.getCount(); i++){
                                        if (i==0) transactionData += ("\"" + adapter.getItem(i).toString() + "\"");
                                        else transactionData += (", \"" + adapter.getItem(i).toString() + "\"");
                                    }
                                    transactionData += "], \"submittedBy\": \"";
                                    transactionData += orgId;
                                    transactionData += "\" }";
                                    new POSTTransactionDataTask().execute(ApplicationParameters.transaction_AddUnitToParcel_api_url, transactionData);
                                    break;
                                case "Trade":
                                    transactionData = "{ \"$class\": \"org.example.mynetwork.Trade\", \"shipper_name\": \"";
                                    transactionData += txtEmployeeName.getText().toString().replace("Employee name: ", "");
                                    transactionData += "\", \"shipper_gender\": \"";
                                    transactionData += txtEmployeeGender.getText().toString().replace("Gender: ", "");
                                    transactionData += "\", \"shipper_id\": \"";
                                    transactionData += txtEmployeeId.getText().toString().replace("Employee ID: ", "");
                                    transactionData += "\", \"parcel\": [";
                                    for (int i = 0; i < adapter.getCount(); i++){
                                        if (i==0) transactionData += ("\"" + adapter.getItem(i).toString() + "\"");
                                        else transactionData += (", \"" + adapter.getItem(i).toString() + "\"");
                                    }
                                    transactionData += "], \"submittedBy\": \"";
                                    transactionData += orgId;
                                    transactionData += "\", \"newOwner\": \"";
                                    transactionData += txtParcelIdOrNewOwnerId.getText().toString().replace("Parcel ID: ", "").replace("Receiver ID: ","");
                                    transactionData += "\" }";
                                    new POSTTransactionDataTask().execute(ApplicationParameters.transaction_Trade_api_url, transactionData);
                                    break;
                                case "PutParcelIntoStock":
                                    transactionData += "{ \"$class\": \"org.example.mynetwork.PutParcelIntoStock\", \"parcel\": [";
                                    for (int i = 0; i < adapter.getCount(); i++){
                                        if (i==0) transactionData += ("\"" + adapter.getItem(i).toString() + "\"");
                                        else transactionData += (", \"" + adapter.getItem(i).toString() + "\"");
                                    }
                                    transactionData += "], \"submittedBy\": \"";
                                    transactionData += orgId;
                                    transactionData += "\" }";
                                    new POSTTransactionDataTask().execute(ApplicationParameters.transaction_PutParcelIntoStock_api_url, transactionData);
                                    break;
                                case "ForSale":
                                    transactionData += "{ \"$class\": \"org.example.mynetwork.ForSale\", \"unit\": [";
                                    for (int i = 0; i < adapter.getCount(); i++){
                                        if (i==0) transactionData += ("\"" + adapter.getItem(i).toString() + "\"");
                                        else transactionData += (", \"" + adapter.getItem(i).toString() + "\"");
                                    }
                                    transactionData += "], \"submittedBy\": \"";
                                    transactionData += orgId;
                                    transactionData += "\" }";
                                    new POSTTransactionDataTask().execute(ApplicationParameters.transaction_ForSale_api_url, transactionData);
                                    break;
                                case "Sold":
                                    transactionData += "{ \"$class\": \"org.example.mynetwork.Sold\", \"unit\": [";
                                    for (int i = 0; i < adapter.getCount(); i++){
                                        if (i==0) transactionData += ("\"" + adapter.getItem(i).toString() + "\"");
                                        else transactionData += (", \"" + adapter.getItem(i).toString() + "\"");
                                    }
                                    transactionData += "], \"submittedBy\": \"";
                                    transactionData += orgId;
                                    transactionData += "\" }";
                                    new POSTTransactionDataTask().execute(ApplicationParameters.transaction_Sold_api_url, transactionData);
                                    break;
                            }
                        }
                        else ShowAlertDialog("There must be at least one item");
                    }
                    else if (btnSubmit.getText().toString().contains("Completed successfully")){
                        CodeSubmitActivity.this.finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == CODESCAN_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (currentOperation.equals("ScanEmployeeID")) {
                    // Get String data from Intent
                    String returnString = data.getStringExtra("EMPLOYEE_ID");

                    if (returnString.contains(",")) {
                        ArrayList<String> employee = new ArrayList<String>(Arrays.asList(returnString.split(",")));
                        if (employee.size() == 3) {
                            // Set text view with string
                            txtEmployeeId.setText("Employee ID: " + employee.get(0));
                            txtEmployeeName.setText("Employee name: " + employee.get(1));
                            txtEmployeeGender.setText("Gender: " + employee.get(2));
                        }
                        else ShowAlertDialog("Invalid employee information");
                    }
                    else ShowAlertDialog("Invalid employee information");
                }
                else if (currentOperation.equals("ScanParcelIdOrNewOwnerID")) {
                    // Get String data from Intent
                    String returnString = data.getStringExtra("PARCEL_ID_OR_NEW_OWNER_ID");
                    if (transaction.equals("Trade"))
                        txtParcelIdOrNewOwnerId.setText("Receiver ID: " + returnString);
                    else if (transaction.equals("AddUnitToParcel"))
                        txtParcelIdOrNewOwnerId.setText("Parcel ID: " + returnString);
                }
            }
        }
    }

    // params[0]: URL
    // params[1]: transactionData
    private class POSTTransactionDataTask extends AsyncTask<String, Integer, String> {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;

        @Override
        protected String doInBackground(String... params){
            String result;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.setUseCaches(false);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cookie", cookies);
                connection.setRequestProperty("Content-Type", "application/json");

                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(params[1]);

                if (connection.getResponseCode() == 200) {      //response should be 200 OK
                    result = "OK";
                }
                else if (connection.getResponseCode() == 401) {
                    result = "401";
                }
                else {
                    result = "Failed to submit. Response:" + connection.getResponseCode() + " " + connection.getResponseMessage();
                }

                outputStream.flush();
                outputStream.close();
            } catch (Exception e2) {
                e2.printStackTrace();
                result = e2.toString();
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {}

        @Override
        protected void onPostExecute(String result){
            if (result.equals("OK")) {
                btnSubmit.setText("Completed successfully. Tap to go back");
                btnSubmit.setEnabled(true);
            }
            else if (result.equals("401")) {
                ClearCookies(CodeSubmitActivity.this);
                Toast.makeText(CodeSubmitActivity.this, "Session expired. Please login again",
                        Toast.LENGTH_LONG).show();
                StartLoginActivity();
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
                CodeSubmitActivity.this.finish();
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
