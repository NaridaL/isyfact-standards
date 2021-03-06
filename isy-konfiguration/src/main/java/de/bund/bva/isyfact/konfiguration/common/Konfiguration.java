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
package de.bund.bva.isyfact.konfiguration.common;

import java.util.Set;

import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationException;

/**
 * Interface für den typsicheren Zugriff auf Konfigurationsparameter.
 * 
 */
public interface Konfiguration {

    /**
     * Liefert den angegebenen Konfigurationsparameter als String zurück. Falls der Parameter nicht gesetzt
     * ist, wird der Default-Wert zurückgegeben. Der String ist nicht getrimmt.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters, wie er in der Konfiguration enthalten ist.
     * @param defaultWert
     *            der Standardwert, welcher übernommen wird, falls der Wert nicht in der Konfiguration
     *            enthalten ist.
     * @throws KonfigurationException
     *             wenn der Konfigurationsparameter nicht in einen String konvertiert werden kann.
     * @return den Konfigurationsparameter als String.
     */
    public String getAsRawString(String schluessel, String defaultWert);

    /**
     * Liefert den angegebenen Konfigurationsparameter als String zurück.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters, wie er in der Konfiguration enthalten ist.
     * @throws KonfigurationException
     *             wenn der Konfigurationsparameter nicht gesetzt ist.
     * @return den Konfigurationsparameter als String.
     */
    public String getAsRawString(String schluessel);

    /**
     * Liefert den angegebenen Konfigurationsparameter als String zurück. Falls der Parameter nicht gesetzt
     * ist, wird der Default-Wert zurückgegeben. Der String wird vor der Rückgabe mit der Methode
     * <code>trim()</code> normalisiert.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters, wie er in der Konfiguration enthalten ist.
     * @param defaultWert
     *            der Standardwert, welcher übernommen wird, falls der Wert nicht in der Konfiguration
     *            enthalten ist.
     * @throws KonfigurationException
     *             wenn der Konfigurationsparameter nicht in einen String konvertiert werden kann.
     * @return den Konfigurationsparameter als String.
     */
    public String getAsString(String schluessel, String defaultWert);

    /**
     * Liefert den angegebenen Konfigurationsparameter als String zurück. Der String wird vor der Rückgabe mit
     * der Methode <code>trim()</code> normalisiert.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters, wie er in der Konfiguration enthalten ist.
     * @throws KonfigurationException
     *             wenn der Konfigurationsparameter nicht gesetzt ist.
     * @return den Konfigurationsparameter als String.
     */
    public String getAsString(String schluessel);

    /**
     * Liefert den angegebenen Konfigurationsparameter als int zurück. Falls der Parameter nicht gesetzt ist,
     * wird der Default-Wert zurückgegeben.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters, wie er in der Konfiguration enthalten ist.
     * @param defaultWert
     *            der Standardwert, welcher übernommen wird, falls der Wert nicht in der Konfiguration
     *            enthalten ist.
     * @throws KonfigurationException
     *             wenn der Konfigurationsparameter nicht in ein Integer konvertiert werden kann.
     * @return den Konfigurationsparameter als int.
     */
    public int getAsInteger(String schluessel, int defaultWert);

    /**
     * Liefert den angegebenen Konfigurationsparameter als int zurück.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters, wie er in der Konfiguration enthalten ist.
     * @throws KonfigurationException
     *             wenn der Konfigurationsparameter nicht gesetzt oder in ein Integer konvertiert werden kann.
     * @return den Konfigurationsparameter als int.
     */
    public int getAsInteger(String schluessel);

    /**
     * Liefert den angegebenen Konfigurationsparameter als double zurück. Falls der Parameter nicht gesetzt
     * ist, wird der Default-Wert zurückgegeben.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters, wie er in der Konfiguration enthalten ist.
     * @param defaultWert
     *            der Standardwert, welcher übernommen wird, falls der Wert nicht in der Konfiguration
     *            enthalten ist.
     * @throws KonfigurationException
     *             wenn der Konfigurationsparameter nicht in ein Double konvertiert werden kann.
     * @return den Konfigurationsparameter als double.
     */
    public double getAsDouble(String schluessel, double defaultWert);

    /**
     * Liefert den angegebenen Konfigurationsparameter als double zurück.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters, wie er in der Konfiguration enthalten ist.
     * @throws KonfigurationException
     *             wenn der Konfigurationsparameter nicht gesetzt oder in ein Double konvertiert werden kann.
     * @return den Konfigurationsparameter als double.
     */
    public double getAsDouble(String schluessel);

    /**
     * Liefert den angegebenen Konfigurationsparameter als long zurück. Falls der Parameter nicht gesetzt ist,
     * wird der Default-Wert zurückgegeben.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters, wie er in der Konfiguration enthalten ist.
     * @param defaultWert
     *            der Standardwert, welcher übernommen wird, falls der Wert nicht in der Konfiguration
     *            enthalten ist.
     * @throws KonfigurationException
     *             wenn der Konfigurationsparameter nicht in ein Long konvertiert werden kann.
     * @return den Konfigurationsparameter als long.
     */
    public long getAsLong(String schluessel, long defaultWert);

    /**
     * Liefert den angegebenen Konfigurationsparameter als long zurück.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters, wie er in der Konfiguration enthalten ist.
     * @throws KonfigurationException
     *             wenn der Konfigurationsparameter nicht gesetzt oder in ein Long konvertiert werden kann.
     * @return den Konfigurationsparameter als long.
     */
    public long getAsLong(String schluessel);

    /**
     * Liefert den angegebenen Konfigurationsparameter als boolean zurück. Falls der Parameter nicht gesetzt
     * ist, wird der Default-Wert zurückgegeben.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters, wie er in der Konfiguration enthalten ist.
     * @param defaultWert
     *            der Standardwert, welcher übernommen wird, falls der Wert nicht in der Konfiguration
     *            enthalten ist.
     * @throws KonfigurationException
     *             wenn der Konfigurationsparameter nicht in ein Boolean konvertiert werden kann.
     * @return den Konfigurationsparameter als boolean.
     */
    public boolean getAsBoolean(String schluessel, boolean defaultWert);

    /**
     * Liefert den angegebenen Konfigurationsparameter als boolean zurück.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters, wie er in der Konfiguration enthalten ist.
     * @throws KonfigurationException
     *             wenn der Konfigurationsparameter nicht gesetzt oder in ein Boolean konvertiert werden kann.
     * @return den Konfigurationsparameter als boolean.
     */
    public boolean getAsBoolean(String schluessel);

    /**
     * Liefert die Menge der Konfigurationsschlüssel.
     * @return alle Konfigurationsschlüssel als Set.
     */
    public Set<String> getSchluessel();

}
