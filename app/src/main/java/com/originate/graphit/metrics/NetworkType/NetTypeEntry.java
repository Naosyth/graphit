package com.originate.graphit.metrics.NetworkType;

public class NetTypeEntry {
    private long time;
    private int type;

    public NetTypeEntry() {
    }

    public NetTypeEntry(long time, int type) {
        this.type = type;
        this.time = time;
    }

    public void setTime(long time) {
      this.time = time;
    }

    public long getTime() {
      return this.time;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
         return this.type;
    }
}
