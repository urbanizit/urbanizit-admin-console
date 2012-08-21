package org.urbanizit.adminconsole.pojo;

/**
 * @author Nicolas Geraud
 */
public class Relationship {

    private String use;
    private String type;
    private String method;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("  - use : ").append(use).append("\n")
                .append("    types : ").append(type).append("\n")
                .append("    method : ").append(method).append("\n");
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
