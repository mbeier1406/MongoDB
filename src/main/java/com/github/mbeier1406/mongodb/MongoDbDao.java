package com.github.mbeier1406.mongodb;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

/**
 * MongoDB-Implementierung für das Persitieren von E-Rezepten.
 * @author mbeier
 */
public class MongoDbDao implements Dao<Dao.ERezept> {

	private static final Logger LOGGER = LogManager.getLogger(MongoDbDao.class);

	/** Die Datenbank, in der die E-Rezepte gespeichert werden, ist {@value} */
	private static final String ERX_DB = "erezepte";

	/** URL, User, AuthenticationDatabase und Passwort zur Datenbank */
	private String url, user, authDb, pass;

	/** Client für DB-Zugriffe */
	private MongoClient mongoClient;

	/** Datenbank für E-Rezepte */
	private MongoDatabase erxDatabase;

	public MongoDbDao() throws IOException {
		final var prop = new Properties();
		prop.load(getClass().getResourceAsStream("application.properties"));
		connect((String) prop.get("url"), (String) prop.get("user"), (String) prop.get("authenticationDatabase"), (String) prop.get("pass"));
	}

	public MongoDbDao(final String url, final String user, final String authDb, final String pass) {
		connect(url, user, authDb, pass);
	}

	/** Verbindung zur MongoDB */
	private void connect(final String url, final String user, final String authDb, final String pass) {
		this.url = requireNonNull(url, "URL ist NULL!");
		this.user = requireNonNull(user, "User ist NULL!");
		this.authDb = requireNonNull(authDb, "AuthenticationDatabase ist NULL!");
		this.pass = requireNonNull(pass, "Passwort ist NULL!");
		LOGGER.trace("URL: {}", this.url);
		mongoClient = MongoClients.create(
			MongoClientSettings
				.builder()
				.credential(MongoCredential.createCredential(this.user, this.authDb, this.pass.toCharArray()))
				.applicationName("E-Rezept Client")
				.applyConnectionString(new ConnectionString(this.url))
				.build()
		);
		erxDatabase = mongoClient.getDatabase(ERX_DB);
	}

	/** {@inheritDoc} */
	@Override
	public String getConnectionInfo() {
		return url;
	}

	/** {@inheritDoc} */
	@Override
	public List<String> getDatabaseNames() {
		final List<String> dbs = new ArrayList<>();
		MongoIterable<String> listDatabaseNames = mongoClient.listDatabaseNames();
		listDatabaseNames.forEach(n -> dbs.add(n));
		return dbs;
	}

	/** {@inheritDoc} */
	@Override
	public List<String> getCollectionNames(final String db) {
		final List<String> colls = new ArrayList<>();
		final var database = mongoClient.getDatabase(requireNonNull(db, "db ist NULL!"));
		final var listCollectionNames = database.listCollectionNames();
		listCollectionNames.forEach(n -> colls.add(n));
		return colls;
	}

	/** {@inheritDoc} */
	@Override
	public Optional<ERezept> find(String eRezeptId) {
		final Map<String, ERezept> eRezept = new HashMap<>();
		getDatabaseNames().stream().forEach(
				db -> {
					getCollectionNames(db).stream().forEach(coll -> {
						final Document query = new Document();
						query.put("eRezeptId", eRezeptId);
						final var database = mongoClient.getDatabase(db);
						final MongoCollection<Document> collection = database.getCollection(coll);
						final FindIterable<Document> cursor = collection.find(query);
						try ( final MongoCursor<Document> itr = cursor.cursor() ) {
							while ( itr.hasNext() ) {
								if ( eRezept.get(eRezeptId) != null )
									throw new IllegalArgumentException("E-Rezept mehrfach vorhanden: "+eRezeptId);
								final Document erx = itr.next();
								String eRezeptData = (String) erx.get("eRezeptData");
								eRezept.put(eRezeptId, new ERezept(eRezeptId, eRezeptData));
							}
						}
					});
				});
		return Optional.ofNullable(eRezept.get(eRezeptId));
	}

	@Override
	public String toString() {
		return "MongoDbDao [url=" + url + ", user=" + user + ", authDb=" + authDb + ", mongoClient=" + mongoClient
				+ ", erxDatabase=" + erxDatabase + "]";
	}

}