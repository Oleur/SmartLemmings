package smartLemmings.environment;

/**
 * Component of the environment represented by a block state and a boolean if there is a lemming.
 * @author VI51
 *
 */
public class EnvironmentObject {
	
	public int blockState;
	public boolean isLem;
	
	/**
	 * Constructor of a environment object with a block state and a lemming state.
	 * @param block Block state.
	 * @param stateLem Lemming state.
	 */
	public EnvironmentObject(int block, boolean stateLem )  {
		this.blockState = block;
		this.isLem = stateLem;
	}

	/**
	 * Get the current block state.
	 * @return A block state.
	 */
	public int getBlockState() {
		return blockState;
	}

	/**
	 * Set the selected block state.
	 * @param blockState A block state.
	 */
	public void setBlockState(int blockState) {
		this.blockState = blockState;
	}

	/**
	 * Set the selected lemming state.
	 * @param isLem A lemming state.
	 */
	public void setLem(boolean isLem) {
		this.isLem = isLem;
	}

	/**
	 * Get the state of the current lemming.
	 * @return The lemming state.
	 */
	public boolean isLem() {
		return isLem;
	}

}
