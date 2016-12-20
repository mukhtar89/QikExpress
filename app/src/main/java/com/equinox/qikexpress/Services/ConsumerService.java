package com.equinox.qikexpress.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.equinox.qikexpress.Utils.GetOrders;

public class ConsumerService extends Service {

    private ConsumerServiceInterface consumerServiceInterface = new ConsumerServiceInterface();
    private Handler ordersHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return consumerServiceInterface;
    }

    public class ConsumerServiceInterface extends Binder {

        public void setOrdersHandler (Handler handler) {
            ordersHandler = handler;
        }

        public void getOrders() {
            GetOrders.updateMeta(ordersHandler, getApplicationContext());
            GetOrders.getOrders();
        }

    }
}
