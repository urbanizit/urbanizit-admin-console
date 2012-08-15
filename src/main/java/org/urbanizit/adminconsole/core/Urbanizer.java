package org.urbanizit.adminconsole.core;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.IteratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urbanizit.adminconsole.core.types.ElementType;
import org.urbanizit.adminconsole.core.types.MapType;
import org.urbanizit.adminconsole.core.types.RelationType;
import org.urbanizit.adminconsole.pojo.Component;
import org.urbanizit.adminconsole.pojo.Relationship;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class Urbanizer {
    private static final Logger logger = LoggerFactory.getLogger(Urbanizer.class);
    private GraphService graphService;

    @Inject
    public Urbanizer(GraphService graphService) {
        this.graphService = graphService;
    }

    public void addMap(String name, MapType type) {
        Node map = getMap(name, type);
        if (map != null) {
            throw new IllegalArgumentException("the map already exist !");
        }
        Node universe = graphService.getDB().getReferenceNode();
        map = graphService.getDB().createNode();
        map.setProperty("name", name);
        map.setProperty("types", ElementType.MAP.toString());
        map.setProperty("mapType", type);
        universe.createRelationshipTo(map, RelationType.CONTAIN);
    }

    public Node getMap(String name, MapType type) {
        ExecutionEngine engine = new ExecutionEngine(graphService.getDB());
        Map<String, Object> params = new HashMap<>();
        params.put("rootNodeId", graphService.getDB().getReferenceNode().getId());
        params.put("mapName", name);
        params.put("types", ElementType.MAP.toString());
        params.put("mapType", type.toString());
        ExecutionResult result = engine.execute(
                " START root=node({rootNodeId}) " +
                " MATCH root-[r:CONTAIN]->map " +
                " WHERE map.types={types} " +
                " AND map.name={mapName} " +
                " AND map.mapType={mapType} " +
                " RETURN map"
                , params);
        Iterator<Node> map_column = result.columnAs("map");
        Node map = null;
        try {
            map = IteratorUtil.first(map_column);
        } catch (NoSuchElementException nse) {
            logger.debug("element {}:{} not found", name, type);
        }
        return map;
    }

    public List<org.urbanizit.adminconsole.pojo.Map> listMaps() {
        List<org.urbanizit.adminconsole.pojo.Map> maps = new ArrayList<>();
        for (org.neo4j.graphdb.Relationship relationship : graphService.getDB().getReferenceNode().getRelationships(Direction.OUTGOING, RelationType.CONTAIN)) {
            org.urbanizit.adminconsole.pojo.Map m = new org.urbanizit.adminconsole.pojo.Map();
            m.setId(relationship.getEndNode().getId());
            m.setName(relationship.getEndNode().getProperty("name").toString());
            m.setMapType(MapType.valueOf(relationship.getEndNode().getProperty("mapType").toString()));
            maps.add(m);
        }
        return maps;
    }

    /**
     * fromPath tree is :
     * ROOT_MAP
     * |_application1
     * |  |_component1
     * |  |_component2
     * |_application2
     * |  |_component3
     * |  |_component4
     *
     * @param fromPath
     * @throws IOException
     */
    public void populate(final Path fromPath, final Node map) throws IOException {
        logger.info("importing {}", fromPath);

        if (!Files.isDirectory(fromPath, LinkOption.NOFOLLOW_LINKS)) {
            throw new IllegalArgumentException(
                    "You must specify a valid directory path where datas to import are");
        }

        final HashMap<String, Node> nameIdNodeMap = new HashMap<>();
        final HashMap<Node, List<Relationship>> relationships = new HashMap<>();

        //prepare indexes
        IndexManager index = graphService.getDB().index();
        final Index<Node> componentNames = index.forNodes("componentNames");
        //add components
        Files.walkFileTree(fromPath, new SimpleFileVisitor<Path>() {
            Yaml yaml = new Yaml(new Constructor(Component.class));
            Node app;

            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                                                     BasicFileAttributes attrs) throws IOException {
                //ignore root element
                if (dir.equals(fromPath)) {
                    logger.info("ignore root");
                }
                //create application node
                else {
                    logger.info("-- import datas from application {}", dir.getFileName());
                    app = graphService.getDB().createNode();
                    app.setProperty("name", dir.getFileName().toString());
                    map.createRelationshipTo(app, RelationType.CONTAIN);
                    logger.debug("name : {}", dir.getFileName());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file,
                                             BasicFileAttributes attrs) throws IOException {
                logger.info("---- import datas from component {}", file.getFileName());
                Component component = (Component) yaml.load(new FileInputStream(file.toFile()));
                logger.debug(component.toString());
                //create component node
                Node nodeComponent = graphService.getDB().createNode();
                nodeComponent.setProperty("name", component.getName());
                nodeComponent.setProperty("filename", component.getFilename());
                nodeComponent.setProperty("types", component.getType());
                nodeComponent.createRelationshipTo(app, RelationType.MEMBER_OF);

                nameIdNodeMap.put(component.getName(), nodeComponent);
                if (component.getRelationships() != null)
                    relationships.put(nodeComponent, component.getRelationships());

                componentNames.add(nodeComponent, "name", component.getName());
                componentNames.add(nodeComponent, "name", component.getFilename());
                return FileVisitResult.CONTINUE;
            }
        });

        //add relationships
        for (Map.Entry<Node, List<Relationship>> entry : relationships.entrySet()) {
            for (Relationship relation : entry.getValue()) {
                Node to = nameIdNodeMap.get(relation.getUse());
                if (to == null) {
                    logger.info("Ignore relation from {} to {}", entry.getKey().getId(), relation.getUse());
                } else {
                    org.neo4j.graphdb.Relationship r = entry.getKey().createRelationshipTo(to, RelationType.USE);
                    r.setProperty("types", relation.getType());
                    r.setProperty("method", relation.getMethod());
                }
            }
        }
    }

    public void initializeDb() {
        /*Transaction tx = DbManager.getDB().beginTx();
        try {
            if (Configuration.isEmbedded()) {
                GlobalGraphOperations ggo = GlobalGraphOperations.at(DbManager.getDB());

                for (org.neo4j.graphdb.Relationship r : ggo.getAllRelationships()) {
                    r.delete();
                }

                for (Node n : ggo.getAllNodes()) {
                    n.delete();
                }
            }

            getUniverse(true);

            tx.success();
        } finally {
            tx.finish();
        } */
    }


}
