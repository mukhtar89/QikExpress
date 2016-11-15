package com.equinox.qikexpress.Activities;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.equinox.qikexpress.Models.DataHolder;
import com.equinox.qikexpress.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class WalletActivity extends AppCompatActivity {

    private TextView walletAmount;
    private LinearLayout creditMoney, debitMoney;
    private DatabaseReference walletReference;
    private Float walletAmountValue = (float) 0.00;
    private DecimalFormat dc2 = new DecimalFormat(".##");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (DataHolder.location == null) {
            Toast.makeText(this, "Please turn on your Location!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            walletAmount = (TextView) findViewById(R.id.wallet_amount);
            creditMoney = (LinearLayout) findViewById(R.id.credit_money_wallet);
            debitMoney = (LinearLayout) findViewById(R.id.debit_money_wallet);
            walletReference = DataHolder.userDatabaseReference.child("wallet");
            walletReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null)
                        walletReference.setValue(0.00);
                    else if (dataSnapshot.getValue() instanceof Double)
                        walletAmountValue = (float) (double) dataSnapshot.getValue();
                    else walletAmountValue = (float) (long) dataSnapshot.getValue();
                    if (DataHolder.localCurrency != null)
                        walletAmount.setText(DataHolder.localCurrency + " " + dc2.format(walletAmountValue));
                    else locationMetaDataFetchSignal.sendMessage(new Message());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    walletAmount.setText("!");
                    Toast.makeText(WalletActivity.this, "Error connecting to database!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private Handler locationMetaDataFetchSignal = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.arg1 == 1) walletAmount.setText(DataHolder.localCurrency + " " + walletAmountValue);
            else DataHolder.getInstance().fetchLocationMetadata(locationMetaDataFetchSignal, DataHolder.location, WalletActivity.this);
            return false;
        }
    });
}
