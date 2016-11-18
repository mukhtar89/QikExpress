package com.equinox.qikexpress.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mukht on 11/12/2016.
 */

public class Periods {

    private CloseOpen[] closeOpen;
    private List<CloseOpen[]> periods = new ArrayList<>();

    public CloseOpen[] getCloseOpen() {
        return closeOpen;
    }

    public CloseOpen[] getNewCloseOpen() {
        CloseOpen[] tempCloseOpen = {new CloseOpen("close"), new CloseOpen("open")};
        return tempCloseOpen;
    }

    public void setCloseOpen(CloseOpen[] closeOpen) {
        this.closeOpen = closeOpen;
    }

    public List<CloseOpen[]> getPeriods() {
        return periods;
    }

    public void setPeriods(List<CloseOpen[]> periods) {
        this.periods = periods;
    }

    public class CloseOpen {
        private String type;
        private Integer day, time;

        public CloseOpen(String type) {
            this.type = type;
        }

        public Integer getDay() {
            return day;
        }

        public void setDay(Integer day) {
            this.day = day;
        }

        public Integer getTime() {
            return time;
        }

        public void setTime(Integer time) {
            this.time = time;
        }
    }

}
