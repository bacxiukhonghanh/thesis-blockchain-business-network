package app.studio.android.com.android_participant_app;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CodeScanActivity extends AppCompatActivity {
    public static Activity fa;

    SurfaceView surfaceView;
    TextView txtBarcodeValue, txtAddedValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnAdd, btnProceed, btnOk;
    String intentData = "";
    boolean isEmail = false;

    String cookies = "";
    String transaction = "";
    String orgId = "";
    List<String> scannedCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scan);
        setTitle("Code scanner");
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        transaction = extras.getString("TRANSACTION");
        orgId = extras.getString("ORG_ID");
        cookies = extras.getString("COOKIES");
        scannedCodes = new ArrayList<String>();
        initViews();
    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        btnAdd = findViewById(R.id.btnAdd);
        btnProceed = findViewById(R.id.btnProceed);
        btnOk = findViewById(R.id.btnOk);
        txtAddedValue = findViewById(R.id.txtAddedValue);
        switch (transaction) {
            case "ScanEmployeeID":
            case "ScanParcelIdOrNewOwnerID":
                btnOk.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.INVISIBLE);
                btnProceed.setVisibility(View.INVISIBLE);
                break;
            case "CreateParcel":
            case "CreateUnit":
            case "Trade":
            case "AddUnitToParcel":
            case "PutParcelIntoStock":
            case "ForSale":
            case "Sold":
                fa = this;
                btnOk.setVisibility(View.INVISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                btnProceed.setVisibility(View.VISIBLE);
                break;
//            case "Sold":
//                fa = this;
//                btnOk.setVisibility(View.VISIBLE);
//                btnAdd.setVisibility(View.INVISIBLE);
//                btnProceed.setVisibility(View.INVISIBLE);
//                break;
        }
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (transaction.equals("Sold")) {
                    Intent intent1 = new Intent(CodeScanActivity.this, CodeSubmitActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("TRANSACTION","Sold");
                    extras.putString("ORG_ID", orgId);
                    extras.putString("COOKIES", cookies);
                    extras.putString("SCANNED", txtBarcodeValue.getText().toString());
                    intent1.putExtras(extras);
                    startActivity(intent1);
                }
                else {
                    Intent intent = new Intent();
                    switch (transaction){
                        case "ScanEmployeeID":
                            intent.putExtra("EMPLOYEE_ID", txtBarcodeValue.getText().toString());
                            break;
                        case "ScanParcelIdOrNewOwnerID":
                            intent.putExtra("PARCEL_ID_OR_NEW_OWNER_ID", txtBarcodeValue.getText().toString());
                            break;
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentData.length() > 0) {
                    scannedCodes.add(txtBarcodeValue.getText().toString());
                    txtAddedValue.setText("Added: " + txtBarcodeValue.getText().toString());
                    btnProceed.setEnabled(true);
                }
            }
        });
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!scannedCodes.isEmpty()) {
                    String scannedResult = "";
                    ListIterator<String> itr = scannedCodes.listIterator();
                    while(itr.hasNext()) {
                        scannedResult = scannedResult + (itr.next().concat(","));
                    }
                    if (scannedResult.contains(","))
                        scannedResult = scannedResult.substring(0, scannedResult.lastIndexOf(","));

                    Intent intent1;
                    if (transaction.equals("CreateParcel") || transaction.equals("CreateUnit"))
                         intent1 = new Intent(getApplicationContext(), CreateParcelUnitActivity.class);
                    else
                        intent1 = new Intent(getApplicationContext(), CodeSubmitActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("TRANSACTION", transaction);
                    extras.putString("ORG_ID", orgId);
                    extras.putString("COOKIES", cookies);
                    extras.putString("SCANNED", scannedResult);
                    intent1.putExtras(extras);
                    startActivity(intent1);
                    //finish();
                }
            }
        });
    }

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Code scanner ready", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(CodeScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(CodeScanActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    ShowAlertDialog("EXCEPTION: " + e.toString());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {
                            if (barcodes.valueAt(0).email != null) {}
                            else {
                                isEmail = false;
                                if (transaction.equals("ScanEmployeeID")
                                        || transaction.equals("ScanParcelIdOrNewOwnerID"))
                                        //|| transaction.equals("Sold"))
                                    btnOk.setEnabled(true);
                                else btnAdd.setEnabled(true);
                                intentData = barcodes.valueAt(0).displayValue;
                                txtBarcodeValue.setText(intentData);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
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