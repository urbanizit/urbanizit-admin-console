package org.urbanizit.adminconsole.core.types;

import org.neo4j.graphdb.RelationshipType;

/**
 * @author nicolasger
 */
public enum RelationType implements RelationshipType {
    USE,
    MEMBER_OF,
    CONTAIN;
}
