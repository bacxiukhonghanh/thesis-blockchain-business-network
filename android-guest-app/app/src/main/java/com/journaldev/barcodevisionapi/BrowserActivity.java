package com.journaldev.barcodevisionapi;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class BrowserActivity extends AppCompatActivity {

    private WebView mWebView = null;

    String IP, FRONTEND_PORT, BASE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        setTitle("Information about the product");

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Bundle extrasGot = intent.getExtras();
        IP = extrasGot.getString("S_IP");
        FRONTEND_PORT = extrasGot.getString("S_FRONTEND_PORT");
        BASE_URL = extrasGot.getString("S_BASE_URL");
        String message = extrasGot.getString("SCAN_RESULT");

        mWebView = findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        if (!IP.trim().equals("")){
            mWebView.loadUrl("http://" + IP + ":" + FRONTEND_PORT + BASE_URL + message);
        }
        else mWebView.loadUrl(message);
    }
}
