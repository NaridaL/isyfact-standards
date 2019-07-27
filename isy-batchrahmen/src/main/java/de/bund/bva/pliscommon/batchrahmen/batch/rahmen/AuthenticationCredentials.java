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
package de.bund.bva.pliscommon.batchrahmen.batch.rahmen;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Diese Klasse enthält alle Daten, die für die Authentifizierung eines Batches notwendig sind.
 *
 *
 */
public class AuthenticationCredentials {

    /**
     * Enthält die Benutzerkennung des Batchbenutzers.
     */
    private @Nullable String benutzerkennung;

    /**
     * Enthält das Behördenkennzeichen des Batchbenutzers.
     */
    private @Nullable String behoerdenkennzeichen;

    /**
     * Enthält das Passwort des Batchbenutzers.
     */
    private @Nullable String passwort;

    /**
     * Liefert das Feld {@link #benutzerkennung} zurück.
     * @return Wert von benutzerkennung
     */
    public @Nullable String getBenutzerkennung() {
        return this.benutzerkennung;
    }

    /**
     * Erstellt eine neues unbefülltes {@link AuthenticationCredentials}-Objekt.
     */
    public AuthenticationCredentials() {
        super();
    }

    /**
     * Erstellt eine neues befülltes {@link AuthenticationCredentials}-Objekt.
     * @param benutzerkennung
     *            der Wert für {@link #benutzerkennung}
     * @param behoerdenkennzeichen
     *            der Wert für {@link #behoerdenkennzeichen}
     * @param passwort
     *            der Wert für {@link #passwort}
     */
    public AuthenticationCredentials(String benutzerkennung, String behoerdenkennzeichen, String passwort) {
        super();
        this.benutzerkennung = benutzerkennung;
        this.behoerdenkennzeichen = behoerdenkennzeichen;
        this.passwort = passwort;
    }

    /**
     * Setzt das Feld {@link #benutzerkennung}.
     * @param benutzerkennung
     *            Neuer Wert für benutzerkennung
     */
    public void setBenutzerkennung(String benutzerkennung) {
        this.benutzerkennung = benutzerkennung;
    }

    /**
     * Liefert das Feld {@link #behoerdenkennzeichen} zurück.
     * @return Wert von behoerdenkennzeichen
     */
    public @Nullable String getBehoerdenkennzeichen() {
        return this.behoerdenkennzeichen;
    }

    /**
     * Setzt das Feld {@link #behoerdenkennzeichen}.
     * @param behoerdenkennzeichen
     *            Neuer Wert für behoerdenkennzeichen
     */
    public void setBehoerdenkennzeichen(String behoerdenkennzeichen) {
        this.behoerdenkennzeichen = behoerdenkennzeichen;
    }

    /**
     * Liefert das Feld {@link #passwort} zurück.
     * @return Wert von passwort
     */
    public @Nullable String getPasswort() {
        return this.passwort;
    }

    /**
     * Setzt das Feld {@link #passwort}.
     * @param passwort
     *            Neuer Wert für passwort
     */
    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

}
