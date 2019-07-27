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
package de.bund.bva.pliscommon.batchrahmen.core.protokoll;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;

import org.eclipse.jdt.annotation.Nullable;
import org.xml.sax.SAXException;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.pliscommon.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.pliscommon.batchrahmen.batch.konstanten.BatchRahmenEreignisSchluessel;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.MeldungTyp;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.StatistikEintrag;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.VerarbeitungsMeldung;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.pliscommon.batchrahmen.core.exception.BatchrahmenProtokollException;
import de.bund.bva.pliscommon.batchrahmen.core.konstanten.NachrichtenSchluessel;

/**
 * Standardimplementierung eines {@link BatchErgebnisProtokoll}.
 *
 *
 */
public class DefaultBatchErgebnisProtokoll implements BatchErgebnisProtokoll {
    /**
     * Der Logger.
     */
    private IsyLogger log;

    /**
     * Liste der Statistik-Einträge.
     */
    private Map<String, StatistikEintrag> statistik = new HashMap<>();

    /**
     * ReturnCode des Batchs.
     */
    private @Nullable BatchReturnCode returnCode;

    /**
     * Flag ob Fehlernachrichten enthalten sind.
     */
    private boolean enthaeltFehlermeldung;

    /**
     * Flag ob der Batch abgerochen wurde.
     */
    private boolean isBatchAbgebrochen;

    /**
     * FileOutputStream zum Protokollschreiben.
     */
    private @Nullable ProtokollGenerator protokollGenerator;

    /**
     * Startdatum des Batches.
     */
    private @Nullable Date startDatum;

    /**
     * Enddatum des Batches.
     */
    private @Nullable Date endeDatum;

    /**
     * Die BatchID des ausgeführten Batches.
     */
    private @Nullable String batchId;

    /**
     * Die Parameter, mit denen der Batch gestartet wurde.
     */
    private String @Nullable [] parameter;

    /**
     * Flag ob die maximale Laufzeit überschritten wurde.
     */
    private boolean maximaleLaufzeitUeberschritten;

    /**
     * Erzeugt ein neues ErgebnisProtokoll.
     * @param ergebnisDatei
     *            Ausgabedatei
     * @throws IOException
     *             Falls die temporäre Datei für die Meldungen nicht angelegt werden kann.
     */
    public DefaultBatchErgebnisProtokoll(String ergebnisDatei) throws IOException {
        // Keine statische Logger konfiguration, da der Batchrahmen Log4j erst zur Laufzeit konfiguriert.
        this.log = IsyLoggerFactory.getLogger(DefaultBatchErgebnisProtokoll.class);

        if (!"".equals(ergebnisDatei) && ergebnisDatei != null) {
            if (this.log != null) {
                this.log.info(LogKategorie.JOURNAL, BatchRahmenEreignisSchluessel.EPLBAT00001,
                    "Erstelle Ergebnisprotokoll '{}' und XMLProtokollGenerator...", ergebnisDatei);
            }
            try {
                // ProtokollGenerator erstellen
                this.protokollGenerator = new XmlProtokollGenerator(new FileOutputStream(ergebnisDatei));
            } catch (TransformerConfigurationException e) {
                throw new BatchrahmenProtokollException(NachrichtenSchluessel.ERR_BATCH_PROTOKOLL, e);
            } catch (SAXException e) {
                throw new BatchrahmenProtokollException(NachrichtenSchluessel.ERR_BATCH_PROTOKOLL, e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReturnCode(BatchReturnCode returnCode) {
        this.returnCode = returnCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registriereStatistikEintrag(StatistikEintrag initialEintrag) {
        if (initialEintrag.getReihenfolge() == 0) {
            initialEintrag.setReihenfolge(ermittleMaximaleReihenfolge() + 1);
        }
        this.statistik.put(initialEintrag.getId(), initialEintrag);
    }

    /**
     * Ermittelt den maximalen Wert für die Reihenfolge der Statistik-Einträge.
     * @return Maximum der Reihenfolge Werte
     */
    private int ermittleMaximaleReihenfolge() {
        int max = 0;
        for (StatistikEintrag eintrag : this.statistik.values()) {
            max = Math.max(max, eintrag.getReihenfolge());
        }
        return max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatistikEintrag getStatistikEintrag(String id) {
        return this.statistik.get(id);
    }

    /**
     * {@inheritDoc}
     * @throws BatchProtokollException
     */
    @Override
    public void ergaenzeMeldung(VerarbeitungsMeldung meldung) {
        if (meldung.getTyp().equals(MeldungTyp.FEHLER)) {
            this.enthaeltFehlermeldung = true;
        }
        ProtokollGenerator protokollGenerator2 = this.protokollGenerator;
        if (protokollGenerator2 != null) {
            protokollGenerator2.erzeugeMeldung(meldung);
            protokollGenerator2.flusheOutput();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable BatchReturnCode getReturnCode() {
        return this.returnCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, StatistikEintrag> getStatistik() {
        return this.statistik;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean enthaeltFehlerNachrichten() {
        return this.enthaeltFehlermeldung;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StatistikEintrag> getStatistikEintraege() {
        List<StatistikEintrag> result = new ArrayList<>(this.statistik.values());
        Collections.sort(result);
        return result;
    }

    /**
     * Liefert das Feld 'startDatum' zurück.
     * @return Wert von startDatum
     */
    @Override
    public @Nullable Date getStartDatum() {
        return this.startDatum;
    }

    /**
     * Setzt das Feld 'startDatum'.
     * @param startDatum
     *            Neuer Wert für startDatum
     */
    @Override
    public void setStartDatum(Date startDatum) {
        this.startDatum = startDatum;
    }

    /**
     * Liefert das Feld 'endeDatum' zurück.
     * @return Wert von endeDatum
     */
    @Override
    public @Nullable Date getEndeDatum() {
        return this.endeDatum;
    }

    /**
     * Setzt das Feld 'endeDatum'.
     * @param endeDatum
     *            Neuer Wert für endeDatum
     */
    @Override
    public void setEndeDatum(Date endeDatum) {
        this.endeDatum = endeDatum;
    }

    /**
     * Liefert das Feld 'batchId' zurück.
     * @return Wert von batchId
     */
    @Override
    public @Nullable String getBatchId() {
        return this.batchId;
    }

    /**
     * Setzt das Feld 'batchId'.
     * @param batchId
     *            Neuer Wert für batchId
     */
    @Override
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    /**
     * Liefert das Feld 'parameter' zurück.
     * @return Wert von parameter
     */
    @Override
    public String @Nullable [] getParameter() {
        return this.parameter;
    }

    /**
     * Setzt das Feld 'parameter'.
     * @param parameter
     *            Neuer Wert für parameter
     */
    @Override
    public void setParameter(String[] parameter) {
        this.parameter = parameter;
    }

    /**
     *
     * {@inheritDoc}.
     */
    @Override
    public void batchEnde() {
        setEndeDatum(new Date());
        ProtokollGenerator protokollGenerator2 = this.protokollGenerator;
        if (protokollGenerator2 != null) {
            protokollGenerator2.erzeugeStatistik(this);
            protokollGenerator2.erzeugeEndeInfoElement(this);
            protokollGenerator2.erzeugeReturnCodeElement(this);
            protokollGenerator2.close();
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void batchStart(BatchKonfiguration konfiguration, String[] args) {
        setStartDatum(new Date());
        setBatchId(konfiguration.getProperties().getProperty("BatchId"));
        setParameter(args);

        if (this.protokollGenerator != null) {
            this.protokollGenerator.erzeugeStartInfoElement(this);
        }
    }

    /**
     * Liefert das Feld 'isBatchAbgebrochen' zurück.
     * @return Wert von isBatchAbgebrochen
     */
    @Override
    public boolean isBatchAbgebrochen() {
        return this.isBatchAbgebrochen;
    }

    /**
     * Setzt das Feld 'isBatchAbgebrochen'.
     * @param isBatchAbgebrochen
     *            Neuer Wert für isBatchAbgebrochen
     */
    @Override
    public void setBatchAbgebrochen(boolean isBatchAbgebrochen) {
        this.isBatchAbgebrochen = isBatchAbgebrochen;
    }

    /**
     * Liefert das Feld 'maximaleLaufzeitUeberschritten' zurück.
     * @return Wert von maximaleLaufzeitUeberschritten
     */
    @Override
    public boolean isMaximaleLaufzeitUeberschritten() {
        return this.maximaleLaufzeitUeberschritten;
    }

    /**
     * Setzt das Feld 'maximaleLaufzeitUeberschritten'.
     * @param maximaleLaufzeitUeberschritten
     *            Neuer Wert für maximaleLaufzeitUeberschritten
     */
    @Override
    public void setMaximaleLaufzeitUeberschritten(boolean maximaleLaufzeitUeberschritten) {
        this.maximaleLaufzeitUeberschritten = maximaleLaufzeitUeberschritten;
    }

}
