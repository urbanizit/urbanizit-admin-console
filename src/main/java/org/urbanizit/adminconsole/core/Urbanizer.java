package org.urbanizit.adminconsole.core;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urbanizit.adminconsole.Configuration;
import org.urbanizit.adminconsole.pojo.Component;
import org.urbanizit.adminconsole.pojo.Relationship;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;


public class Urbanizer {
	private static final Logger logger = LoggerFactory
			.getLogger(Urbanizer.class);

	private Urbanizer() {}

    //TODO refactoring
	private static Node getUnivers(boolean createIfNotExist) {
		Node univers = DbManager.getDB().getNodeById(0);
		if (univers != null) {
            try {
                if(univers.getProperty("isRoot").equals(true))
                    return univers;
            } catch (NotFoundException nfe) {
                if (univers.getPropertyKeys().iterator().hasNext()) {
                    throw new RuntimeException("The dabase doesn't seem to be well configured : no root node for all maps");
                } else {
                    univers.setProperty("isRoot", true);
                    return univers;
                }
            }
		} else if(createIfNotExist) {
            if(univers==null)
                univers = DbManager.getDB().createNode();
            univers.setProperty("isRoot", true);
            return univers;
        }
		throw new RuntimeException("the database doesn't seem to be well configured : no root node for all maps");
	}
	
	public static List<org.urbanizit.adminconsole.pojo.Map> listMaps() {
		List<org.urbanizit.adminconsole.pojo.Map> maps = new ArrayList<>();
		for(org.neo4j.graphdb.Relationship relationship : getUnivers(false).getRelationships(Direction.OUTGOING, RelationType.CONTAIN)) {
			org.urbanizit.adminconsole.pojo.Map m = new org.urbanizit.adminconsole.pojo.Map();
			m.setId(relationship.getEndNode().getId());
			m.setName(relationship.getEndNode().getProperty("name").toString());
			m.setType(MapType.valueOf(relationship.getEndNode().getProperty("type").toString()));
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
	public static void populate(final Path fromPath, final String graphName) throws IOException {
		logger.info("importing {}", fromPath);

		if (!Files.isDirectory(fromPath, LinkOption.NOFOLLOW_LINKS)) {
			throw new IllegalArgumentException(
					"You must specify a valid directory path where datas to import are");
		}

		final HashMap<String, Node> nameIdNodeMap = new HashMap<>();
		final HashMap<Node, List<Relationship>> relationships = new HashMap<>();
		
		//prepare indexes
		IndexManager index = DbManager.getDB().index();
		final Index<Node> componentNames = index.forNodes( "componentNames" );
		//add components
		Files.walkFileTree(fromPath, new SimpleFileVisitor<Path>() {
			Yaml yaml = new Yaml(new Constructor(Component.class));
			Node app;
			Node root;
			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				//ignore root element
				if(dir.equals(fromPath)) {
					logger.info("create root node");
                    root = DbManager.getDB().createNode();
                    root.setProperty("name", graphName);
                    root.setProperty("date", new SimpleDateFormat("dd/mm/yyyy").format(new Date()));
				}
				//create application node
				else {
					logger.info("-- import datas from application {}", dir.getFileName());
					app = DbManager.getDB().createNode();
					app.setProperty("name", dir.getFileName().toString());
					root.createRelationshipTo(app, RelationType.CONTAIN);
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
				Node nodeComponent = DbManager.getDB().createNode();
				nodeComponent.setProperty("name", component.getName());
				nodeComponent.setProperty("filename", component.getFilename());
				nodeComponent.setProperty("type", component.getType());
				nodeComponent.createRelationshipTo(app, RelationType.MEMBER_OF);
				
				nameIdNodeMap.put(component.getName(), nodeComponent);
				if(component.getRelationships() != null)
					relationships.put(nodeComponent, component.getRelationships());
				
				componentNames.add(nodeComponent, "name", component.getName());
				componentNames.add(nodeComponent, "name", component.getFilename());
				return FileVisitResult.CONTINUE;
			}
		});
		
		//add relationships
		for(Map.Entry<Node, List<Relationship>> entry : relationships.entrySet()) {
			for(Relationship relation : entry.getValue()) {
				Node to = nameIdNodeMap.get(relation.getUse());
				if(to == null) {
					logger.info("Ignore relation from {} to {}", entry.getKey().getId(), relation.getUse());
				} else {
					org.neo4j.graphdb.Relationship r = entry.getKey().createRelationshipTo(to, RelationType.USE);
					r.setProperty("type", relation.getType());
				}
			}
		}
	}

    public static void initializeDb() {
        Transaction tx = DbManager.getDB().beginTx();
        try {
            if(Configuration.isEmbedded()) {
                GlobalGraphOperations ggo = GlobalGraphOperations.at(DbManager.getDB());

                for(org.neo4j.graphdb.Relationship r : ggo.getAllRelationships()) {
                    r.delete();
                }

                for(Node n : ggo.getAllNodes()) {
                    n.delete();
                }
            }

            getUnivers(true);

            tx.success();
        } finally {
            tx.finish();
        }
    }

    public static void startDb(){
        DbManager.start();
    }

    public static void stopDb(){
        DbManager.shutdown();
    }

}
