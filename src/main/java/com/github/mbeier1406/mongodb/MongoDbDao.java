package com.github.mbeier1406.mongodb;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
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
		List<String> dbs = new ArrayList<>();
		MongoIterable<String> listDatabaseNames = mongoClient.listDatabaseNames();
		listDatabaseNames.forEach(n -> dbs.add(n));
		return dbs;
	}

	/** {@inheritDoc} */
	@Override
	public ERezept find(String eRezeptId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return "MongoDbDao [url=" + url + ", user=" + user + ", authDb=" + authDb + ", mongoClient=" + mongoClient
				+ ", erxDatabase=" + erxDatabase + "]";
	}

}
