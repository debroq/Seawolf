package Game;


import javafx.scene.shape.Rectangle;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import javafx.scene.canvas.GraphicsContext;



public class SeaWolfSubmarine {
	
	private static int xLoc;
	private static int yLoc;
	private int xOffset;
	private static int gunImageIndexSelected= 1;
	
	private static int gun1of5ImageWidth;
	private static int gun1of5ImageHeight;
	private static int gunLevelDamage;
	
	private List<Image> nozzleActiveImages;
	private List<Image> nozzleInactiveImages;
	private static List<Boolean> nozzleHit;
	private javafx.scene.image.Image submarineImageLtoRA;
	private javafx.scene.image.Image submarineImageLtoRB;
	private javafx.scene.image.Image submarineImageRtoLA;
	private Image submarineImageRtoLB;
	

	
	private static Image[] submarineLtoRHitImages;
	private static Image[] submarineRtoLHitImages;
	
	private static String[] submarineBlastImageFilenamesLtoR;
	private static String[] submarineBlastImageFilenamesRtoL;
	
	private int numTimesShownDamaged;
	private Boolean submarineHit;
	private static int[] nozzleOffsets;
	
	private javafx.scene.image.Image currentImage;	
	
	public enum Direction { L_to_R, R_to_L};
	
	private Direction subDirection;
	
	private ScheduledFuture scheduleSubmarineExplodingTask;
	private ScheduledFuture scheduledDamageDialCycle;
	private ScheduledExecutorService scheduledExecutorService;
	
	public static boolean explosionTimerScheduled= false;
	public static boolean shipSunk= false;
	public static boolean updateNumShipsLeftDisplay= false;
	
	public static int numTimesAnimExplExpired;

	
	private MediaPlayer subSonarMediaPlayer;
	private JukeBox jukeBox;
	
	private int STARTX= 100;
	private int STARTY= 500;
	
	private AAGun aAAGun;
	protected ScheduledFuture scheduledResetSub;
	
	private static boolean spinDamageGaugeSetupTasks= false;
	private static boolean animateSubExplodingSetupTasks= false;
	public static boolean inProcessAbsorbingHit= false;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	

	public SeaWolfSubmarine(MediaPlayer subSonarMediaPlayer, JukeBox jukeBox) {
		
		Image aGunImage;
		
		Image aNozzleImage;
		xLoc= STARTX; // 375;
		yLoc= STARTY; // 494;
		xOffset= 0;
		

		nozzleActiveImages= new ArrayList<Image>(3);
		nozzleInactiveImages= new ArrayList<Image>(3);
		nozzleHit= new ArrayList<Boolean>(3);

		
		for (int index= 0; index< 3; index++)
			nozzleHit.add(index, false);
		
		
		
		Image imageIcon = new Image("/ImagesSeaWolfSubmarine/Gun2of5x.png");
//		aGunImage= Utilities.toVolatileImage(imageIcon.getImage());
		aGunImage= imageIcon;
		gun1of5ImageWidth= (int) aGunImage.getWidth();
		gun1of5ImageHeight= (int) aGunImage.getHeight();	
		
		
		imageIcon= new Image("/ImagesSeaWolfSubmarine/Nozzle2active.png");
		aNozzleImage= imageIcon;
		nozzleActiveImages.add(aNozzleImage);
		
		imageIcon= new Image("/ImagesSeaWolfSubmarine/Nozzle3active.png");
		aNozzleImage= imageIcon;
		nozzleActiveImages.add(aNozzleImage);
		
		imageIcon= new Image("/ImagesSeaWolfSubmarine/Nozzle4active.png");
		aNozzleImage= imageIcon;
		nozzleActiveImages.add(aNozzleImage);
	
		
		imageIcon= new Image("/ImagesSeaWolfSubmarine/Nozzle2InActiveCut.png");
		aNozzleImage= imageIcon;
		nozzleInactiveImages.add(aNozzleImage);
		
		imageIcon= new Image("/ImagesSeaWolfSubmarine/Nozzle3InActiveCut.png");
		aNozzleImage= imageIcon;
		nozzleInactiveImages.add(aNozzleImage);
		
		imageIcon= new Image("/ImagesSeaWolfSubmarine/Nozzle4InActiveCut.png");
		aNozzleImage= imageIcon;
		nozzleInactiveImages.add(aNozzleImage);
		
				

		imageIcon= new Image("/ImagesSeaWolfSubmarine/NewSubLtoRA.png");
		submarineImageLtoRA= imageIcon;
		
		imageIcon= new Image("/ImagesSeaWolfSubmarine/NewSubLtoRB.png");
		submarineImageLtoRB= imageIcon;
		
		imageIcon= new Image("/ImagesSeaWolfSubmarine/NewSubRtoLA.png");
		submarineImageRtoLA= imageIcon;
		
		imageIcon= new Image("/ImagesSeaWolfSubmarine/NewSubRtoLB.png");
		submarineImageRtoLB= imageIcon;
		
		submarineBlastImageFilenamesLtoR= new String[5];
		submarineBlastImageFilenamesRtoL= new String[5];
		
		submarineBlastImageFilenamesLtoR[0]= "/ImagesSeaWolfSubmarineHit/NewSubLtoRAHit1.png";
		submarineBlastImageFilenamesLtoR[1]= "/ImagesSeaWolfSubmarineHit/NewSubLtoRAHit2.png";
		submarineBlastImageFilenamesLtoR[2]= "/ImagesSeaWolfSubmarineHit/NewSubLtoRAHit3.png";
		submarineBlastImageFilenamesLtoR[3]= "/ImagesSeaWolfSubmarineHit/NewSubLtoRAHit4.png";
		submarineBlastImageFilenamesLtoR[4]= "/ImagesSeaWolfSubmarineHit/NewSubLtoRAHit5.png";
		
		submarineBlastImageFilenamesRtoL[0]= "/ImagesSeaWolfSubmarineHit/NewSubRtoLAHit1.png";
		submarineBlastImageFilenamesRtoL[1]= "/ImagesSeaWolfSubmarineHit/NewSubRtoLAHit2.png";
		submarineBlastImageFilenamesRtoL[2]= "/ImagesSeaWolfSubmarineHit/NewSubRtoLAHit3.png";
		submarineBlastImageFilenamesRtoL[3]= "/ImagesSeaWolfSubmarineHit/NewSubRtoLAHit4.png";
		submarineBlastImageFilenamesRtoL[4]= "/ImagesSeaWolfSubmarineHit/NewSubRtoLAHit5.png";
				
		submarineLtoRHitImages= new Image[5];
		submarineRtoLHitImages= new Image[5];
		
		for (int index= 0; index< 5; index++) {
			Image aImageIcon= new Image(submarineBlastImageFilenamesLtoR[index]);
			submarineLtoRHitImages[index]= recolorAsRed(aImageIcon);
			
			aImageIcon= new Image(submarineBlastImageFilenamesRtoL[index]);
			submarineRtoLHitImages[index]= recolorAsRed(aImageIcon);
		}
		
      
        submarineHit= false;
		
		nozzleOffsets= new int[3];
		nozzleOffsets[0]= 11; // 61;  // 49
		nozzleOffsets[1]= 28; // 74;
		nozzleOffsets[2]= 39; // 86;  // 96
		
		// Experiment with ExecutorService
		scheduledExecutorService =
		        Executors.newScheduledThreadPool(5);
		
		subDirection= Direction.L_to_R;
		
		currentImage= submarineImageLtoRA;
		
//		soundPlayer= soundPlayer;
		
//		theMediaPlayer= soundPlayer.getBackgrooundClip();
//		theMediaPlayer= GameAction.subSonarMediaPlayer();
		
		this.subSonarMediaPlayer= subSonarMediaPlayer;
		this.subSonarMediaPlayer.play();
		
		this.jukeBox= jukeBox;
		
		aAAGun= new AAGun(jukeBox);
	}
	
	public AAGun getAAGun() {
		
		return aAAGun;
	}
	
	public Direction getDirection() {
		return subDirection;
	}
	
	public void render(GraphicsContext gc, String direction)
	{
		if (direction== "moveLeft") {
			subDirection= Direction.R_to_L;
			if (xLoc > 0)
				xLoc--;
		} else if (direction== "moveRight"){
			subDirection= Direction.L_to_R;
			if (xLoc < SeaWolf2.CANVAS_WIDTH - 95)  // 1105
				xLoc++;
		} else {
			int x= 7;
			int y= 5;
			int z= 3;
			//			System.out.println("HELLLLO");
		}
		gc.drawImage(getGunBaseImageNEW(), getX(), getY());

		if (SeaWolf2.gameOver== false || SeaWolfSubmarine.inProcessAbsorbingHit== false || SeaWolfSubmarine.explosionTimerScheduled== false) {  //Add - and the ship is not exploding!!!!

			if (shipSunk== false) {  // If ship in process of sinking, don't draw this stuff
				// Draw the launch tubes
				if (aAAGun.getGunActivationStatus()== false) {
					for (int index= 0; index< 3; index++) { 
//						if (getNozzleHit(index)== false) {
							if (getGunImageIndexSelected()== index)
							{
								if (subDirection== SeaWolfSubmarine.Direction.L_to_R) 
									gc.drawImage(getAnActiveNozzle(index), getX() + getNozzleOffSet(index), getY() + 7);
								else
									gc.drawImage(getAnActiveNozzle(index), getX() + getNozzleOffSet(index) + 31, getY() + 7);

							}
							else
							{
								if (subDirection== SeaWolfSubmarine.Direction.L_to_R)
									gc.drawImage(getAnInactiveNozzle(index), getX() + getNozzleOffSet(index), getY() + 7);
								else
									gc.drawImage(getAnInactiveNozzle(index), getX() + getNozzleOffSet(index) + 31, getY() + 7);
							}
//						}				
					}
				}

				// Draw the anti aircraft gun

				if (aAAGun.getGunActivationStatus()== false) {

					// Draw the AA Gun in the deactivated state.
					// Draw the submarine AA gun
					if (subDirection== SeaWolfSubmarine.Direction.L_to_R)
					{
						gc.drawImage(aAAGun.getAAImageDeActive(Game.AAGun.Direction.L_to_R), getX() + 68, getY() + 4);
					}
					else
					{
						gc.drawImage(aAAGun.getAAImageDeActive(Game.AAGun.Direction.R_to_L), getX() + 4, getY() + 4);
					}

				}
				else
				{

					// Draw the submarine AA gun
					if (subDirection== SeaWolfSubmarine.Direction.L_to_R)
					{
						gc.drawImage(aAAGun.getAAImage(Game.AAGun.Direction.L_to_R), getX() + 68, getY() + 4);
					}
					else
					{
						gc.drawImage(aAAGun.getAAImage(Game.AAGun.Direction.R_to_L), getX() + 4, getY() + 4);
					}

				}
			}
		}
	}
	
	public static int getNozzleOffSet(int index)
	{
		return nozzleOffsets[index];
	}
	
//	public static Boolean getNozzleHit(int index)
//	{
//		return nozzleHit.get(index);
//	}
	
//	public static void setNozzleHit(int index)
//	{
//		nozzleHit.set(index, true);
//	}
	
	public void reset()
	{
		debugOut("**Inside reset!**");
		xLoc= STARTX;
		xOffset= 0;
		yLoc= STARTY; // 454;
		
		
		gunImageIndexSelected= 1;
		
		gunLevelDamage= 0;
		
		
		numTimesShownDamaged= 0;
		System.out.println("Setting SubmarineHit to False A");
		submarineHit= false;
		debugOut("Setting subDamageLevelCounter to ZERO");
		SeaWolf2.subDamageLevelCounter= 0;	
		GameAction.spinDamageGaugeValue= 0;
		
//		subSonarMediaPlayer.stop();

		subSonarMediaPlayer.play();
		numTimesAnimExplExpired= 0;
		
		shipSunk= false;
		
		spinDamageGaugeSetupTasks= false;
		animateSubExplodingSetupTasks= false;
		
	}
	
	public void cycleActiveTorpedoTubeLeft() {
		
		gunImageIndexSelected= gunImageIndexSelected - 1;
		
		if (gunImageIndexSelected== -1) 
			gunImageIndexSelected= 2;
		
		while (nozzleHit.get(gunImageIndexSelected)== true) {
			gunImageIndexSelected= gunImageIndexSelected - 1;
			if (gunImageIndexSelected== -1)
				gunImageIndexSelected= 2;
		}
	}
	
	public void cycleActiveTorpedoTubeRight() {
		
		gunImageIndexSelected= (gunImageIndexSelected + 1) % 3;
		
		while (nozzleHit.get(gunImageIndexSelected)== true) {
			gunImageIndexSelected= (gunImageIndexSelected + 1) % 3;
		}
		
	}
	
	
	public static int getX() {
		return xLoc;
	}
	
	public static int getXMidPoint() {
		return xLoc + (gun1of5ImageWidth/2);
	}
	
	public static int getY() {
		return yLoc;
	}
	
	Runnable resetSubmarineTask= new Runnable() {
		
		public void run() {
			
			scheduledResetSub.cancel(true);
			
			debugOut("inside resetSubmarineTask");
			
			// Reset the Submarine
			reset();
			
//			numTimesAnimExplExpired= 0;
			
			
//			scheduledDamageDialCycle.cancel(false);
			
//			SeaWolf2.damageGauge.setValue(0);
//			SeaWolf2.subDamageLevelCounter= 0;
			
			// Stop damage dial from cycling.
			
		}
	};
	
	// Rock the gauge back and forth
	Runnable spinDamageDialTask = new Runnable(){
		

		public void run() {
			
			debugOut("inside spinDamageDialTask1");
			
			scheduledDamageDialCycle.cancel(false);
			
			for (int k=  0; k< 1; k++)   {
			
				for (int i = 0; i<= 100; i+=1) {
				
					GameAction.spinDamageGaugeValue= i;
					
					debugOut("Setting gauge value to: " + i);
					
//					SeaWolf2.damageGauge.setValue(i);
									
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
								
				}
				
				if (SeaWolf2.numSubs!= 0) {
					for (int i = 99; i>= 0; i-=1) {
						
						GameAction.spinDamageGaugeValue= i;
						
						debugOut("Setting gauge value to: " + i);
//						SeaWolf2.damageGauge.setValue(i);
											
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}				
					
					}
				}
			}
			
			debugOut("inside spinDamageDialTask2");
			
//			scheduledDamageDialCycle.cancel(false);
			
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
//			if (SeaWolf2.numSubs!= 0) {
//				SeaWolf2.damageGauge.setValue(0);
//				SeaWolf2.subDamageLevelCounter= 0;
//				SeaWolf2.resetGaugeNeedle= false;
//				shipSunk= false;
//			}
			
//			scheduledDamageDialCycle.cancel(false);
			
		}
	};
	
	Runnable animateSubmarineExplodingTask2 = new Runnable(){
		
		public void run() {
			
			numTimesAnimExplExpired++;
			
			debugOut("animateSubmarineExplodingTask2 value: " + numTimesAnimExplExpired);
			
		}
	};
	
	public javafx.scene.image.Image getGunBaseImageNEW() {	

		if (SeaWolf2.gameOver== true) {
			return currentImage;
		}

		if (SeaWolf2.subDamageLevelCounter== 4) {
			debugOut("SublevelCounter is 4");
			// This Sub is Done
			// avoid processing anymore hits on it while it is exploding
			if (explosionTimerScheduled== false) {

				if (animateSubExplodingSetupTasks== false) {
					debugOut("SHIP SUNK!!! ****Scheduling Animate Sub Exploding Task*****");
					
					shipSunk= true;

					subSonarMediaPlayer.stop();
					subSonarMediaPlayer.seek(subSonarMediaPlayer.getStartTime());

					scheduleSubmarineExplodingTask =
							scheduledExecutorService.scheduleAtFixedRate(animateSubmarineExplodingTask2, 50, 1100, TimeUnit.MILLISECONDS);

					explosionTimerScheduled= true;

					animateSubExplodingSetupTasks= true;				
				}

			} else {

				switch (numTimesAnimExplExpired) {
				case 0: 

					// Todo: Disable all Submarine Controls

					if (spinDamageGaugeSetupTasks== false) {
						MediaPlayer mediaPlayer= jukeBox.getSubmarineSunkClip();
						mediaPlayer.play();

						debugOut("case 0: explosionTimerScheduled: about to schedule spinDamageDialTask");

						// Start damage dial cycling.
						scheduledDamageDialCycle =
								scheduledExecutorService.scheduleAtFixedRate(spinDamageDialTask, 8, 5000, TimeUnit.MILLISECONDS);
						
						// We do this here, so change in numSub count is picked up by spinDamageDialTask
						SeaWolf2.numSubs--;

						spinDamageGaugeSetupTasks= true;
					}

					if (subDirection== Direction.L_to_R) 
						currentImage= submarineLtoRHitImages[0];
					else
						currentImage= submarineRtoLHitImages[0];
					break;

				case 1:
					debugOut("case 1: explosionTimerScheduled");
					if (subDirection== Direction.L_to_R) 
						currentImage= submarineLtoRHitImages[1];
					else
						currentImage= submarineRtoLHitImages[1];
					break;

				case 2:
					debugOut("case 2: explosionTimerScheduled");
					if (subDirection== Direction.L_to_R) 
						currentImage= submarineLtoRHitImages[2];
					else
						currentImage= submarineRtoLHitImages[2];
					break;

				case 3:
					debugOut("case 3: explosionTimerScheduled");
					if (subDirection== Direction.L_to_R) 
						currentImage= submarineLtoRHitImages[3];
					else
						currentImage= submarineRtoLHitImages[3];
					break;

				case 4:
					debugOut("case 4: explosionTimerScheduled");
					if (subDirection== Direction.L_to_R)
						currentImage= submarineLtoRHitImages[4];
					else
						currentImage= submarineRtoLHitImages[4];
					break;	

				case 5:
					debugOut("explosionTimerScheduled case 5");
					// Cancel the Timer
					scheduleSubmarineExplodingTask.cancel(false);

					debugOut("animateSubmarineExplodingTask: about to cancel scheduledDamageDialCycle case 5");

					debugOut("case 5: Setting explosionTimerScheduled to false");
					explosionTimerScheduled= false;

					updateNumShipsLeftDisplay= true;

					numTimesAnimExplExpired= 6;

					spinDamageGaugeSetupTasks= false;


					// *** Setup Timer to load any remaining sub --> Change code to now use the timer to perform a reset other game will hang for 3 seconds !!!! ***
					if (SeaWolf2.numSubs > 0)
					{
						// Flush the sub queues.
						//		    				Screen.subLaunchWeaponsQueue.clear();
						//		    				Screen.subControlsQueue.clear();

						debugOut("case 5: about to call resetSubmarineTask");

						scheduledResetSub =
								scheduledExecutorService.scheduleAtFixedRate(resetSubmarineTask, 2, 2000, TimeUnit.MILLISECONDS);

					} else {
						// Game Over
						debugOut("I want to Exit");

						SeaWolf2.gameOver= true;

					}
					break;
					
				case 6:
					// Do nothing while awaiting resetSubTask to fire
					debugOut("case 6: stall awaiting reset to reload");
					break;

				}

				return currentImage;
			}	
		} else {
			// Change this to be a timeline??
			if (inProcessAbsorbingHit== true) {  // While inProcessAbsorbin hit, ignore all other collisions.

				debugOut("inProcessAbsorbingHit is true");

				if (numTimesShownDamaged < 10)
				{
					debugOut("numTimesShownDamged lt 10");
					if ((numTimesShownDamaged % 2)== 0 )
					{
						// Recolor Image as Red
						currentImage= recolorAsRed(currentImage);

					}
					else
					{
						// Recolor Image as Black
						currentImage= recolorAsBlack(currentImage);
					}

					numTimesShownDamaged++;
				}
				else
				{
					debugOut("numTimesShownDamaged ge 10");
					numTimesShownDamaged= 0;
					inProcessAbsorbingHit= false;

					currentImage= recolorAsBlack(currentImage);

				}
				submarineHit= false;
			}
			else if (submarineHit== true) {
				
				debugOut("submarineHit is true");
				inProcessAbsorbingHit= true;
				// Setup code to make Sub Blink
				// Avoid processing anymore hits while it is exploding.
				submarineHit= false;
				
			} else if (submarineHit== false) {
				// ************************************************************
				// Flip between Sub patterns to make it appear prop is spinning
				// ************************************************************
				debugOut("submarineHit is false");
				if (subDirection== Direction.L_to_R)
				{
					if (currentImage== submarineImageLtoRA)
					{
						currentImage= submarineImageLtoRB;
					}
					else
						currentImage= submarineImageLtoRA;
				}
				else
				{
					if (currentImage== submarineImageRtoLA)
					{
						currentImage= submarineImageRtoLB;
					}
					else
						currentImage= submarineImageRtoLA;
				}
			} else {
				// Shouldn't be here!
				System.exit(0);
			}
			
		}
		return currentImage;
	}
	
    
	// ****************************************
	// Change this to be strictly timer driven.
	// ****************************************
	// Redo this whole method to be Timer Driven!  To dependent on CPU!
//	public javafx.scene.image.Image getGunBaseImageOLD() {	
//
//		if (SeaWolf2.gameOver== true) {
//			return currentImage;
//		}
//
//		if (explosionTimerScheduled== true) {
//
//			debugOut("entered explosiionTimerScheduled");
//			
//			subSonarMediaPlayer.stop();
//			subSonarMediaPlayer.seek(subSonarMediaPlayer.getStartTime());
//
//			switch (numTimesAnimExplExpired) {
//			//		    		case 0:
//			//		    			break;
//			case 0: 
//				
//				// Todo: Disable all Submarine Controls
//				
//				if (doOnce== false) {
//					MediaPlayer mediaPlayer= jukeBox.getSubmarineSunkClip();
//					mediaPlayer.play();
//	
//					debugOut("explosionTimerScheduled: about to schedule spinDamageDialTask");
//	
//					// Start damage dial cycling.
//					scheduledDamageDialCycle =
//							scheduledExecutorService.scheduleAtFixedRate(spinDamageDialTask, 2, 5000, TimeUnit.MILLISECONDS);
//	
//					doOnce= true;
//				}
//				
//				if (subDirection== Direction.L_to_R) 
//					currentImage= submarineLtoRHitImages[0];
//				else
//					currentImage= submarineRtoLHitImages[0];
//				break;
//
//			case 1:
//				debugOut("explosionTimerScheduled case 1");
//				if (subDirection== Direction.L_to_R) 
//					currentImage= submarineLtoRHitImages[1];
//				else
//					currentImage= submarineRtoLHitImages[1];
//				break;
//
//			case 2:
//				debugOut("explosionTimerScheduled case 2");
//				if (subDirection== Direction.L_to_R) 
//					currentImage= submarineLtoRHitImages[2];
//				else
//					currentImage= submarineRtoLHitImages[2];
//				break;
//
//			case 3:
//				debugOut("explosionTimerScheduled case 3");
//				if (subDirection== Direction.L_to_R) 
//					currentImage= submarineLtoRHitImages[3];
//				else
//					currentImage= submarineRtoLHitImages[3];
//				break;
//
//			case 4:
//				debugOut("explosionTimerScheduled case 4");
//				if (subDirection== Direction.L_to_R)
//					currentImage= submarineLtoRHitImages[4];
//				else
//					currentImage= submarineRtoLHitImages[4];
//				break;	
//
//			case 5:
//				debugOut("explosionTimerScheduled case 5");
//				// Cancel the Timer
//				scheduleSubmarineExplodingTask.cancel(false);
//
//				debugOut("animateSubmarineExplodingTask: about to cancel scheduledDamageDialCycle");
//
//				debugOut("Setting explosionTimerScheduled to false");
//				explosionTimerScheduled= false;
//
//				SeaWolf2.numSubs--;
//
//				updateNumShipsLeftDisplay= true;
//
//				numTimesAnimExplExpired= 6;
//				
//				doOnce= false;
//				doOnceX= false;
//
//
//				// *** Setup Timer to load any remaining sub --> Change code to now use the timer to perform a reset other game will hang for 3 seconds !!!! ***
//				if (SeaWolf2.numSubs > 0)
//				{
//					// Flush the sub queues.
//					//		    				Screen.subLaunchWeaponsQueue.clear();
//					//		    				Screen.subControlsQueue.clear();
//
//					debugOut("case 5: about to call resetSubmarineTask");
//
//					scheduledResetSub =
//							scheduledExecutorService.scheduleAtFixedRate(resetSubmarineTask, 2, 2000, TimeUnit.MILLISECONDS);
//
//				} else {
//					// Game Over
//					debugOut("I want to Exit");
//
//					SeaWolf2.gameOver= true;
//
//				}
//				break;
//				
//			case 6:
//				// Do nothing - await load of the reset sub!
//				break;
//
//			}
//
//			return currentImage;
//		} // End
//		// End - do animate Exploding
//
//		if (submarineHit== false) {
//			if (subDirection== Direction.L_to_R)
//			{
//				if (currentImage== submarineImageLtoRA)
//				{
//					currentImage= submarineImageLtoRB;
//				}
//				else
//					currentImage= submarineImageLtoRA;
//			}
//			else
//			{
//				if (currentImage== submarineImageRtoLA)
//				{
//					currentImage= submarineImageRtoLB;
//				}
//				else
//					currentImage= submarineImageRtoLA;
//			}
//		}
//		else if (submarineHit== true) {
//
//			// ****************************************************************
//			// Note: Submarine is considered hit until it has finished blinking.
//			// ****************************************************************
//
//			debugOut("submarineHit is True. Submarine Damage Counter: " + SeaWolf2.subDamageLevelCounter);
//
//			if (SeaWolf2.subDamageLevelCounter <= 3) {
//				debugOut("subDamageLevelCounter less than or equal to three");
//
//				if (numTimesShownDamaged < 10)
//				{
//					debugOut("numTimesShownDamged le 10");
//					if ((numTimesShownDamaged % 2)== 0 )
//					{
//						// Recolor Image as Red
//						currentImage= recolorAsRed(currentImage);
//
//					}
//					else
//					{
//						// Recolor Image as Black
//						currentImage= recolorAsBlack(currentImage);
//					}
//
//					numTimesShownDamaged++;
//				}
//				else
//				{
//					// ****************************************************************
//					// This might be getting called even though there are no subs left!
//					// ****************************************************************
//					debugOut("numTimesShownDamaged ge 10");
//					submarineHit= false;   // <== need to shutoff code that marks sub has been hit while a sub explosion is taking place
//					numTimesShownDamaged= 0;
//
//				}
//
//			}
//			else {
//				debugOut("SubLevelCounter is: " + SeaWolf2.subDamageLevelCounter);
//				if (SeaWolf2.subDamageLevelCounter == 4) {
//					// Start animation to show submarine sinking.
//					if (explosionTimerScheduled== false) {
//
//						shipSunk= true;
//
//						debugOut("about to call: animateSubmarineExplodingTask2");
//						
//						SeaWolf2.numSubs--;
//
//						scheduleSubmarineExplodingTask =
//								scheduledExecutorService.scheduleAtFixedRate(animateSubmarineExplodingTask2, 50, 1100, TimeUnit.MILLISECONDS);
//
//						explosionTimerScheduled= true;
//
//					}
//				}
//			}
//		}	
//
//		return currentImage;
//	}
	
	
	public void debugOut(String msg) {
		Date date= new Date();
		Timestamp timeStamp= new Timestamp(date.getTime());
		System.out.println(timeStamp + " Msg: " + msg);
	}
	
	
	public Image getAnActiveNozzle(int index) {
		return nozzleActiveImages.get(index);
	}
	

	public Image getAnInactiveNozzle(int index) {	
		return nozzleInactiveImages.get(index);
	}
	

	public int getGunImageIndexSelected() {
		return gunImageIndexSelected;
	}
	
//	public void keyPressed(KeyEvent e) {
//		
//		int key= e.getKeyCode();
//		
//		switch (key) {
//		case KeyEvent.VK_LEFT:
//			System.out.println("cycle left");
//			break;
//		case KeyEvent.VK_RIGHT:
//			System.out.println("cycle right");
//			break;
//		case KeyEvent.VK_SPACE:
//			System.out.println("fire");
//			break;
//		default:
//			System.out.println("Unknown key");
//		}
//	}
	
	public void moveLeft() {
		
		if (subDirection== Direction.L_to_R) {
			
//			if (nozzleHit.get(2)== false && nozzleHit.get(0)== true) {
//				
//				nozzleHit.set(2, true);
//				nozzleHit.set(0, false);
//				
//				gunImageIndexSelected= 0;
//				
//			} else if (nozzleHit.get(0)== false && nozzleHit.get(2)== true) {
//				
//				nozzleHit.set(0,  true);
//				nozzleHit.set(2, false);
//				
//				gunImageIndexSelected= 2;
//				
//			} else if (nozzleHit.get(0)== false && nozzleHit.get(2)== false) {
//				 
//				if (gunImageIndexSelected== 0)
//					gunImageIndexSelected= 2;
//				else if (gunImageIndexSelected== 2)
//					gunImageIndexSelected= 0;
//			}
			
//			AAGun.setDirectionLeft();
		}
		
		subDirection= Direction.R_to_L;
		
		xLoc-=10;
		xOffset-=10;
		
//		DrawingPanel.logFile.println(Utilities.getTimeStamp() + ": moveLeft() - New xLoc is: " + xLoc);
	}
	
	public void moveRight() {
		
		if (subDirection== Direction.R_to_L) {

			
//			if (nozzleHit.get(2)== false && nozzleHit.get(0)== true) {
//				
//				nozzleHit.set(2, true);
//				nozzleHit.set(0, false);
//				
//				gunImageIndexSelected= 0;
//				
//			} else if (nozzleHit.get(0)== false && nozzleHit.get(2)== true) {
//				
//				nozzleHit.set(0,  true);
//				nozzleHit.set(2, false);
//				
//				gunImageIndexSelected= 2;
//				
//			} else if (nozzleHit.get(0)== false && nozzleHit.get(2)== false) {
//				 
//				if (gunImageIndexSelected== 0)
//					gunImageIndexSelected= 2;
//				else if (gunImageIndexSelected== 2)
//					gunImageIndexSelected= 0;
//			}
			
//			AAGun.setDirectionRight();
			
		}
		
		subDirection= Direction.L_to_R;
		
		xLoc+=10;
		xOffset+=10;
		
//		DrawingPanel.logFile.println(Utilities.getTimeStamp() + ": moveRight() - New xLoc is: " + xLoc);
	}
	
	
	public int getXOffset() {
		
		return xOffset;
	}
	
	public static Rectangle getBounds() {
		Rectangle aRectangle= null;
		
		switch (gunImageIndexSelected) {
		case 0:
			aRectangle= new Rectangle(xLoc, yLoc, gun1of5ImageWidth, gun1of5ImageHeight);
			break;
		case 1:
			aRectangle= new Rectangle(xLoc, yLoc, gun1of5ImageWidth, gun1of5ImageHeight);
			break;
		case 2:
			aRectangle= new Rectangle(xLoc, yLoc, gun1of5ImageWidth, gun1of5ImageHeight);
			break;
		}
		
		return aRectangle;
	}
	
	
	public int getWidth() {
		
		return gun1of5ImageWidth;
	}
	
	public int getHeight() {
		
		return gun1of5ImageHeight;
		
	}
	
	public static void gunHit()
	{
		// Make Gun turn red
		gunLevelDamage+=1;
	}
	
	public boolean isGunHit()
	{
		if (gunLevelDamage!= 0)
			return true;
		else
			return false;
	}
	
	public int getGunLevelDamage()
	{
		return gunLevelDamage;
	}
	
	public void torpedoGunHit()
	{
		submarineHit= true;
	}
	
	private URL getImageURL(String imageFilename) {
		URL aURL= null;
		
//		ClassLoader cl = this.getClass().getClassLoader();
		
		aURL= SeaWolfSubmarine.class.getResource(imageFilename);
   
	     return aURL;
	}

	public void launchTorpedo() {
		
		MediaPlayer mediaPlayer= jukeBox.getTorpedoLaunchClip();
		mediaPlayer.play();
		SeaWolf2.updateTorpedoTubePanel();
	}
	
	private Image recolorAsBlack(Image sourceImage)
	{
        
		PixelReader pixelReader = sourceImage.getPixelReader();
		int width= (int) sourceImage.getWidth();
		int height = (int) sourceImage.getHeight();
		
        
        //Copy from source to destination pixel by pixel
        WritableImage writableImage 
                = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
         
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                Color color = pixelReader.getColor(x, y);
//                if (color== Color.RED)
                if (color.getRed()!= 0)
                	pixelWriter.setColor(x, y, Color.BLACK);
            }
        }
        
        ImageView destImageView = new ImageView();
        destImageView.setImage(writableImage);
        
        Image img= destImageView.getImage();
            
        return img;
	}
	

	
	private Image recolorAsRed(Image sourceImage)
	{	
		
		PixelReader pixelReader = sourceImage.getPixelReader();
		int width= (int) sourceImage.getWidth();
		int height = (int) sourceImage.getHeight();
		
        
      //Copy from source to destination pixel by pixel
        WritableImage writableImage 
                = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
         
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                Color color = pixelReader.getColor(x, y);
                if (color.getRed()==0 && color.getBlue()== 0 && color.getGreen()== 0 && color.getOpacity()== 1)
                	pixelWriter.setColor(x, y, Color.RED);
            }
        }
        
        ImageView destImageView = new ImageView();
        destImageView.setImage(writableImage);
        
        Image img= destImageView.getImage();
            
        return img;
		 
	}
}