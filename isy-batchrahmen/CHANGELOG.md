# 2.0.0
- `IFS-228`: Umstellung auf Java Config
- `IFS-32`: Package-Name auf de.bund.bva.isyfact geändert
- `IFS-251`: Abhängigkeiten zu log4j entfernt

# 1.10.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM
- `IFS-255`: Schließen des Anwendungskontexts nach Ausführung eines Batches.
- `IFS-270`: UUID als Korrelations-ID für Batches statt Batch-ID

# 1.9.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert
- `IFS-227`: Encoding der Ergebnisdateien korrigiert

# 1.8.0
- `IFS-47`: Korrelations-ID für jede Satzverarbeitung

# 1.7.0
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken
- `IFS-66`: Im Status "Abbruch" kann ein neuer Lauf nur mit "Restart" erfolgen, auch wenn im abgebrochenen Lauf zuvor kein Satz verarbeitet wurde.

# 1.6.0
- `IFS-17`: Umbenennung der Artifact-ID und Group-ID

# 1.5.3
- `IFS-5`: Der Isy-Batchrahmen verweist auf "Register".