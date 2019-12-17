package Aircraft;

import javafx.scene.shape.Rectangle;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


import AntiSubWeapons.ParachutedBomb;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import Game.AircraftStruckByBulletCollisionFunctions;
//import AntiSubWeapons.ParachutedBomb;
//import Game.AircraftStruckByBulletCollisionFunctions;
//import Game.DrawingPanel;
import Game.ImageDisplayFunctions;
import Game.ImageMoveFunctions;
import Game.SeaWolf2;
import Game.JukeBox;
import Game.Sprite;
import Game.GameAction;

public class SeaSprite extends Sprite implements ImageDisplayFunctions, ImageMoveFunctions, AircraftStruckByBulletCollisionFunctions {
	
	private Image helicopterLtoRImageA;   // <-- This is probably the prolem.  Can't be static, when now working with 2 Helicopters!!
	private static Image origHelicopterLtoRImageA;
	private Image helicopterLtoRImageB;
	private static Image origHelicopterLtoRImageB;
	private Image helicopterRtoLImageA;
	private static Image origHelicopterRtoLImageA;
	private Image helicopterRtoLImageB;
	private static Image origHelicopterRtoLImageB;	
	
	private static Image[][] helicopterLtoRHitImages;
	private static Image[][] helicopterRtoLHitImages;

	private static String[][] helicopterBlastImageFilenamesLtoR;
	private static String[][] helicopterBlastImageFilenamesRtoL;
	
	public final static int HELICOPTERA= 0;
	public final static int HELICOPTERB= 1;

	
	private boolean aliveStatus;
	private int[] heliBombReleaseCoordsLtoR;
	private int[] bombReleaseCoordsRtoL;
	private JukeBox jukeBox;
	private Random randomGenerator;
	
	private int bombReleaseCoordIndex;
	
	private Image currentImage;
	
	private int movementCounter;
	
	private boolean helicopterHit;
	private boolean helicopterDestroyed;
	
	ScheduledFuture scheduledFuture;
	ScheduledExecutorService scheduledExecutorService;
	
	boolean explosionTimerScheduled;
	
	public static boolean jawsClipPlaying= false;
	
	private GraphicsContext gc;
	
	private static int POINTS= 100;

	
	private static boolean helicopterDataInitialized= initHelicopterData();
	
	public SeaSprite(JukeBox jukeBox, GraphicsContext gc, boolean usePrevHeliData, int helicopterPosition, int prevXLoc, int prevYLoc, Direction prevDirection, int movementInc) {
		
		super(movementInc);
		
		
		aliveStatus= true;
		
		if (GameAction.activeHelicopterCount== 0) {
			GameAction.helicopterMediaPlayer= jukeBox.getHelicopterInFlightClip();
			GameAction.helicopterMediaPlayer.play();
		}
		
		GameAction.activeHelicopterCount++;
		
		helicopterLtoRImageA= origHelicopterLtoRImageA;
		helicopterLtoRImageB= origHelicopterLtoRImageB;
		helicopterRtoLImageA= origHelicopterRtoLImageA;
		helicopterRtoLImageB= origHelicopterRtoLImageB;
		
		randomGenerator= new Random();
	
		
		if (usePrevHeliData== false) {
			int randomDir= randomGenerator.nextInt(2);
			switch (randomDir) {
				case 0:
					if (helicopterPosition== HELICOPTERA)
						xloc= 0;
					else
						xloc= 0 - 10;
						
					direction= Direction.L_TO_R;
					currentImage= helicopterLtoRImageA;
					break;
				case 1:
					if (helicopterPosition== HELICOPTERA)
						xloc= SeaWolf2.CANVAS_WIDTH;
					else
						xloc= SeaWolf2.CANVAS_WIDTH + 10;
					
					direction= Direction.R_TO_L;
					currentImage= helicopterRtoLImageA;
					break;
				default:
					System.out.println("Should not be here!");
					break;
			}
			
			yloc= SeaWolf2.HELICOPTER_DISPLAY_ROW;
		}
		else
		{
			if (prevDirection== Direction.L_TO_R) {
				xloc= 0 - 18;   // Was 20
				yloc= prevYLoc+ 10;
				direction= Direction.L_TO_R;
				currentImage= helicopterLtoRImageA;
			}
			else
			{
				xloc= SeaWolf2.CANVAS_WIDTH + 21;  // Was 21
				yloc= prevYLoc + 10;
				direction= Direction.R_TO_L;
				currentImage= helicopterRtoLImageA;
				
			}
		
		}
		
		heliBombReleaseCoordsLtoR= new int[3];
		bombReleaseCoordsRtoL= new int[3];
		genBombReleaseCoords();
		
		this.jukeBox= jukeBox;
		
		this.movementInc= movementInc;
		
		movementCounter= 0;
		
		helicopterHit= false;
		
		helicopterDestroyed= false;
		
		// Experiment with ExecutorService
		scheduledExecutorService =
		        Executors.newScheduledThreadPool(5);
		
//		numHelicopters++;
		
		this.gc= gc;
		
	}
	
	private static boolean initHelicopterData() {
		
		Image imageIcon= new Image("/ImagesHelicopter/HelicopterLtoRA.png");
		origHelicopterLtoRImageA= imageIcon;
		
		imageIcon= new Image("/ImagesHelicopter/HelicopterLtoRB.png");
		origHelicopterLtoRImageB= imageIcon;
		
		imageIcon= new Image("/ImagesHelicopter/HelicopterRtoLA.png");
		origHelicopterRtoLImageA= imageIcon;
		
		imageIcon= new Image("/ImagesHelicopter/HelicopterRtoLB.png");
		origHelicopterRtoLImageB= imageIcon;
		
		helicopterBlastImageFilenamesLtoR= new String[1][4];
		helicopterBlastImageFilenamesRtoL= new String[1][4];
		
		helicopterBlastImageFilenamesLtoR[0]= new String[4];
		helicopterBlastImageFilenamesRtoL[0]= new String[4];
		
		helicopterBlastImageFilenamesLtoR[0][0]= "/ImagesHelicopterHit/HelicopterLtoRAHit1.png";
		helicopterBlastImageFilenamesLtoR[0][1]= "/ImagesHelicopterHit/HelicopterLtoRAHit2.png";
		helicopterBlastImageFilenamesLtoR[0][2]= "/ImagesHelicopterHit/HelicopterLtoRAHit3.png";
		helicopterBlastImageFilenamesLtoR[0][3]= "/ImagesHelicopterHit/HelicopterLtoRAHit4.png";
		
		helicopterBlastImageFilenamesRtoL[0][0]= "/ImagesHelicopterHit/HelicopterRtoLAHit1.png";
		helicopterBlastImageFilenamesRtoL[0][1]= "/ImagesHelicopterHit/HelicopterRtoLAHit2.png";
		helicopterBlastImageFilenamesRtoL[0][2]= "/ImagesHelicopterHit/HelicopterRtoLAHit3.png";
		helicopterBlastImageFilenamesRtoL[0][3]= "/ImagesHelicopterHit/HelicopterRtoLAHit4.png";
		
		helicopterLtoRHitImages= new Image[1][4];
		helicopterRtoLHitImages= new Image[1][4];
		
		for (int index= 0; index< 4; index++) {
			Image aImageIcon= new Image((helicopterBlastImageFilenamesLtoR[0][index]));
			helicopterLtoRHitImages[0][index]= aImageIcon;
			
			aImageIcon= new Image(helicopterBlastImageFilenamesRtoL[0][index]);
			helicopterRtoLHitImages[0][index]= aImageIcon;
		}
		
		return true;
	}
	

	
	public boolean isHelicopterHit() {
		
		return helicopterHit;
	}
	
	public boolean isHelicopterDestroyed() {
		
		return helicopterDestroyed;
	}
	
	// Must be called after hit() is called NOT BEFORE
	public void stopAudio()
	{
		if (GameAction.activeHelicopterCount== 0) {
			GameAction.helicopterMediaPlayer.stop();
			GameAction.helicopterMediaPlayer.dispose();
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
					GameAction.activeHelicopterCount--;
					stopAudio();
					
				} else
					xloc+=movementInc;
				
			} 
			else  // Move R to L
			{
				if (xloc<= 0) {
					
					// End of the run for this airplane.
				    // Somehow flag it for destruction.
					
					aliveStatus= false;
					GameAction.activeHelicopterCount--;
					stopAudio();
					
				}
				else
					xloc-=movementInc;
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
			aRectangle= new Rectangle(xloc, yloc, helicopterLtoRImageA.getWidth(), helicopterLtoRImageA.getHeight());
		}
		else {
			aRectangle= new Rectangle(xloc, yloc, helicopterRtoLImageA.getWidth(), helicopterRtoLImageA.getHeight());
		}
		
		return aRectangle;
		
	}
	
    Runnable animateHelicopterExplodingTask = new Runnable(){
    	private int numTimesExpired;

		public void run() {
			Thread.currentThread().setName("animate exploding SeaSprite");
//    		System.out.println("Hello World"); 
   
	    		switch (numTimesExpired) {
	    		case 0:   
	    			if (direction== Direction.L_TO_R)
	    				helicopterLtoRImageA= helicopterLtoRHitImages[0][0];
	    			else
	    				helicopterRtoLImageA= helicopterRtoLHitImages[0][0];
	    			
	    			numTimesExpired++;
	    			break;
	    			
	    		case 1:
	    			if (direction== Direction.L_TO_R)
	    				helicopterLtoRImageA= helicopterLtoRHitImages[0][1];
	    			else
	    				helicopterRtoLImageA= helicopterRtoLHitImages[0][1];
	    	
	    			numTimesExpired++;
	    			break;
	    			
	    		case 2:
	    			if (direction== Direction.L_TO_R)
	    				helicopterLtoRImageA= helicopterLtoRHitImages[0][2];
	    			else
	    				helicopterRtoLImageA= helicopterRtoLHitImages[0][2];
	    			
	    			numTimesExpired++;
	    			break;
	    			
	    		case 3:
	    			if (direction== Direction.L_TO_R)
	    				helicopterLtoRImageA= helicopterLtoRHitImages[0][3];
	    			else
	    				helicopterRtoLImageA= helicopterRtoLHitImages[0][3];
	    				
	    			numTimesExpired++;
	    			
	    			MsgBubble aMsgBubble= new MsgBubble(Integer.toString(getPoints()), gc, xloc, yloc, GameAction.MSG_BUBBLE_DISPLAY_MILLIS );
					GameAction.drawMsgBubbleList.add(aMsgBubble);
	    			break;
	    			
	    		case 4:
	    			// Replace with display of Points in a separate DrawList
//	    			helicopterLtoRImageA= Utilities.modifyImage(helicopterLtoRHitImages[0][3], 400);
	    			numTimesExpired++;
	    			break;
	    			
	    		default:
					helicopterDestroyed= true;
					
					// Cancel the Timer
					scheduledFuture.cancel(true);
					
					explosionTimerScheduled= false;
					
					aliveStatus= false;
					
					GameAction.activeHelicopterCount--;
//					soundPlayer.getHelicopterInFlightClip().stop();
					stopAudio();
	    			break;
					
	    		}   		
    	}
    };
	
	
	// REDO THIS WITH TIMERS!
	public Image getImage() {

		if (helicopterHit== true) {
			
			if (direction== Direction.L_TO_R) {

				if (explosionTimerScheduled== false) {
					scheduledFuture =
							scheduledExecutorService.scheduleAtFixedRate(animateHelicopterExplodingTask, 2, 70, TimeUnit.MILLISECONDS);

					explosionTimerScheduled= true;
				}
				
				return helicopterLtoRImageA;
				
			}
			else
			{
				if (explosionTimerScheduled== false) {
					scheduledFuture =
							scheduledExecutorService.scheduleAtFixedRate(animateHelicopterExplodingTask, 2, 70, TimeUnit.MILLISECONDS);

					explosionTimerScheduled= true;
				}

				return helicopterRtoLImageA;
			}


		}  // End Hit
		else
		{
			if (direction== Direction.L_TO_R) {
				if (currentImage== helicopterLtoRImageA)
				{
					currentImage= helicopterLtoRImageB;
				}
				else if (currentImage== helicopterLtoRImageB)
					currentImage= helicopterLtoRImageA;

//				System.out.println("Heli xcoord: " + xloc);
//				System.out.println("bombReleaseCoordIndexL1: " + bombReleaseCoordIndex);

				
				// Appears to cause a compiler bug
				if ((SeaWolf2.gameOver== false) && (bombReleaseCoordIndex<3) && (xloc== heliBombReleaseCoordsLtoR[bombReleaseCoordIndex]) )
				{
					SeaWolf2.tBombCount++;
					
					if (SeaWolf2.tBombCount < 18)   {
						
						ParachutedBomb aParachutedBomb= new ParachutedBomb(direction, xloc);
						GameAction.drawParachutedBombList.add(aParachutedBomb);
						bombReleaseCoordIndex++;
						
						if (bombReleaseCoordIndex== 1 && jawsClipPlaying== false) 
						{
							jukeBox.getCruiseMissileTravelClip().play();
							
							jawsClipPlaying= true;
							
						}
					}
				}
			}
			else
			{
				if (currentImage== helicopterRtoLImageA)
				{
					currentImage= helicopterRtoLImageB;
				}
				else if (currentImage== helicopterRtoLImageB)
					currentImage= helicopterRtoLImageA;

//				System.out.println("Heli xcoord: " + xloc);

//				System.out.println("bombReleaseCoordIndexL2: " + bombReleaseCoordIndex);
				// NOTE:  ADD THIS CODE BACK IN!!
				if ((SeaWolf2.gameOver== false) && (bombReleaseCoordIndex<3) && (xloc== bombReleaseCoordsRtoL[bombReleaseCoordIndex]))
				{
					SeaWolf2.tBombCount++;
					
					if (SeaWolf2.tBombCount < 18)   {
						
						ParachutedBomb aParachutedBomb= new ParachutedBomb(direction, xloc);
	//					DrawingPanel.drawParachutedBombList.add(new ParachutedBomb(soundPlayer, direction, xloc));
						GameAction.drawParachutedBombList.add(aParachutedBomb);
						bombReleaseCoordIndex++;
						
						if (bombReleaseCoordIndex== 1 && jawsClipPlaying== false) 
						{
							jukeBox.getCruiseMissileTravelClip().play();
							
							jawsClipPlaying= true;
							
						}
					}
				}
			}

			return currentImage;
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
		helicopterHit= true;
		
//		numHelicopters--;
	}
	
	private void genBombReleaseCoords()
	{
//		heliBombReleaseCoordsLtoR[0]= 238; // 444
//		heliBombReleaseCoordsLtoR[1]= 282;
//		heliBombReleaseCoordsLtoR[2]= 330;
		
		heliBombReleaseCoordsLtoR[0]= 60; // 444
		heliBombReleaseCoordsLtoR[1]= 453;
		heliBombReleaseCoordsLtoR[2]= 708;
		
		
		bombReleaseCoordsRtoL[0]= 660;
		bombReleaseCoordsRtoL[1]= 330;
		bombReleaseCoordsRtoL[2]= 140;
	}
	
	private static URL getImageURL(String imageFilename) {
		URL aURL= null;
		
//		ClassLoader cl = this.getClass().getClassLoader();
		
		aURL= SeaSprite.class.getResource(imageFilename);
   
	     return aURL;
	}

	@Override
	// Never used
	public boolean isHitByBullet() {
		// TODO Auto-generated method stub
		return helicopterHit;
	}
}
