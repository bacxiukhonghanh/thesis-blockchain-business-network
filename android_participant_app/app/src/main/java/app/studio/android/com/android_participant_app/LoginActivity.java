package app.studio.android.com.android_participant_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

import static app.studio.android.com.android_participant_app.ApplicationParameters.explorer_api_url;
import static app.studio.android.com.android_participant_app.ApplicationParameters.passport_auth_url;

public class LoginActivity extends AppCompatActivity {
    String cookies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Hyperledger Composer REST Client");

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(explorer_api_url)) {
                    cookies = CookieManager.getInstance().getCookie(ApplicationParameters.hostname);
                    CheckIfCardExists(ApplicationParameters.card_get_api_url);
                    return true;
                }
                else {
                    view.loadUrl(url);
                    return false;
                }
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                String additionalDescription = "";
                if (error.getErrorCode() == -2) additionalDescription = "Make sure your device has a working network connection";
                view.loadData("ERROR: Cannot load<br>" + request.getUrl().toString()
                        + "<br>because of the error " + error.getErrorCode() + ":<br>" + error.getDescription()
                        + "<br><br>" + additionalDescription,
                        "text/html; charset=utf-8", "UTF-8");
            }
        });
        myWebView.loadUrl(passport_auth_url);
    }

    public void CheckIfCardExists(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Cookie", cookies);
        client.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(LoginActivity.this, "Something's wrong. Response status code: " + statusCode + ". Response is: " + responseString,
                        Toast.LENGTH_LONG).show();
                ShowAlertDialog("Something's wrong. Response status code: " + statusCode + ". Response is: " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (responseString.equals("[]")) {      //no card imported
                    Intent intent = new Intent(getApplicationContext(),
                            CardImportActivity.class);
                    intent.putExtra("COOKIES", cookies);
                    startActivity(intent);
                    finish();
                }
                else if (responseString.contains("\"name\"")) {
                    try {
                        JSONArray jsonArray = new JSONArray(responseString);

                        //assume each user is limited to have only 1 card in their wallet
                        String cardname = jsonArray.getJSONObject(0).getString("name");

                        Intent intent = new Intent(getApplicationContext(),
                                CoopHomeActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("COOKIES", cookies);
                        extras.putString("CARDNAME", cardname);
                        intent.putExtras(extras);
                        startActivity(intent);
                        finish();
                    }
                    catch (Exception e) {
                        ShowAlertDialog("EXCEPTION: " + e.toString());
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, responseString, Toast.LENGTH_LONG).show();
                    ShowAlertDialog(responseString);
                }
            }
        });
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

}