package smartLemmings.qlearning;

/*
 * author Clément Desoche
 * adapted from QLearning by Stéfane Galland
 */


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;

import smartLemmings.graph.GraphArc;
import smartLemmings.graph.GraphNode;
import smartLemmings.qlearning.QAction;
import smartLemmings.qlearning.QComparator;
import smartLemmings.qlearning.QProblem;
import smartLemmings.qlearning.QState;

/*
 * 
 */

public class QLearning<S extends QState, A extends QAction> {

	private final QProblem<S,A> problem;
	private final Map<S,Map<A,Float>> qValues = new TreeMap<S,Map<A,Float>>(new QComparator()); 
	
	/*
	 * CONSTRUCTOR
	 */
	
	public QLearning(QProblem<S, A> problem){
		this.problem = problem;
		//System.out.println("pouet: "+problem.getAvailableStates().size());
		for(S s : problem.getAvailableStates()) {
			Map<A,Float> m = new TreeMap<A, Float>(new QComparator());
			this.qValues.put(s, m);
			for(A a : problem.getAvailableActionsFor(s)) {
				m.put(a, 0f);
			}
		}
	}
	
	/*
	 * This constructor built the Map from the graph structre, wich can be loaded from the outside. 
	 */
	public QLearning(QProblem<S, A> problem, ArrayList<GraphNode> nodes){
		this.problem = problem;
		int i=0;
		int j;
		for(S s : problem.getAvailableStates()) {
			ArrayList<GraphArc> arcs = nodes.get(i).getActions();
			Map<A,Float> m = new TreeMap<A, Float>(new QComparator());
			this.qValues.put(s, m);
			j=0;
			for(A a : problem.getAvailableActionsFor(s)) {
				m.put(a, arcs.get(j).getCost());
				++j;
			}
			++i;
		}
	}
	
	/*
	 * This function update the learning map when encountering new state in the simulation
	 */
	public void updateMapWithNewState(S newState){
		Map<A,Float> m = new TreeMap<A, Float>(new QComparator());
		this.qValues.put(newState, m);
		for(A a : problem.getAvailableActionsFor(newState)) {
			m.put(a, 0f);
		}
	}
	/*
	 * This action update the learning Map, from the lemming's previous state using the QLearning formula.
	 *  
	 */
	public void learn(S currentState, A actionTaken, QFeedback<S> result){
		
		Random r=new Random();
		float q=getQ(result.state, actionTaken);
		float maxQ= getQ(currentState, getBestAction(r, currentState));
		putQ(result.state, actionTaken, (1-problem.getAlpha())*q+(problem.getAlpha()*(result.score+problem.getGamma()*maxQ)));
	}
	
	/*
	 * This function replies the Lemming's future action.
	 * The best one are a Random one.
	 */
	
	public A chooseAction(Random r, S state){
		List<A> actions;
		A action;
		float cmp = r.nextFloat();
		if(cmp < problem.getRho()){
			actions = this.problem.getAvailableActionsFor(state);
			action = actions.get(r.nextInt(actions.size()));
		}
		else {
			action = getBestAction(r,state);
		}
		return action;
	}
	
	/*
	 * This function replies the best action are random action among the best
	 */
	
	public A getBestAction(Random random, S state) {
		//System.out.println(state.toInt());
		Map<A,Float> m = this.qValues.get(state);
		
		assert(m!=null);
		List<A> bestActions = new ArrayList<A>();
		float bestScore = Float.NEGATIVE_INFINITY;
		
		for(Entry<A,Float> entry : m.entrySet()) {
			if (entry.getValue()>bestScore) {
				bestActions.clear();
				bestActions.add(entry.getKey());
				bestScore = entry.getValue();
			}
			else if (entry.getValue()==bestScore) {
				bestActions.add(entry.getKey());
			}
		}
		assert(!bestActions.isEmpty());
		return bestActions.get(random.nextInt(bestActions.size()));
	}
	
	/*
	 * GETTER/SETTER
	 */
	
	private float getQ(S state, A action) {
		Map<A,Float> m = this.qValues.get(state);
		assert(m!=null);
		Float q = m.get(action);
		if (q==null) return 0f;
		return q.floatValue();
	}

	private void putQ(S state, A action, float q) {
		Map<A,Float> m = this.qValues.get(state);
		assert(m!=null);
		m.put(action, q);
	}
	
	public Map<S,Map<A,Float>> getQMap (){
		return qValues;
		
	}

	public void addNewState(S state) {
		Map<A,Float> m = new TreeMap<A, Float>(new QComparator());
		this.qValues.put(state, m);
		for(A a : problem.getAvailableActionsFor(state)) {
			m.put(a, 0f);
		}
		
	}
	
}
