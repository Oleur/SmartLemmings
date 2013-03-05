package smartLemmings.environment;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import smartLemmings.agent.LemmingBody;
import smartLemmings.agent.LemmingBody.Direction;
import smartLemmings.environment.Perception.PerceptionType;

/**
 * Create a discrete environment where the lemmings will manage their actions in order to reach the exit.
 */
public class Environment {
	
	private final int width;
	private final int height;
	public EnvironmentObject[][] envGrid;
	private int[][] wLevel;
	private Point exitPosition;
	
	/**
	 * Constructor with the following parameters:
	 * @param w World's width.
	 * @param h Wolrd's height.
	 * @param wl Array of integer which represents the world's blocks.
	 */
	public Environment(int w, int h, int[][] wl) {
		this.height = h;
		this.width = w;
		this.wLevel = wl;
		this.envGrid = new EnvironmentObject[h][w];

		for (int a=0; a<18; a++) {
			for (int z=0; z<27; z++) {
				envGrid[a][z] = new EnvironmentObject(wl[a][z], false);
				if (wl[a][z] == 4) {
					exitPosition = new Point(z, a);
				}
			}
		}
	}
	
	/**
	 * Get the world's height.
	 * @return The world's height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the world's width.
	 * @return The world's width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the array of integer that represents the world's block.
	 * @return The array of integer that represents the world's block.
	 */
	public int[][] getLevelGrid() {
		return wLevel;
	}

	/**
	 * Get the array of environmentObject that represents the environment as a grid.
	 * @return The environment grid.
	 */
	public EnvironmentObject[][] getEnvGrid() {
		return envGrid;
	}
	
	/**
	 * Get the environment object thanks to a given position.
	 * @param x X position.
	 * @param y Y position.
	 * @return If it exists, return the corresponding environment object. 
	 */
	public EnvironmentObject getEnvironmentObject(int x, int y) {
		if (x>=0 && y>=0 && x<this.width && y<this.height)
			return this.envGrid[y][x];
		return null;
	}
	
	/**
	 * Set the body's presence to true thanks to a given position x, y.
	 * @param x X position.
	 * @param y Y position.
	 * @param body Lemming's body.
	 */
	public void addLemming(int x, int y, LemmingBody body) {
		body.setPosX(x);
		body.setPosY(y);
		this.envGrid[y][x].isLem = true;
	}
	
	/**
	 * Get the block type thanks to a given position x, y.
	 * @param x X position.
	 * @param y Y position.
	 * @return The corresponding perception type.
	 */
	public PerceptionType isBlockType(int x, int y) {
		if(x>=0 && y>=0 && x<this.width && y<this.height){
			if (this.envGrid[y][x].blockState == 0) {
				return PerceptionType.EMPTY;
			} else if (this.envGrid[y][x].blockState == 1) {
				return PerceptionType.BREAKABLE;
			} else if (this.envGrid[y][x].blockState == 2) {
				return PerceptionType.UNBREAKABLE;
			} else if (this.envGrid[y][x].blockState == 3) {
				return PerceptionType.IN;
			} else {
				return PerceptionType.OUT;
			}
		} else {
			return PerceptionType.VOID;
		}
		
			
	}
	
	/**
	 * Return if there is a lemming in the given position.
	 * @param x X position.
	 * @param y Y position.
	 * @return Return the corresponding lemming's state.
	 */
	private boolean isLemming(int x, int y) {
		if (x>=0 && y>=0 && x<this.width && y<this.height) {
			if (this.envGrid[y][x].isLem == true) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Get the position of the lemming in the environmnet.
	 * @param body
	 * @return
	 */
	private Point getLemmingPosition(LemmingBody body) {
		return new Point(body.getPosX(), body.getPosY());
	}
	
	/**
	 * Get the perceptions around the lemmings.
	 * @param lemming Active lemming in the current environment.
	 * @return All the perceptions.
	 */
	public List<Perception> perceive(LemmingBody lemming) {
		List<Perception> allPercepts = new ArrayList<Perception>();
		int lemX = lemming.getPosX();
		int lemY = lemming.getPosY();
		System.out.println("LEMMING_x: "+lemX+" ,LEMMING_y: "+lemY);
		Point[] frustum = {new Point(lemX-1, lemY-1), new Point(lemX, lemY-1), new Point(lemX+1, lemY-1),
				 new Point(lemX+1, lemY), new Point(lemX+1, lemY+1), new Point(lemX, lemY+1), new Point(lemX-1, lemY+1), new Point(lemX-1, lemY)};

		Perception perception;
		
		for (Point point : frustum) {
			perception = new Perception(this.isLemming(point.x, point.y), this.isBlockType(point.x, point.y), exitPosition);
			allPercepts.add(perception);
		}
		return allPercepts;
	}

	/**
	 * Dig the environment with repsect to the lemming and its direction.
	 * @param body Lemming's body.
	 * @param dir Lemming's direction.
	 */
	public void dig(LemmingBody body, Direction dir) {
		if(!applyGravity(body)){
			switch(dir){
			case SOUTH : 
				if(body.getPosY() < this.height){
					if(envGrid[body.getPosY()+1][body.getPosX()].blockState == 1){
						this.envGrid[body.getPosY()+1][body.getPosX()].blockState = 0;
						body.setPosY(body.getPosY()+1);
						applyGravity(body);
					}
				} else {
					body.setPosY(body.getPosY()+1);
				}
				
				break;
			case WEST :
				if(body.getPosX() > 0){
					if(envGrid[body.getPosY()][body.getPosX()-1].blockState == 1){
						this.envGrid[body.getPosY()][body.getPosX()-1].blockState = 0;
						body.setPosX(body.getPosX()-1);
						applyGravity(body);
					} else if(envGrid[body.getPosY()][body.getPosX()-1].blockState == 3 || this.envGrid[body.getPosY()][body.getPosX()-1].blockState == 0){
						body.setPosX(body.getPosX()-1);
						applyGravity(body);
					} else if(envGrid[body.getPosY()][body.getPosX()-1].blockState == 4){
						body.setPosX(body.getPosX()-1);
					}
				} else {
					body.setPosX(body.getPosX()-1);
				}
				
				break;
			case EAST :
				if(body.getPosX() < this.width){
					if(envGrid[body.getPosY()][body.getPosX()+1].blockState == 1){
						this.envGrid[body.getPosY()][body.getPosX()+1].blockState = 0;
						body.setPosX(body.getPosX()+1);
						applyGravity(body);
					} else if(envGrid[body.getPosY()][body.getPosX()+1].blockState == 3 || this.envGrid[body.getPosY()][body.getPosX()+1].blockState == 0){
						body.setPosX(body.getPosX()+1);
						applyGravity(body);
					} else if(envGrid[body.getPosY()][body.getPosX()+1].blockState == 4){
						body.setPosX(body.getPosX()+1);
					}
				} else {
					body.setPosX(body.getPosX()+1);
				}
				
				break;
			}
		}		
	}
	
	/**
	 * The lemming jump one case up to a east or west direction
	 * @param body
	 * @param dir
	 */
	public void jump(LemmingBody body, Direction dir) {
		if(!applyGravity(body)){
		switch(dir){
		case WEST :
			if(body.getPosX() > 0 && body.getPosY() > 0
					&& (this.envGrid[body.getPosY()-1][body.getPosX()-1].blockState != 1 || this.envGrid[body.getPosY()-1][body.getPosX()-1].blockState != 2)){
				if(this.envGrid[body.getPosY()-1][body.getPosX()-1].blockState == 0 
						&& this.envGrid[body.getPosY()-1][body.getPosX()].blockState == 0){
						body.setPosX(body.getPosX()-1);
						body.setPosY(body.getPosY()-1);
						applyGravity(body);
				} else if(envGrid[body.getPosY()-1][body.getPosX()-1].blockState == 4
						   && this.envGrid[body.getPosY()-1][body.getPosX()].blockState == 0){
					body.setPosX(body.getPosX()-1);
					body.setPosY(body.getPosY()-1);
				} else if(this.envGrid[body.getPosY()-1][body.getPosX()].blockState == 4){
					body.setPosY(body.getPosY()-1);
				}
			} else {
				body.setPosX(body.getPosX());
				body.setPosY(body.getPosY());
			}
			break;
		case EAST :
			if(body.getPosX() < this.width && body.getPosY() > 0
					&& (this.envGrid[body.getPosY()-1][body.getPosX()+1].blockState != 1 || this.envGrid[body.getPosY()-1][body.getPosX()+1].blockState != 2)){
				if(this.envGrid[body.getPosY()-1][body.getPosX()+1].blockState == 0 
						&& this.envGrid[body.getPosY()-1][body.getPosX()].blockState == 0){
					body.setPosX(body.getPosX()+1);
					body.setPosY(body.getPosY()-1);
					applyGravity(body);
				} else if(this.envGrid[body.getPosY()-1][body.getPosX()+1].blockState == 4
						&& this.envGrid[body.getPosY()-1][body.getPosX()].blockState == 0){
					body.setPosX(body.getPosX()+1);
					body.setPosY(body.getPosY()-1);
				}  else if(this.envGrid[body.getPosY()-1][body.getPosX()].blockState == 4){
					body.setPosY(body.getPosY()-1);
				}
			}else {
				body.setPosX(body.getPosX());
				body.setPosY(body.getPosY());
			}
			break;
		}
		}
		
	}
	
	/**
	 * While falling, the lemming opens the parachute to avoid dying.
	 * @param body Lemming's body.
	 */
	public void openParachute(LemmingBody body) {
		if(this.envGrid[body.getPosY()+1][body.getPosX()].blockState != 1 
				&& this.envGrid[body.getPosY()+1][body.getPosX()].blockState != 2) {
			body.setParachuteIsOpen(true);
		}
		
		applyGravity(body);
	}
	
	/**
	 * Move forward or backward with respect to the direction.
	 * @param body Lemming's body.
	 * @param direction Direction.
	 */
	public void walk(LemmingBody body, Direction direction) {
		
		if(!applyGravity(body)){
		if (body.getPosX()> 0 && body.getPosY()>0 && body.getPosX()<this.width && body.getPosY()<this.height) {
			if (direction == Direction.EAST) {
				if (body.getEnvironment().getEnvGrid()[body.getPosY()][body.getPosX()+1].blockState == 1 
						|| body.getEnvironment().getEnvGrid()[body.getPosY()][body.getPosX()+1].blockState == 2
						/*|| (body.getPosX()+1) == 18)*/) {
					body.setDirection(Direction.WEST);
				} else {
					body.setPosX(body.getPosX()+1);
				}
			} else if (direction == Direction.WEST) {
				if (body.getEnvironment().getEnvGrid()[body.getPosY()][body.getPosX()-1].blockState ==2 
						|| body.getEnvironment().getEnvGrid()[body.getPosY()][body.getPosX()-1].blockState == 1
						/*|| (body.getPosX()-1) == -1*/
						) {
					body.setDirection(Direction.EAST);
				} else {
					body.setPosX(body.getPosX()-1);
				}
			}
		} else {
			/*if (direction == Direction.EAST) {
				body.setPosX(body.getPosX()+1);
			} else if(direction == Direction.WEST){
				body.setPosX(body.getPosX()-1);
			}*/
		}
		}
		applyGravity(body);
	}
	
	/**
	 * The lemming can fill a block of the world around him.
	 * @param body Lemming's body.
	 */
	public void fill(LemmingBody body){
		if(!applyGravity(body)){
			if(body.getDirection() == Direction.EAST){
				if(body.getPosX() < this.width && envGrid[body.getPosY()][body.getPosX()+1].blockState == 0){
					if(envGrid[body.getPosY()+1][body.getPosX()+1].blockState == 0){
						envGrid[body.getPosY()+1][body.getPosX()+1].blockState = 1;
					} else {
						envGrid[body.getPosY()][body.getPosX()+1].blockState = 1;
					}
				} else if((envGrid[body.getPosY()][body.getPosX()+1].blockState == 4 || envGrid[body.getPosY()][body.getPosX()+1].blockState == 3) && envGrid[body.getPosY()+1][body.getPosX()+1].blockState == 0){
					envGrid[body.getPosY()+1][body.getPosX()+1].blockState = 0;
				}
		} else {
			if(body.getPosX() > 0 && body.getPosX() < this.width && envGrid[body.getPosY()][body.getPosX()-1].blockState == 0){
				if(envGrid[body.getPosY()+1][body.getPosX()-1].blockState == 0){
					envGrid[body.getPosY()+1][body.getPosX()-1].blockState = 1;
				} else {
					envGrid[body.getPosY()][body.getPosX()-1].blockState = 1;
				}
			} else if(body.getPosX() > 0 && (envGrid[body.getPosY()][body.getPosX()-1].blockState == 4 || envGrid[body.getPosY()][body.getPosX()+1].blockState == 3) && envGrid[body.getPosY()+1][body.getPosX()+1].blockState == 0){
				envGrid[body.getPosY()-1][body.getPosX()+1].blockState = 0;
			}
		}
		}
	}

	
	/**
	 * The gravity is applied to the environment.
	 * @param body Lemming's body.
	 * @return Return true if the gravity is applied, false else.
	 */
	public boolean applyGravity(LemmingBody body){
		if(body.getPosY()< height-3){
			if(envGrid[body.getPosY()+1][body.getPosX()].blockState != 1 && envGrid[body.getPosY()+1][body.getPosX()].blockState != 2 ){
				body.setPosY(body.getPosY()+1);
				if(envGrid[body.getPosY()+1][body.getPosX()].blockState != 4){
					if(envGrid[body.getPosY()+1][body.getPosX()].blockState == 0 || envGrid[body.getPosY()+1][body.getPosX()].blockState == 3){
						body.setPosY(body.getPosY()+1);// the lemming fall
						body.fall(2);
						if(envGrid[body.getPosY()+1][body.getPosX()].blockState == 1 || envGrid[body.getPosY()+1][body.getPosX()].blockState == 2){
							body.checkDeath();
						}
					} else if(envGrid[body.getPosY()+1][body.getPosX()].blockState == 4){
						body.setPosY(body.getPosY()+1);
					} else {
						body.fall(1);
						body.checkDeath();
					}
				}
				
				
				return true;
			}
		} else if(body.getPosY() == height-3){
			
			if(envGrid[body.getPosY()+1][body.getPosX()].blockState != 1 && envGrid[body.getPosY()+1][body.getPosX()].blockState != 2){
				body.setPosY(body.getPosY()+1);
				if(envGrid[body.getPosY()+1][body.getPosX()].blockState != 4){
					if(envGrid[body.getPosY()+1][body.getPosX()].blockState == 0 || envGrid[body.getPosY()+1][body.getPosX()].blockState == 3){
						body.setPosY(body.getPosY()+2);// the lemming fall out of bound (void attract)
					} else if(envGrid[body.getPosY()+1][body.getPosX()].blockState == 4){
						body.setPosY(body.getPosY()+1);// the lemming fall
					} else {
						body.fall(1);
						body.checkDeath();
					}
				}
				
				
				return true;
			}
		} else if(body.getPosY() == height-2){
			if(envGrid[body.getPosY()+1][body.getPosX()].blockState == 0 || envGrid[body.getPosY()+1][body.getPosX()].blockState == 3){
				body.setPosY(body.getPosY()+2);// the lemming fall out of the bound of the world
				return true;
			} else if(envGrid[body.getPosY()+1][body.getPosX()].blockState == 4){
				body.setPosY(body.getPosY()+1);
				return true;
			}
		} else {
			body.setPosY(body.getPosY()+1);// the lemming fall out of the bound of the world
			return true;
		}
		return false;
	}
}
