package smartLemmings.graph;

import java.io.Serializable;
import java.util.ArrayList;

import smartLemmings.agent.LemmingBody.Direction;
import smartLemmings.environment.Perception;



public class GraphNode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Perception> perceptions;
	private Direction d;
	private Direction dExit;
	private ArrayList<GraphArc> actions;
	
	public GraphNode(ArrayList<Perception> p, Direction d, Direction dExit){
		perceptions = p;
		this.d = d;
		this.dExit = dExit;
		actions = new ArrayList<GraphArc>();
	}
	
	public ArrayList<Perception> getPerceptions() {
		return perceptions;
	}
	
	public ArrayList<GraphArc> getActions() {
		return actions;
	}

	public Direction getLemmingDirection() {
		
		return this.d;
	}

	public Direction getExitDirection() {
		return this.dExit;
	}

}
