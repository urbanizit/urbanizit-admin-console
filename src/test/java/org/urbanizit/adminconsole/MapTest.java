package org.urbanizit.adminconsole;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.urbanizit.adminconsole.core.*;
import org.urbanizit.adminconsole.core.types.ElementType;
import org.urbanizit.adminconsole.core.types.MapType;
import org.urbanizit.adminconsole.core.types.RelationType;
import org.urbanizit.adminconsole.modules.GraphModule;
import org.urbanizit.adminconsole.modules.UrbanizerModule;

import java.io.IOException;
import java.util.Iterator;

/**
 * User: nicolasger
 * Date: 14/08/12
 * Time: 13:30
 */
public class MapTest extends LocalServerTest {

    private Urbanizer urbanizer;
    @Before public void init() throws IOException {
        Injector injector = Guice.createInjector(new UrbanizerModule(), new GraphModule());
        urbanizer = injector.getInstance(Urbanizer.class);
    }

    @Test public void addMap() {
        Assert.assertEquals(urbanizer.listMaps().size(), 0);
        urbanizer.addMap("test", MapType.APPLICATION);
        //validate creation
        Assert.assertEquals(urbanizer.listMaps().size(), 1);
        Node map = urbanizer.getMap("test", MapType.APPLICATION);
        Assert.assertNotNull(map);
        //validate node content
        Assert.assertEquals(map.getProperty("types"), ElementType.MAP.toString());
        Assert.assertEquals(map.getProperty("mapType"), MapType.APPLICATION.toString());
        Assert.assertEquals(map.getProperty("name"), "test");
        //validate relationships
        Iterator<Relationship> relationships = map.getRelationships(RelationType.CONTAIN, Direction.INCOMING).iterator();
        Assert.assertTrue(relationships.hasNext());
        Relationship relation = relationships.next();
        Assert.assertFalse(relationships.hasNext());
        Node root = relation.getStartNode();
        Assert.assertEquals(root.getId(), 0L);
    }

}
