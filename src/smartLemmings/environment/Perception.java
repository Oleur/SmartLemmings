package smartLemmings.environment;

import java.awt.Point;
import java.io.Serializable;

/**
 * The lemming's perception which will help the lemming moving in the world.
 */
public class Perception implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private boolean isLemming;	
	private PerceptionType type;
	private Point exitPosition;

	/**
	 * a type based on enum type
	 * is Equal return true if the calling perception and the perception in pararmeter as the same id
	 * When building the Perception list the perception are storred the following order:
	 * 0 1 2
	 * 7 L 3
	 * 6 5 4 */
	public static enum PerceptionType{
		UNBREAKABLE,
		BREAKABLE,
		EMPTY,
		IN,
		OUT,
		VOID
	}
	
	/**
	 * Constructor thanks tot the folling parameters:
	 * @param isLem Boolean if there is a lemming.
	 * @param bType The block type.
	 * @param exitPos The exit position.
	 */
	public Perception(boolean isLem, PerceptionType bType, Point exitPos) {
		this.isLemming = isLem;
		this.type = bType;
		this.setExitPosition(exitPos);
	}
		
	/**
	 * Get the block type.
	 * @return The block type.
	 */
	public PerceptionType getType(){
		return this.type;
	}
	
	/**
	 * Check if a perception is equal to another.
	 * @param perception A perception.
	 * @return Return true if the perceptions are the same, false else.
	 */
	 public boolean isEqual(Perception perception) {
		if (this.isLemming == perception.isLemming && this.type == perception.type) {
			return true;
		}
		return false;
	 }

	 /**
	  * Chech if there is a lemming in the perceived block.
	  * @return True if there is a lemming, false else.
	  */
	 public boolean isLemming() {
		return this.isLemming;
	 }

	 /**
	  * Get the exit position.
	  * @return The exit position.
	  */
	 public Point getExitPosition() {
		return exitPosition;
	 }

	 /**
	  * Set the position of the exit.
	  * @param exitPosition
	  */
	 public void setExitPosition(Point exitPosition) {
		this.exitPosition = exitPosition;
	 }

}
