***
<div align="center">
	<img src="doc/MongoDB.svg" width="350" alt="MongoDB">
	<p><b><em>MongoDB</em></b><br>Installation und Nutzung mit Java</p>
</div>

***

# MongoDB
MongoDB installieren und Java-Client bereitstellen. Dieses Beispiel verwendet den einfachsten Fall, d. h. eine
Standalone Installation ohne externen Netzwerkzugriff. Allerdings wird Authetifizierung und RBAC implementiert.

## Systemvoraussetzungen
Diese Anleitung wurde in folgender Umgebung getestet:

|Item             |Einstellung                  |Info                         |
| :-------------: | :-------------------------: | :-------------------------: |
|Systemarchitektur|x86_64                       |[Unterstützte Systemplattformen](https://www.mongodb.com/docs/manual/administration/production-notes/#platform-support-notes)|
|Betriebssystem   |Ubuntu 22.04.2 LTS           |[Unterstützte Betriebssysteme](https://www.mongodb.com/docs/manual/administration/production-notes/#std-label-prod-notes-recommended-platforms)|
|MongoDB           |Version 6.0 Community Edition|[Release Notes](https://www.mongodb.com/docs/manual/release-notes/)|

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
		<b>Speicherkonfiguration für Datenbankdateien</b>:	
		Der <a href="https://www.mongodb.com/docs/v6.0/reference/configuration-options/#mongodb-setting-storage.dbPath">dbPath</a> legt den Speicherort für die
		Datenbankdateien fest. Hier ist ein eigenes Dateisystem sinnvoll um Abhängigkeiten zu anderen Programmen zu vermeiden. Es sollen sich dort nur Dateien
		befinden, die zur aktuell konfigurierten <a href="https://www.mongodb.com/docs/v6.0/core/storage-engines/">Speichertechnik</a> passen. Für das Verzeichnis
		benötigt MongoDB Lese- und Schreibrechte. Virenscanner müssen das Verzichnis ignorieren. Es kann eine Speichertechnik mit automatischer Verschlüsselung
		oder Kompression konfiguriert werden. Die Standard Speicherkonfiguraation
		<i><a href="https://www.mongodb.com/docs/v6.0/core/wiredtiger/#std-label-storage-wiredtiger">Wired Tiger</a></i> wird beibehalten.
	</li>
	<li>
		<b>Journaling</b>:
		Um in Folge eines Systemabsturzes wiederaufsetzen zu können und die Datenbank im gültigen Zustand zu halten,
		sollte <a href="https://www.mongodb.com/docs/v6.0/core/journaling/">Journaling</a> eingeschaltet sein.
	</li>
	<li>
		<b><i><a href="https://www.mongodb.com/docs/v6.0/reference/write-concern/">write concern</a></i></b>:
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
		<b><a href="https://www.mongodb.com/docs/v6.0/administration/production-notes/#std-label-prod-notes-ram">Hardwareausstattung</a></b>:
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
Für den Betrieb mit <a href="https://www.mongodb.com/docs/v6.0/administration/production-notes/#vmware">VMWare</a> gelten weitere Empfehlungen.

## Installation
Installiert wird das offizielle <i>mongodb-org</i> Paket in der Version MongoDB Community für
[Ubuntu 22.04](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-ubuntu/). Das eventuell in der Ubuntu-Version
vorinstallierte <i>mongodb</i>-Paket muss zuvor de-installiert werden! Damit das entsprechende Paket-Managementsystem verwendet
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

Der Public Key befindet sich jetzt in ``/usr/share/keyrings/mongodb-server-6.0.gpg``. Damit das Paket-Managementsystem verwendet werden kann,
muss ein entsprechndes <i>list file</i> (``/etc/apt/sources.list.d/mongodb-org-6.0.list`) angelegt werden. Dies wird für Ubuntu Version 22 mit
folgender Anweisung erledigt:

	$ echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-6.0.gpg ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/6.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-6.0.list

Damit das Repository verwendet werden kann muss die lokale Paketdatenbank neu geladen werden:

	$ sudo apt-get update

Es wird die aktuellste (stable) Version von MongoDB installiert:

	$ sudo apt-get install -y mongodb-org # ggf. sudo systemctl daemon-reload

Der automatische Start des Dienstes wird mit folgendem Kommando eingerichtet:

	$ sudo systemctl enable mongod

Manueller Start/Stopp entsprechend über ``sudo systemctl start/stop/restart mongod``. Während der Installation wird ein Benutzer und eine Gruppe <i>mongodb</i>
angelegt.

Das Paket <i>mongodb-org</i> enthält folgende Pakete, die automatisch mit installiert werden:

- <i>mongodb-org-database</i>: die Serverprozesse mongod und mongos
- <i>mongodb-mongosh</i>: die [MongoDB Shell](https://www.mongodb.com/docs/mongodb-shell/#mongodb-binary-bin.mongosh)
- <i>mongodb-org-tools</i>: verschiedene Diagnosetools, Im- und Exportwerkzeuge usw.

Da die Datenbank in dieser Demo nicht über das Netzwerk erreichbar sein soll, ist das Einrichten einer Firewall
(z. B. [ufw](https://wiki.ubuntuusers.de/ufw/)) optional.

## Konfiguration

### Netzwerk

Nach der Installation ist der Service standardmäßig nur über das lokale loopback Interface zu erreichen.
Mittels Konfigurationsdatei bzw. Kommandozeilenargument kann die [bindIp](https://www.mongodb.com/docs/manual/reference/configuration-options/#mongodb-setting-net.bindIp)
gesetzt werden. Wird in diesem Beispiel nicht verwendet (siehe [IP Binding](https://www.mongodb.com/docs/manual/core/security-mongodb-configuration/)). Hinweise dazu ganz unten.
### Monitoring

Auslastung der Datenbank anzeigen:

	> db.stats() /* Zusammengefasste Statistik */
	> db.serverStatus() /* Nutzung des Servers */

	$ mongostat -u root --authenticationDatabase=admin # Status der laufenden Instanz
	$ mongotop -u root --authenticationDatabase=admin  # Benötigte Zeit für Lesen und Schreiben der Instanz

## Authentifizierung

Damit nicht jeder auf dem Rechner angemeldete Benutzer auf die Datenbank zugreifen kann, wird eine Authentifizierung eingerichtet.
Zunächst der administartive Benutzer in der Datenbank <i>admin</i>:

```
$ mongosh
> show dbs
admin      180.00 KiB
config      60.00 KiB
local       72.00 KiB
> use admin
switched to db admin
admin> db.createUser({ /* Administrativen Benutzer anlegen */
user: "root",
pwd: passwordPrompt(),
roles: [ { role: "userAdminAnyDatabase", db: "admin" }, "readWriteAnyDatabase" ]
})
/* Für Superuser admin> db.grantRolesToUser('root', [{ role: 'root', db: 'admin' }]) */
admin> db.runCommand({ connectionStatus: 1 })
/* Authinfo anzeigen */
admin> quit
```

Damit die Authentifizierung wirksam wir muss sie in <code>/etc/mongod.conf</code> konfiguriert werden.

	$ sudo vi /etc/mongod.conf

Der Eintrag <code># security:</code> wird geändert in:

```
security:
  authorization: enabled
```

Der MongoDB-Deamon muss danach neu gestartet werden:

	$ sudo systemctl restart mongod

Um die Datenbank im Anschluß benutzen zu können, ist eine Anmeldung erforderlich:

```
$ mongosh
> show dbs
MongoServerError: command listDatabases requires authentication
> exit
$ mongosh -u root -p --authenticationDatabase admin
Enter password: ****
> show dbs
admin      180.00 KiB
config     108.00 KiB
local       72.00 KiB
```

## Role-Based Access Control (RBAC)

Da die Fachanwendung nicht mit dem administrativen User <i>root</i> arbeiten sollen, wird für
jede Anwendung ein entsprechender Benutzer angelegt, der einer oder mehreren Rollen (auf welche Ressourcen
darf zugegriffen werden und welche Aktionen dürfen ausgeführt werden?) zugewiesen wird. Standardmäßig
in MongoDB vorhandene Rollen sind [hier zu finden](https://docs.mongodb.com/manual/reference/built-in-roles/).
Anlegen einer Datenbank <i>erezepte</i> mit einer Collection <i>erx_202307</i> Users:

```
$ mongosh -u root -p --authenticationDatabase=admin
Enter password: ****

admin> db.createUser({ user: "erx", pwd: passwordPrompt(), roles: [{ role: "readWrite", db: "erezepte" }] })
Enter password
***{ ok: 1 } /* User erx darf Dokumente einstellen und lesen */
admin> use erezepte;
erezepte> db.grantRolesToUser("erx", [{role: "dbAdmin", db: "erezepte"}]); /* "erx" muss später ein Schema zuordnen dürfen! */
{ ok: 1 }

$ mongosh -u erx -p --authenticationDatabase=erezepte
test> use erezepte
switched to db erezepte /* DB existiert hier noch nicht */
erezepte> db.getUsers() /* Erzeugten User ansehen */
erezepte> use admin
switched to db admin
admin> db.system.users.find()
[ ... ] /* zeigt die User root und erx an */
erezepte> db.erx_202307.insert({eRezeptId: '123.456.789.00', eRezeptData: 'ZVJlemVwdAo='})
DeprecationWarning: Collection.insertOne() is deprecated. Use insertOne, insertMany, or bulkWrite.
{
  acknowledged: true,
  insertedIds: { '0': ObjectId("64a5a358f060147b960cfe84") }
} /* DB wurde hier angelegt */
/*
	Zum Löschen
	db.erx_202307.find()
	db.erx_202307.deleteOne({_id: ObjectId("..."))
	Leeren:
	db.erx_202307.remove({})
*/
/* E-Rezepte sollen eindeutig sein! */
erezepte> db.erx_202307.createIndex({ "eRezeptId": 1 }, { unique: true })
eRezeptId_1

```

# Nutzung der Datenbank über die Shell

Der neu angelegt User kann jetzt verwendet werden:

```
$ mongosh -u erx -p --authenticationDatabase=erezepte
Enter password: ***
test> show dbs
erezepte  72.00 KiB
test> use erezepte
switched to db erezepte

/* Standard CRUD Operationen */
erezepte> db.erx_202307.insertOne({ eRezetId: '123.456.789.00', eRezeptData: '0987654321FEDCBA='});
erezepte> db.erx_202307.insertMany([ { eRezeptId: '123.456.788.00', eRezeptData: 'FEDCBA0987654321=' }, { eRezeptId: '123.456.787.00', eRezeptData: 'ABCDEF1234567890=' } ]);
erezepte> db.runCommand({ /* Schema zuordnen um die Konsistenz der eingestellten Dokumente zu sichern */
    "collMod": "erx_202307",
    "validator": {
        $jsonSchema: {
	    "bsonType": "object",
	    "description": "Enthaelt jeweils ein E-Rezept mit ID und Daten",
	    "required": [ "eRezeptId", "eRezeptData", "requestIds" ],
	    "properties": {
		"eRezeptId": {
		    "bsonType": "string",
		    "maxLength": 222,
		    "description": "Eindeutige ID des E-Rezeptes"
		},
		"eRezeptData": {
		    "bsonType": "string",
		    "description": "Inhalt des E-Rezeptes verschlüsselt"
		},
		"requestIds": {
		    "bsonType": "array",
		    "description": "Mit welchem Request das E-Rezept eingereicht wurde",
		    "minItems": 1,
		    "uniqueItems": true,
		    "items": {
		    	"bsonType": "number"
		    }
		}
	    }
        }
    }
});
erezepte> db.erx_202307.find().pretty();
erezepte> db.erx_202307.find({ eRezeptId: '123.456.787.00' });
erezepte> db.erx_202307.find({ eRezeptId: { $ne: '123.456.788.00' } }); /* Alle außer dem genannten */
erezepte> db.erx_202307.find({ eRezeptId: { $in: [ '123.456.788.00', '123.456.790.00' ] } }); /* Aus einer Liste */
erezepte> db.erx_202307.find({ datum: { $lt: ISODate("2023-07-16T01:14:24.001Z") } }); /* entsprechend $gt */
erezepte> db.erx_202307.find({ datum: { $lt: ISODate("2023-07-16T01:14:24.001Z") }, eRezetId: '123.456.789.00' }); /* Kombination mehrerer Attribute, implizites AND */
erezepte> db.erx_202307.find({ $or: [ { datum: { $lt: ISODate("2023-07-16T01:14:24.001Z") } }, { eRezetId: '123.456.789.00' } ] }); /* Entsprechend $and */
erezepte> db.erx_202307.find({ requestIds: 2 }); /* Abfrage von Werten eines Arrays */
erezepte> db.erx_202307.find({ "wawi.version": { $gt: 1 } }); /* Abfrage einer Unterstruktur */
erezepte> db.erx_202307.find({ requestIds: 2 }, { "wawi.name": 1, "wawi.version": 1 } ); /* Projektionen: ausgewählte Felder anzeigen (Inklusion) */
erezepte> db.erx_202307.find({}, { wawi: 0 }); /* Projektionen: ausgewählte Felder anzeigen (Exklusion), z. B. ohne ID: ...find({}, { _id: 0 }) */
erezepte> db.erx_202307.find({}).limit(1); /* Treffermenge begrenzen */
erezepte> db.erx_202307.find({ "wawi.version": { $gt: 0 } }, { _id: 0, eRezeptId: 1, eRezeptData: 1 }).limit(10).sort({ eRezeptId: -1}); /* Ausgabe absteigend sortieren und begrenzen */
erezepte> db.erx_202307.countDocuments(); /* Oder erezepte> db.erx_202307.find({}).count(); */
erezepte> db.erx_202307.updateOne({ eRezeptId: '123.456.787.00' }, { $set: { eRezeptData: 'ABCDEF1234567890xxx=' } });
erezepte> db.erx_202307.updateOne({ eRezeptId: '123.456.790.00' }, { $set: { requestIds: [ 1, 2, 3 ] }}); /* Array als Attribut hinzufügen */
erezepte> db.erx_202307.updateOne({ eRezeptId: '123.456.788.00' }, { $set: { wawi: { name: 'GFI', version: 1 } }}); /* Struktur als Attribut hinzufügen */
erezepte> db.erx_202307.updateMany({}, { $set: { datum: ISODate("2023-07-16T01:14:24.000Z") }}); /* Ein Feld hinzufügen */
erezepte> db.erx_202307.deleteOne({ eRezeptId: '123.456.787.00' });
/* erezepte> db.erx_202307.deleteMany(...); */

erezepte> show collections;
erx_202307
```

Falls eine intensive Benutzung der Datenbank erforderlch ist, kann statt <code>mongosh</code> auch
der [MongoDB Compass](https://www.mongodb.com/docs/compass/master/) verwendet werden.i

Anwendung der Prinzipien der Datenmodellierung:

* Zusammen speichern, was in einem Zugriff abgerufen wird: in dem E-Rezept-Beispiel sind ID und Daten in einem Dokument gespeichert. 
* 1:1/few/many-Relationen mit eingebetteten Dokumenten (auf die referenzierten Daten wird nicht unabhängig zugegriffen, kleine Datenmengen) oder Child/Parent-Referenzen über die Objekt-Id: in diesem Beispiel nicht erforderlich

# Anwendung
In [Beispiel MongoDB-API](https://github.com/mbeier1406/MongoDB/tree/main/src) befindet sich ein Beispiel für die Implementierung eines
CRUD-Interfaces zur Nutzung von MongoDB in Java.z

# Hinweise

## Netzwerkbetrieb

Der exemplarische [CRUD-Client](https://github.com/mbeier1406/MongoDB) unterstützt keine Transaktionen, da die beschriebene Datenbank als *stand-alone* Lösung,und nicht als *shared database cluster* oder *replica set* aufgesetzt ist. Entsprechend wird auch kein *sharding* eingesetzt. *Full-text Search* wird nicht benötigt, da nicht nach E-Rezept-Daten
gesucht werden muss.

Falls die Installation über das Netzwerk erreichbar ist, sollten folgende Sicherheitsempfehlungen beachtet werden:

- [Checklste](https://www.mongodb.com/docs/manual/administration/security-checklist/)
- [Authetifizierung](https://www.mongodb.com/docs/manual/administration/security-checklist/#std-label-checklist-auth)
- [Serverhärtung](https://www.mongodb.com/docs/manual/core/security-hardening/)

Konfigurationsbeispiel in ``/etc/mongod.conf``:

```
# network interfaces
net:
  port: 27017
  bindIp: 192.168.83.128
```

Aufruf der CLI dann: ``$ mongosh -u erx -p --authenticationDatabase=erezepte mongodb://192.168.83.128:27017``. <b>Unbedingt</b> die
oben genannten Sicherheitshinweise bzgl Serverhärtung, Authentifizierung usw. beachten:
<p>Konfiguration MongoDB:</p>
* [Client-Authentication](https://www.mongodb.com/docs/manual/administration/security-checklist/#std-label-checklist-auth) konfigurieren
* Access Control und User-Authentifizierung (s.o.)
* Einsatz von RBAC (s.o.)
* Kommunikation über [TLS verschlüsseln](https://www.mongodb.com/docs/manual/tutorial/configure-ssl/)
* Falls die gespeicherten Daten selbst nicht verschlüsselt sind, bei WiredTiger [Verschlüsselung](https://www.mongodb.com/docs/manual/core/security-encryption-at-rest/) aktivieren
* Beim start die <i>--noscripting</i>-Option verwenden um JavaScript usw. zu de-aktivieren
<p>Konfiguration Betriebssystem:</p>
* [Netzwerkhärtungen](https://www.mongodb.com/docs/manual/core/security-hardening/) (IP-Forwarding ausschalten etc.) und weietre Maßnahmen wie Ant-Virus usw. implementieren
* Betriebssystem: in ein vertrauenswürdiges Netzwerk einbinden, kein root-ssh Zugang, Port nur für Trusted Clients (Firewall)
* Monitoring, Logging einschalten und überwachen
* MongoDB in einem dedizierten User ohne spezielle Berechtigungen ausführen

