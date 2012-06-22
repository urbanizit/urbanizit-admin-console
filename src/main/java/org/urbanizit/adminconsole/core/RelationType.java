package org.urbanizit.adminconsole.core;

import org.neo4j.graphdb.RelationshipType;

public enum RelationType implements RelationshipType {
	USE,
	MEMBER_OF,
    CONTAIN;
}
