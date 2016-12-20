package com.equinox.qikexpress.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.equinox.qikexpress.Services.ConsumerService;

/**
 * Created by mukht on 12/20/2016.
 */

public class BaseOrderActivity extends AppCompatActivity implements ServiceConnection{

    private ConsumerService.ConsumerServiceInterface consumerServiceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().bindService(new Intent(this, ConsumerService.class), this, BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (ConsumerService.class.getName().equals(name.getClassName())) {
            consumerServiceInterface = (ConsumerService.ConsumerServiceInterface) service;
            onServiceConnected();
        }
    }

    protected void onServiceConnected() {
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (ConsumerService.class.getName().equals(name.getClassName())) {
            consumerServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceDisconnected() {
    }

    protected ConsumerService.ConsumerServiceInterface getConsumerServiceInterface() {
        return consumerServiceInterface;
    }
}
