package com.example.barcode.packing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.widget.Toast;

import com.example.barcode.R;
import com.example.barcode.adapter.StatusAdapter;
import com.example.barcode.billing.CreateProduct;
import com.example.barcode.credentials.login;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class allUser extends AppCompatActivity {

    StatusAdapter statusAdapter;
    public static ArrayList<String> CustomerStatus = new ArrayList<>();
    public static ArrayList<String>CustomerName = new ArrayList<>();
    public static ArrayList<Integer>uid = new ArrayList<>();
    public static ArrayList<String>TotProd = new ArrayList<>();
    public static ArrayList<String>ProdList = new ArrayList<>();
    public static ArrayList<String>TotCost = new ArrayList<>();
    FirebaseDatabase root;

    DatabaseReference reference;
    int i;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        db();

    }

    public void db() {
        root = FirebaseDatabase.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("user");
        Query user = reference.orderByChild("mobile");
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                     i=0;
                    for(DataSnapshot d:snapshot.getChildren()){
                        UID = d.getKey();
                        uid.add(Integer.valueOf(UID));
                    }
                    for (int j=0;j<uid.size();j++){
                       String NAME =  String.valueOf(snapshot.child(String.valueOf(uid.get(j))).child("name").getValue());
                       String STATUS =  String.valueOf(snapshot.child(String.valueOf(uid.get(j))).child("status").getValue());
                       String TOTCOST =  String.valueOf(snapshot.child(String.valueOf(uid.get(j))).child("TotCost").getValue());
                       String TOTPROD =  String.valueOf(snapshot.child(String.valueOf(uid.get(j))).child("TotProd").getValue());
                       String PRODList =  String.valueOf(snapshot.child(String.valueOf(uid.get(j))).child("purchaseList").getValue());
                        CustomerName.add(NAME);
                        CustomerStatus.add(STATUS);
                        TotCost.add(TOTCOST);
                        TotProd.add(TOTPROD);
                        ProdList.add(PRODList);
                    }

                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    statusAdapter = new StatusAdapter(allUser.this, CustomerName,uid,CustomerStatus,TotCost,TotProd,ProdList);
                    recyclerView.setItemViewCacheSize(CustomerName.size());
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(allUser.this);
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(statusAdapter);
                    recyclerView.setHasFixedSize(true);

                } else {
                    Toast.makeText(allUser.this, "No Users Available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}