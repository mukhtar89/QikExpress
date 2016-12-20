package com.equinox.qikexpress.Services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class OrderService extends Service implements ServiceConnection{

    private ConsumerService.ConsumerServiceInterface consumerServiceInterface;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getApplicationContext().bindService(new Intent(this, ConsumerService.class), this, BIND_AUTO_CREATE);
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
