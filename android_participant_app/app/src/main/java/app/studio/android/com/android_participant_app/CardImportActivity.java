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
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class CardImportActivity extends AppCompatActivity {
    static String cookies;
    String tmpCardFilePath;
    String tmpCardFileName = "my_card.card";
    EditText txtCardName, txtCardFileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardimport);
        setTitle("Please import a business card");

        Intent intent = getIntent();
        cookies = intent.getStringExtra("COOKIES");
        txtCardName = findViewById(R.id.txtCardName);
        txtCardFileUrl = findViewById(R.id.txtCardFileUrl);
        Button button = findViewById(R.id.btnImport);

        tmpCardFilePath = getFilesDir() + "/cards/";
        //tmpCardFilePath = Environment.getExternalStorageDirectory() + "/download/";

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtCardName.getText().toString().equals("") && !txtCardFileUrl.getText().toString().equals("")){
                    PerformCardImport(txtCardFileUrl.getText().toString(),
                            ApplicationParameters.card_import_api_url
                                    + txtCardName.getText().toString().replace("@", "%40"));
                }
            }
        });
    }

    private void PerformCardImport(String filepath, String urlTo){
        GetCardFile(filepath, urlTo);
    }

    public void GetCardFile(String url, final String uploadDestination) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Cookie", cookies);

        //well it has to be pure bytes =_= the file is just a zip anyway
        //so don't use TextHttpResponseHandler, as it will treat the response as a text file
        //therefore, when we upload the "text" file, the REST server won't accept it
        //is it really taking me half a day to figure this out ??????
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    File secondFile = new File(tmpCardFilePath, tmpCardFileName);
                    if (secondFile.getParentFile().mkdirs()) {
                        secondFile.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(secondFile);
                    fos.write(responseBody);
                    fos.flush();
                    fos.close();

                    new POSTCardDataTask().execute(uploadDestination);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 401) {
                    ClearCookies(CardImportActivity.this);
                    Toast.makeText(CardImportActivity.this, "Session expired. Please login again",
                            Toast.LENGTH_LONG).show();
                    StartLoginActivity();
                }
                else {
                    Toast.makeText(CardImportActivity.this, "Something's wrong. Response status code: " + statusCode + ". Response is: " + responseBody.toString(),
                            Toast.LENGTH_LONG).show();
                    ShowAlertDialog("Something's wrong. Response status code: " + statusCode + ". Response is: " + responseBody.toString());
                }
            }
        });
    }

    private class POSTCardDataTask extends AsyncTask<String, Integer, String> {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;

        String twoHyphens = "--";
        String boundary = "----WebKitFormBoundary" + ApplicationParameters.GetRandomBoundaryString();
        String lineEnd = "\r\n";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;

        @Override
        protected String doInBackground(String... params) {
            String result;
            try {
                String filePath = tmpCardFilePath + tmpCardFileName;
                File file = new File(filePath);
                FileInputStream fileInputStream = new FileInputStream(file);

                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.setUseCaches(false);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cookie", cookies);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"card\"; filename=\"" + file.getName() + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: application/octet-stream" + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = bytesAvailable;
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = bytesAvailable;
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                publishProgress(4000);
                Thread.sleep(4000);

                if (connection.getResponseCode() == 204) {      //response should be 204 No Content
                    result = "OK";
                }
                else if (connection.getResponseCode() == 401) {
                    result = "401";
                }
                else {
                    result = "Failed to upload. Response:" + connection.getResponseCode() + " " + connection.getResponseMessage();
                }

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();
            }
            catch (Exception e2) {
                e2.printStackTrace();
                result = e2.toString();
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Toast.makeText(CardImportActivity.this, "Please wait...",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String result){
            if (result.equals("OK")) {
                String filePath = tmpCardFilePath + tmpCardFileName;
                File file = new File(filePath);
                file.delete();
                Toast.makeText(CardImportActivity.this, "Successful. Redirecting to landing page",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),
                        CoopHomeActivity.class);
                Bundle extras = new Bundle();
                extras.putString("COOKIES", cookies);
                extras.putString("CARDNAME", txtCardName.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                finish();
            }
            else if (result.equals("401")) {
                ClearCookies(CardImportActivity.this);
                Toast.makeText(CardImportActivity.this, "Session expired. Please login again",
                        Toast.LENGTH_LONG).show();
                StartLoginActivity();
            }
            else {
                ShowAlertDialog(result);
            }
        }
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