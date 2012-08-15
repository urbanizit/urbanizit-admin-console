package org.urbanizit.adminconsole.pojo;

import org.urbanizit.adminconsole.core.types.MapType;

/**
 * @author nicolasger
 */
public class Map {
    private long id;
    private MapType mapType;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MapType getMapType() {
        return mapType;
    }

    public void setMapType(MapType MapType) {
        this.mapType = mapType;
    }
}