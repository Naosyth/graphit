package com.originate.graphit.metrics.battery;

public class BatteryEntry {
    private int id;
    private float percentage;
    private long time;

    public BatteryEntry() {
    }

    public BatteryEntry(int id, float pct, long time) {
        this.id = id;
        this.percentage = pct;
        this.time = time;
    }

    public BatteryEntry(float pct, long time) {
        this.percentage = pct;
        this.time = time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
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
