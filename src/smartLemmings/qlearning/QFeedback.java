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

import smartLemmings.qlearning.QState;

/**
 * Feedback for the QLearning.
 * 
 * @param <S> is the type of the states
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class QFeedback<S extends QState> {

	/** Score given to the action by the feedback algorithm.
	 */
	public final float score;
	
	/** Arriving state after the execution of the action.
	 */
	public final S state;
	
	/**
	 * @param newState
	 * @param score
	 */
	public QFeedback(S state, float score) {
		this.state = state;
		this.score = score;
	}

}