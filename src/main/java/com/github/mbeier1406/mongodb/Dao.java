package com.github.mbeier1406.mongodb;

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
	 * Liefert zu einer ID das zugehörige E-Rezept.
	 * @param eRezeptId die Id
	 * @return das E-Rezept
	 */
	public T find(final String eRezeptId);

}
