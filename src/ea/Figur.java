/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2014 Michael Andonie and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ea;

import ea.internal.collision.BoxCollider;
import ea.internal.collision.Collider;
import ea.internal.collision.ColliderGroup;
import ea.internal.gra.PixelFeld;
import ea.internal.util.Logger;

import java.awt.*;
import java.util.ArrayList;

/**
 * Eine Figur ist eine aus einer Datei geladene Sammlung von Pixeln, die orientierungsmaessig
 * rechteckig gehandelt werden.<br /> <code> //Die Figur laden<br /> Figur meineFigur = new
 * Figur(30, 100, "meineFigurDateiImProjektordner.eaf"); //Laedt die Figur und setzt sie an Position
 * (30|100) <br /><br /> //Die Bewegung der Figur starten (muss nicht ausgefuehrt werden, Sa
 * standard) <br /> meineFigur.animiertSetzen(true);<br /><br /> // Die Figur an dem entsprechenden
 * Knoten zum halten und Zeichnen anmelden (In diesem Fall die Wurzel in der Klasse Game)<br />
 * wurzel.add(meineFigur);<br /> </code><br /> <br /> <br /> Dies ist einfachste Methode, eine Figur
 * zu laden.<br /> Der Figureneditor zum Erstellen der zu ladenden ".eaf"-Dateien ist als
 * ausfuehrbare ".jar"-Datei fester Bestandteil des Engine-Alpha-Programmierkits.
 *
 * @author Michael Andonie
 */
public class Figur extends Raum {
	private static final long serialVersionUID = -1063599158092163887L;

	/**
	 * Eine Liste aller Figuren.
	 */
	private static ArrayList<Figur> liste;

	/**
	 * Die einzelnen Bilder der Figur.<br /> hat es mehr als eines, so wird ein periodischer Wechsel
	 * vollzogen.
	 */
	protected PixelFeld[] animation;

	/**
	 * In diesem Intervall wird die Figur animiert.
	 */
	private int intervall = 100;

	/**
	 * Der Index des Aktuelle benutzten PixelFeldes.
	 */
	private int aktuelle = 0;

	/**
	 * Gibt an, ob die Figur gerade animiert werden soll.
	 */
	private boolean laeuft;

	/**
	 * Gibt an, ob diese Figur gerade entlang der X-Achse (waagrecht) gespiegelt wird.
	 */
	private boolean spiegelX = false;

	/**
	 * Gibt an, ob diese Figur gerade entlang der Y-Achse (senkrecht) gespiegelt wird.
	 */
	private boolean spiegelY = false;

	static {
		liste = new ArrayList<>();
		Manager.standard.anmelden((new Ticker() {
			int runde = 0;

			@Override
			public void tick () {
				runde++;
				try {
					for (Figur f : liste) {
						if (f.animiert()) {
							f.animationsSchritt(runde);
						}
					}
				} catch (java.util.ConcurrentModificationException e) {
					//
				}
			}
		}), 1);
	}

	/**
	 * Erstellt eine Figur <b>ohne Positionsangabe</b>. Die Figur liegt an Position (0|0). Dieser
	 * Konstruktor vereinfacht das Laden <i>vieler Figuren</i>, z.B. für eine
	 * <code>ActionFigur</code>.
	 *
	 * @param verzeichnis
	 * 		Das Verzeichnis, aus dem die Figur zu laden ist.
	 *
	 * @see ea.ActionFigur
	 */
	public Figur (String verzeichnis) {
		this(0, 0, verzeichnis);
	}

	/**
	 * Standart-Konstruktor für Objekte der Klasse <code>Figur</code>.
	 *
	 * @param x
	 * 		X-Position; die links obere Ecke
	 * @param y
	 * 		Y-Position; die links obere Ecke
	 * @param verzeichnis
	 * 		Das verzeichnis, aus dem die Figur zu laden ist.
	 */
	public Figur (float x, float y, String verzeichnis) {
		this(x, y, verzeichnis, true);
	}

	/**
	 * Besonderer Konstruktor fuer Objekte der Klasse <code>Figur</code>. Dieser Konstruktor wird
	 * vor allem intern (fuer Actionfiguren) verwendet. Anders ist nur die Option darauf, dass die
	 * Figur am Animationssystem direkt teilnimmt. Dies ist beim Standart-Konstruktor immer der
	 * Fall.
	 *
	 * @param x
	 * 		X-Position; die links obere Ecke
	 * @param y
	 * 		Y-Position; die links obere Ecke
	 * @param verzeichnis
	 * 		Das verzeichnis, aus dem die Figur zu laden ist.
	 * @param add
	 * 		Ob diese Figur am Animationssystem direkt teilnehmen soll. (Standard)
	 */
	public Figur (float x, float y, String verzeichnis, boolean add) {
		super();
		position = new Punkt(x, y);

		this.animation = DateiManager.figurEinlesen(verzeichnis).animation;

		if (add) {
			liste.add(this);
		}

		laeuft = true;
	}

	/**
	 * Der parameterlose Konstruktor.<br /> Hiebei wird nichts gesetzt, die Figur hat die Position
	 * (0|0) sowie keine Animationen, die Referenz auf die einzelnen Pixelfelder ist
	 * <code>null</code>.<br /> Dieser Konstruktor wird intern verwendet, um Figurdaten zu laden.<br
	 * /> Daher ist er nicht für die direkte Verwendung in Spielen gedacht.
	 */
	public Figur () {
		liste.add(this);
	}

	/**
	 * Löscht ein Animationsbild an einem bestimmten Index und rückt den Rest nach.
	 *
	 * @param index
	 * 		Der Index des zu löschenden Einzelbildes.
	 *
	 * @throws java.lang.ArrayIndexOutOfBoundsException
	 * 		Wenn ein Index außerhalb der Größes des internen Arrays gewählt wurde.
	 * @throws java.lang.RuntimeException
	 * 		Falls nur noch ein Element vorhanden war. Das letzte Element darf nicht entfernt werden!
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void animationLoeschen (int index) { // TODO War das vorher buggy?
		if (animation.length < 2) {
			throw new RuntimeException("Es muss mindestens ein Pixelfeld erhalten bleiben! Eine " + "weitere Löschung hätte das letzte Element entfernt.");
		}

		PixelFeld[] neu = new PixelFeld[animation.length - 1];

		System.arraycopy(animation, 0, neu, 0, index);
		System.arraycopy(animation, index + 1, neu, index, neu.length - index);

		aktuelle = 0;
		animation = neu;
	}

	/**
	 * Setzt das Animationsbild auf einer bestimmten Position auf ein neues Pixelfeld.
	 *
	 * @param bild
	 * 		Neues Pixelfeld an der angegebenen Position. Darf nicht <code>null</code> sein!
	 * @param index
	 * 		Index des zu ersetzenden Pixelfeldes
	 *
	 * @throws java.lang.ArrayIndexOutOfBoundsException
	 * 		Wenn ein Index außerhalb der Größes des internen Arrays gewählt wurde.
	 * @throws java.lang.IllegalArgumentException
	 * 		Wenn der Parameter <code>bild</code> <code>null</code> war.
	 */
	@NoExternalUse // da PixelFeld in ea.internal ist
	@SuppressWarnings ( "unused" )
	public void animationsBildSetzen (PixelFeld bild, int index) {
		if (bild == null) {
			throw new IllegalArgumentException("Parameter bild darf nicht null sein!");
		}

		animation[index] = bild;
	}

	/**
	 * Verschiebt die Position eines Animationsbildes.<br /> Hierbei wird ein bisschen mit den
	 * Werten des Arrays gespielt, jedoch kein neues Array erstellt. Sind beide Eingabeparameter
	 * exakt gleich, passiert gar nichts, auch wenn die beiden Werte außerhalb des Arrays liegen
	 * sollten.
	 *
	 * @param indexAlt
	 * 		Index des zu verschiebenden Bildes
	 * @param indexNeu
	 * 		Index, den das Bild nach dem verschieben haben soll.
	 *
	 * @throws ArrayIndexOutOfBoundsException
	 * 		Wenn ein Index außerhalb der Größes des internen Arrays gewählt wurde.
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void animationsBildVerschieben (int indexAlt, int indexNeu) {
		if (indexAlt == indexNeu) {
			return;
		}

		PixelFeld bild = animation[indexAlt];

		if (indexAlt > indexNeu) {
			System.arraycopy(animation, indexNeu + 1, animation, indexNeu, indexAlt - indexNeu);
		} else {
			System.arraycopy(animation, indexAlt, animation, indexAlt - 1, indexNeu - indexAlt);
		}

		animation[indexNeu] = bild;
	}

	/**
	 * Ruft das naechste Bild im Animationszyklus auf.<br /> Sollte nicht von aussen aufgerufen
	 * werden, stellt aber in keinem mathematisch greifbaren Fall ein Problem dar.
	 *
	 * @param runde
	 * 		Die Runde dieses Schrittes; dieser Wert wird intern benoetigt, um zu entscheiden, ob etwas
	 * 		passieren soll oder nicht.
	 */
	@NoExternalUse
	public void animationsSchritt (int runde) {
		if (runde % intervall != 0) {
			return;
		}

		if (aktuelle == animation.length - 1) {
			aktuelle = 0;
		} else {
			aktuelle++;
		}
	}

	/**
	 * Setzt eine neue Animationsreihe.
	 *
	 * @param a
	 * 		Die neue Animationsreihe. Das Array muss mindestens ein Pixelfeld zum Inhalt haben<br />
	 * 		Diese <b>muss</b> aus Pixelfeldern gleicher Maße bestehen!
	 *
	 * @throws java.lang.IllegalArgumentException
	 * 		Falls <code>animation</code> <code>null</code> war oder keine Elemente enthalten hat.
	 */
	public void animationSetzen (PixelFeld[] a) {
		if (a == null) {
			throw new IllegalArgumentException("Parameter a darf nicht null sein!");
		} else if (a.length < 1) {
			throw new IllegalArgumentException("Parameter a muss mindestens die Länge 1 haben.");
		}

		animation = a;
		aktuelle = 0;
	}

	/**
	 * Setzt die Animationsarbeit bei dieser Figur.
	 *
	 * @param animiert
	 * 		ob die Figur animiert werden soll, oder ob sie ein Standbild sein soll.
	 */
	public void animiertSetzen (boolean animiert) {
		this.laeuft = animiert;
	}

	/**
	 * Gibt an, ob das Bild momentan animiert wird bzw. animiert werden soll.
	 */
	public boolean animiert () {
		return laeuft;
	}

	/**
	 * Setzt das aktuelle Animationsbild.<br /> Die Figur, die im Pixelfeldeditor erstellt wurde
	 * besteht den Bildern (1, 2, ..., n), aber <b>ACHTUNG</b>, die Indizes fangen bei <b>0</b> an
	 * und hören dann eins frueher auf (0, 1, ..., (n-1)). Das heißt, dass wenn als Index
	 * <code>5</code> eingegeben wird, ist das Bild gemeint, das im Figureneditor als <code>Bild
	 * 6</code> bezeichnet wurde.
	 *
	 * @param bildIndex
	 * 		Der Index des anzuzeigenden Bildes
	 */
	public void animationsBildSetzen (int bildIndex) {
		if (bildIndex < 0 || bildIndex >= animation.length) {
			Logger.error("Achtung! Der zu setzende Bildindex war größer als der größte " + "vorhandene Index oder kleiner 0! Daher wird nichts gesetzt.");
			return;
		}

		aktuelle = bildIndex;
	}

	/**
	 * Setzt den Größenfaktor dieser Figur neu.
	 * <p/>
	 * Das heisst, ab sofort wird ein Unterquadrat dieser Figur eine Seitenlänge von Pixeln in Größe
	 * des Faktors haben
	 *
	 * @param faktor
	 * 		Der neue Größenfaktor
	 */
	public void faktorSetzen (int faktor) {
		for (int i = 0; i < animation.length; i++) {
			animation[i].faktorSetzen(faktor);
		}
	}

	/**
	 * Setzt sämtliche Farbwerte sämtlicher Bilder der Figur in ihr Negativ.<br /> Dadurch ändert
	 * sich die Erscheinung der Figur.
	 *
	 * @see #heller()
	 * @see #dunkler()
	 * @see #farbenTransformieren(int, int, int)
	 */
	public void negativ () {
		for (int i = 0; i < animation.length; i++) {
			animation[i].negativ();
		}
	}

	/**
	 * Hellt alle Farbwerte der Figur auf.<br /> Gegenstück zur Methode <code>dunkler()</code>.<br
	 * /> <b>Achtung:</b><br /> Wegen Rundungsfehlern muss der Aufruf von <code>dunkler()</code>
	 * nach dem Aufruf von <code>heller()</code> nicht zwanghaft zum urspruenglichen Zustand
	 * fuehren!
	 *
	 * @see #dunkler()
	 * @see #negativ()
	 * @see #farbenTransformieren(int, int, int)
	 */
	public void heller () {
		for (int i = 0; i < animation.length; i++) {
			animation[i].heller();
		}
	}

	/**
	 * Dunkelt alle Farbwerte der Figur ab.<br /> Gegenstueck zur Methode <code>heller()</code>.<br
	 * /> <b>Achtung:</b><br /> Wegen Rundungsfehlern muss der Aufruf von <code>dunkler()</code>
	 * nach dem Aufruf von <code>heller()</code> nicht zwanghaft zum urspruenglichen Zustand
	 * fuehren!
	 *
	 * @see #heller()
	 * @see #negativ()
	 * @see #farbenTransformieren(int, int, int)
	 */
	public void dunkler () {
		for (int i = 0; i < animation.length; i++) {
			animation[i].dunkler();
		}
	}

	/**
	 * Sorgt fuer eine Farbtransformation.<br /> Das heißt, zu jeder Farbe der Figur werden die
	 * RGB-Werte um feste Betraege geaendert (positive oder negative). Sprengt ein entstehender Wert
	 * den Rahmen [0; 255] (zum Beispiel 160 + 100 oder 50 - 80), so bleibt der Farbwert am Rand des
	 * moeglichen Bereiches (also 0 bzw. 255).
	 *
	 * @param r
	 * 		Der Rot-Aenderungswert (positiv und negativ moeglich)
	 * @param g
	 * 		Der Gruen-Aenderungswert (positiv und negativ moeglich)
	 * @param b
	 * 		Der Blau-Aenderungswert (positiv und negativ moeglich)
	 *
	 * @see #heller()
	 * @see #dunkler()
	 * @see #negativ()
	 */
	public void farbenTransformieren (int r, int g, int b) {
		for (int i = 0; i < animation.length; i++) {
			animation[i].transformieren(r, g, b);
		}
	}

	/**
	 * Faerbt alle Elemente in einer Farbe ein.<br /> Dieser Zustand laesst sich zuruecksetzen mit
	 * der Methode <code>zurueckFaerben()</code>.
	 *
	 * @param farbe
	 * 		Die Farbe, mit der alle farbenthaltenden Unterquadrate der Figur eingefaerbt werden.<br />
	 * 		Eingabe als <code>String</code>, so wie bei den anderen einfachen Farbeingaben auch.
	 *
	 * @see #zurueckFaerben()
	 * @see #einfaerben(Farbe)
	 */
	public void einfaerben (String farbe) {
		einfaerben(Farbe.vonString(farbe));
	}

	/**
	 * Faerbt alle Elemente in einer Farbe ein.<br /> Dieser Zustand laesst sich zuruecksetzen mit
	 * der Methode <code>zurueckFaerben()</code>.
	 *
	 * @param f
	 * 		Die Farbe, mit der alle farbenthaltenden Unterquadrate der Figur eingefaerbt werden.
	 *
	 * @see #zurueckFaerben()
	 * @see #einfaerben(String)
	 */
	public void einfaerben (Farbe f) {
		for (int i = 0; i < animation.length; i++) {
			animation[i].einfaerben(f.wert());
		}
	}

	/**
	 * Setzt, ob diese Figur bei der Darstellung waagrecht zentral gespiegelt werden soll oder
	 * nicht.<br /> Dies aendert die Drehungsrichtung einer Figur von N nach S bzw. umgekehrt.
	 *
	 * @param spiegel
	 * 		Ist dieser Wert <code>true</code>, so wird die Figur waagrecht gespiegelt im Vergleich zu
	 * 		ihrer Quelldatei dargestellt. Durch <code>false</code> kann dieser Zustand schnell wieder
	 * 		zurueckgesetzt werden.
	 *
	 * @see #spiegelYSetzen(boolean)
	 * @see #yGespiegelt()
	 * @see #xGespiegelt()
	 */
	public void spiegelXSetzen (boolean spiegel) {
		this.spiegelX = spiegel;
	}

	/**
	 * Setzt, ob diese Figur bei der Darstellung senkrecht zentral gespiegelt werden soll oder
	 * nicht.<br /> So laesst sich extrem schnell z.B. Drehung einer Spielfigur von links nach
	 * rechts im Spiel realisieren.
	 *
	 * @param spiegel
	 * 		Ist dieser Wert <code>true</code>, so wird die Figur senkrecht gespiegelt im Vergleich zu
	 * 		ihrer Quelldatei dargestellt. Durch <code>false</code> kann dieser Zustand schnell wieder
	 * 		zurueckgesetzt werden.
	 *
	 * @see #spiegelXSetzen(boolean)
	 * @see #yGespiegelt()
	 * @see #xGespiegelt()
	 */
	public void spiegelYSetzen (boolean spiegel) {
		this.spiegelY = spiegel;
	}

	/**
	 * Diese Methode gibt aus, ob diese Figur derzeit an der X-Achse (waagrecht) gespiegelt ist.
	 *
	 * @return Ist dieser Wert <code>true</code>, wird diese Figur derzeit genau an der X-Achse
	 * gespiegelt dargestellt, im Verhältnis zu der ursprünglichen Figurdatei.
	 *
	 * @see #spiegelXSetzen(boolean)
	 * @see #spiegelYSetzen(boolean)
	 * @see #yGespiegelt()
	 */
	public boolean xGespiegelt () {
		return spiegelX;
	}

	/**
	 * Diese Methode gibt aus, ob diese Figur derzeit an der Y-Achse (senkrecht) gespiegelt ist.
	 *
	 * @return Ist dieser Wert <code>true</code>, wird diese Figur derzeit genau an der Y-Achse
	 * gespiegelt dargestellt, im Verhältnis zu der ursprünglichen Figurdatei.
	 *
	 * @see #spiegelXSetzen(boolean)
	 * @see #spiegelYSetzen(boolean)
	 * @see #xGespiegelt()
	 */
	public boolean yGespiegelt () {
		return spiegelY;
	}

	/**
	 * Sorgt dafuer, dass nach dem Aufruf von <code>einfaerben(Farbe)</code> die Figur wieder ihre
	 * normalen Farbgegebenheiten kriegt.
	 *
	 * @see #einfaerben(Farbe)
	 * @see #einfaerben(String)
	 */
	public void zurueckFaerben () {
		for (int i = 0; i < animation.length; i++) {
			animation[i].zurueckFaerben();
		}
	}

	/**
	 * Diese Methode wird verwendet, um die Figur vom direkten Animationssystem zu loesen. Sie ist
	 * <i>package private</i>, da diese Einstellung nur intern vorgenommen werden soll.
	 */
	void entfernen () {
		liste.remove(this);
	}

	/**
	 * Zeichnet das Objekt.
	 *
	 * @param g
	 * 		Das zeichnende Graphics-Objekt
	 * @param r
	 * 		Das BoundingRechteck, dass die Kameraperspektive Repraesentiert.<br /> Hierbei soll
	 * 		zunaechst getestet werden, ob das Objekt innerhalb der Kamera liegt, und erst dann
	 * 		gezeichnet werden.
	 */
	@Override
	public void zeichnen (Graphics2D g, BoundingRechteck r) {
		if (r.schneidetBasic(this.dimension())) {
			super.beforeRender(g, r);
			animation[aktuelle].zeichnen(g, (int) (position.x - r.x), (int) (position.y - r.y), spiegelX, spiegelY);
			super.afterRender(g, r);
		}
	}

	/**
	 * @return Ein BoundingRechteck mit minimal nötigem Umfang, um das Objekt <b>voll
	 * einzuschließen</b>.
	 */
	@Override
	public BoundingRechteck dimension () {
		if (animation != null && animation[aktuelle] != null) {
			// FIXME: animation[aktuelle] == null => else => Nullpointer
			return new BoundingRechteck(position.x, position.y, animation[0].breite(), animation[0].hoehe());
		} else {
			return new BoundingRechteck(position.x, position.y, animation[aktuelle].breite(), animation[aktuelle].hoehe());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collider erzeugeCollider () {
		ColliderGroup cg = new ColliderGroup();

		for (BoundingRechteck r : flaechen()) {
			cg.addCollider(BoxCollider.fromBoundingRechteck(new Vektor(r.x - position.x, r.y - position.y), r));
		}

		return cg;

		// TODO: Entscheiden, ob genauere Collisionsbehandlung es wirklich wert ist.
		// return new BoxCollider(new Vektor(dimension().breite, dimension().hoehe));
	}

	@Override
	public BoundingRechteck[] flaechen () {
		return animation[aktuelle].flaechen(position.x, position.y);
	}

	/**
	 * Gibt den Index des aktuellen Bildes zurueck.<br /> Die Figur, die im Pixelfeldeditor erstellt
	 * wurde besteht den Bildern (1, 2, ..., n), aber <b>ACHTUNG</b>, die Indizes fangen bei
	 * <b>0</b> an und hoeren dann eins frueher auf (0, 1, ..., (n-1)). Das heisst, dass wenn als
	 * Index <code>5</code> zurueckgegeben wird, wird zur Zeit das Bild gezeigt, das im
	 * Figureneditor als <code>Bild 6</code> bezeichnet wurde.
	 *
	 * @return Der Index des aktuell angezeigten Bildes
	 */
	public int aktuellesBild () {
		return aktuelle;
	}

	/**
	 * @return Alle PixelFelder der Animation.
	 */
	public PixelFeld[] animation () {
		return animation;
	}

	/**
	 * Gibt das Intervall dieser Figur zurueck.
	 *
	 * @return Das Intervall dieser Figur. Dies ist die Zeit in Millisekunden, die ein
	 * Animationsbild zu sehen bleibt
	 *
	 * @see #animationsGeschwindigkeitSetzen(int)
	 */
	public int intervall () {
		return intervall;
	}

	/**
	 * Setzt die Geschwindigkeit der Animation, die diese Figur Figuren steuert.<br /> Jed groesser
	 * Die Zahl ist, desto langsamer laeuft die Animation, da der Eingabeparamter die Wartezeit
	 * zwischen der Schaltung der Animationsbilder in Millisekunden angibt!
	 *
	 * @param intervall
	 * 		Die Wartezeit in Millisekunden zwischen den Bildaufrufen dieser Figur.
	 */
	public void animationsGeschwindigkeitSetzen (int intervall) {
		this.intervall = intervall;
	}
}
