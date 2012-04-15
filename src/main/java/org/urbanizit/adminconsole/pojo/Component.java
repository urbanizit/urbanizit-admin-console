package org.urbanizit.adminconsole.pojo;

import java.util.List;

public class Component {

	private String name;
	private String filename;
	private String type;
	private String domain;
	private List<Relationship> relationships;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("\n")
		.append("-----------------------------").append("\n")
		.append("name : ").append(name).append("\n")
		.append("filename : ").append(filename).append("\n")
		.append("type : ").append(type).append("\n")
		.append("domain : ").append(domain).append("\n")
		;
		if(relationships!=null) {
			sb.append("relationships : ").append("\n");
			for(Relationship r : relationships) {
				sb.append(r.toString());
			}
		}
		sb.append("-----------------------------");
		return sb.toString();
	}
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public List<Relationship> getRelationships() {
		return relationships;
	}
	public void setRelationships(List<Relationship> relationships) {
		this.relationships = relationships;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
