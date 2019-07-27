/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.pliscommon.batchrahmen.core.launcher;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.PropertySource;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.pliscommon.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.pliscommon.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.pliscommon.batchrahmen.batch.konstanten.BatchRahmenEreignisSchluessel;
import de.bund.bva.pliscommon.batchrahmen.batch.konstanten.KonfigurationSchluessel;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.MeldungTyp;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.VerarbeitungsMeldung;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.pliscommon.batchrahmen.core.exception.BatchrahmenException;
import de.bund.bva.pliscommon.batchrahmen.core.exception.BatchrahmenInitialisierungException;
import de.bund.bva.pliscommon.batchrahmen.core.exception.BatchrahmenProtokollException;
import de.bund.bva.pliscommon.batchrahmen.core.konstanten.NachrichtenSchluessel;
import de.bund.bva.pliscommon.batchrahmen.core.protokoll.DefaultBatchErgebnisProtokoll;
import de.bund.bva.pliscommon.batchrahmen.core.rahmen.Batchrahmen;
import de.bund.bva.pliscommon.batchrahmen.core.rahmen.impl.BatchrahmenPropertySource;
import de.bund.bva.pliscommon.sicherheit.common.exception.AutorisierungFehlgeschlagenException;
import de.bund.bva.pliscommon.sicherheit.common.exception.SicherheitTechnicalRuntimeException;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * Diese Klasse startet einen Batch (siehe {@link Batchrahmen} mit der ï¿½bergebenen Konfiguration. Die
 * Konfiguration erfolgt über Kommandozeilen-Argumente sowie über eine Property-Datei.
 * <p>
 * Die Verarbeitungs-Logik ist dabei auf einen Batchrahmen und eine Ausfuehrungsbean aufgeteilt. Siehe dazu
 * das Detailkonzept Batch der Migrationsstufe 1.
 * <p>
 * Kommandozeilenargumente müssen stets die Form <tt>-ParameterName ParameterWert</tt> oder
 * <tt>-ParameterName</tt> besitzen. Folgende Parameter sind fuer den Batchrahmen relevant:
 * <ul>
 * <li>cfg &lt;Dateiname&gt;: Name der Property-Datei
 * <li>start: Starten des Batches im "Start" Modus.
 * <li>restart: Starten des Batches im "Restart" Modus nach einem Fehler-Abbruch.
 * <li>ignoriereRestart: Auch bei Fehlern Start akzeptieren, nicht auf Restart beharren.
 * <li>ignoriereLauf: Auch bei Status "Laeuft" Start akzeptieren.
 * </ul>
 * <p>
 * Die Property-Datei darf folgende Grüüe besitzen:
 * <ul>
 * <li>Batchrahmen.BeanName: Name der Batchrahmen-Bean.
 * <li>Anwendung.SpringDateien.&lt;N&gt;: Namen der Spring-Konfigurationsdateien des Systems.
 * <li>Batchrahmen.SpringDateien.&lt;N&gt;: Namen der Spring-Konfigurationsdateien des Batchrahmens.
 * <li>Batchrahmen.Log4JConfigFile: Pfad zur Log4J Konfigurationsdatei.
 * <li>Batchrahmen.CommitIntervall: Anzahl Satz-Verarbeitungen pro Commit.
 * <li>Batchrahmen.AnzahlZuVerarbeitendeDatensaetze: Anzahl zu verarbeitende Datensütze.
 * <li>AusfuehrungsBean: Name der Ausfuehrungsbean fuer die Batch-Logik.
 * <li>BatchId: ID des Batches (ID des Batch-Status-Datensatzes).
 * <li>BatchName: Name des Batches in der Batch-Status-Tabelle.
 * </ul>
 * <p>
 * Es künnen beliebige weitere Kommandozeilen-Parameter und Properties eingetragen werden. Die
 * Kommandozeilen-Parameter werden den Properties hinzugefügt und überschreiben sie ggf., bevor sie an die
 * Batchrahmen-Bean weitergegeben werden. Die Batchrahmen-Bean gibt die komplette Konfiguration an die
 * Ausfuehrungsbean weiter, welche sie zur Konfiguration verwenden kann.
 *
 *
 */
public class BatchLauncher {
    /** Die Konfiguration fuer den Batch-Rahmen. */
    private BatchKonfiguration rahmenKonfiguration;

    /**
     * Das Protokoll, zur Speicherung von Meldungen und Statistiken der Batch-Ausfuehrung.
     */
    private BatchErgebnisProtokoll protokoll;

    /**
     * Main-Methode zum Starten des Batches. Diese Methode ruft die Methode {@link #run(String[])} auf und
     * liefert deren ReturnCode per System.exit() zurück.
     *
     * @param args
     *            Kommandozeilen-Parameter.
     */
    public static void main(String[] args) {
        System.exit(BatchLauncher.run(args));
    }

    /**
     * Startet den Batch. Zur Konfiguration siehe Klassen-Kommentar.
     *
     * @param args
     *            Die Kommandozeilen-Argumente. Beschreibung siehe Klassen-Kommentar.
     * @return Return-Code des Batches.
     */
    public static int run(final String[] args) {
        IsyLogger log = null;
        BatchKonfiguration rahmenKonfiguration = null;
        DefaultBatchErgebnisProtokoll protokoll = null;
        String ergebnisDatei = null;
        BatchReturnCode returnCode = BatchReturnCode.FEHLER_ABBRUCH;
        try {
            rahmenKonfiguration = new BatchKonfiguration(args);
            ergebnisDatei =
                rahmenKonfiguration.getAsString(KonfigurationSchluessel.PROPERTY_BATCHRAHMEN_ERGEBNIS_DATEI,
                    null);
            initialisiereLogback(rahmenKonfiguration);
            protokoll = new DefaultBatchErgebnisProtokoll(ergebnisDatei);
            protokoll.batchStart(rahmenKonfiguration, args);

            log = IsyLoggerFactory.getLogger(BatchLauncher.class);
            log.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001, "Starte Batch.");
            new BatchLauncher(rahmenKonfiguration, protokoll).launch();
            if (protokoll.isBatchAbgebrochen()) {
                returnCode = BatchReturnCode.FEHLER_MANUELLER_ABBRUCH;
            }
            if (protokoll.isMaximaleLaufzeitUeberschritten()) {
                returnCode = BatchReturnCode.FEHLER_MAX_LAUFZEIT_UEBERSCHRITTEN;
            } else if (protokoll.enthaeltFehlerNachrichten()) {
                returnCode = BatchReturnCode.FEHLER_AUSGEFUEHRT;
            } else {
                returnCode = protokoll.getReturnCode();
                if (returnCode == null) {
                    returnCode = BatchReturnCode.OK;
                }
            }
        } catch (BatchAusfuehrungsException ex) {
            protokolliereFehler(log, protokoll, ex);
            if (ex.getReturnCode() != null) {
                returnCode = ex.getReturnCode();
            } else {
                returnCode = BatchReturnCode.FEHLER_ABBRUCH;
            }
        } catch (BatchrahmenException ex) {
            protokolliereFehler(log, protokoll, ex);
            returnCode = ex.getReturnCode();
        } catch (AutorisierungFehlgeschlagenException ex) {
            protokolliereFehler(log, protokoll, ex);
            returnCode = BatchReturnCode.FEHLER_KONFIGURATION;
        } catch (SicherheitTechnicalRuntimeException ex) {
            protokolliereFehler(log, protokoll, ex);
            returnCode = BatchReturnCode.FEHLER_KONFIGURATION;
        } catch (Throwable ex) {
            protokolliereFehler(log, protokoll, ex);
            returnCode = BatchReturnCode.FEHLER_ABBRUCH;
        } finally {
            if (protokoll != null) {
                protokoll.setReturnCode(returnCode);
                protokoll.batchEnde();
            }
        }
        System.out.print(returnCode.getWert() + ": " + returnCode.getText());
        return returnCode.getWert();
    }

    /**
     * Protokolliert einen Fehler. Wenn möglich wird dieses über den {@link Logger} log durchgeführt. Auf
     * jeden Fall erfolgt eine Ausgabe auf System.Err. Zusätzlich wird im ErgebnisProtokoll protokolliert.
     * @param log
     *            Der Logger.
     * @param protokoll
     *            Das DefaultBatchErgebnisProtokoll.
     * @param ex
     *            Die Aufgetretene Exception.
     * @throws BatchAusfuehrungsException
     */
    private static void protokolliereFehler(IsyLogger log, BatchErgebnisProtokoll protokoll, Throwable ex) {
        String nachricht = exceptionToString(ex);
        System.err.println(nachricht);
        if (log != null) {
            log.error(BatchRahmenEreignisSchluessel.EPLBAT00001, "Fehler bei der Batchausführung.", ex);
        } else {
            ex.printStackTrace();
        }
        String ausnahmeId = "ERROR";
        if (ex instanceof BatchAusfuehrungsException) {
            ausnahmeId = ((BatchAusfuehrungsException) ex).getAusnahmeId();
        } else if (ex instanceof BatchrahmenException) {
            ausnahmeId = ((BatchrahmenException) ex).getAusnahmeId();
        }
        if (protokoll != null) {
            try {
                protokoll.ergaenzeMeldung(new VerarbeitungsMeldung(ausnahmeId, MeldungTyp.FEHLER, nachricht));
            } catch (BatchrahmenProtokollException protokollEx) {
                System.err.println("Die Fehlermeldung " + protokollEx.toString()
                    + " konnte nicht in das Ergebnisprotokoll geschrieben werden.");
            }
        }
    }

    /**
     * Konvertiert eine Exception inkl. Stacktrace in einen String.
     * @param t
     *            Exception
     * @return String inkl. Stacktrace ohne Zeilenumbrüche.
     */
    private static String exceptionToString(Throwable t) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        t.printStackTrace(new PrintStream(out));
        return out.toString().replaceAll("\\r{0,1}\\n", " | ");
    }

    /**
     * initialisiert Logback mit der Log-Konfiguration, welche in der Property-Datei mit Schluessel
     * {@link BatchKonfiguration#PROPERTY_BATCHRAHMEN_LOG_CONF} angegeben wurde. Als default wird
     * /config/logback-batch.xml verwendet.
     *
     * @param konf
     *            die Batch-Konfiguration.
     * @throws JoranException
     *             Wenn der Logger nicht konfiguriert werden konnte.
     * @throws FileNotFoundException
     *             Wenn die Log Konfiguration nicht gefunden wurde.
     */
    private static void initialisiereLogback(BatchKonfiguration konf) throws FileNotFoundException,
        JoranException {
        String propertyFile =
            konf.getAsString(KonfigurationSchluessel.PROPERTY_BATCHRAHMEN_LOGBACK_CONF,
                "/config/logback-batch.xml");
        URL configLocation = BatchLauncher.class.getResource(propertyFile);
        if (configLocation == null) {
            throw new BatchrahmenInitialisierungException(NachrichtenSchluessel.ERR_KONF_DATEI_LESEN,
                propertyFile);
        }
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        context.reset(); // override default configuration
        context.putProperty("BatchId", konf.getAsString(KonfigurationSchluessel.PROPERTY_BATCH_ID));
        jc.doConfigure(configLocation);
    }

    /**
     * erzeugt eine neue Instanz und setzt die Konfiguration.
     *
     * @param rahmenKonfiguration
     *            die Konfiguration fuer den Batch-Rahmen.
     * @param protokoll
     *            das ErgebinsProtokoll.
     */
    public BatchLauncher(BatchKonfiguration rahmenKonfiguration, BatchErgebnisProtokoll protokoll) {
        this.rahmenKonfiguration = rahmenKonfiguration;
        this.protokoll = protokoll;
    }

    /**
     * Erzeugt die Spring-Kontexte fuer die Anwendung sowie den Batchrahmen. Startet die Batchrahmen-Bean über
     * Methode {@link Batchrahmen#runBatch(BatchKonfiguration)}.
     * @throws BatchAusfuehrungsException
     *             Wenn ein Fehler während der Batchausführung auftritt.
     *
     */
    private void launch() throws BatchAusfuehrungsException {
        ClassPathXmlApplicationContext anwendung = new ClassPathXmlApplicationContext();
        setzeSpringProfiles(anwendung);
        anwendung.setConfigLocations(this.rahmenKonfiguration.getAnwendungSpringKonfigFiles());
        anwendung.refresh();
        ClassPathXmlApplicationContext rahmen =
            new ClassPathXmlApplicationContext(this.rahmenKonfiguration.getBatchRahmenSpringKonfigFiles(),
                anwendung);
        rahmen.registerShutdownHook();
        rahmen.start();
        String rahmenBeanName =
            this.rahmenKonfiguration.getAsString(KonfigurationSchluessel.PROPERTY_BATCHRAHMEN_BEAN_NAME,
                "batchrahmen");
        Batchrahmen rahmenBean = (Batchrahmen) rahmen.getBean(rahmenBeanName);
        try {
            rahmenBean.runBatch(this.rahmenKonfiguration, this.protokoll);
        } finally {
            rahmen.close();
        }
    }

    /**
     * Setzt die Spring-Profile im ApplicationContext über eine Property-Source.
     *
     * @param applicationContext
     *            Spring-ApplicationContext.
     */
    private void setzeSpringProfiles(ClassPathXmlApplicationContext applicationContext) {
        PropertySource<Properties> ps =
            new BatchrahmenPropertySource(this.rahmenKonfiguration.getSpringProfilesKonfigFiles(),
                this.rahmenKonfiguration.getSpringProfilesProperties());
        // aktive Profile werden nur über die Property-Source gesteurt, deshalb alle anderen entfernen
        applicationContext.getEnvironment().setActiveProfiles(new String[] {});
        applicationContext.getEnvironment().getPropertySources().addFirst(ps);
    }

}
