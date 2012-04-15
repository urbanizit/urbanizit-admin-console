package org.urbanizit.adminconsole.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urbanizit.adminconsole.pojo.Component;
import org.urbanizit.adminconsole.pojo.Relationship;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;


public class Urbanizer {
	private static final Logger logger = LoggerFactory
			.getLogger(Urbanizer.class);
	private GraphDatabaseService graphDb;

	public Urbanizer(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
	}

	/**
	 * fromPath tree is :
	 * ROOT
	 * |_domain1
	 * |  |_application1
	 * |  |  |_component1
	 * |  |  |_component2
	 * |  |_application2
	 * |  |  |_component3
	 * |  |  |_component4
	 * |_domain2
	 * 
	 * @param fromPath
	 * @throws IOException
	 */
	public void populate(final Path fromPath) throws IOException {
		logger.info("importing {}", fromPath);

		if (!Files.isDirectory(fromPath, LinkOption.NOFOLLOW_LINKS)) {
			throw new IllegalArgumentException(
					"You must specify a valid directory path where datas to import are");
		}

		final HashMap<String, Node> nameIdNodeMap = new HashMap<>();
		final HashMap<Node, List<Relationship>> relationships = new HashMap<>();
		
		//prepare indexes
		IndexManager index = graphDb.index();
		final Index<Node> coponentNames = index.forNodes( "coponentNames" );
		//add components
		Files.walkFileTree(fromPath, new SimpleFileVisitor<Path>() {
			Yaml yaml = new Yaml(new Constructor(Component.class));
			Node app;
			Node domain;
			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				//ignore root element
				if(dir.equals(fromPath)) {
					logger.info("ignoring root path {}", dir);

				} 
				//create domain node
				else if(dir.getParent().equals(fromPath)){
					logger.info("-- import datas from domain {}", dir.getFileName());
					domain = graphDb.createNode();
					domain.setProperty("name", dir.getFileName().toString());
					logger.debug("name : {}", dir.getFileName());
				} 
				//create application node
				else {
					logger.info("-- import datas from application {}", dir.getFileName());
					app = graphDb.createNode();
					app.setProperty("name", dir.getFileName().toString());
					app.createRelationshipTo(domain, RelationType.MEMBER_OF);
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
				Node nodeComponent = graphDb.createNode();
				nodeComponent.setProperty("name", component.getName());
				nodeComponent.setProperty("filename", component.getFilename());
				nodeComponent.setProperty("type", component.getType());
				nodeComponent.createRelationshipTo(app, RelationType.MEMBER_OF);
				
				nameIdNodeMap.put(component.getName(), nodeComponent);
				if(component.getRelationships() != null)
					relationships.put(nodeComponent, component.getRelationships());
				
				coponentNames.add(nodeComponent, "name", component.getName());
				coponentNames.add(nodeComponent, "name", component.getFilename());
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
}
