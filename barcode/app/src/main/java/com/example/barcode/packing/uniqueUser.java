package com.example.barcode.packing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barcode.R;
import com.example.barcode.adapter.StatusAdapter;
import com.example.barcode.billing.CreateProduct;
import com.example.barcode.billing.scan;
import com.example.barcode.credentials.login;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class uniqueUser extends AppCompatActivity {

    TextView CustName, TotProd, Prod, TotCost;
    JSONObject prodList = new JSONObject();
    ArrayList<String> prod = new ArrayList<String>();
    MaterialButton pay;
    int CASH = 0, CARD = 0, UPI = 0, BALANCE = 0, TOT = 0,TOTALSALE=0;
    TextView balance;
    TextInputEditText cash;
    TextInputEditText upi;
    TextInputEditText card;
    Button done;
    FirebaseDatabase root;
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    String UID;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    public static String CashDb,CardDb,UpiDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unique_user);
        Iterator<String> keys = null;

        CustName = findViewById(R.id.custName);
        TotProd = findViewById(R.id.TotProd);
        Prod = findViewById(R.id.product);
        TotCost = findViewById(R.id.costTot);
        pay = findViewById(R.id.Pay);

        DbRetrieval();
        sharedPreferences = getApplicationContext().getSharedPreferences("Login", 0);
        UID = sharedPreferences.getString("uid", null);
        Log.e("UID",UID);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popLayout = LayoutInflater.from(uniqueUser.this).inflate(R.layout.bill_final, null);
                AlertDialog.Builder popBuilder = new AlertDialog.Builder(uniqueUser.this).setView(popLayout);
                AlertDialog pop = popBuilder.show();

                TextView CostTot = popLayout.findViewById(R.id.cost);
                balance = popLayout.findViewById(R.id.balance);
                cash = popLayout.findViewById(R.id.cash);
                card = popLayout.findViewById(R.id.card);
                upi = popLayout.findViewById(R.id.upi);
                done = popLayout.findViewById(R.id.finish);

                TOT = Integer.parseInt(TotCost.getText().toString());
                CostTot.setText(TotCost.getText().toString());
                cash.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (cash.getText().toString().isEmpty()){
                            CASH=0;
                            updateBal();
                        }
                        else {
                            CASH = Integer.parseInt(cash.getText().toString());
                            updateBal();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
                card.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (card.getText().toString().isEmpty()){
                            CARD=0;
                            updateBal();
                        }
                        else {
                            CARD = Integer.parseInt(card.getText().toString());
                            updateBal();
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                upi.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (upi.getText().toString().isEmpty()){
                            UPI=0;
                            updateBal();
                        }
                        else {
                            UPI = Integer.parseInt(upi.getText().toString());
                            updateBal();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db();


                    }
                });


            }
        });

        CustName.setText(StatusAdapter.SelectedUser);
        TotProd.setText(StatusAdapter.SelectedTotProd);
        //Prod.setText(StatusAdapter.SelectedProdList);
        TotCost.setText(StatusAdapter.SelectedTotCost);
        prodList = StatusAdapter.object;
        keys = prodList.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            prod.add(key);
        }
        int c = 1;
        for (int i = 0; i < prod.size(); i++) {
            Prod.setText(Prod.getText() + "\n" + c + ". " + prod.get(i));
            c++;
        }
    }

    private void db() {
        Log.e("KIDDo cash", String.valueOf(CashDb));
        Log.e("KIDDo card", String.valueOf(CardDb));
        Log.e("KIDDO upi", String.valueOf(UpiDb));
        CASH +=Integer.parseInt(CashDb);
        CARD +=Integer.parseInt(CardDb);
        UPI +=Integer.parseInt(UpiDb);
        TOTALSALE +=CASH+CARD+UPI;
        com.example.barcode.packing.BillingHelperClass billingHelperClass = new com.example.barcode.packing.BillingHelperClass(String.valueOf(CASH),String.valueOf(CARD),String.valueOf(UPI),String.valueOf(TOTALSALE));

        reference.child("mode").setValue(billingHelperClass);
        Toast.makeText(this, "Payment Done Successfully", Toast.LENGTH_SHORT).show();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user");

        databaseReference.child(UID).child("status").setValue("purchase done")
                .addOnSuccessListener(aVoid -> Toast.makeText(uniqueUser.this, "Purchase Done", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(uniqueUser.this, "Something wrong", Toast.LENGTH_SHORT).show());
        Intent intent = new Intent(this, allUser.class);
        startActivity(intent);
    }

    public void updateBal() {

        TOT = Integer.parseInt(TotCost.getText().toString());
        BALANCE = TOT - (CASH + CARD + UPI);
//        balance.setText(BALANCE);
        balance.setText(String.valueOf(BALANCE));
    }
    public void DbRetrieval(){
        root = FirebaseDatabase.getInstance();
        reference = root.getReference("payment");

        Query user = reference.child("mode");
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CashDb = snapshot.child("cash").getValue(String.class);
                    CardDb = snapshot.child("card").getValue(String.class);
                    UpiDb = snapshot.child("upi").getValue(String.class);

                } else {
                    Toast.makeText(uniqueUser.this, "Account does not exits", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}