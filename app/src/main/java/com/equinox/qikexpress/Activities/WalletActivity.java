package com.equinox.qikexpress.Activities;

import android.app.ProgressDialog;
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

import com.equinox.qikexpress.R;
import com.equinox.qikexpress.Utils.AppVolleyController;
import com.equinox.qikexpress.Utils.FetchGeoAddress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import static com.equinox.qikexpress.Models.DataHolder.currentUser;
import static com.equinox.qikexpress.Models.DataHolder.userDatabaseReference;

public class WalletActivity extends AppCompatActivity {

    private TextView walletAmount;
    private LinearLayout creditMoney, debitMoney;
    private DatabaseReference walletReference;
    private Float walletAmountValue = (float) 0.00;
    private DecimalFormat dc2 = new DecimalFormat(".##");
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.category_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Wallet Amount...");
        progressDialog.show();

        walletAmount = (TextView) findViewById(R.id.wallet_amount);
        creditMoney = (LinearLayout) findViewById(R.id.credit_money_wallet);
        debitMoney = (LinearLayout) findViewById(R.id.debit_money_wallet);
        walletReference = userDatabaseReference.child("wallet");
        walletReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    walletReference.setValue(0.00);
                else if (dataSnapshot.getValue() instanceof Double)
                    walletAmountValue = (float) (double) dataSnapshot.getValue();
                else walletAmountValue = (float) (long) dataSnapshot.getValue();
                if (currentUser.getLocalCurrency() != null) {
                    walletAmount.setText(currentUser.getLocalCurrency() + " " + dc2.format(walletAmountValue));
                    progressDialog.dismiss();
                }
                else walletCurrencyHandler.sendMessage(new Message());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                walletAmount.setText("!");
                Toast.makeText(WalletActivity.this, "Error connecting to database!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppVolleyController.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppVolleyController.activityResumed();
    }

    private Handler walletCurrencyHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (currentUser.getLocalCurrency() != null) {
                walletAmount.setText(currentUser.getLocalCurrency() + " " + walletAmountValue);
                progressDialog.dismiss();
            }
            else if (AppVolleyController.isActivityVisible())
                new FetchGeoAddress().fetchCurrencyMetadata(walletCurrencyHandler);
            return false;
        }
    });
}
