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

package ea.internal.collision;

import ea.Farbe;
import ea.Knoten;
import ea.Punkt;
import ea.Raum;

import java.util.ArrayList;
import java.util.List;

/**
 * Eine Aggregation von Collidern.
 *
 * @author Michael Andonie
 */
public class ColliderGroup extends Collider {

	/**
	 * Die Liste der Collider, die zu dieser Collider-Group gehören.
	 */
	private List<Collider> colliders = new ArrayList<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verursachtCollision (Punkt positionThis, Punkt positionOther, Collider collider) {
		Punkt positionMitOffset = positionThis.verschobenerPunkt(offset);
		for (Collider c : colliders) {
			if (c.verursachtCollision(positionMitOffset, positionOther, collider)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc} Gibt <code>true</code> zurück.
	 */
	@Override
	public boolean istNullCollider () {
		return false;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Raum visualize(Punkt p, Farbe color) {
        Punkt positionMitOffset = p.verschobenerPunkt(offset);
        Knoten ret = new Knoten();
        for(Collider c : colliders) {
            ret.add(c.visualize(positionMitOffset, color));
        }
        return ret;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public Collider clone () {
		ColliderGroup group = new ColliderGroup();
		for (Collider c : colliders) {
			group.addCollider(c.clone());
		}
		return group;
	}

	/**
	 * Fügt einen neuen Collider zu dieser Group hinzu.
	 *
	 * @param c
	 * 		Der hinzuzufügende Collider.
	 */
	public void addCollider (Collider c) {
		colliders.add(c);
	}
}
