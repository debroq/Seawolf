package Aircraft;

import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class MsgBubble {
	
	private boolean aliveStatus;
	
	private String msg;
	private int xLoc;
	private int yLoc;
	private int millis;
	
	private GraphicsContext gc;
	
	
	public MsgBubble(String msg, GraphicsContext gc, int xLoc, int yLoc, int millis) {
		this.msg= msg;
		this.xLoc= xLoc;
		this.yLoc= yLoc;
		this.millis= millis;
		this.gc= gc;
		
		aliveStatus= true;
	}
	
	public void drawMessage() {
		
		gc.setStroke(Color.FLORALWHITE);
		gc.strokeText(msg, xLoc, yLoc-5);
		
		Timer displayMsgTimer= new Timer();
		displayMsgTimer.schedule(new DisplayMsgTask(), millis);
	}
	
	public boolean aliveStatus() {
		return aliveStatus;
	}
	
	class DisplayMsgTask extends TimerTask {
		
		public DisplayMsgTask() {
			
		}
		
		// Timer expired
		public void run() {
			System.out.println("Set flag to delete msg");
			
			aliveStatus= false;
		}
		
	}
	

}
