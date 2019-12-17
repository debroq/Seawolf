package Game;


public abstract class Sprite {
	
	protected int xloc;
	protected int yloc;
	
	protected float movementInc;
	
	public static enum Direction { L_TO_R, R_TO_L};
	protected Direction direction;
	
	public Sprite(float movementInc) {
		this.movementInc= movementInc;
	}
}
