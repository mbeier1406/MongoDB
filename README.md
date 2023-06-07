***
<div align="center">
	<img src="doc/MongoDB.svg" width="350" alt="MongoDB">
	<p><b><em>MongoDB</em></b><br>Installation und Nutzung mit Java</p>
</div>

***

# MongoDB
MongoDB installieren und Java-Client bereitstellen.

## Systemvoraussetzungen
Diese Anleitung wurde in folgender Umgebung getestet:

|Item             |Einstelung                   |Info                         |
| :-------------: | :-------------------------: | :-------------------------: |
|Systemarchitektur|x86_64                       |[Unterstützte Systemplattformen](https://www.mongodb.com/docs/manual/administration/production-notes/#platform-support-notes)|
|Betriebssystem   |Ubuntu 22,04.2 LTS           |[Unterstützte Betriebssysteme](https://www.mongodb.com/docs/manual/administration/production-notes/#std-label-prod-notes-recommended-platforms)|
|MngoDB           |Version 6.0 Community Edition|[Release Notes](https://www.mongodb.com/docs/manual/release-notes/)|


## Installation
Die offizielle Anleitung zur Installation befindet sich auf der
[MongoDB Installationsseite](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-ubuntu/). Bevor eine
Installation in Betrieb genommen wird, sollten die [Hinweise zur Inbetriebnahme](https://www.mongodb.com/docs/manual/administration/production-notes/)
beachtet werden. Insbesondere:
<ul>
	<li>
		<b>Systemarchitektur</b>:
		MongoDB kann als Standaloneinstallation, <i>replica set</i> oder <i>shared cluster</i> betrieben werden. In diesem Beispiel wird
		lediglich die Standalone-Lösung betrachtet.
	</li>
	<li>
		<b>Speichekonfiguration für Datenbankdateien</b>:	
		Der <a href="https://www.mongodb.com/docs/v6.0/reference/configuration-options/#mongodb-setting-storage.dbPath">dbPath</a> legt den Speicherort für die
		Datenbankdateien fest. Hier ist ein eigenes Dateisystem sinnvoll um Abhängigkeiten zu anderen Programmen zu vermeiden. Es dürfen sich dort nur Dateien
		befinden, die zur aktuell konfigurierten <a href="https://www.mongodb.com/docs/v6.0/core/storage-engines/">Speichertechnik</a>. Für das Verzeichnis
		benötigt MongoDB Lese- und Schreibrechte. Virenscanner müssen das Verzichnis ignorieren. Es kann eine Speichertechnik mit automatischer Verschlüsselung
		oder Kompression konfiguriert werden. Die Standard Speicherkonfiguraation
		<i>[Wired Tiger](https://www.mongodb.com/docs/v6.0/core/wiredtiger/#std-label-storage-wiredtiger)</i> wird beibehalten.
	</li>
	<li>
		<b>Journaling</b>:
		Um in Folge eines Systemabsturzes wiederaufsetzen zu können und die Datenbank im gültigen Zustand zu halten,
		sollte <a href="https://www.mongodb.com/docs/v6.0/core/journaling/">Journaling</a> eingeschaltet sein.
	</li>
	<li>
		<b><i>[write concern](https://www.mongodb.com/docs/v6.0/reference/write-concern/)</i></b>:
		Beim Standalonebetrieb wird als Konfiguration <code>w:1, j: true, wtimeout: 500</code> gewählt.
	</li>
	<li>
		<b>Netzwerk</b>:
		Der MongoDB-Server muss in ein sicheres Netwerk eingebunden werden, in dem der Zugriff nur für Systemservices (Monitoring etc.),
		Wartung und die berechtigten Clients mögich ist.
	</li>
	<li>
		<b>Connction Pool Size</b>:
		Der Standardwert von <a href="https://www.mongodb.com/docs/v6.0/reference/connection-string/#std-label-connection-pool-options">
		100 parallelen Verbindungen</a> wird beibehalten und im Betrieb <a href="https://www.mongodb.com/docs/v6.0/reference/command/connPoolStats/#mongodb-dbcommand-dbcmd.connPoolStats">
		überwacht</a>.
	</li>
	<li>
		<b>[Hardwareausstattung](https://www.mongodb.com/docs/v6.0/administration/production-notes/#std-label-prod-notes-ram)</b>:
		Es wird mit der Minimalausstattung von zwei Cores und 4 GB RAm (1,5 GB für MongoDB) verwendet. Als Speichersystem wird RAID
		über SSD empfohlen, SATA ist möglich.
	</li>
	<li>
		<b>Betriebssystem</b>:
		Linux Kernel-Version ab 2.6.36. XFS ist <u>dringend</u> empfohlen, da <i>Wired Tiger</i> eingesetzt wird, EXT4 aber auch möglich.
		Eine möglichst aktuelle Version der <i>GNU C Librry</b> ist erforderlich. Im Speicherverzeichnis sollte <i>atime</a> ausgeschaltet sein
		und [ulimit](https://www.mongodb.com/docs/v6.0/reference/ulimit/) angepasst werden (ggf. <i>unlimited</i>).
	</li>
	<li>
		<b>Systemzeit</b>:
		Damit zeitgesteuerte Abläufe korrekt funktionieren, sollte NTP auf dem Host konfiguriert sein.
	</li>
</ul>
<p/>
Für den Betrieb mit [VMWare](https://www.mongodb.com/docs/v6.0/administration/production-notes/#vmware) gelten weitere Empfehlungen.


# Java

