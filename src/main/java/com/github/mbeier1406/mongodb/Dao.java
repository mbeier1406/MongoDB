package com.github.mbeier1406.mongodb;

import java.util.List;
import java.util.Optional;

/**
 * Definiert alle Mehoden zum persistieren der E-Rezepte.
 * @author mbeier
 * @param <T> Hier ein E-Rezept
 */
public interface Dao<T> {

	/** Standardmäßig diese Implementierung verwenden */
	public static record ERezept(String eRezeptId, String eRezeptData) {};

	/** Liefert die URL zur verbundenen DB */
	public String getConnectionInfo();

	/**
	 * Listet alle vorhandenen Datenbanken auf.
	 * @return Liste der Datenbanknamen
	 */
	public List<String> getDatabaseNames();

	/**
	 * Listet alle vorhandenen Collections auf.
	 * @param db Name der Datenbank
	 * @return Liste der Collections
	 */
	public List<String> getCollectionNames(final String db);

	/**
	 * Liefert zu einer ID das zugehörige E-Rezept.</p>
	 * <b>Achtung</b>: sucht in allen Collections aller Datenbanken!
	 * @param eRezeptId die Id
	 * @return das E-Rezept
	 */
	public Optional<T> find(final String eRezeptId);

}