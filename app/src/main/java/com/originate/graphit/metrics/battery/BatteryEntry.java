package com.originate.graphit.metrics.battery;

public class BatteryEntry {
    private float percentage;
    private long time;

    public BatteryEntry() {
    }

    public BatteryEntry(float pct, long time) {
        this.percentage = pct;
        this.time = time;
    }

    public void setPercentage(float pct) {
        this.percentage = pct;
    }

    public float getPercentage() {
        return this.percentage;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }
}
