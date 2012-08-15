package org.urbanizit.adminconsole.core;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * User: nicolasger
 * Date: 16/08/12
 * Time: 00:01
 */
public interface GraphService {
    GraphDatabaseService getDB();
}
