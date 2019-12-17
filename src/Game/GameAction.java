package Game;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;


import Aircraft.SeaSprite;
import Aircraft.SeaStallion;
import AntiSubWeapons.AntiShipBomb;
import AntiSubWeapons.Mine;
import AntiSubWeapons.ParachutedBomb;
import AntiSubWeapons.ParachutedMine;
import Aircraft.Airplane;
import Aircraft.MsgBubble;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GameAction {
	
	public static SeaWolfSubmarine seaWolfSubmarine;
	private Canvas canvas;
	public static MediaPlayer subSonarMediaPlayer;
	public static JukeBox jukeBox;
	public static MediaPlayer helicopterMediaPlayer;
	public static MediaPlayer airPlaneMediaPlayer;
	public static MediaPlayer explosionMediaPlayer;
	public static MediaPlayer aircraftHitMediaPlayer;
	public static MediaPlayer metalKlinkMediaPlayer;
	public static MediaPlayer mineHitMediaPlayer;
	public static MediaPlayer gameOverMediaPlayer;
	public static int numHelicopters= 0;
	
//	public static BlockingQueue<SubLaunchedWeapon> subLaunchWeaponsQueue;
	private BlockingQueue<String> subControlsQueue;
	
	private List<Torpedo> drawTorpedoList;
	private List<AABullet> drawAABulletList;
	public static List<Mine> drawMineList;
	public static List<SeaSprite> drawSeaSpriteList;
	public static List<ParachutedBomb> drawParachutedBombList;
	public static List<ParachutedMine> drawParachutedMineList;
	public static List<SeaStallion> drawSeaStallionList;
	public static List<MsgBubble> drawMsgBubbleList;
	public static List<Airplane> drawAirplaneList;
	public static List<AntiShipBomb> drawAntiShipBombList;
	
	
	public static boolean mineSoundWaveExplosionSequenceInProgress= false;
	
	public static StringBuffer resultsLine1= new StringBuffer();
	public static StringBuffer resultsLine2= new StringBuffer();
	public static StringBuffer resultsLine3= new StringBuffer();
	public static StringBuffer resultsLine4= new StringBuffer();
	public static StringBuffer resultsLine5= new StringBuffer();
	public static StringBuffer resultsLine6= new StringBuffer();
	public static StringBuffer resultsLine7= new StringBuffer();
	public static StringBuffer resultsLine8= new StringBuffer();
	public static StringBuffer resultsLine9= new StringBuffer();
	
	public final static int SEA_LANE_ROW_HEIGHT= 40;
	public final static int SEA_LANES_START_Y= 69;
	
	public final static int MINE_DISPLAY_ROW1X= 103;
	public final static int MINE_DISPLAY_ROW2X=	142;
	public final static int MINE_DISPLAY_ROW3X= 182; // 160;
	public final static int MINE_DISPLAY_ROW4X= 222; // 200;
	public final static int MINE_DISPLAY_ROW5X= 262; // 230;
	public final static int MINE_DISPLAY_ROW6X= 302; // 260;
	public final static int MINE_DISPLAY_ROW7X= 342; // 290;
	private final int MAX_NUMBER_OF_MINES= 21;  // 21;
	
	public final static int HELICOPTER_DISPLAY_ROW= 30;
	public final static int AIRPLANE_SECONDARY_DISPLAY_ROW= 26;
	public final static int AIRPLANE_DISPLAY_ROW= 10;
	
	public static Random randomGenerator;
	
	public final static int MSG_BUBBLE_DISPLAY_MILLIS= 2000;
	
	
	public static Timer createMineTimer; 
	public static Timer createHelicopterTimer;
	public static Timer createAirplaneTimer;
	public static Timer createSeaStallionTimer;
	
	public static int activeHelicopterCount= 0;
	public static int activeAirplaneCount= 0;
	
	private Font defaultFont;
	
	private int gameScore;
	
	private long startTime;
	
	private GraphicsContext gc;
	
	
	public static int createdCount;
	public static  boolean stopCreate;
	public static int spinDamageGaugeValue;
	
	GameAction(Canvas canvas, Scene scene, JukeBox jukeBox) {
		this.canvas= canvas;
		subSonarMediaPlayer= jukeBox.getBackgrooundClip();
		explosionMediaPlayer= jukeBox.getExplosionClip();
		aircraftHitMediaPlayer= jukeBox.getAircraftHitClip();
		metalKlinkMediaPlayer= jukeBox.getMetalKlinkClip();
		mineHitMediaPlayer= jukeBox.getMineHitClip();
		gameOverMediaPlayer= jukeBox.getGameOverSoundClip();
		seaWolfSubmarine= new SeaWolfSubmarine(subSonarMediaPlayer, jukeBox);
		this.jukeBox= jukeBox;
		
		drawAABulletList= new ArrayList<AABullet>(3);
		drawTorpedoList= new ArrayList<Torpedo>(3);
		drawMineList= new ArrayList<Mine>(12);
		drawSeaSpriteList= new ArrayList<SeaSprite>();
		drawParachutedBombList= new ArrayList<ParachutedBomb>();
		drawParachutedMineList= new ArrayList<ParachutedMine>(3);
		drawSeaStallionList= new ArrayList<SeaStallion>(1);
		drawAirplaneList= new ArrayList<Airplane>(1);
		drawAntiShipBombList= new ArrayList<AntiShipBomb>(1);
		drawMsgBubbleList= new ArrayList<MsgBubble>(1);
		
		randomGenerator= new Random();
		
		startTime= System.currentTimeMillis();
	}
	
//	LongValue lastNanoTime = new LongValue( System.nanoTime() );
	
	public void debugOut(String msg) {
		Date date= new Date();
		Timestamp timeStamp= new Timestamp(date.getTime());
		System.out.println(timeStamp + " Msg: " + msg);
	}
	
	public void doit() {
		
		gc= canvas.getGraphicsContext2D();
				

		Timeline testTime= new Timeline(new KeyFrame(
		        Duration.seconds(15),
		        ae -> clearReportDataLines()));
		    testTime.play();
		    
		displayReport("Mission1:\nDestroy Enemy Submarines\nAvoid Enemy Depth Charges\nAttack Enemy Aircraft\n\nGood Luck!\n");
		


        
        initTimers();
		
		new AnimationTimer()
		{

			private boolean gameOverMsgDisplayed;
			private Timer weaponReleaseTimer;

			public void handle(long currentNanoTime) {
				
				String direction = null;
				
				// calculate time since last update.
//		        double elapsedTime = (currentNanoTime - lastNanoTime.value) / 1000000000.0;
//		        lastNanoTime.value = currentNanoTime;
				
				// ******
				// UPDATE
				// ******
				
				if (SeaWolf2.subCommands.contains("restartGame")) {
					System.out.println("Restart Game");
					SeaWolf2.initGameData();
					clearMovingObjects();
					SeaWolf2.clearStatusDisplays();
					SeaWolf2.loadSpareSubs();
					SeaWolf2.initLaunchTubes();
					startTime= System.currentTimeMillis();
					initTimers();
					SeaWolf2.tBombCount= 0;
					SeaWolf2.subCommands.remove("restartGame");
					seaWolfSubmarine.getAAGun().setSeaMode();
					gc.setFont(defaultFont);
					gc.setFill(Color.BLACK);
					seaWolfSubmarine.reset();  //Probably set the mode back as well!
					gameOverMsgDisplayed= false;
					Timeline testTime= new Timeline(new KeyFrame(
					        Duration.seconds(15),
					        ae -> clearReportDataLines()));
					    testTime.play();
					    
					clearStatusFields();
					SeaWolf2.deactivateTorpedoTubes();
					SeaWolf2.resetNumShips();
					SeaWolf2.subDamageLevelCounter= 0;
					SeaWolf2.damageGauge.setValue(0);
					    
					displayReport("Mission1:\nDestroy Enemy Submarines\nAvoid Enemy Depth Charges\nAttack Enemy Aircraft\n\nGood Luck!\n");
				}
				
				updateMovingObjects();
				
				updateMsgBubbles();
				
				SeaWolf2.damageGauge.setValue(GameAction.spinDamageGaugeValue);
				
				if (SeaWolf2.gameOver== false && CreateMineTask.getCreateMine()== true && mineSoundWaveExplosionSequenceInProgress== false)
					createAMine();

				
				if (SeaWolf2.gameOver== false && CreateHelicopterTask.getLaunchHelicopter()== true) {
				
					if (stopCreate== false) {
						
						SeaSprite seaSprite= new SeaSprite(jukeBox, gc, false, SeaSprite.HELICOPTERA, 0, 0, null, 3);
						drawSeaSpriteList.add(seaSprite);
		
						seaSprite= new SeaSprite(jukeBox, gc, true, SeaSprite.HELICOPTERB, seaSprite.getX(), seaSprite.getY(), seaSprite.getCurrentDirection(), 3);
						drawSeaSpriteList.add(seaSprite);
						
						createdCount+=2;
						
						Game.Sprite.Direction direction1;
		
						if (seaSprite.getCurrentDirection()== Sprite.Direction.L_TO_R) {
							direction1= Sprite.Direction.R_TO_L;
						}
						else
							direction1= Sprite.Direction.L_TO_R;
						
						seaSprite= new SeaSprite(jukeBox, gc, true, SeaSprite.HELICOPTERA, seaSprite.getX(), seaSprite.getY(), direction1, 3);
						
						createdCount+=1;
						
						if (createdCount== 6)  // <-- Make this paramaterized, and increase value through course of the game.
							stopCreate= true;
						
												
						drawSeaSpriteList.add(seaSprite);					
						
					}

					CreateHelicopterTask.resetLaunchHelicopter();
					
					createHelicopterTimer.schedule(new CreateHelicopterTask(), 4000);
				
					if (drawSeaSpriteList.isEmpty()== true) {
						createdCount= 0;
						stopCreate= false;
					}
				}
				
				// BEGIN
				if (SeaWolf2.gameOver== false && CreateAirplaneTask.getLaunchAirplane()== true && drawAirplaneList.size()== 0) {
					

					Airplane airplane= new Airplane(jukeBox, gc, 2);
					GameAction.drawAirplaneList.add(airplane);
					
					
					int scenario= randomGenerator.nextInt(3);
					
					switch (scenario) {
					case 0:
						// Single plane flies
						break;
						
					case 1:
						// Dual planes fly together
						if (airplane.getCurrentDirection()== Sprite.Direction.L_TO_R)  {
							
							airplane= new Airplane(jukeBox, gc, 2, 0, Sprite.Direction.L_TO_R, Airplane.ALT_ROW);
							
						} else {
												
							airplane= new Airplane(jukeBox, gc, 2, SeaWolf2.CANVAS_WIDTH, Sprite.Direction.R_TO_L, Airplane.ALT_ROW);
							
						}
						break;
						
					case 2:
						// Planes fly from opposite sides of screen
						if (airplane.getCurrentDirection()== Sprite.Direction.L_TO_R) {
							
							airplane= new Airplane(jukeBox, gc, 2, SeaWolf2.CANVAS_WIDTH, Sprite.Direction.R_TO_L, Airplane.ALT_ROW);
							
						} else {
							
							airplane= new Airplane(jukeBox, gc, 2, 0, Sprite.Direction.L_TO_R, Airplane.ALT_ROW);
							
						}
						break;
					}
					
					drawAirplaneList.add(airplane);
					
					CreateAirplaneTask.resetLaunchAirplane();
					
					// Change this to create a new timer only once the SeaStallion has left the screen
					createAirplaneTimer.schedule(new CreateAirplaneTask(), CreateAirplaneTask.randomAirplaneCreateTimerValue());
					
				}	 
				else {
					
					// **********************************************
					// NOTE: Might be able to use Java Generics here!
					// **********************************************
					
					drawAirplaneList= moveObject(drawAirplaneList, "Airplane");
				

				}			
				// END
				
				// *************** Begin Experiment!
				if (SeaWolf2.gameOver== false && CreateSeaStallionTask.getLaunchSeaStallion()== true && drawSeaStallionList.size()== 0) {
					

					SeaStallion seaStallion= new SeaStallion(jukeBox, gc, false, SeaStallion.HELICOPTERA, 0, 0, null, 3);
					GameAction.drawSeaStallionList.add(seaStallion);

					CreateSeaStallionTask.resetLaunchSeaStallion();
					
					// Change this to create a new timer only once the SeaStallion has left the screen
					createSeaStallionTimer.schedule(new CreateSeaStallionTask(), CreateSeaStallionTask.randomHelicopterCreateTimerValue());
					
				}	 
				else {
					
					// **********************************************
					// NOTE: Might be able to use Java Generics here!
					// **********************************************
					
					drawSeaStallionList= moveObject(drawSeaStallionList, "SeaStallions");
				

				}
				// *************** End Experiment!
				if (SeaWolf2.subCommands.contains("moveLeft")) {
					System.out.println("Move sub left");
					direction= "moveLeft";
				}
				else if (SeaWolf2.subCommands.contains("moveRight")) {
					System.out.println("Move sub right");
					direction= "moveRight";
				}
				else if (SeaWolf2.subCommands.contains("cycleRight")) {
					System.out.println("Cycle weapon right");
					if (seaWolfSubmarine.getAAGun().getGunActivationStatus()== false)
						seaWolfSubmarine.cycleActiveTorpedoTubeRight();
					// 
					else
					{
						seaWolfSubmarine.getAAGun().cycleGunRight();
					}
					// Remove the entry from the queue to force another keypress for action to occur
					SeaWolf2.subCommands.remove("cycleRight");
				}
				else if (SeaWolf2.subCommands.contains("cycleLeft")) {
					System.out.println("Cycle weapon left");
					if (seaWolfSubmarine.getAAGun().getGunActivationStatus()== false)
						seaWolfSubmarine.cycleActiveTorpedoTubeLeft();
					else
					{
						seaWolfSubmarine.getAAGun().cycleGunLeft();
					}
					// Remove the entry from the queue to force another keypress for action to occur
					SeaWolf2.subCommands.remove("cycleLeft");
				}
				else if (SeaWolf2.subCommands.contains("changeMode")) {
					System.out.println("Change Mode");
					
	    			if (seaWolfSubmarine.getDirection()== SeaWolfSubmarine.Direction.L_to_R)
	    				seaWolfSubmarine.getAAGun().toggleGunStatus(AAGun.Direction.L_to_R);
	    			else
	    				seaWolfSubmarine.getAAGun().toggleGunStatus(AAGun.Direction.L_to_R);  // Was R_to_L


	    			if (seaWolfSubmarine.getAAGun().getGunActivationStatus()== true) {
	    				SeaWolf2.modeTextField.setText("Air");
	    			}
	    			else {
	    				SeaWolf2.modeTextField.setText("Sea");
	    			}
	    			SeaWolf2.subCommands.remove("changeMode");
				}
				else if (SeaWolf2.subCommands.contains("fireWeapon")) {
					System.out.println("Fire Weapon");
					
					if (seaWolfSubmarine.getAAGun().getGunActivationStatus()== true) {
					
						seaWolfSubmarine.getAAGun().fireGun();
						AABullet aLeftAABullet= new AABullet(seaWolfSubmarine.getAAGun().getGunImageIndexSelected(), seaWolfSubmarine.getX(), seaWolfSubmarine.getY() + 4 , seaWolfSubmarine.getDirection());
						SeaWolf2.updateBulletCount();
						drawAABulletList.add(aLeftAABullet);
						
					}
					else
					{
						seaWolfSubmarine.launchTorpedo();
						// Launch a Torpedo
						Torpedo aTorpedo= new Torpedo(seaWolfSubmarine.getGunImageIndexSelected(), 4, seaWolfSubmarine.getX(), seaWolfSubmarine.getY(), seaWolfSubmarine.getNozzleOffSet(seaWolfSubmarine.getGunImageIndexSelected()), seaWolfSubmarine.getDirection());
						SeaWolf2.updateTorpedoCount();
						drawTorpedoList.add(aTorpedo);
//						weaponReleaseTimer= new Timer();
//						weaponReleaseTimer.schedule(new WeaponRelease(aTorpedo), 0, 50);
									
					}
					SeaWolf2.subCommands.remove("fireWeapon");
				}
				else if (SeaWolf2.subCommands.contains("antiMineOps")) {
					System.out.println("Anti Mine Ops");
					SeaWolf2.antiMineOpsIcon.setGraphic(null);
					
					// Probably should make a copy of mthe list before
					// start destroying them, as the list is continually
					// added to.  Or maybe stop Mine genaration while destruction
					// is occuring.
					
					// 1. Setup a timer to cause periodic explosions.
					// 2. Radomly selected a new index from the drawList - once mine exploded should be off the list
					// 3. Reset timer to make sure is gapped enough so does no go off while previous mine is
					//    still exploding - do not want to cause the same mine to explode twice.
					
					if (drawMineList.size()!= 0) {
						
						
							mineSoundWaveExplosionSequenceInProgress= true;
							
							
							// Create a list of indices of Sill Alive Mines
							
							int randomIndex= randomGenerator.nextInt(drawMineList.size());			
							Mine aMineToDestroy= drawMineList.get(randomIndex);			
							aMineToDestroy.mineHit();
							
							createMineTimer= new Timer();
							
							createMineTimer.schedule(new ExplodeAMineTask(), 225);

					}
					else {
						
						jukeBox.getMetalKlinkClip().play();
						
					}
					SeaWolf2.subCommands.remove("antiMineOps");
				}
				
				
				
				// ******
				// RENDER
				// ******
				
				gc.clearRect(0, 0, 1200, 550);
				
				drawSeaLanes();
				
				// Draw the submarine along with torpedo tubes and gun			
				seaWolfSubmarine.render(gc, direction);
				
				drawMovingObjects();
				
				drawReports();
				
				drawMsgBubbles();
				
				if (SeaWolf2.numSubs > 0) {
					if (SeaWolfSubmarine.updateNumShipsLeftDisplay== true) {
						SeaWolf2.updateNumShipsLeftDisplay();
						SeaWolfSubmarine.updateNumShipsLeftDisplay= false;
					}
				}
				
				if (SeaWolf2.torpedoTubesStatus.equals("Empty")) {
					SeaWolf2.torpedoStatusTextField.clear();
				} else if (SeaWolf2.torpedoTubesStatus.equals("Loading")) {
					SeaWolf2.torpedoStatusTextField.setText("Loading");
				} else if (SeaWolf2.torpedoTubesStatus.equals("Ready")) {
					SeaWolf2.torpedoStatusTextField.setText("Ready");
				} else {
					
				}
						
				
				if (SeaWolf2.gameOver== true) {
					
					defaultFont= Font.getDefault();
					
					gc.setFont(new Font("Helvetica", 40));
					gc.strokeText("Game Over", (SeaWolf2.CANVAS_WIDTH - 5)/2 - 120, SeaWolf2.CANVAS_HEIGHT /2 - 240);
					
					gc.setFont(new Font("Helvetica", 20));
					gc.strokeText("press y to play again", (SeaWolf2.CANVAS_WIDTH - 5)/2 - 105, SeaWolf2.CANVAS_HEIGHT /2 - 180);
					
					// Display Stats
					// Stop all timers (Let items fly off the screen but not return
					
					if (gameOverMsgDisplayed== false) {
//						soundPlayer.getBackgrooundClip().stop();
						subSonarMediaPlayer.stop();
						subSonarMediaPlayer.seek(subSonarMediaPlayer.getStartTime());
//						subSonarMediaPlayer.dispose();
						
						gameOverMediaPlayer.play();
						gameOverMediaPlayer.seek(gameOverMediaPlayer.getStartTime());
						CreateAirplaneTask.resetLaunchAirplane();
						CreateHelicopterTask.resetLaunchHelicopter();
						CreateMineTask.resetCreateMine();
						CreateSeaStallionTask.resetLaunchSeaStallion();
						SeaWolf2.subCommands.clear();
						
						createHelicopterTimer.cancel();
						createSeaStallionTimer.cancel();
						createAirplaneTimer.cancel();
						createMineTimer.cancel();
						
						
//						helicopterMediaPlayer.dispose();
//						airPlaneMediaPlayer.dispose();
						
				    }
					
					gameOverMsgDisplayed= true;
				}

				
				// *******************
				// TEST FOR COLLISIONS
				// *******************
				
				checkCollisions();
				
			}
			
		}.start();
		
	}
	
	class WeaponRelease extends TimerTask
	{
		private Torpedo aTorpedo;

		public WeaponRelease(Torpedo aTorpedo)
		{
			this.aTorpedo= aTorpedo;
		}

		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			drawTorpedoList.add(aTorpedo);

		}
	}
	
	private void initTimers() {
		// TODO Auto-generated method stub
				
		createMineTimer= new Timer("Create Mine Timer");
		createMineTimer.schedule(new CreateMineTask(), 15000);
		
		createHelicopterTimer= new Timer("Create Helicopter Timer");
		createHelicopterTimer.schedule(new CreateHelicopterTask(), 4000);
		
		createSeaStallionTimer= new Timer("Create Sea Stallion Timer");
		createSeaStallionTimer.schedule(new CreateSeaStallionTask(), 7000);
		
		createAirplaneTimer= new Timer("Create Airplane Timer");
		createAirplaneTimer.schedule(new CreateAirplaneTask(),  30000);
	}
	
	private void clearStatusFields()
	{
		SeaWolf2.scoreTextField.clear();
		SeaWolf2.modeTextField.clear();
		SeaWolf2.torpedoTubesStatus= "";
		SeaWolf2.torpedoStatusTextField.clear();	
		SeaWolf2.torpedosTextField.clear();
		SeaWolf2.bulletsTextField.clear();	
	}
	

	private void createAMine() {
		if (drawMineList.size()< MAX_NUMBER_OF_MINES + 40) {
			
			// Determine how long program has been running
			long currentTime= System.currentTimeMillis();
			
			long seconds = currentTime - startTime;
			
			int maxRow= 0;
			
		    if (seconds < 45000)
		    {
		    	maxRow= 3;
		    }
		    else if (seconds < 90000 )
		    {
		    	maxRow= 5;
		    }
		    else
		    {
		    	maxRow= 7;
		    }
			
			int newMineRow;
		
			int randomRowIndex= randomGenerator.nextInt(maxRow);

			switch (randomRowIndex) {
			case 0:
				randomRowIndex= MINE_DISPLAY_ROW1X;
				break;
			case 1:
				randomRowIndex= MINE_DISPLAY_ROW2X;
				break;
			case 2:
				randomRowIndex= MINE_DISPLAY_ROW3X;
				break;
			case 3:
				randomRowIndex= MINE_DISPLAY_ROW4X;
				break;
			case 4:
				randomRowIndex= MINE_DISPLAY_ROW5X;
				break;
			case 5:
				randomRowIndex= MINE_DISPLAY_ROW6X;
				break;
			case 6:
				randomRowIndex= MINE_DISPLAY_ROW7X;
				break;
			}

			newMineRow= randomRowIndex;
			Mine shipMine= new Mine(newMineRow, 6);
			drawMineList.add(shipMine);

			CreateMineTask.resetCreateMine();
			
			// Now create a new timer object.
			createMineTimer.schedule(new CreateMineTask(), CreateMineTask.randomMineCreateTimerValue());
			

		}
	}
		
	private Object clearReportDataLines() {
		// TODO Auto-generated method stub
		System.out.println("HELLO WORLD!!!!!!");
		resultsLine1.setLength(0);
		resultsLine2.setLength(0);
		resultsLine3.setLength(0);
		resultsLine4.setLength(0);
		resultsLine5.setLength(0);
		resultsLine6.setLength(0);
		resultsLine7.setLength(0);
		resultsLine8.setLength(0);
		resultsLine9.setLength(0);
		
		// Now start the game!
		// Get the Mine Field going
		return null;
	}
	

	public void drawSeaLanes() {
		Color seaColor= Color.rgb(153, 206,230, 1.0);
		
		Color[] seaLaneColor= new Color[9];
		seaLaneColor[0]= Color.rgb(46, 157, 255, 1.0);
		seaLaneColor[1]= Color.rgb(46, 140, 255, 1.0);
		seaLaneColor[2]= Color.rgb(46, 127, 255, 1.0);
		seaLaneColor[3]= Color.rgb(46, 107, 255, 1.0);
		seaLaneColor[4]= Color.rgb(46, 87, 255, 1.0);
		seaLaneColor[5]= Color.rgb(46, 67, 255, 1.0);
		seaLaneColor[6]= Color.rgb(46, 47, 255, 1.0);
		seaLaneColor[7]= Color.rgb(46, 27, 255, 1.0);
		seaLaneColor[8]= Color.rgb(46, 4, 255, 1.0);
		
					
		// Height of each sea lane is 40.
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		gc.setFill(seaColor);
		gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
		
		gc.setFill(seaLaneColor[0]);
		gc.fillRect(0, 0, 1200, 69);
		
		for (int i= 0; i< 9; i++) {
			gc.setFill(seaLaneColor[i]);
			if (i== 8) {
				gc.fillRect(0, SEA_LANES_START_Y + (i * SEA_LANE_ROW_HEIGHT), 1200, 189);
			} else {
				gc.fillRect(0, SEA_LANES_START_Y + (i * SEA_LANE_ROW_HEIGHT), 1200, 40);
			}
		}
	}
	
	public void drawReports()
	{
		int xLoc= 530;
		
		// Now draw whatever happens to be in the buffers at this point
		gc.strokeText(resultsLine1.toString(), xLoc, 205 - 50);
		gc.strokeText(resultsLine2.toString(), xLoc, 225 - 50);
		gc.strokeText(resultsLine3.toString(), xLoc, 245 - 50);
		gc.strokeText(resultsLine4.toString(), xLoc, 265 - 50);
		gc.strokeText(resultsLine5.toString(), xLoc, 295 - 50);
		gc.strokeText(resultsLine6.toString(), xLoc, 315 - 50);
		gc.strokeText(resultsLine7.toString(), xLoc, 335 - 50);
		gc.strokeText(resultsLine8.toString(), xLoc, 365 - 50);
		gc.strokeText(resultsLine9.toString(), xLoc, 395 - 50);
	}
	
	public void drawMsgBubbles()
	{
		for (MsgBubble aMsgBubble: drawMsgBubbleList) {
				aMsgBubble.drawMessage();
		}
	}
	
	public void clearMovingObjects()
	{
		drawAABulletList.clear();
		drawTorpedoList.clear();
		drawMineList.clear();
		drawSeaSpriteList.clear();
		drawParachutedBombList.clear();
		drawParachutedMineList.clear();
		drawSeaStallionList.clear();
		drawAirplaneList.clear();
		drawAntiShipBombList.clear();
	}
	
	public void drawMovingObjects()
	{
		drawObjectList(drawAABulletList, gc, "AA BulletsLeft");
		drawObjectList(drawTorpedoList, gc, "torpedoes");
		drawObjectList(drawMineList, gc, "mines");
		drawObjectList(drawSeaSpriteList, gc, "helicopters");
		drawObjectList(drawParachutedBombList, gc, "parachutedBomb");
		drawObjectList(drawParachutedMineList, gc, "parachuted mines");
		drawObjectList(drawSeaStallionList, gc, "sea stallion helicopters");
		drawObjectList(drawAirplaneList, gc, "airplane list");
		drawObjectList(drawAntiShipBombList, gc, "antiship bomb list");
	}
	
	private  <ScreenObj extends ImageDisplayFunctions> void drawObjectList(List<ScreenObj> screenObjectList, GraphicsContext gc, String itemName)
	{	
		for (ScreenObj aScreenObj: screenObjectList) {
			
			gc.drawImage(aScreenObj.getImage(), aScreenObj.getX(), aScreenObj.getY());
			
		}

	}
	
	public void updateMovingObjects()
	{
		// Rebuild the drawTorpedoList to clear out torpedos that hit
		// the end of the screen
					
		drawTorpedoList= moveObject(drawTorpedoList, "torpedoes");

		drawAABulletList= moveObject(drawAABulletList, "AAbulletsLeft");
		
		drawMineList= moveObject(drawMineList, "mines");
		
		drawSeaSpriteList= moveObject(drawSeaSpriteList, "helicopters");
		
		drawParachutedBombList= moveObject(drawParachutedBombList, "parachutedBomb");
		
		drawParachutedMineList= moveObject(drawParachutedMineList, "parachutedMine");
		
		drawSeaStallionList= moveObject(drawSeaStallionList, "sea stallion helicopters");
		
		drawAirplaneList= moveObject(drawAirplaneList, "airplanes");
		
		drawAntiShipBombList= moveObject(drawAntiShipBombList, "AntiShipBombs");
	}
	
	public void updateMsgBubbles()
	{
		List<MsgBubble> tempObjList= new ArrayList<MsgBubble>();
		
		for (MsgBubble aMsgBubble: drawMsgBubbleList) {
			if (aMsgBubble.aliveStatus()== true)
				tempObjList.add(aMsgBubble);
		}
		
		drawMsgBubbleList= tempObjList;
	}
	
	private <ScreenObj extends ImageMoveFunctions> List<ScreenObj> moveObject(List<ScreenObj> screenObjectList, String objectName)
	{
		List<ScreenObj> tempScreenObjList= new ArrayList<ScreenObj>();
		
	
		for (ScreenObj aScreenObj: screenObjectList)
		{
			if (aScreenObj.aliveStatus()== true) {
				aScreenObj.move();
				tempScreenObjList.add(aScreenObj);
				
			}
//			else
//			{
//				System.out.println("Bullet out of scope!");
//				
//			}
		}
		
	
		
		return tempScreenObjList;
		
	}
	
	public void checkCollisions() {
		for (int index= 0; index< drawTorpedoList.size(); index++) {
			
			Rectangle torpedoBounds= drawTorpedoList.get(index).getBounds();
			
			for (Mine aMine: drawMineList) {
				Rectangle aMineBounds= aMine.getBounds();
				if (overlap(torpedoBounds, aMineBounds))
				{

//					System.out.println("COLLISION!");
					mineHitMediaPlayer.play();
					mineHitMediaPlayer.seek(mineHitMediaPlayer.getStartTime());
					
					Torpedo theTorpedo= drawTorpedoList.get(index);
					//							theTorpedo.hitMine();
					theTorpedo.killTorpedo();
					
					// Make mine flash red
					aMine.mineHit();
					
//						Screen.explodeAMineTimer= new Timer(40, new ExplodeAMine(aMine));
//						Screen.explodeAMineTimer.setRepeats(false);
//						Screen.explodeAMineTimer.start();

//						// Make mine flash red
//						aMine.mineHit();

					break;
				}
			}
			
		}
		
		boolean aircraftHit= false;
		
		// Issue here of not having the rightList bullets registering hits against a plane that 
		// has already been hit by a leftList bullet.
		// *** CHANGE THIS TO HAVE DOUBLE BULLET ICON AND AVOID THESE ISSUES ***
		aircraftHit= checkCollisonBulletWithAircraftTypes(drawAABulletList);
//		if (aircraftHit== true) {
//			gameRoundStats.incrementNumBulletsHitTargetCount();
//		}
		
		Rectangle torpedoGunBounds= SeaWolfSubmarine.getBounds();
		
		debugOut("--------");
		debugOut("value of shipSunk: " + SeaWolfSubmarine.shipSunk);
		debugOut("value of inProcessAbsorbingHist: " + SeaWolfSubmarine.inProcessAbsorbingHit);
		debugOut("value of explosionTimerScheduled: " + SeaWolfSubmarine.explosionTimerScheduled);
		debugOut("--------");
		
		if (SeaWolfSubmarine.shipSunk== false && SeaWolfSubmarine.inProcessAbsorbingHit== false && SeaWolfSubmarine.explosionTimerScheduled== false) {
			
			debugOut("Checking for Collision!");
			
			checkCollisionOfExplosiveWithSubmarine(drawAntiShipBombList, torpedoGunBounds);
			
			checkCollisionOfExplosiveWithSubmarine(drawParachutedBombList, torpedoGunBounds);
			
		}
	
		
//		checkCollisionOfExplosiveWithSubmarine(drawDepthChargeList, torpedoGunBounds);
	
				
	}
	
	private boolean overlap(Rectangle torpedo, Rectangle mine) {
		return torpedo.getX() < mine.getX() + mine.getWidth() && torpedo.getX() + torpedo.getWidth() > mine.getX() && torpedo.getY() < mine.getY() + mine.getHeight() && torpedo.getY() + torpedo.getHeight() > mine.getY();
	}
	
	
	private <ScreenObj extends SubmarineStruckByExplosiveCollisionFunctions & ImageDisplayFunctions > void checkCollisionOfExplosiveWithSubmarine(List<ScreenObj> screenObjectList, Rectangle torpedoGunBounds)
	{		
		for (ScreenObj aScreenObj: screenObjectList) {
			Rectangle aScreenObjBounds= aScreenObj.getBounds();
			if (overlap(torpedoGunBounds, aScreenObjBounds))
			{
				if (aScreenObj.getHitGun()== false) {

						// Make gunbase flash red
						seaWolfSubmarine.torpedoGunHit();

						SeaWolf2.updateDamageLevel();


						// Note - add code to make sure only hit this method once.
						// Otherwise soundPlayer is repeatedly started. 
						explosionMediaPlayer.play();  // Rewind
						explosionMediaPlayer.seek(explosionMediaPlayer.getStartTime());

						aScreenObj.setHitGun();

						SeaWolfSubmarine.gunHit();
//					}
				}
			}
		}
	}
	
	private <ScreenObj extends SubmarineStruckByExplosiveCollisionFunctions & ImageDisplayFunctions > void ORIGcheckCollisionOfExplosiveWithSubmarine(List<ScreenObj> screenObjectList, Rectangle torpedoGunBounds)
	{		
		for (ScreenObj aScreenObj: screenObjectList) {
			Rectangle aScreenObjBounds= aScreenObj.getBounds();
			if (overlap(torpedoGunBounds, aScreenObjBounds))
			{
				if (aScreenObj.getHitGun()== false) {

						// Make gunbase flash red
						seaWolfSubmarine.torpedoGunHit();

						SeaWolf2.updateDamageLevel();


						// Note - add code to make sure only hit this method once.
						// Otherwise soundPlayer is repeatedly started. 
						explosionMediaPlayer.play();  // Rewind
						explosionMediaPlayer.seek(explosionMediaPlayer.getStartTime());

						aScreenObj.setHitGun();

						SeaWolfSubmarine.gunHit();
//					}
				}
			}
		}
	}
	
	private boolean checkCollisonBulletWithAircraftTypes(List<AABullet> drawAABulletList )
	{
		boolean aircraftHit= false;
		
		for (AABullet aAABullet: drawAABulletList) {
			
			if (aAABullet.isHitAirborne()== false) {
			
				// ********************************************************
				// Only check Bullet when reached certain height!!!!!!!!!!! (Not done yet!)
				// ********************************************************
				
				if (aAABullet.getY() <= HELICOPTER_DISPLAY_ROW + 20) {
	
					boolean retVal= checkCollisionOfAABulletWithAircraft(drawSeaSpriteList, aAABullet);
					if (retVal== true) {
	
 						aircraftHit= true;
						
						aAABullet.hitAirborne();
	
						break;
					} 
	
	
					// Check for collision of AA bullet with SeaStallion
					retVal= checkCollisionOfAABulletWithAircraft(drawSeaStallionList, aAABullet);
					if (retVal== true) {
	
//						seaStallionDestroyedForThisRound= true;
						aircraftHit= true;
						
						aAABullet.hitAirborne();
	
						break;
					}
					
	
	
					// Check for collision of AA bullet with Airplane
					retVal= checkCollisionOfAABulletWithAircraft(drawAirplaneList, aAABullet);
					if (retVal== true) {
	
//						airplaneDestroyedForThisRound= true;
						aircraftHit= true;
						
						aAABullet.hitAirborne();
	
						break;
					}
//					
//					retVal= checkCollisionOfAABulletWithAircraft(drawParachutedBombList, aAABullet);
//					if (retVal== true) {
//						
//					}
				}
			}
		}
		
		if (aircraftHit== true) {
			aircraftHitMediaPlayer.play();  // Note - change all music clips to be like this!!
			aircraftHitMediaPlayer.seek(aircraftHitMediaPlayer.getStartTime());
		}
		
		return aircraftHit;
	}
	
	private <ScreenObj extends ImageDisplayFunctions & AircraftStruckByBulletCollisionFunctions> boolean checkCollisionOfAABulletWithAircraft(List<ScreenObj> screenObjectList, AABullet aAABullet)
	{
		Rectangle aAABulletBounds= aAABullet.getBounds();

		for (ScreenObj aScreenObj: screenObjectList) {  
			if (aScreenObj.isHitByBullet()== false) {
										
				Rectangle aScreenObjBounds= aScreenObj.getBounds();

				if (overlap(aScreenObjBounds, aAABulletBounds))
				{

						// Object has been struck by Bullet!
 						System.out.println("Aircraft Hit!");

						aScreenObj.setHitByBullet();
						aScreenObj.stopAudio();
						
						gameScore+= aScreenObj.getPoints();
						SeaWolf2.scoreTextField.setText(Integer.toString(gameScore));

						return true;
				}	
			}
			else
			{
				System.out.println("Already hit!");
			}
		}  // End For
		return false;
	}
	
	private void resetCounters()
	{
			
		gameScore= 0;
		SeaWolf2.scoreTextField.setText(Integer.toString(0));
		SeaWolf2.modeTextField.setText("Sea");
		SeaWolf2.torpedoTubesStatus= "Ready";
		SeaWolf2.torpedoStatusTextField.setText("Ready");
		
		SeaWolf2.torpedosTextField.setText(Integer.toString(SeaWolf2.numTorpedoes));;
		SeaWolf2.activateTorpedoTubes();
		SeaWolf2.bulletsTextField.setText(Integer.toString(SeaWolf2.numBullets));
	}
	
	public void displayReport(String reportToDisplay) {
		
		// Test a Timeline object
		final IntegerProperty displayLineNumber = new SimpleIntegerProperty(0);
//		final String displayReport= "Mission1:\nDestroy Enemy Submarines\nAvoid Enemy Depth Charges\nAttack Enemy Aircraft\n\nGood Luck!\n";
		final String displayReport= reportToDisplay;
		final IntegerProperty displayReportCharIndex = new SimpleIntegerProperty(0);
	    Timeline timeline = new Timeline();
//	    jukeBox.getTypeWriterClip().play();
//	    final JukeBox tempSoundPlayer = jukeBox;
	    MediaPlayer mediaPlayer= jukeBox.getTypeWriterKeyStrokeClip();
	    KeyFrame keyFrame = new KeyFrame(
	            Duration.millis(75),
	            event -> {
	            	System.out.println("index is: " + displayReportCharIndex.get());
	            	System.out.println("length of string is: " + displayReport.length());
	                if (displayReportCharIndex.get() == displayReport.length()) {
	                    timeline.stop();
//	                    tempSoundPlayer.getTypeWriterClip().stop();
	                } else {
	                	
	                	mediaPlayer.seek(mediaPlayer.getStartTime());
	                	mediaPlayer.play();
	                	
	                	switch (displayLineNumber.get()) {
	                	case 0:
	                		if (displayReport.charAt(displayReportCharIndex.get())!= '\n')
	        					resultsLine1.append(displayReport.charAt(displayReportCharIndex.get()));
	        				else
	        					displayLineNumber.set(displayLineNumber.get() + 1);
	                		break;
	                	case 1:
	                		// Add the character.  When detect a CR, increase the displayLineNumber.
	        				if (displayReport.charAt(displayReportCharIndex.get())!= '\n')
	        					resultsLine2.append(displayReport.charAt(displayReportCharIndex.get()));
	        				else
	        					displayLineNumber.set(displayLineNumber.get() + 1);
	                		break;
	                	case 2:
	                		if (displayReport.charAt(displayReportCharIndex.get())!= '\n')
	        					resultsLine3.append(displayReport.charAt(displayReportCharIndex.get()));
	        				else
	        					displayLineNumber.set(displayLineNumber.get() + 1);
	                		break;
	                	case 3:
	                		// Add the character.  When detect a CR, increase the displayLineNumber.
	        				if (displayReport.charAt(displayReportCharIndex.get())!= '\n')
	        					resultsLine4.append(displayReport.charAt(displayReportCharIndex.get()));
	        				else
	        					displayLineNumber.set(displayLineNumber.get() + 1);
	                		break;
	                	case 4:
	                		// Add the character.  When detect a CR, increase the displayLineNumber.
	        				if (displayReport.charAt(displayReportCharIndex.get())!= '\n')
	        					resultsLine5.append(displayReport.charAt(displayReportCharIndex.get()));
	        				else
	        					displayLineNumber.set(displayLineNumber.get() + 1);
	                		break;
	                	case 5:
	                		// Add the character.  When detect a CR, increase the displayLineNumber.
	        				if (displayReport.charAt(displayReportCharIndex.get())!= '\n')
	        					resultsLine5.append(displayReport.charAt(displayReportCharIndex.get()));
	        				else {
	        					resetCounters();
	        					displayLineNumber.set(displayLineNumber.get() + 1);
	        				}
	                		break;
	                	}
	                	
	                	
	                    displayReportCharIndex.set(displayReportCharIndex.get() + 1);
	                }
	            });
	    timeline.getKeyFrames().add(keyFrame);
	    timeline.setCycleCount(Animation.INDEFINITE);
	    timeline.play();
    
	}
}

// Utilized by a Timer for when to Launch Helicopters
class CreateHelicopterTask extends TimerTask {
	private static boolean timerExpired;
	
	public CreateHelicopterTask() {

		System.out.println("Launch Helicopter constructed!");
		
		timerExpired= false;
		
	}

	// Add code to reset the helicopter to "unhit"
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Set flag to create a helicopter");
		
		timerExpired= true;
		
//		if (GameAction.drawSeaSpriteList.isEmpty()== true)
//			GameAction.stopCreate= false;
		
	}
	
	public static boolean getLaunchHelicopter()
	{
		return timerExpired;
	}
	
	public static void resetLaunchHelicopter()
	{
		timerExpired= false;
	}
	
	public static int randomHelicopterCreateTimerValue() {
		int helicopterCreateDelay=  GameAction.randomGenerator.nextInt(5);
		switch (helicopterCreateDelay) {
			case 0:
				helicopterCreateDelay= 20000;
				break;
			case 1:
				helicopterCreateDelay= 30000;
				break;
			case 2:
				helicopterCreateDelay= 40000;  // 15000
				break;
			case 3:
				helicopterCreateDelay= 50000;  // 20000
				break;
			case 4:
				helicopterCreateDelay= 60000;  // 25000
				break;			
		}
		return helicopterCreateDelay;
	}
	
	
	
}

// BEGIN
class CreateAirplaneTask extends TimerTask {
	private static boolean timerExpired;
	
	public CreateAirplaneTask() {
		System.out.println("CreateSeaStallion Constructor");
		
		timerExpired= false;
	}
	
	public void run() {
		
		timerExpired= true;
		
	}
	
	public static boolean getLaunchAirplane() {
		
		return timerExpired;
	}
	
	public static void resetLaunchAirplane() {
		
		timerExpired= false;
	}
	
	public static int randomAirplaneCreateTimerValue() {
		int airplaneCreateDelay=  GameAction.randomGenerator.nextInt(5);
		switch (airplaneCreateDelay) {
			case 0:
				airplaneCreateDelay= 5000;
				break;
			case 1:
				airplaneCreateDelay= 23000;
				break;
			case 2:
				airplaneCreateDelay= 12000;
				break;
			case 3:
				airplaneCreateDelay= 9000;
				break;
			case 4:
				airplaneCreateDelay= 30000;
				break;			
		}
		return airplaneCreateDelay;
	}
	
	
}
//END
	
class CreateSeaStallionTask extends TimerTask {
	private static boolean timerExpired;
	
	public CreateSeaStallionTask() {
		System.out.println("CreateSeaStallion Constructor");
		
		timerExpired= false;
	}
	
	public void run() {
		
		timerExpired= true;
		
	}
	
	public static boolean getLaunchSeaStallion() {
		
		return timerExpired;
	}
	
	public static void resetLaunchSeaStallion() {
		
		timerExpired= false;
	}
	
	public static int randomHelicopterCreateTimerValue() {
		int helicopterCreateDelay=  GameAction.randomGenerator.nextInt(5);
		switch (helicopterCreateDelay) {
			case 0:
				helicopterCreateDelay= 10000;
				break;
			case 1:
				helicopterCreateDelay= 14000;
				break;
			case 2:
				helicopterCreateDelay= 18000;
				break;
			case 3:
				helicopterCreateDelay= 22000;
				break;
			case 4:
				helicopterCreateDelay= 29000;
				break;			
		}
		return helicopterCreateDelay;
	}
	
}

class CreateMineTask extends TimerTask {
	private static boolean createMine= false;
	
	public CreateMineTask() {
		System.out.println("CreateMineTask Constructor");
	}
	
	public void run() {
		
		System.out.println("Set flag to create a mine");
		
		createMine= true;
	}
	
	public static boolean getCreateMine()
	{
		return createMine;
	}
	
	public static void resetCreateMine()
	{
		createMine= false;
	}
	
	public static int randomMineCreateTimerValue() {
    	int mineCreateDelay= GameAction.randomGenerator.nextInt(6);
		switch (mineCreateDelay) {
		case 0:
			mineCreateDelay= 1400; // 700
			break;
		case 1:
			mineCreateDelay= 2400; // 1200
			break;
		case 2:
			mineCreateDelay= 4000; // 2000
			break;
		case 3:
			mineCreateDelay= 3000; // 2400
			break;
		case 4:
			mineCreateDelay= 3400; // 1600
			break;
		case 5:
			mineCreateDelay= 5000; // 900
			break;
		}
		
		return mineCreateDelay;

	}
	

}
	
class ExplodeAMineTask extends TimerTask {
	
	private boolean explodeAllMines= false;
	
	public ExplodeAMineTask() {
		explodeAllMines= true;
	}
	
	
	public void run() {

		if (explodeAllMines== true) {

			// Explode a Mine
			int randomIndex= GameAction.randomGenerator.nextInt(GameAction.drawMineList.size());

			Mine aMineToDestroy= GameAction.drawMineList.get(randomIndex);			
			aMineToDestroy.mineHit();

			// Setup a timer to explode the next one.
			// Keep doing this till run out of mines.
			if (GameAction.drawMineList.size()!= 0) {
				
				System.out.println("Number of Mines left in list is: " + GameAction.drawMineList.size());


				Timer createMineTimer= new Timer();
				createMineTimer.schedule(new ExplodeAMineTask(), 225);
			}
			else {

				GameAction.metalKlinkMediaPlayer.play();
				GameAction.metalKlinkMediaPlayer.seek(GameAction.metalKlinkMediaPlayer.getStartTime());

				GameAction.mineSoundWaveExplosionSequenceInProgress= false;
			}

		}
		

	}


}

