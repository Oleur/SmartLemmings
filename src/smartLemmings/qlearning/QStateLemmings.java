/**
 * Author Clément Desoche
 * This class represent a given Lemmings state in the simulation
 * 
 *  
 */

package smartLemmings.qlearning;

import java.util.ArrayList;

import smartLemmings.agent.LemmingBody.Direction;
import smartLemmings.environment.Perception;

public class QStateLemmings implements QState {
	
	/**
	 * 
	 */
	private ArrayList<Perception> perceptions;
	private Direction d;
	private Direction dExit;
	private int numState;
	/*
	 * CONSTRUCTOR
	 */
	
	public QStateLemmings(){
		perceptions = new ArrayList<Perception>();
	}
	


	public QStateLemmings(ArrayList<Perception> p, int n, Direction d, Direction dExit){
		this.perceptions = p;
		this.numState = n;
		this.d =d;
		this.dExit=dExit;
	}
	/*
	 * This function compare the calling state with the perceptions in parameter
	 * return true if the perceptions are the same, false otherwise 
	 */
	public boolean isDifferentState(ArrayList<Perception> perception, Direction d, Direction dExit){
		int i =0;
		if(this.d == d &&  this.dExit == dExit){
			while( i<perception.size() && perception.get(i).isEqual(perceptions.get(i))){
				i++;
			}
			if(i<perception.size()){
				return true;
			} else {
				return  false;
			}
		} else {
			return  false;
		}
		
	}
	
	/*
	 * GETTER/SETTER
	 */
	public ArrayList<Perception> getPerceptions(){
		return perceptions;
	}
	
	public int getNumState() {
		return numState;
	}

	public void setNumState(int numState) {
		this.numState = numState;
	}

	public void setPerceptions(ArrayList<Perception> perceptions) {
		this.perceptions = perceptions;
	}
	public Direction getD() {
		return d;
	}

	public void setD(Direction d) {
		this.d = d;
	}



	public Direction getdExit() {
		return dExit;
	}



	public void setdExit(Direction dExit) {
		this.dExit = dExit;
	}
/*
 * OVERRIDE FUNCTION INHERITED FROM THE INTERFACE
 */
	@Override
	public int toInt() {
		return numState;
	}

	@Override
	public int compareTo(QComparable o) {
		return QComparator.QCOMPARATOR.compare(this, o);
	}

	
}
