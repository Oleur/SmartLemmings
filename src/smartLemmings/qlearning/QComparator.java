/* 
 * $Id$
 * 
 * Copyright (C) 2011 Stephane Galland <stephane.galland@utbm.fr>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package smartLemmings.qlearning;

import java.util.Comparator;

/**
 * Implementation of a comparator of QComparable.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class QComparator implements Comparator<QComparable> {

	/** Default singleton for this comparator.
	 */
	public static final QComparator QCOMPARATOR = new QComparator();
	
	/**
	 */
	public QComparator() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(QComparable o1, QComparable o2) {
		if (o1==o2) return 0;
		if (o1==null) return Integer.MIN_VALUE;
		if (o2==null) return Integer.MAX_VALUE;
		return o1.toInt() - o2.toInt();
	}

}