package AntiSubWeapons;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import Game.GameAction;
import Game.ImageDisplayFunctions;
import Game.ImageMoveFunctions;
import Game.JukeBox;
import Game.Sprite;


public class ParachutedMine extends Sprite implements ImageDisplayFunctions, ImageMoveFunctions {
	private final static float MOVEMENTINC=1;
	private boolean aliveStatus;
	
	private Image antiShipBombImage;
	private Image antiShipBombImageDeployedA;
	private Image antiShipBombImageDeployedB;
	private Image antiShipBombImageDeployedC;
	
	private static int antiShipBombImageWidth;
	private static int antiShipBombImageHeight;
	
	private boolean hitGun= false;
	
	private JukeBox jukeBox;
	
	private MediaPlayer antiShipBombEnteredWaterClip;
	
	private int imageNumber;
	
	ScheduledFuture scheduledFuture;
	
	boolean mineBobTimerScheduled;
	
	private boolean antiShipBombDataInitialized= initAntiShipBombData();
	
	public ParachutedMine(JukeBox jukeBox, int xloc) {
		
		super(MOVEMENTINC);
		
		Image imageIcon= new Image("/ImagesParachutedMine/ParachutedMine.png");
		antiShipBombImage= imageIcon;
		antiShipBombImageWidth= (int) antiShipBombImage.getWidth();
		antiShipBombImageHeight= (int) antiShipBombImage.getHeight();
		
		imageIcon= new Image("/ImagesParachutedMine/ParachutedMineDeployedA.png");
		antiShipBombImageDeployedA= imageIcon;
		
		imageIcon= new Image("/ImagesParachutedMine/ParachutedMineDeployedB.png");
		antiShipBombImageDeployedB= imageIcon;
		
		imageIcon= new Image("/ImagesParachutedMine/ParachutedMineDeployedC.png");
		antiShipBombImageDeployedC= imageIcon;
		
		
		this.xloc= xloc;
		yloc= GameAction.AIRPLANE_DISPLAY_ROW+ 40;
		
		this.jukeBox= jukeBox;
		
		imageNumber= 0;
		
		aliveStatus= true;
		
		antiShipBombEnteredWaterClip= jukeBox.getAntiShipBombEnteredWaterClip();
	}
	
	private boolean initAntiShipBombData() {
		
		Image imageIcon= new Image("/ImagesParachutedMine/ParachutedMine.png");
		antiShipBombImage= imageIcon;
		antiShipBombImageWidth= (int) antiShipBombImage.getWidth();
		antiShipBombImageHeight= (int) antiShipBombImage.getHeight();
		
		return true;
	}
	
	public Image getImage() {
		return antiShipBombImage;
	}
	
	public void move() {
		
		yloc+= movementInc;
		
		if (movementInc== 1)
			movementInc= 0;	
		else if (movementInc== 0)
			movementInc= 1;
		
		if (yloc== 495) {
			antiShipBombImage= antiShipBombImageDeployedC;
		}
			
	    if (yloc> 495) {
	    	
	    	aliveStatus= false;
	    	
	    }
			
//			if (mineBobTimerScheduled== false) {
//				scheduledFuture =  // Was 70
//					    scheduledExecutorService.scheduleAtFixedRate(animateMineBobbingTask, 2, 400, TimeUnit.MILLISECONDS);
//				
//				mineBobTimerScheduled= true;
//			}
//		}
	}
	
	public int getX() {
		return xloc;
	}
	
	public int getY() {
		
		if (yloc== 62 )
		{
			// Play the bomb entered water track
			antiShipBombEnteredWaterClip.play();
			antiShipBombEnteredWaterClip.seek(antiShipBombEnteredWaterClip.getStartTime());
			
		}
		return yloc;
	}
	
	// Experiment with ExecutorService
	ScheduledExecutorService scheduledExecutorService =
	        Executors.newScheduledThreadPool(5);
	
	
//	Runnable animateMineBobbingTask=  new Runnable() {
//
//		public void run() {
//			System.out.println("Hello World");
//
//			switch (imageNumber) {
////			case 0:
////				antiShipBombImage= antiShipBombImageDeployedA;
////				imageNumber++;
////				break;
//			case 0:
//				antiShipBombImage= antiShipBombImageDeployedB;
//				imageNumber++;
//				break;
//			case 1:
//				antiShipBombImage= antiShipBombImageDeployedC;
//				imageNumber= 0;
//				break;
//			}
//
//		}
//	};
	
	public boolean aliveStatus() {
		return aliveStatus;
	} 
	
	public Rectangle getBounds() {
		// Fix this!
		return new Rectangle(xloc, yloc+12, antiShipBombImageWidth, antiShipBombImageHeight-12 );
	}
	
	public boolean getHitGun() {
		
		return hitGun;
	}
	
	public void setHitGun() {
		hitGun= true;
	}
	
//	private static URL getImageURL(String imageFilename) {
//		URL aURL= null;
//		
////		ClassLoader cl = this.getClass().getClassLoader();
//		
//		aURL= ParachutedMine.class.getResource(imageFilename);
//   
//	     return aURL;
//	}
	
}
