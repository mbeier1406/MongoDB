package com.github.mbeier1406.mongodb;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.mbeier1406.mongodb.Dao.ERezept;

/**
 * Tests für die Klasse {@linkplain MongoDbDao}.</p>
 * <b>Achting</b>: Integrationstest, benötigt eine korrekt konfigurierte, laufende MongoDB-Instanz.
 * Nich beim automatischen Build ausführen.
 * @author mbeier
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("deprecation")
public class MongoDbDaoIT {

	private static final Logger LOGGER = LogManager.getLogger(MongoDbDaoIT.class);

	/** Die Collection, mit der getestet wird ist {@value} */
	private static final String COLLECTION = "erx_202307";

	/** Das zu testende Objekt */
	public MongoDbDao mongoDbDao;

	/** Verbindung über die Standard-URL */
	@Before
	public void init() throws IOException {
		mongoDbDao = new MongoDbDao();
	}

	/** Prüfen, ob die Standard-URL korrekt geladen wird */
	@Test
	public void a_testeUrl() {
		LOGGER.info("URL: {}", mongoDbDao.getConnectionInfo());
		assertThat(mongoDbDao.getConnectionInfo(), equalTo("mongodb://localhost:27017"));
	}

	/** Stellt sicher, dass mit dem aktuellen Client auf die E-Rezept Datenbank zugegriffen werden kann */
	@Test
	public void b_checktDatabaseERezepte() {
		List<String> databaseNames = mongoDbDao.getDatabaseNames();
		LOGGER.info("databaseNames={}", databaseNames);
		assertThat(databaseNames, hasItem("erezepte"));
	}

	/** Stellt sicher, dass mit dem aktuellen Client auf die Collection "erx_202307" in der E-Rezept Datenbank zugegriffen werden kann */
	@Test
	public void c_checktCollectionERezepte() {
		List<String> collections = mongoDbDao.getCollectionNames("erezepte");
		LOGGER.info("collections={}", collections);
		assertThat(collections, hasItem(COLLECTION));
	}

	/** */
	@Test
	public void d_testeInsert() {
		mongoDbDao.insert(COLLECTION, "123.456.790.00", new ERezept("123.456.790.00", "ZVJlemVwdAoXX="));
	}

	/** Stellt sicher, dass das zuvor manuell eingestellte E-Rezept gefunden wird */ 
	@Test
	public void e_testeERezeptSuche() {
		final var eRezeptId = "123.456.790.00";
		Optional<ERezept> eRezept = mongoDbDao.find(eRezeptId);
		LOGGER.info("eRezept={}", eRezept);
		assertThat(eRezept, not(equalTo(Optional.empty())));
		assertThat(eRezept.get().eRezeptId(), equalTo((eRezeptId)));
	}

}
