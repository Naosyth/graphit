package com.originate.graphit.metrics.screenUsage;

public class ScreenEntry {
    private long time;
    private boolean on;

    public ScreenEntry() {
    }

    public ScreenEntry(long time, boolean on) {
        this.on = on;
        this.time = time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public boolean getOn() {
        return this.on;
    }
}
