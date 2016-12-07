package com.equinox.qikexpress.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.Models.Place;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mukht on 10/31/2016.
 */

public class ListSortFunc<T extends Place> {

    private T[] arrayList;
    private int size;
    private Handler handler;
    private Message message;
    private ProgressDialog progressDialog;

    public ListSortFunc(Class<T> c, int s, Handler handler, Context context) {
        @SuppressWarnings("unchecked")
        final T[] arrayList = (T[]) Array.newInstance(c, s);
        this.arrayList = arrayList;
        this.size = s;
        this.handler = handler;
        this.message = new Message();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Sorting List...");
    }

    public List<T> sortByDistance(final Object[] listItems) {
        showpDialog();
        try {
            for (int i = 0; i < size; i++)
                arrayList[i] = (T) listItems[i];
            for (int i = 0; i < arrayList.length - 1; i++) {
                for (int j = 0; j < arrayList.length - i - 1; j++) {
                    if (arrayList[j].getDistanceFromCurrent() > arrayList[j + 1].getDistanceFromCurrent()) {
                        T temp = arrayList[j];
                        arrayList[j] = arrayList[j + 1];
                        arrayList[j + 1] = temp;
                    }
                }
            }
            hidepDialog();
            return Arrays.asList(arrayList);
        } catch (Exception e) {
            final Message message = new Message();
            message.arg1 = 1;
            handler.sendMessage(message);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendMessage(message);
                }
            }, 1000);
            return null;
        }
    }

    public List<T> sortByTime(final Object[] listItems) {
        showpDialog();
        try {
            for (int i = 0; i < size; i++)
                arrayList[i] = (T) listItems[i];
            for (int i = 0; i < arrayList.length - 1; i++) {
                for (int j = 0; j < arrayList.length - i - 1; j++) {
                    if (arrayList[j].getTimeFromCurrent() > arrayList[j + 1].getTimeFromCurrent()) {
                        T temp = arrayList[j];
                        arrayList[j] = arrayList[j + 1];
                        arrayList[j + 1] = temp;
                    }
                }
            }
        } catch (Exception e) {
            final Message message = new Message();
            message.arg1 = 2;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendMessage(message);
                }
            }, 1000);
        }
        hidepDialog();
        return Arrays.asList(arrayList);
    }

    private void hidepDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void showpDialog() {
        if (progressDialog != null) {
            if (!progressDialog.isShowing())
                progressDialog.show();
        }
    }
}
