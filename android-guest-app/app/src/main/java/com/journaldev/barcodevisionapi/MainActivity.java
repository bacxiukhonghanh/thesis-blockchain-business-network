package com.journaldev.barcodevisionapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnScanBarcode;
    TextView txtIP, txtFrontendPort, txtBaseURL;

//    public static String IP = "";
//    public static String PORT = "";
//    public static String BASE_URL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        txtIP = findViewById(R.id.txtIP);
        txtFrontendPort = findViewById(R.id.txtFrontendPort);
        txtBaseURL = findViewById(R.id.txtBaseURL);

        //btnTakePicture = findViewById(R.id.btnTakePicture);
        btnScanBarcode = findViewById(R.id.btnScanBarcode);
        //btnTakePicture.setOnClickListener(this);
        btnScanBarcode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.btnTakePicture:
//                startActivity(new Intent(MainActivity.this, PictureBarcodeActivity.class));
//                break;
            case R.id.btnScanBarcode:
                Intent intent = new Intent(MainActivity.this, ScannedBarcodeActivity.class);
                Bundle extras = new Bundle();
                extras.putString("IP", txtIP.getText().toString());
                extras.putString("FRONTEND_PORT", txtFrontendPort.getText().toString());

                if (txtBaseURL.getText().toString().trim().equals("")){
                    extras.putString("BASE_URL", "/");
                }
                else extras.putString("BASE_URL", txtBaseURL.getText().toString());

                intent.putExtras(extras);
                startActivity(intent);
                //startActivity(new Intent(MainActivity.this, ScannedBarcodeActivity.class));
                break;
        }

    }
}
