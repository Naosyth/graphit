package com.originate.graphit.metrics.battery;

public class BatteryEntry {
    private long time;
    private int percentage;
    private boolean critical;

    public BatteryEntry() {
    }

    public BatteryEntry(long time, int pct, boolean critical) {
        this.percentage = pct;
        this.time = time;
        this.critical = critical;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public void setPercentage(int pct) {
        this.percentage = pct;
    }

    public int getPercentage() {
        return this.percentage;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    public boolean getCritical() {
        return this.critical;
    }
}
