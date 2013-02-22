package org.urbanizit.adminconsole;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.urbanizit.adminconsole.core.*;
import org.urbanizit.adminconsole.core.types.ElementType;
import org.urbanizit.adminconsole.core.types.MapType;
import org.urbanizit.adminconsole.core.types.RelationType;
import org.urbanizit.adminconsole.modules.GraphModule;
import org.urbanizit.adminconsole.modules.UrbanizerModule;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Nicolas Geraud
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
        Assert.assertEquals(map.getProperty("type"), ElementType.MAP.toString());
        Assert.assertEquals(map.getProperty("mapType"), MapType.APPLICATION.toString());
        Assert.assertEquals(map.getProperty("name"), "test");
        //validate relationships
        Iterator<Relationship> relationships = map.getRelationships(RelationType.CONTAIN, Direction.INCOMING).iterator();
        Assert.assertTrue(relationships.hasNext());
        Relationship relation = relationships.next();
        Assert.assertFalse(relationships.hasNext());
        Node root = relation.getStartNode();
        Assert.assertEquals(root.getId(), 0L);
        urbanizer.deleteAll();
        Assert.assertEquals(urbanizer.listMaps().size(), 0);
        urbanizer.addMap("test", MapType.APPLICATION);
        map = urbanizer.getMap("test", MapType.APPLICATION);
        Path importPath = Paths.get("external-resources/datas-to-import/").toAbsolutePath();
        /*
         import following graph :
             map
                 -> application1
                     -> component1
                         -> component2
                         -> component3
                     -> component2
                         -> component1
                         -> component3
                 -> application2
                     -> component3
                     -> COMPONENT_4
         */
        try {
            urbanizer.populate(importPath, map);
            Iterable<Relationship> outgoingRelationships = map.getRelationships(Direction.OUTGOING);
            Map<String, Integer> applications = new HashMap<>();
            applications.put("application1", 0);
            applications.put("application2", 0);
            applications.put("COMPONENT_4", 0);
            //control number of applications
            List<Relationship> rels = new ArrayList<>();
            for(Relationship rel : outgoingRelationships) {
                rels.add(rel);
                Assert.assertTrue(rel.isType(RelationType.CONTAIN));
            }
            //control applications
            Assert.assertEquals(applications.size(), rels.size());
            for(Relationship rel : rels) {
                Node application = rel.getEndNode();
                Assert.assertEquals(application.getProperty("type"), ElementType.APPLICATION.toString());
                Assert.assertTrue(applications.containsKey(application.getProperty("name")));
                Integer count = applications.get(application.getProperty("name"));
                Assert.assertEquals(new Integer(0), count);
                applications.put((String) application.getProperty("name"), ++count);
            }
            //control index
            Assert.assertTrue(graphDatabase.index().existsForNodes("componentNames"));
            Index<Node> components = graphDatabase.index().forNodes("componentNames");
            Assert.assertEquals(4, components.query("name","*COMP*").size());
            Assert.assertEquals(4, components.query("name","*omp*").size());

            //control component


        } catch (IOException e){
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }

    }
}
