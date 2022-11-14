package com.murongyehua.tpplus.common;

import java.util.List;

public class TpInfo {

    private String name;

    private String location;

    private List<String> canTpLocation;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getCanTpLocation() {
        return canTpLocation;
    }

    public void setCanTpLocation(List<String> canTpLocation) {
        this.canTpLocation = canTpLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
