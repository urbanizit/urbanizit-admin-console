package org.urbanizit.adminconsole.pojo;

public class Relationship {

	private String use;
	private String type;
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
		.append("  - use : ").append(use).append("\n")
		.append("    type : ").append(type).append("\n");
		return sb.toString();
	}
	
	public String getUse() {
		return use;
	}
	public void setUse(String use) {
		this.use = use;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
