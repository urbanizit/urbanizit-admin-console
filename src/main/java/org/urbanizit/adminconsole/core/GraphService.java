package org.urbanizit.adminconsole.core;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * @author Nicolas Geraud
 */
public interface GraphService {
    GraphDatabaseService getDB();
}
