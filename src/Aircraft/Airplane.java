package Aircraft;

import javafx.scene.shape.Rectangle;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import AntiSubWeapons.AntiShipBomb;
import Game.GameAction;
import Game.SeaWolf2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import Game.AircraftStruckByBulletCollisionFunctions;
import Game.ImageDisplayFunctions;
import Game.ImageMoveFunctions;
import Game.JukeBox;
import Game.Sprite;


public class Airplane extends Sprite implements ImageDisplayFunctions, ImageMoveFunctions, AircraftStruckByBulletCollisionFunctions {
	
	private Image airplaneRtoLImage;
	private static Image origAirplaneRtoLImage;
	private Image airplaneLtoRImage;
	private static Image origAirplaneLtoRImage;
	private static Image airplaneRtoLImageDoorOpen;
	
	private static Image airplaneLtoRImageDoorOpen;
	
	private static Image[][] airplaneLtoRHitImages;
	private static Image[][] airplaneRtoLHitImages;

	private static String[][] airplaneBlastImageFilenamesLtoR;
	private static String[][] airplaneBlastImageFilenamesRtoL;

	
	private boolean aliveStatus;
	private int[] bombReleaseCoordsLtoR;
	private int[] bombReleaseCoordsRtoL;
    private Random randomGenerator;
    
	private int bombReleaseCoordIndex;
	
	private int movementCounter;
	
	private boolean airplaneHit;
		
	private ScheduledFuture scheduledFuture;
	private ScheduledExecutorService scheduledExecutorService;
	
	private boolean explosionTimerScheduled;
	
	public static boolean ALT_ROW= true;
	
	private GraphicsContext gc;
	
	private static int POINTS= 400;
	
	private static boolean airplaneDataInitialized= initAirplaneData();
	
	public Airplane(JukeBox jukeBox, GraphicsContext gc,  int movementInc, int xloc,  Direction direction, boolean altRow) {
		
		this(jukeBox, gc, movementInc);
		
		this.gc = gc;
		
		this.direction= direction;
		this.xloc= xloc;
		
		if (altRow== true) {
			yloc= GameAction.AIRPLANE_SECONDARY_DISPLAY_ROW;
		}
		
//		// Set explicit direction
//		if (direction== Direction.L_TO_R) {
//			
//			xloc= DrawingPanel.FIRST_SCREEN_COLUMN;
//			direction= Direction.L_TO_R;
//			
//		} else {
//			
//			xloc= DrawingPanel.FINAL_SCREEN_COLUMN;
//			direction= Direction.R_TO_L;
//			
//		}
		
	}
	
	public Airplane(JukeBox jukeBox, GraphicsContext gc, int movementInc) {
		
		super(movementInc);
		
		this.gc= gc;
		
		aliveStatus= true;
		
		if (GameAction.activeAirplaneCount== 0) {
			GameAction.airPlaneMediaPlayer= jukeBox.getAirplaneFlyByClip();
			GameAction.airPlaneMediaPlayer.play();
		}
		
		GameAction.activeAirplaneCount++;
		
		randomGenerator= new Random();
		
		int randomDir= randomGenerator.nextInt(2);

		switch (randomDir) {
			case 0:
				xloc= 0;
				direction= Direction.L_TO_R;
				break;
			case 1:
				xloc= SeaWolf2.CANVAS_WIDTH;
				direction= Direction.R_TO_L;
				break;
			default:
				System.out.println("Should not be here!");
				break;
		}
		yloc= GameAction.AIRPLANE_DISPLAY_ROW;
		
		bombReleaseCoordsLtoR= new int[5];
		bombReleaseCoordsRtoL= new int[5];
		genBombReleaseCoords();
		
		
		this.movementInc= movementInc;
		
		movementCounter= 0;	
		
		airplaneHit= false;
		
//		airplaneDestroyed= false;
		
		// Experiment with ExecutorService
		scheduledExecutorService =
		        Executors.newScheduledThreadPool(5);
		
		airplaneRtoLImage= origAirplaneRtoLImage;
		airplaneLtoRImage= origAirplaneLtoRImage;
	}
	
	private static boolean initAirplaneData() {
		
		Image imageIcon= new Image("/ImageAirplanes/AirplaneRtoL.png");
		origAirplaneRtoLImage= imageIcon;
		
		imageIcon= new Image("/ImageAirplanes/AirplaneRtoLDoorOpen.png");
		airplaneRtoLImageDoorOpen= imageIcon;
		
		imageIcon= new Image("/ImageAirplanes/AirplaneLtoR.png");
		origAirplaneLtoRImage= imageIcon;
		
		imageIcon= new Image("/ImageAirplanes/AirplaneLtoRDoorOpen.png");
		airplaneLtoRImageDoorOpen=imageIcon;
		
		
		airplaneBlastImageFilenamesLtoR= new String[1][4];
		airplaneBlastImageFilenamesRtoL= new String[1][4];
		
		airplaneBlastImageFilenamesLtoR[0]= new String[4];
		airplaneBlastImageFilenamesRtoL[0]= new String[4];
		
		airplaneBlastImageFilenamesLtoR[0][0]= "/ImagesAirplaneHit/AirplaneLtoRHit1.png";
		airplaneBlastImageFilenamesLtoR[0][1]= "/ImagesAirplaneHit/AirplaneLtoRHit2.png";
		airplaneBlastImageFilenamesLtoR[0][2]= "/ImagesAirplaneHit/AirplaneLtoRHit3.png";
		airplaneBlastImageFilenamesLtoR[0][3]= "/ImagesAirplaneHit/AirplaneLtoRHit4.png";
		
		airplaneBlastImageFilenamesRtoL[0][0]= "/ImagesAirplaneHit/AirplaneRtoLHit1.png";
		airplaneBlastImageFilenamesRtoL[0][1]= "/ImagesAirplaneHit/AirplaneRtoLHit2.png";
		airplaneBlastImageFilenamesRtoL[0][2]= "/ImagesAirplaneHit/AirplaneRtoLHit3.png";
		airplaneBlastImageFilenamesRtoL[0][3]= "/ImagesAirplaneHit/AirplaneRtoLHit4.png";
		
		airplaneLtoRHitImages= new Image[1][4];
		airplaneRtoLHitImages= new Image[1][4];
		
		
		for (int index= 0; index< 4; index++) {
//			System.out.println("Inside for loop to load airplane hit images");
//			System.out.println("Filename: " + airplaneBlastImageFilenamesLtoR[0][index]);
//			System.out.println("Airplane Image URL: " + getImageURL(airplaneBlastImageFilenamesLtoR[0][index]));
			Image aImageIcon= new Image(airplaneBlastImageFilenamesLtoR[0][index]);
			airplaneLtoRHitImages[0][index]= aImageIcon;
			
			aImageIcon= new Image(airplaneBlastImageFilenamesRtoL[0][index]);
			airplaneRtoLHitImages[0][index]= aImageIcon;
		}
		
		return true;
	}
	
	public boolean isHit() {
		
		return airplaneHit;
	}
	
	
	private static URL getImageURL(String imageFilename) {
		URL aURL= null;
		
//		ClassLoader cl = this.getClass().getClassLoader();
		
		aURL= Airplane.class.getResource(imageFilename);
		
//		System.out.println("About to access: " + aURL.toString());
   
	     return aURL;
	}
	
	public void stopAudio()
	{
		if (GameAction.activeAirplaneCount== 0) {
			GameAction.airPlaneMediaPlayer.stop();
			GameAction.airPlaneMediaPlayer.dispose();
		}
	}
	

	public void move() {
		
		movementCounter++;
		
		if (movementCounter== movementInc) {
			if (direction== Direction.L_TO_R) {
				if (xloc>= SeaWolf2.CANVAS_WIDTH) {
					
				    // End of the run for this airplane.
				    // Somehow flag it for destruction.
					
					aliveStatus= false;
					GameAction.activeAirplaneCount--;
					stopAudio();
					
				} else {
					xloc+=movementInc;
				}
				
			} 
			else  // Move R to L
			{
				if (xloc<= 0) {
					
					// End of the run for this airplane.
				    // Somehow flag it for destruction.
					
					aliveStatus= false;
					GameAction.activeAirplaneCount--;
					stopAudio();
					
				}
				else {
					xloc-=movementInc;
				}
			}
			
			movementCounter= 0;
		}
	}
	
	
	public boolean aliveStatus() {
		return aliveStatus;
	}
	
	
	public Rectangle getBounds() {
		Rectangle aRectangle= null;
		
		if (direction== Direction.L_TO_R) {
			aRectangle= new Rectangle(xloc, yloc, airplaneLtoRImage.getWidth(), airplaneLtoRImage.getHeight());
		}
		else {
			aRectangle= new Rectangle(xloc, yloc, airplaneRtoLImage.getWidth(), airplaneRtoLImage.getHeight());
		}
		
		return aRectangle;
	}
	
	Runnable animateAirplaneExplodingTask = new Runnable(){
		private int numTimesExpired;

		public void run() {
			//    		System.out.println("Hello World"); 

			switch (numTimesExpired) {
			case 0:   
				if (direction== Direction.L_TO_R)
					airplaneLtoRImage= airplaneLtoRHitImages[0][0];
				else
					airplaneRtoLImage= airplaneRtoLHitImages[0][0];

				numTimesExpired++;
				break;

			case 1:
				if (direction== Direction.L_TO_R)
					airplaneLtoRImage= airplaneLtoRHitImages[0][1];
				else
					airplaneRtoLImage= airplaneRtoLHitImages[0][1];

				numTimesExpired++;
				break;

			case 2:
				if (direction== Direction.L_TO_R)
					airplaneLtoRImage= airplaneLtoRHitImages[0][2];
				else
					airplaneRtoLImage= airplaneRtoLHitImages[0][2];

				numTimesExpired++;
				break;

			case 3:
				if (direction== Direction.L_TO_R)
					airplaneLtoRImage= airplaneLtoRHitImages[0][3];
				else
					airplaneRtoLImage= airplaneRtoLHitImages[0][3];

				numTimesExpired++;

				MsgBubble aMsgBubble= new MsgBubble(Integer.toString(getPoints()), gc, xloc, yloc + 10, GameAction.MSG_BUBBLE_DISPLAY_MILLIS );
				GameAction.drawMsgBubbleList.add(aMsgBubble);
				break;

			case 4:
				//	    			airplaneLtoRImage= Utilities.modifyImage(airplaneLtoRHitImages[0][3], 400);
				numTimesExpired++;
				break;

			default:

				// Cancel the Timer
				scheduledFuture.cancel(true);

				explosionTimerScheduled= false;

				aliveStatus= false;  // Added as test on 2/11

				GameAction.activeAirplaneCount--;
				stopAudio();
				break;


			}

		}
	};
	

	
	public Image getImage() {
		if (airplaneHit== true) {
			// ****************************************
			// NOTE: This code seems could be condensed
			// ****************************************
			if (direction== Direction.L_TO_R) {

				if (explosionTimerScheduled== false) {
					scheduledFuture =
							scheduledExecutorService.scheduleAtFixedRate(animateAirplaneExplodingTask, 2, 70, TimeUnit.MILLISECONDS);

					explosionTimerScheduled= true;
				}
				
				return airplaneLtoRImage;
				
			}
			else
			{
				if (explosionTimerScheduled== false) {
					scheduledFuture =
							scheduledExecutorService.scheduleAtFixedRate(animateAirplaneExplodingTask, 2, 70, TimeUnit.MILLISECONDS);

					explosionTimerScheduled= true;
				}

				return airplaneRtoLImage;
			}
		}
		else
		{
			if (direction== Direction.L_TO_R) {
//				System.out.println("plane xloc= " + xloc);
				if (((SeaWolf2.gameOver== false) && (bombReleaseCoordIndex<5) && (xloc== bombReleaseCoordsLtoR[bombReleaseCoordIndex])))
				{
					AntiShipBomb aAntiShipBomb= new AntiShipBomb(xloc);
					GameAction.drawAntiShipBombList.add(aAntiShipBomb);
					bombReleaseCoordIndex++;

					return airplaneLtoRImageDoorOpen;
				}
				if (bombReleaseCoordIndex== 5)
					return airplaneLtoRImage;
				else
					return airplaneLtoRImageDoorOpen;

			}
			else
			{
//				System.out.println("plane xloc= " + xloc);
				if (((SeaWolf2.gameOver== false) && (bombReleaseCoordIndex<5) && (xloc== bombReleaseCoordsRtoL[bombReleaseCoordIndex])))
				{
					AntiShipBomb aAntiShipBomb= new AntiShipBomb(xloc);
					GameAction.drawAntiShipBombList.add(aAntiShipBomb);
					bombReleaseCoordIndex++;

					return airplaneRtoLImageDoorOpen;
				}
				if (bombReleaseCoordIndex== 5)
					return airplaneRtoLImage;
				else
					return airplaneRtoLImageDoorOpen;
			}
		}
	}
    
	public Direction getCurrentDirection() {
		return direction;
	}

	public int getX() {
		return xloc;
	}

	public int getY() {
		return yloc;
	}
	
	public int getPoints() {
		return POINTS;
	}
	
	
	public void setHitByBullet() {
		airplaneHit= true;
	}
	
	public boolean isHitByBullet() {
		return airplaneHit;
	}
	
	private void genBombReleaseCoords()
	{
		int randomXCoord= randomGenerator.nextInt(3);
		
			{
			switch (randomXCoord) {
			case 0:
				bombReleaseCoordsLtoR[0]= 680;
				bombReleaseCoordsLtoR[1]= 720;
				bombReleaseCoordsLtoR[2]= 752;
				bombReleaseCoordsLtoR[3]= 800;
				bombReleaseCoordsLtoR[4]= 816;
				break;
			case 1:
				bombReleaseCoordsLtoR[0]= 328;
				bombReleaseCoordsLtoR[1]= 432;
				bombReleaseCoordsLtoR[2]= 556;
				bombReleaseCoordsLtoR[3]= 740;
				bombReleaseCoordsLtoR[4]= 820;
				break;
			case 2:
				bombReleaseCoordsLtoR[0]= 176;
				bombReleaseCoordsLtoR[1]= 384;
				bombReleaseCoordsLtoR[2]= 536;
				bombReleaseCoordsLtoR[3]= 652;
				bombReleaseCoordsLtoR[4]= 816;
				break;
			default:
				break;
			}
			
		randomXCoord= randomGenerator.nextInt(3);
		
			{
			switch (randomXCoord) {
			case 0:
				bombReleaseCoordsRtoL[0]= 408;
				bombReleaseCoordsRtoL[1]= 396;
				bombReleaseCoordsRtoL[2]= 292;
				bombReleaseCoordsRtoL[3]= 256;
				bombReleaseCoordsRtoL[4]= 232;
				break;
			case 1:
				bombReleaseCoordsRtoL[0]= 720;
				bombReleaseCoordsRtoL[1]= 528;
				bombReleaseCoordsRtoL[2]= 244;
				bombReleaseCoordsRtoL[3]= 216;
				bombReleaseCoordsRtoL[4]= 192;
				break;
			case 2:
				bombReleaseCoordsRtoL[0]= 816;
				bombReleaseCoordsRtoL[1]= 700;
				bombReleaseCoordsRtoL[2]= 592;
				bombReleaseCoordsRtoL[3]= 400;
				bombReleaseCoordsRtoL[4]= 264;
				break;
			default:
				break;
					
			}
		}
			
				
		}
		
	}
}
