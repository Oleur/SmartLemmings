/**
 * Author Clément Desoche
 */

package smartLemmings.graph;

import java.io.Serializable;

public class GraphArc implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float cost;
	private String action;

	public GraphArc(){
		cost=0;
		action="Empty";
	
		
	}
	
	public GraphArc(float c, String a){
		cost=c;
		action=a;
		
	}
	
	
	public void updateCost(float newCost){
		cost = newCost;
	}
	
	public float getCost(){
		return cost;
	}
	
	public String getAction(){
		return action;
	}
	
	public void setCost(float c){
		cost = c;
	}
	
	public void setAction(String a){
		action= a;
	}
	
	
}
