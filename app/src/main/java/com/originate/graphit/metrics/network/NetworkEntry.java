package com.originate.graphit.metrics.network;

public class NetworkEntry {
    private long time;
    private float down;
    private float up;

    public NetworkEntry() {
    }

    public NetworkEntry(long time, float up, float down) {
        this.time = time;
        this.down = up;
        this.up = down;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public void setDown(float pct) {
        this.down = pct;
    }

    public float getDown() {
        return this.down;
    }

    public void setUp(float up) {
        this.up = up;
    }

    public float getUp() {
        return this.up;
    }
}
