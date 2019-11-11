package com.cj.ticketsys.entities;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public abstract class PropertyEntity implements Serializable{
    private String properties = "";
    private Map<String, Object> map = new TreeMap<>();

    public String getProperties() {
        return properties;
    }

    public Map<String, Object> getMap() {
        return this.map;
    }

    public void setProperties(String properties) {
        this.properties = properties;
        synchronized (this) {
            if (!Strings.isNullOrEmpty(properties)) {
                map = JSON.parseObject(properties);
            }
        }
    }

    public void setProperty(String name, Object value) {
        synchronized (this) {
            if (map.containsKey(name)) {
                map.replace(name, value);
                refreshProperties();
                return;
            }
            map.put(name, value);
            refreshProperties();
        }
    }

    public Object getProperty(String name) {
        synchronized (this) {
            if (map.containsKey(name)) {
                return map.get(name);
            }
        }
        return null;
    }

    public void removeProperty(String name) {
        synchronized (this) {
            if (!map.containsKey(name)) {
                return;
            }
            map.remove(name);
            refreshProperties();
        }
    }

    public int propertySize() {
        return this.map.size();
    }

    private void refreshProperties() {
        this.properties = JSON.toJSONString(map);
    }
}
