# 2.0.0
- `IFS-228`: Einführung von Spring Boot
- `IFS-32`: Package-Name auf de.bund.bva.isyfact geändert

# 1.8.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM

# 1.7.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert
- `IFS-273`: Task zur Aktualisierung der Konfiguration muss Korrelations-ID setzen

# 1.5.2
`IFS-98`: Sortierung von Dateien darf nur bei Konfiguration als Ordnerstruktur verwendet werden

# 1.5.1
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken
- `IFS-26`: Konfigurationsaktualisierung via Timertask nun mit Korrelations-ID
- `IFS-59`: Konfiguration kann beliebige Konfigurationsdateien lesen

# 1.3.7
- `RF-107`: Beim Reload von properties wurden gelöschte properties nicht erfolgreich entfernt.
