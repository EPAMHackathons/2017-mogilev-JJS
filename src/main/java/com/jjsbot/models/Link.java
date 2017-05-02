package com.jjsbot.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Link {
    @JsonProperty("source")
    private int source;
    @JsonProperty("target")
    private int target;

    @Override
    public int hashCode() {
        return source;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Link && this.source == ((Link) obj).getSource();
    }
}
