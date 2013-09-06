package ea.edu;

/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 *
 * Copyright (C) 2011  Michael Andonie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 

import ea.*;
import ea.edu.*;

/**
 * Die Klasse SPIEL ist ein Template, das so wie es ist (nur package
 * entsprechend ändern) an Schueler ausgegeben werden kann. Es startet alles 
 * Notwendige fuer ein Spiel. Bei Konstruktoren mit Parametern kann die 
 * Anzeige abgeaendert werden.
 * @author Andonie
 */
public class SPIEL 
{
    /**
     * Die Anzeige des Spiels.
     */
    private AnzeigeE anzeige;
    /**
     * Dieser Zaehler sorgt ermoeglicht den Tik-Tak-Wechsel.
     */
    //MIKE
    private int zaehler;
    //-----
    
    /**
     * Erstellt ein Spiel. Startet die Anzeige.
     * @param punkteLinks   ist dieser Wert <code>true</code>, so sieht man
     *                      links eine Punkteanzeige. Ist er <code>false</code>
     *                      sieht man keine.
     * @param punkteRechts  ist dieser Wert <code>true</code>, so sieht man
     *                      rechts eine Punkteanzeige. Ist er <code>false</code>
     *                      sieht man keine.
     * @param maus          ist dieser Wert <code>true</code>, wird eine Maus im
     *                      Spiel angezeigt und verwendet. Ist er
     *                      <code>false</code>, gibt es keine Maus.
     */
    public SPIEL(boolean punkteLinks, boolean punkteRechts, boolean maus) {
        //MIKE
        zaehler = 0;
        //-----
        anzeige = new AnzeigeE();
        //Punkteanzeige
        anzeige.punkteLinksSichtbarSetzen(punkteLinks);
        anzeige.punkteRechtsSichtbarSetzen(punkteRechts);
        
        //Maus ggf. aktivieren
        if(maus) {
            anzeige.klickReagierbarAnmelden(this, true);
        }
        
        //Tastatur
        anzeige.tastenReagierbarAnmelden(this);
        
        //Ticker
        //Alle 500 Millisekunden (=Jede halbe Sekunde) ein Tick
        anzeige.tickerAnmelden(this, 500); 
    }
    
    /**
     * Erstellt ein einfaches Spiel ohne Anzeige und Maus.<br />
     * Das Spiel hat somit Ticker und Tastatureingaben.
     */
    public SPIEL() {
        this(false, false, false);
    }

    /**
     * Wird regelmaessig aufgerufen. So kommt Bewegung ins Spiel!
     */
    public void tick() {
        //Einfache Bildschirmausgabe. Kann beliebig abgeaendert werden.
        //MIKE
        zaehler++;
        zaehler = zaehler % 2;
        if (zaehler == 1) {
            System.out.println("Tick!");
        }
        else {
            System.out.println("Tack!");
        }
        //-----
        
    }

    /**
     * Wird bei jedem Mausklick (Linksklick) aufgerufen.
     * @param x Die X-Koordinate des Klicks
     * @param y Die Y-Koordinate des Klicks
     */
    public void klickReagieren(int x, int y) {
        //Einfache Bildschirmausgabe. Kann beliebig abgeaendert werden.
        System.out.println("Klick im bei (" + x  + ", " + y + ").");
    }
    
    /**
     * Wird bei jedem Tastendruck ausgefuehrt.
     * @param tastenkuerzel Der int-Wert, der fuer die gedrueckte Taste steht.
     *                      Details koennen in der <i>Tabelle aller 
     *                      Tastaturkuerzel</i> abgelesen werden.
     */
    public void tasteReagieren(int tastenkuerzel) {
        System.out.println("Taste mit K�rzel " + tastenkuerzel + 
                " wurde gedr�ckt");
    }
    
    //MIKE - weitere W�nschenswerte Methoden
    /**
     * Setzt das Ticker-Intervall.
     * @param ms	Die Zeit in Millisekunden zwischen zwei
     * 				Aufrufen der <code>tick()</code>-Methode.
     */
    public void tickerIntervallSetzen(int ms) {
        anzeige.tickerAbmelden(this);
        anzeige.tickerAnmelden(this, ms);
    }
    
    /**
     * Stoppt die Ticker-Funktion. Die <code>tick()</code>-Methode
     * wird nicht weiter aufgerufen. Der Tick-Zustand kann durch die �
     * Methode <code>tuckerNeuStarten(int ms)</code> wiederhergestellt
     * werden.
     * @see #tickerNeuStarten(int)
     */
    public void tickerStoppen() {
        anzeige.tickerAbmelden(this);
    }
    
    /**
     * Startet den Ticker neu. 
     * @param ms	Die Zeit in Millisekunden zwischen zwei
     * 				Aufrufen der <code>tick()</code>-Methode. 
     */
    public void tickerNeuStarten(int ms) {
        anzeige.tickerAnmelden(this, ms);
    }
    
    /**
     * Setzt ein neues Maus-Icon.
     * @param pfad	Der Pfad zu dem Bild (jpg, bmp, png), das 
     * 				das neue Maus-Icon werden soll. ZB: "mausicon.png"
     * @param hotspotX	Die X-Koordinate des Hotspots f�r das neue
     * 					Maus-Icon.
     * @param hotspotY	Die Y-Koordinate des Hotspots f�r das neue
     * 					Maus-Icon.
     */
    public void MausIconSetzen(String pfad, int hotspotX, int hotspotY) {
        ea.edu.FensterE.getFenster().mausAnmelden(new Maus(new Bild(0,0,pfad), new Punkt(hotspotX, hotspotY)));
    }
    
    /**
     * Sorgt dafuer, dass sowohl der rechte als auch der linke Punktestand sichtbar ist.
     */
    public void allePunkteSichtbar() {
    	anzeige.punkteLinksSichtbarSetzen(true);
    	anzeige.punkteRechtsSichtbarSetzen(true);
    }
    
    /**
     * Sorgt dafuer, dass nur der linke Punktestand sichtbar ist.
     */
    public void nurRechtePunkteSichtbar() {
    	anzeige.punkteLinksSichtbarSetzen(false);
    	anzeige.punkteRechtsSichtbarSetzen(true);
    }
    
    /**
     * Sorgt dafuer, dass nur der rechte Punktestand sichtbar ist.
     */
    public void nurLinkePunkteSichtbar() {
    	anzeige.punkteLinksSichtbarSetzen(true);
    	anzeige.punkteRechtsSichtbarSetzen(false);
    }
    
    /**
     * Sorgt dafuer, dass weder der noch der linke Punktestand sichtbar ist.
     */
    public void allePunkteUnsichtbar() {
    	anzeige.punkteLinksSichtbarSetzen(false);
    	anzeige.punkteRechtsSichtbarSetzen(false);
    }
    
    /**
     * Setzt den linken Punktestand. Aenderungen sind nur sichtbar,
     * wenn auch der linke Punktestand sichtbar ist.
     * @param pl	Der neue linke Punktestand.
     */
    public void punkteLinksSetzen(int pl) {
        anzeige.punkteLinksSetzen(pl);
    }
    
    /**
     * Setzt den rechten Punktestand. Aenderungen sind nur sichtbar,
     * wenn auch der rechte Punktestand sichtbar ist.
     * @param pr	Der neue rechte Punktestand.
     */
    public void punkteRechtsSetzen(int pr) {
        anzeige.punkteRechtsSetzen(pr);
    }
    
    /**
     * Gibt eine Zufallszahl aus.
     * @param von   Die Untergrenze der Zufallszahl (INKLUSIVE)
     * @param bis   Die Obergrenze der Zufallszahl (EXKLUSIVE)
     * @return      Eine Zufallszahl z mit von <= z < bis
     */
    public int zufallszahlVonBis(int von, int bis) {
        return anzeige.zufallszahlVonBis(von, bis);
    }
    
    /**
     * Setzt eine Hintergrundgrafik fuer das Spiel.
     * @param pfad	Der Pfad der Bilddatei (jpg, bmp, png) des Bildes,
     * 				das benutzt werden soll. ZB: "hintergrund.jpg"
     */
    public void hintergrundgrafikSetzen(String pfad) {
    	ea.edu.FensterE.getFenster().hintergrundSetzen(new Bild(0,0,pfad));
    }
    
    public static void main(String[] args) {
        new SPIEL(true, true, true);
    }

    
    
}
