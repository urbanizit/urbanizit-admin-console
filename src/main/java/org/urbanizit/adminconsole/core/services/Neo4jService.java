package org.urbanizit.adminconsole.core.services;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urbanizit.adminconsole.core.ConfigurationService;
import org.urbanizit.adminconsole.core.GraphService;

import javax.inject.Inject;

/**
 * @author nicolasger
 */
public class Neo4jService implements GraphService {
    private static final Logger logger = LoggerFactory.getLogger(Neo4jService.class);

    private GraphDatabaseService graphDb;
    @Inject
    private ConfigurationService config;

    @Override
    public GraphDatabaseService getDB() {
        if (graphDb == null) {
            graphDb = new RestGraphDatabase(config.getDatabaseURI());
            if (graphDb != null) {
                logger.info("Connected to database ({})", config.getDatabaseURI());
            } else {
                throw new IllegalArgumentException("The neo4j database (" + config.getDatabaseURI() + ") is not reachable");
            }
        }
        return graphDb;
    }
}
