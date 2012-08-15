package org.urbanizit.adminconsole.core.types;

import org.neo4j.graphdb.RelationshipType;

public enum RelationType implements RelationshipType {
    USE,
    MEMBER_OF,
    CONTAIN;
}
