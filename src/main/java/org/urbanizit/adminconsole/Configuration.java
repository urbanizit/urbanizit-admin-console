package org.urbanizit.adminconsole;

import java.util.ResourceBundle;

/**
 *
 * @author nicolasger
 */
public class Configuration {
	private static final ResourceBundle rs = ResourceBundle.getBundle("urbanizit");

    public static boolean isEmbedded() {
        return Boolean.parseBoolean(rs.getString("neo4j.embedded"));
    }

    public static String getDatabaseDirectory() {
        return rs.getString("neo4j.db.dir");
    }
	
	public static String getDatabaseURI() {
		return rs.getString("neo4j.db.url");
	}
}
