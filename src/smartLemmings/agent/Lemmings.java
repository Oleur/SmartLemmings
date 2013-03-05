package smartLemmings.agent;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Random;

import smartLemmings.graph.Graph;
import smartLemmings.graph.GraphArc;
import smartLemmings.graph.GraphNode;
import smartLemmings.qlearning.QComparable;
import smartLemmings.qlearning.QFeedback;
import smartLemmings.qlearning.QLearning;
import smartLemmings.qlearning.QProblem;

import smartLemmings.qlearning.QStateLemmings;
import smartLemmings.qlearning.QAction;
import smartLemmings.agent.LemmingBody.Direction;
import smartLemmings.agent.LemmingsAction.LemmingsActionType;
import smartLemmings.environment.Perception;
import smartLemmings.environment.Perception.PerceptionType;
public class Lemmings {
	
	QLearning<QStateLemmings,LemmingsAction> learning;
	private Problem qProblem;	
	private LemmingBody body;
	private int nbDeath=0;
	
	private final Random random = new Random();
	
	public Lemmings(boolean load,String path, LemmingBody body) {
		this.body = body;
		
		if(load){
			Graph graph = new Graph(path, true);
			qProblem = new Problem(graph.getNodes());
			learning = new QLearning<QStateLemmings,LemmingsAction>(qProblem,graph.getNodes());
		} else {
			qProblem = new Problem();
			learning = new QLearning<QStateLemmings,LemmingsAction>(qProblem);
		}
	}
	
	public void live(){
		ArrayList<Perception> perceptions = (ArrayList<Perception>) this.getPerception();
		Direction dExit = findExitDirection(perceptions.get(0).getExitPosition());
		if(qProblem.newStateEncountered(perceptions,body.getDirection(),dExit)){
				learning.addNewState(qProblem.getCurrentState());
		}
		QFeedback<QStateLemmings> result = qProblem.takedAction();
		
		if(qProblem.getActionTaken().getType() != null){
			learning.learn(qProblem.getPreviousState(), qProblem.getActionTaken(), result);
		}
		LemmingsAction action = learning.chooseAction(random, qProblem.getCurrentState());
		qProblem.setActionTaken(action);
		body.influence(action);
		qProblem.iterate();
	}
	
	private Direction findExitDirection(Point exitPosition) {
		if(exitPosition.x == body.getPosX()){
			
		}
		if(exitPosition.x > body.getPosX()){
			if(exitPosition.y > body.getPosY()){
				return Direction.SOUTHEAST;
			} else if(exitPosition.y < body.getPosY()){
				return Direction.NORTHEAST;
			} else {
				return Direction.EAST;
			}
		} else if(exitPosition.x < body.getPosX()){
			if(exitPosition.y > body.getPosY()){
				return Direction.SOUTHWEST;
			} else if(exitPosition.y < body.getPosY()) {
				return Direction.NORTHWEST;
			} else {
				return Direction.WEST;
			}
		} else {
			if(exitPosition.y > body.getPosY()){
				return Direction.SOUTH;
			} else {
				return Direction.NORTH;
			}
		}
	}

	public LemmingBody getBody() {
		return body;
	}

	public void setBody(LemmingBody body) {
		this.body = body;
	}

	
	public void killedByDeath(){
		QFeedback<QStateLemmings> result = new QFeedback<QStateLemmings>(qProblem.getPreviousState(), -10);
		learning.learn(qProblem.getPreviousState(), qProblem.getActionTaken(), result);
		nbDeath++;
		System.out.println("death counter "+nbDeath);
	}
	
	public void killedByEnd(String path){
		saveAsGraph(path);
		System.out.println("Death: "+nbDeath);
	}
	
	public List<Perception> getPerception(){
		return body.getEnvironment().perceive(body);
	}
	
	public void saveAsGraph(String path){
		Graph grap= new Graph(path, false);
		Map<QStateLemmings, Map<LemmingsAction,Float>> map = learning.getQMap();
		ArrayList<GraphNode> nodes = grap.getNodes();
		for (QStateLemmings s : map.keySet()) {
			nodes.add(new GraphNode(s.getPerceptions(), s.getD(),s.getdExit()));
			Map<LemmingsAction,Float> m = map.get(s);
			ArrayList<GraphArc> arcs = nodes.get(nodes.size()-1).getActions();
			for (LemmingsAction a : m.keySet()) {
				arcs.add(new GraphArc(m.get(a),a.toString()));
			}
		}
		try {
			grap.saveGraph();
		} catch (IOException e) {
			
			System.out.println(e.getMessage());
		}
	}
	
	public void initializedFromGraph(Graph graph){
		
	}
}
class Problem implements QProblem<QStateLemmings, LemmingsAction>{
	private ArrayList<QStateLemmings> states;
	private int currentState;
	private int previousState;
	private LemmingsAction actionTaken;
	private int iteration=0;

	public Problem(){
		super();
		states = new ArrayList<QStateLemmings>();
		currentState=0;
		previousState=0;
		actionTaken= new LemmingsAction();
	}
	public void setActionTaken(LemmingsAction action) {
		actionTaken = action;
		
	}
	public Problem(ArrayList<GraphNode> nodes) {
		// TODO Auto-generated constructor stub
		states = new ArrayList<QStateLemmings>();
		for (GraphNode n : nodes) {
			states.add(new QStateLemmings(n.getPerceptions(),states.size(),n.getLemmingDirection(),n.getExitDirection()));
		}
		currentState=0;
		previousState=0;
		actionTaken= new LemmingsAction(); 
	}


	public LemmingsAction getActionTaken(){
		return actionTaken;
	}
	
	
	@Override
	public float getAlpha() {
		// TODO Auto-generated method stub
		// balance the between old Q-value (1-alpha) and  new Q-Value (alpha)
		if(iteration<1000){
			return 0.4f;
		} else if(iteration<2000){
			return 0.2f;
		} else {
			return 0.1f;
		}
	}

	@Override
	public float getGamma() {
		// TODO Auto-generated method stub
		//balanced the contribution of the following state
		if(iteration<1000){
			return 0.9f;
		} else if(iteration<2000){
			return 0.2f;
		} else {
			return 0.1f;
		}
		
	}

	@Override
	public float getRho() {
		// TODO Auto-generated method stub
		//rate to choose random action (rand< rho) or best action (rand>rho)
		if(iteration<1000){
			return 0.7f;
		} else if(iteration<2000){
			return 0.4f;
		} else {
			return 0.1f;
		}
		
	}

	@Override
	public QStateLemmings getCurrentState() {
		// TODO Auto-generated method stub
		return states.get(currentState);
	}
	
	public QStateLemmings getPreviousState() {
		// TODO Auto-generated method stub
		return states.get(previousState);
	}

	@Override
	public QStateLemmings getRandomState() {
		// TODO Auto-generated method stub
		Random r=new Random();
		return states.get(r.nextInt(states.size()));
	}

	@Override
	public List<QStateLemmings> getAvailableStates() {
		// TODO Auto-generated method stub
		return states;
	}

	@Override
	public List<LemmingsAction> getAvailableActionsFor(QStateLemmings state) {
		// TODO Auto-generated method stub
		List<LemmingsAction> actions = new ArrayList<LemmingsAction>();
		for(LemmingsActionType type : LemmingsActionType.values()) {
			actions.add(new LemmingsAction(type));
		}
		return actions;
	}
	
	public void iterate(){
		iteration++;
	}
	
	public boolean newStateEncountered(ArrayList<Perception> perception,Direction d,Direction dExit){
		boolean isNewState=true;
		if(states.size() == 0){
			 states.add(new QStateLemmings(perception,states.size(), d,dExit));
			 currentState=0;
		} else {
			 int i=0;
			 while(i<states.size() && states.get(i).isDifferentState(perception, d,dExit)){
				 i++;
			 }
			 if(i<states.size()){
				previousState=currentState;
				currentState=i; 
				isNewState = false;
			 } else {
				 states.add(new QStateLemmings(perception,states.size(),d,dExit));
				 previousState=currentState;
				 currentState=i;
			 }
		}
		
		return isNewState;
		
	 }

	public QFeedback<QStateLemmings> takedAction() {
		QStateLemmings state = states.get(previousState);
		System.out.println("state n°"+state.getNumState());
		
		LemmingsAction action = actionTaken;
		ArrayList<Perception> perception = state.getPerceptions();
		for (Perception p : perception) {
			System.out.println("STATE PERCEP: "+p.getType());
		}
		System.out.println("Direction lemmings: "+ state.getD()+"Exit direction : "+state.getdExit());
		System.out.println("action taked: "+actionTaken.getType());
		Direction exit = state.getdExit();
		Direction direction = state.getD();
		
		float result= 0.0f;
		if(action.getType() == null){
			return new QFeedback<QStateLemmings>(state,result);
		}
		
		for (int p = 0; p < 8; p++) {
			System.out.println("PercepREWARD: "+perception.get(p).getType());
		}
		
		switch(action.getType()){
		case DIG:
			if(perception.get(5).getType() == PerceptionType.BREAKABLE){
				/* It's a good idea to dig if the block below the lemming is breakable*/
				if(exit == Direction.SOUTH){
					/* It's better if the exit is down*/
					result+=60.0f;
				} else if(exit == Direction.SOUTHEAST || exit == Direction.SOUTHWEST){
					/* If the exit is up or in the same floor it's not a good idea*/
					result +=30.0f;
				} else {
					result = 60.0f;
				}
			} else {
				/* But if the block is not breakable it's not a good idea*/
				result = -100.0f;

			}
			
			break;
		case EXCAVATE:
			if(direction == Direction.EAST){
				if(perception.get(3).getType() == PerceptionType.BREAKABLE || perception.get(3).getType() == PerceptionType.EMPTY){
					/* It's a good idea to excavate if the block behind or in front of the lemming is breakable*/
					if(direction == Direction.EAST){
						result+=100.0f;
					} else if(exit == Direction.NORTHEAST || exit == Direction.SOUTHEAST){
						result+=80.0f;
					} else {
						/*But excaving in the opposite direction of the exit is not a good idea*/
						result-=100.0f;
					}
				} else {
					/*Whatever if the blocks is not breakable it's not a good idea */
					result += -100.0f;
					if(perception.get(3).getType() == PerceptionType.VOID){
						/*And worst idea of all going out of bound */
						result+= -150.0f;
					}
				}
			} else if(direction == Direction.WEST){
				if(perception.get(7).getType() == PerceptionType.BREAKABLE || perception.get(7).getType() == PerceptionType.EMPTY){
					/* It's a good idea to excavate if the block behind or in front of the lemming is breakable*/
					result += 5.0f;
					if(exit == Direction.WEST){
						/*It far better idea if the lemming excavate in direction of the exit*/
						result+=60.0f;
					} else if(exit == Direction.NORTHWEST || exit == Direction.SOUTHWEST){
						result+=30.0f;
					}
						else {
					}
						/*But excaving in the opposite direction of the exit is not a good idea*/
						result-=60.0f;
					}
				} else {
					/*Whatever if the blocks is not breakable it's not a good idea */
					result += -100.0f;
					if(perception.get(7).getType() == PerceptionType.VOID){
						/*And worst idea of all going out of bound */
						result+= -150.0f;
					}
				}
			
			break;
		case FILL:
			if(direction == Direction.EAST){
				if(perception.get(3).getType() == PerceptionType.EMPTY 
					&& perception.get(4).getType() != PerceptionType.EMPTY){
					/*in this case we will fill the case in front of the lemming*/
					if(exit == Direction.NORTH || exit == Direction.NORTHEAST){
						/*It's a good idea if the exit is up, in this way we can jump on it*/
						result += 30.0f;
					} else {
						/*else bad idea*/
						result += -60.0f;
					}
				} else if(perception.get(3).getType() == PerceptionType.EMPTY && perception.get(4).getType() == PerceptionType.EMPTY) {
					
					if(exit == Direction.EAST){
						/*It's a good idea if the exit is in the same floor*/
						result += 40.0f;
					} else {
						/*else bad idea*/
						result += -60.0f;
					}
				} else {
					/*else if we are not in a good configuration bad idea*/
					result -= 100.0f;
				}
			} else {
				if(perception.get(7).getType() == PerceptionType.EMPTY 
						&& perception.get(6).getType() != PerceptionType.EMPTY){
					/*in this case we will fill the case in front of the lemming*/
							if(exit == Direction.NORTH || exit == Direction.NORTHWEST){
								result += 30.0f;
								/*It's a good idea if the exit is up, in this way we can jump on it*/
							} else {
								/*else bad idea*/
								result += -60.0f;
							}
						
					} else if(perception.get(7).getType() == PerceptionType.EMPTY && perception.get(6).getType() == PerceptionType.EMPTY) {
						if(exit == Direction.WEST){
							/*It's a good idea if the exit is up or in the same floor*/
							result += 40.0f;
						} else {
							/*else bad idea*/
							result += -60.0f;
						}
					} else {
						/*else if we are not in a good configuration bad idea*/
						result -= 100.0f;
					}
			}
			break;
		case JUMP:
			if(direction == Direction.EAST){
				if(perception.get(1).getType() == PerceptionType.EMPTY 
					&& (perception.get(2).getType() == PerceptionType.EMPTY && (perception.get(3).getType() != PerceptionType.EMPTY))){
					/*if the lemming have a block that he can jump, on it's a good idea*/
					if(exit == Direction.NORTH || exit == Direction.NORTHEAST){
						/*better idea if the exit is on a upper floor*/
						result+=80.0f;

					} else {
						/*else bad idea*/
						result-=60.0f;

					}
					
				} else if(perception.get(2).getType() == PerceptionType.VOID){
					result-=150.0f;
				} else if(perception.get(1).getType() == PerceptionType.EMPTY && perception.get(2).getType() == PerceptionType.OUT){
					result+=300.0f;
				} else{
					result += -80.0f;
				}
				
			} else if(direction == Direction.WEST) {
				if(perception.get(1).getType() == PerceptionType.EMPTY 
						&& (perception.get(0).getType() == PerceptionType.EMPTY && (perception.get(7).getType() != PerceptionType.EMPTY))){
					/*if the lemming have a block that he can jump, on it's a good idea*/
				if(exit != Direction.WEST || exit != Direction.SOUTHWEST || exit != Direction.SOUTH){
					/*better idea if the exit is on a upper floor*/
					result+=60.0f;
				} else {
							/*else bad idea*/
					result-=80.0f;
				}
					} else if(perception.get(0).getType() == PerceptionType.VOID){
						result-=150.0f;
					} else if(perception.get(0).getType() == PerceptionType.EMPTY && perception.get(2).getType() == PerceptionType.OUT){
						result+=300.0f;
					} else {
						result -= 80.0f;
					}
			}
			if(perception.get(7).getType() != PerceptionType.BREAKABLE || perception.get(7).getType() != PerceptionType.UNBREAKABLE  && perception.get(3).getType() != PerceptionType.BREAKABLE || perception.get(3).getType() != PerceptionType.UNBREAKABLE){
				result+=9.0f;
			}
			
			
			break;
		case PARACHUTE:
			if(perception.get(5).getType() == PerceptionType.EMPTY){
				/*it's a good idea to deploy parachute the lemming is falling*/
				result += 50.0f;

			} else {
				/*else bad idea*/
				result -= 100.0f;
			}
			break;
		case WALK:
			if(direction == Direction.EAST){
					if(perception.get(3).getType() == PerceptionType.EMPTY ){
						/*if the block in front of the lemming is empty*/
						if(perception.get(4).getType() == PerceptionType.UNBREAKABLE || perception.get(4).getType() == PerceptionType.BREAKABLE){
							/*and there is a floor to walk*/
							if(exit == Direction.EAST){
								result += 80.0f;
							} else {
								result -= 150.0f;
							}
						}  else {
							/*if there is no floor*/
							if(exit == Direction.SOUTHEAST || exit == Direction.SOUTH){
								/*if exit in a lower floor: good idea*/
								result += 40.0f;
							} else {
								/*else bad idea*/
								result -= 100.0f;
							}
						}
								
					} else if((perception.get(3).getType() == PerceptionType.UNBREAKABLE || perception.get(3).getType() == PerceptionType.BREAKABLE) && perception.get(7).getType() == PerceptionType.EMPTY){
						/*if the lemmings in front of a block */
						if(exit == Direction.WEST){
							/*if the lemming walk in the wrong direction good idea because this will change the direction*/
							result += 60.0f;

						} else {
							/*else bad idea*/
							result -= 100.0f;

						}
					} else if(perception.get(3).getType() == PerceptionType.VOID){
						result -= 150.0f;
					} else if(perception.get(3).getType() == PerceptionType.OUT){
						result += 300.0;
					}
					
					
			} else if(direction == Direction.WEST){
				if(perception.get(7).getType() == PerceptionType.EMPTY){
					if(perception.get(6).getType() != PerceptionType.EMPTY){
						/*and there is a floor to walk*/
						if(exit == Direction.WEST){
							result += 80.0f;
						} else {
							result -= 150.0f;
						}
					}  else {
						/*if there is no floor*/
						if(exit == Direction.SOUTHWEST || exit == Direction.SOUTH){
							/*if exit in a lower floor: good idea*/
							result += 40.0f;
						} else {
							/*else bad idea*/
							result -= 100.0f;
						}
					}
					
				} else if((perception.get(7).getType() == PerceptionType.UNBREAKABLE || perception.get(7).getType() == PerceptionType.BREAKABLE) && perception.get(3).getType() == PerceptionType.EMPTY){
					/*if the lemmings in front of a block */
					if(exit == Direction.EAST){
						/*if the lemming walk in the wrong direction good idea because this will change the direction*/
						result += 60.0f;

					} else {
						/*else bad idea*/
						result -= 100.0f;

					}
				} else if(perception.get(7).getType() == PerceptionType.VOID){
					result -= 150.0f;
				} else if(perception.get(7).getType() == PerceptionType.OUT){
					result += 300.0;
				}
				
			}
			
			break;
		}
		System.out.println("reward: "+result);
		
		return new QFeedback<QStateLemmings>(state,result);
	}
	
	
	
}

class LemmingsAction implements QAction{

	private LemmingsActionType type;
	
	public LemmingsAction(LemmingsActionType lType) {
		this.type = lType;
	}
	
	public LemmingsAction() {
		
	}
	
	@Override
	public int toInt() {
		// TODO Auto-generated method stub
		
		return type.ordinal();
	}

	@Override
	public int compareTo(QComparable o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public LemmingsActionType getType() {
		return type;
	}

	public void setType(LemmingsActionType type) {
		this.type = type;
	}

	public static enum LemmingsActionType {
		WALK,
		DIG,
		EXCAVATE,
		JUMP,
		PARACHUTE,
		FILL;
		
	}
}
