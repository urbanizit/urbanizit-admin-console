package org.urbanizit.adminconsole.pojo;

import org.urbanizit.adminconsole.core.MapType;

/**
 * @author nicolasger
 */
public class Map {
	private long id;
	private MapType type;
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

	public MapType getType() {
		return type;
	}

	public void setType(MapType type) {
		this.type = type;
	}
}