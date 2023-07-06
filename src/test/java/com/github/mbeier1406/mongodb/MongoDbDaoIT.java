package com.github.mbeier1406.mongodb;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests für die Klasse {@linkplain MongoDbDao}.
 * @author mbeier
 */
@SuppressWarnings("deprecation")
public class MongoDbDaoIT {

	private static final Logger LOGGER = LogManager.getLogger(MongoDbDaoIT.class);

	/** Das zu testende Objekt */
	public MongoDbDao mongoDbDao;

	/** Verbindung über die Standard-URL */
	@Before
	public void init() throws IOException {
		mongoDbDao = new MongoDbDao();
	}

	/** Prüfen, ob die Standard-URL korrekt geladen wird */
	@Test
	public void testeUrl() {
		LOGGER.info("URL: {}", mongoDbDao.getConnectionInfo());
		assertThat(mongoDbDao.getConnectionInfo(), equalTo("mongodb://localhost:27017"));
	}

	/** Stellt sicher, dass mit dem aktuellen Client auf die E-Rezept Datenbank zugegriffen werdne kann */
	@Test
	public void checktDatabaseERezepte() {
		List<String> databaseNames = mongoDbDao.getDatabaseNames();
		LOGGER.info("databaseNames={}", databaseNames);
		assertThat(databaseNames, hasItem("erezepte"));
	}

}
