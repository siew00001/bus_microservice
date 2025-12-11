# 🚌 Bus & Route Management Microservice

Ein Spring Boot Microservice zur zentralen Verwaltung von Bussen, Fahrplänen und Routen. Diese Anwendung stellt eine REST-API bereit und ist für den Einsatz in Container-Umgebungen sowie als Standalone-Service optimiert.

## 🛠 Tech Stack

* **Backend:** Java / Spring Boot
* **API Dokumentation:** Swagger UI (OpenAPI)
* **Datenbank:** H2 (In-Memory), PostgerSQL (Container)
* **Containerisierung:** Docker & Docker Compose
* **Build Tool:** Maven

---

## 🚀 Installation & Start

Es gibt zwei Möglichkeiten, diesen Service zu starten. Bitte wähle die Methode, die deinem Anwendungsfall entspricht.

### Option 1: Start mit Docker Compose (Empfohlen) 🐳

Dies ist die bevorzugte Methode für eine vollständige Umgebung.

**Voraussetzungen:**
* Die **Docker Engine** (z.B. Docker Desktop) muss installiert sein und **laufen**.

**Befehl:**
Öffne ein Terminal im Hauptverzeichnis des Projekts und führe aus:

```bash
docker-compose up --build
````

  * Der `--build` Flag stellt sicher, dass das Image mit den aktuellsten Code-Änderungen neu gebaut wird.
  * Die Anwendung ist danach unter dem konfigurierten Port (Standard: 8080) erreichbar.

-----

### Option 2: Start als JAR-Datei (CI/CD) ☕

Diese Methode wird meist verwendet, wenn das Artefakt aus einer CI/CD-Pipeline kommt oder für schnelle lokale Tests ohne Docker.

**⚠️ Wichtiger Hinweis zur Datenhaltung:**
Beim Start über die JAR wird standardmäßig eine **nicht-persistente H2 In-Memory Datenbank** verwendet.

> **Das bedeutet: Alle angelegten Busse und Routen gehen verloren, sobald die Anwendung gestoppt wird.**

**Voraussetzungen:**

  * Installiertes Java JRE/JDK 

**Befehl:**
Navigiere zu dem Verzeichnis mit der JAR-Datei (z.B. `/target`) und führe aus:

```bash
java -jar route-service-0.0.1-SNAPSHOT.jar
```

Oder installiere das im Workflow erstellte Artifact

-----

## 📚 API Dokumentation (Swagger UI)

Sobald die Anwendung läuft, steht die interaktive API-Dokumentation zur Verfügung. Hier können alle Endpunkte direkt im Browser getestet werden.

👉 **Swagger UI öffnen:** [http://localhost:8080/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui/index.html)


### Kern-Funktionalitäten

  * **Bus-Management:** Hinzufügen, Aktualisieren und Löschen von Bussen.
  * **Routen-Verwaltung:** Erstellen und Modifizieren von Fahrplänen.
  * **Zuweisung:** Verknüpfung von Bussen mit spezifischen Routen.

-----
```
