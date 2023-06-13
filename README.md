***
<div align="center">
	<img src="doc/MongoDB.svg" width="350" alt="MongoDB">
	<p><b><em>MongoDB</em></b><br>Installation und Nutzung mit Java</p>
</div>

***

# MongoDB
MongoDB installieren und Java-Client bereitstellen. Dieses Beispiel verwendet den einfachsten Fall, d. h. eine
Standalone Installation ohne externen Netzwerkzugriff. Allerdings wird Authetifizierung implementiert.

## Systemvoraussetzungen
Diese Anleitung wurde in folgender Umgebung getestet:

|Item             |Einstelung                   |Info                         |
| :-------------: | :-------------------------: | :-------------------------: |
|Systemarchitektur|x86_64                       |[Unterstützte Systemplattformen](https://www.mongodb.com/docs/manual/administration/production-notes/#platform-support-notes)|
|Betriebssystem   |Ubuntu 22.04.2 LTS           |[Unterstützte Betriebssysteme](https://www.mongodb.com/docs/manual/administration/production-notes/#std-label-prod-notes-recommended-platforms)|
|MngoDB           |Version 6.0 Community Edition|[Release Notes](https://www.mongodb.com/docs/manual/release-notes/)|

## Einstellungen für den Produktionsbetrieb
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
		Eine möglichst aktuelle Version der <i>GNU C Library</i> ist erforderlich. Im Speicherverzeichnis sollte <i>atime</i> ausgeschaltet sein
		und [ulimit](https://www.mongodb.com/docs/v6.0/reference/ulimit/) angepasst werden (ggf. <i>unlimited</i>).
	</li>
	<li>
		<b>Systemzeit</b>:
		Damit zeitgesteuerte Abläufe korrekt funktionieren, sollte NTP auf dem Host konfiguriert sein.
	</li>
	<li>
		<b>Prozesskontrolle</b>:
		Es wird die Prozessteuerung über <i>systemd</i> (<code>systemctl</code>) verwendet, nicht der System V init Service.
	</li>
</ul>
<p/>
Für den Betrieb mit [VMWare](https://www.mongodb.com/docs/v6.0/administration/production-notes/#vmware) gelten weitere Empfehlungen.

## Installation
Installiert wird das offizielle <i>mongodb-org</i> Paket in der Version MongoDB Community für
[Ubuntu 22.04](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-ubuntu/). Das eventuell in der Ubuntu-Version
vorinstallierte <i>mongodb</i>-Paket muss zuvor de-nstalliert werden! Damit das entsprechende Paket-Managementsystem verwendet
werden kann, muss der öffentlich Schlüssel installiert werden. Zur Verwendung ist <i>gnupg</i> erforderlich, das ggf. zuvor
installiert werden muss:

	$ sudo apt-get install gnupg

Sollte das <i>curl</i>-Tool nicht vorhanden sein, so wird dieses mittels folgendem Kommando installiert:

	$ sudo apt install curl

Der notwendige GPG-Key befindet sich unter folgender Adresse: [server-6.0.asc](https://pgp.mongodb.com/server-6.0.asc) und
wird wie folgt installiert:.

	$ curl -fsSL https://pgp.mongodb.com/server-6.0.asc | \
	   sudo gpg -o /usr/share/keyrings/mongodb-server-6.0.gpg \
	   --dearmor

Der Public Key befindet sich jetzt in ``/usr/share/keyrings/mongodb-server-6.0.gpg``. Damit das Paket-Managementsystem verwendet werdenkann,
muss ein entsprechndes <i>list file</i> (``/etc/apt/sources.list.d/mongodb-org-6.0.list`)angelegt werden. Dies wird für Ubuntu Version 22 mit
folgender Anweisung erledigt:

	$ echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-6.0.gpg ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/6.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-6.0.list

Damit das Repository verwendet werden kann muss die lokale Paketdatenbank neu geladen werden:

	$ sudo apt-get update

Es wird die aktuellste (stable) Version von MongoDB installiert:

	$ sudo apt-get install -y mongodb-org # ggf. sudo systemctl daemon-reload

Der automatische Start des Dienstes wird mit folgendem Kommando eingerichtet:

	$ sudo systemctl enable mongod

Manueller Start/Stopp entsprechend über ``sudo systemctl start/stop/restart mongod``.

Das Paket <i>mongodb-org</i> enthält folgende Pakete, die automatisch mit installiert werden:

- <i>mongodb-org-database</i>: die Serverprozesse mongod und mongos
- <i>mongodb-mongosh</i>: die [MongoDB Shell](https://www.mongodb.com/docs/mongodb-shell/#mongodb-binary-bin.mongosh)
- <i>mongodb-org-tools</i>: verschiedene Diagnosetools, Im- und Exportwerkzeuge usw.

## Konfiguration

### Netzwerk

Nach der Installation ist der Service standardmäßig nur über das lokale loopback Interface zu erreichen.
Mittels Konfigurationsdatei bzw. Kommandozeilenargument kann die [bindIp](https://www.mongodb.com/docs/manual/reference/configuration-options/#mongodb-setting-net.bindIp)
gesetzt werden. Wird in diesem Beispiel nicht verwendet (siehe [IP Binding](https://www.mongodb.com/docs/manual/core/security-mongodb-configuration/)).

### Sicherheit

Falls die Installation über das Netzwerk erreichbar ist, sollten folgende Sicherheitsempfehlungen beachtet werden:

- [Checklste](https://www.mongodb.com/docs/manual/administration/security-checklist/)
- [Authetifizierung](https://www.mongodb.com/docs/manual/administration/security-checklist/#std-label-checklist-auth)
- Serverhärtung(https://www.mongodb.com/docs/manual/core/security-hardening/)

# Java

