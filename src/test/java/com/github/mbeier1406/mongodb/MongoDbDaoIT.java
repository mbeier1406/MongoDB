package com.github.mbeier1406.mongodb;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests für die Klasse {@linkplain MongoDbDao}.
 * @author mbeier
 */
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

}
