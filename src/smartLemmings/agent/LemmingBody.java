package smartLemmings.agent;

import smartLemmings.environment.Environment;

/**
 * Lemmings body which will move in the environment
 *
 */
public class LemmingBody {
	
	private Environment environment;
	private boolean isAlive;
	private boolean parachuteIsOpen;
	private int posX;
	private int posY;
	private int nbCaseFall;
	private Direction direction;
	
	/**
	 * Constructor of the lemming's body.
	 * @param ev
	 * @param alive
	 * @param parachute
	 * @param posx
	 * @param posy
	 * @param dir
	 */
	public LemmingBody(Environment ev, boolean alive, boolean parachute, int posx, int posy, Direction dir) {
		this.setEnvironment(ev);
		this.isAlive = alive;
		this.parachuteIsOpen = parachute;
		this.posX = posx;
		this.posY = posy;
		this.nbCaseFall = 0;
		this.setDirection(dir);
	}
	
	/**
	 * Allows us to know if the lemming is still alive.
	 * @return The lemming state.
	 */
	public boolean isAlive() {
		return this.isAlive;
	}	
	
	/**
	 * Set the lemming's state if he is alive or not. True for alive, 
	 * @param isAlive
	 */
	public void setAlive(boolean isAlive){
		this.isAlive = isAlive;
	}
	public boolean isParachuteIsOpen() {
		return this.parachuteIsOpen;
	}
	public void setParachuteIsOpen(boolean parachuteIsOpen) {
		this.parachuteIsOpen = parachuteIsOpen;
	}
	public int getPosY() {
		return posY;
	}
	public void setPosY(int posY) {
		this.posY = posY;
	}
	public int getPosX() {
		return posX;
	}
	public void setPosX(int posX) {
		this.posX = posX;
	}

	public void influence(LemmingsAction chooseAction) {
		System.out.println("ACTION TAKEN: "+chooseAction.getType() + ", DIRECTION: "+this.getDirection().toString());
		switch(chooseAction.getType()){
		case WALK :
			if(this.getDirection() == Direction.SOUTH)
				this.setDirection(Direction.EAST);
			environment.walk(this, direction);
			break;
		case PARACHUTE :
			environment.openParachute(this);
			break;
		case JUMP :
			if(this.getDirection() == Direction.SOUTH)
				this.setDirection(Direction.EAST);
			environment.jump(this, direction);
			break;
		case FILL : 
			environment.fill(this);
			break;
		case DIG :
			environment.dig(this, Direction.SOUTH);
			break;
		case EXCAVATE :
			if(this.getDirection() == Direction.SOUTH)
				this.setDirection(Direction.EAST);
			environment.dig(this, direction);
			break;
		}
	}
	
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public static enum Direction{
		NORTH,
		SOUTH,
		EAST,
		WEST,
		NORTHEAST,
		NORTHWEST,
		SOUTHEAST,
		SOUTHWEST
		
	}

	public void fall(int n) {
		nbCaseFall+=n;
	}
	
	public void checkDeath() {
		if(nbCaseFall>3 && !isParachuteIsOpen()){
			this.isAlive = false;
		}
		this.parachuteIsOpen = false;
		nbCaseFall=0;
	}

}
