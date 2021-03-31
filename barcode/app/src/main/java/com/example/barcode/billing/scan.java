package com.example.barcode.billing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barcode.R;
import com.example.barcode.credentials.OtpAuth;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class scan extends AppCompatActivity {
    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private TextView barcodeText, cost, name,totCost;
    public String barcodeData, pNAME, pCOST;
    FirebaseDatabase root;
    DatabaseReference reference;
    MaterialButton Finish;
    public String costDB;
    public String nameDB;
    public int TOTCOST=0;
    public int TotProd=0;
    Button add;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    public JSONObject jsonObject = new JSONObject();
    String UID;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);
        cost = findViewById(R.id.costDis);
        name = findViewById(R.id.nameDis);
        totCost = findViewById(R.id.costTot);
        Finish = findViewById(R.id.Pay);
        add = findViewById(R.id.add);
        initialiseDetectorsAndSources();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user");

        sharedPreferences = getApplicationContext().getSharedPreferences("Login", 0);
        UID = sharedPreferences.getString("uid", null);
        Log.e("UID",UID);
    }

    private void initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

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
                    if (ActivityCompat.checkSelfPermission(scan.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(scan.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
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
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {

                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            update();
                            totCost.setText(String.valueOf(TOTCOST));
                            Toast.makeText(scan.this, "Product Added", Toast.LENGTH_SHORT).show();
                            try {
                                jsonObject.put(nameDB,costDB);
                                Log.e("KIDDO", jsonObject.toString());
                            }
                            catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });

                    Finish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            databaseReference.child(UID).child("purchaseList").setValue(String.valueOf(jsonObject))
                                    .addOnSuccessListener(aVoid -> Toast.makeText(scan.this, "Purchase Completed ", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(scan.this, "Something wrong", Toast.LENGTH_SHORT).show());
                            databaseReference.child(UID).child("TotCost").setValue(String.valueOf(TOTCOST))
                                    .addOnFailureListener(e -> Toast.makeText(scan.this, "Something wrong", Toast.LENGTH_SHORT).show());
                            databaseReference.child(UID).child("TotProd").setValue(String.valueOf(TotProd))
                                    .addOnFailureListener(e -> Toast.makeText(scan.this, "Something wrong", Toast.LENGTH_SHORT).show());
                            databaseReference.child(UID).child("status").setValue("Heading To Packing Desk")
                                    .addOnSuccessListener(aVoid -> Toast.makeText(scan.this, "Head To Packing Desk", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(scan.this, "Something wrong", Toast.LENGTH_SHORT).show());
                        }
                    });

                    barcodeText.post(new Runnable() {
                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                barcodeText.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP, 150);
                                db();

                            } else {

                                barcodeData = barcodes.valueAt(0).displayValue;
                                barcodeText.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP, 150);
                                db();

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
        getSupportActionBar().hide();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
        initialiseDetectorsAndSources();
    }

    public void db() {
        root = FirebaseDatabase.getInstance();
        reference = root.getReference("products");
        Query check = reference.orderByChild("pcode").equalTo(barcodeText.getText().toString());

        check.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String barcodeDB = snapshot.child(barcodeText.getText().toString()).child("pcode").getValue(String.class);
                    if (barcodeDB.equals(barcodeText.getText().toString())) {
                        nameDB = snapshot.child(barcodeText.getText().toString()).child("pname").getValue(String.class);
                        costDB = snapshot.child(barcodeText.getText().toString()).child("pcost").getValue(String.class);
                        cost.setText(costDB);
                        name.setText(nameDB);
                    }
                }
                else {
                    cost.setText("");
                    name.setText("No Such Product Exits");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void update(){
        TotProd++;
        TOTCOST += Integer.parseInt(costDB);
    }

}