package com.equinox.qikexpress.Utils;

import android.os.Handler;
import android.os.Message;

import com.equinox.qikexpress.Models.Grocery;
import com.equinox.qikexpress.Models.Place;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mukht on 10/31/2016.
 */

public class ListSortFunc<T extends Place> {

    private T[] arrayList;
    private int size;
    private Handler handler;

    public ListSortFunc(Class<T> c, int s, Handler handler) {
        @SuppressWarnings("unchecked")
        final T[] arrayList = (T[]) Array.newInstance(c, s);
        this.arrayList = arrayList;
        this.size = s;
        this.handler = handler;
    }

    public List<T> sortByDistance(final Object[] listItems) {
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
        } catch (Exception e) {
            handler.sendMessage(new Message());
        }
        return Arrays.asList(arrayList);
    }
}
