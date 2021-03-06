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

package ea.internal.phy;

import ea.StehReagierbar;

/**
 * Die nichts-tuende Dummy-Standartklasse, die fuer einen Gravitator der nichtstuende
 * Initial-StehReagierbar-Listener ist. Wird nur intern verwendet.
 *
 * @author Michael Andonie
 * @see ea.StehReagierbar
 */
public class StehDummy implements StehReagierbar {

	/**
	 * <i>Singleton</i>-Referenz auf die eine Instanz.
	 */
	private static StehDummy instance = null;

	/**
	 * <i>Singleton</i>-Getter-Methode für den Dummy.
	 *
	 * @return Die eine existente Instanz des <code>StehDummy</code>-Objekts.
	 */
	public static StehReagierbar getDummy () {
		return instance == null ? instance = new StehDummy() : instance;
	}

	/**
	 * In der Verarbeitung des stehens passiert <b>nichts</b>.
	 */
	@Override
	public void stehReagieren () {
		//
	}
}
