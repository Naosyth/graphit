package com.originate.graphit;

public class MetricModel {
    private String name;
    private String key;
    private boolean enabled;

    public MetricModel(String name, String key) {
        this.name = name;
        this.key = key;
        this.enabled = false;
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
