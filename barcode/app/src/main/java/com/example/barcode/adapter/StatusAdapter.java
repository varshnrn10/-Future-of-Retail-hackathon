package com.example.barcode.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcode.R;
import com.example.barcode.packing.uniqueUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyViewHolder>{

    public Context mContext;
    ArrayList<String>CustomerName;
    ArrayList<Integer>uid;
    public static String SelectedUser;
    public static String SelectedTotProd;
    public static String SelectedProdList;
    public static String SelectedTotCost;
    ArrayList<String>CustomerStatus;
    ArrayList<String>TotCost;
    ArrayList<String>TotProd;
    ArrayList<String>ProdList;
    ArrayList<String>PROD;
    public static JSONObject object=new JSONObject();

    public StatusAdapter(Context mContext, ArrayList<String> customerName,ArrayList<Integer>uid,ArrayList<String> CustomerStatus,ArrayList<String>TotCost,ArrayList<String>TotProd,ArrayList<String>ProdList){
        this.mContext = mContext;
        this.CustomerName = customerName;
        this.uid = uid;
        this.CustomerStatus = CustomerStatus;
        this.TotCost = TotCost;
        this.TotProd = TotProd;
        this.ProdList = ProdList;
    }


    @NonNull
    @Override
    public StatusAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent,
                false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.MyViewHolder holder, int position) {
        holder.mTextView1.setText(CustomerName.get(position));
        holder.mTextView2.setText(CustomerStatus.get(position));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectedUser =CustomerName.get(position);
                SelectedProdList = ProdList.get(position);
                SelectedTotCost = TotCost.get(position);
                SelectedTotProd = TotProd.get(position);
                Log.e("Selected User", SelectedUser);
                Intent intent = new Intent(mContext, uniqueUser.class);
                mContext.startActivity(intent);
                try {
                    object=new JSONObject(SelectedProdList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CustomerName.clear();
                uid.clear();
                CustomerStatus.clear();
                TotCost.clear();
                TotProd.clear();
                ProdList.clear();
            }
        });
    }

    @Override
    public int getItemCount() {
        return CustomerName.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView1,mTextView2;
        public CardView cardView;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            mTextView1 = itemView.findViewById(R.id.custName);
            mTextView2 = itemView.findViewById(R.id.costDis);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
