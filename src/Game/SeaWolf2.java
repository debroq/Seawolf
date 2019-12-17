package Game;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import Game.RoundGaugePanel;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.NeedleSize;
import eu.hansolo.medusa.Gauge.ScaleDirection;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Marker;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.TickLabelLocation;
import eu.hansolo.medusa.TickMarkType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.text.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



public class SeaWolf2 extends Application {
	

	public static RoundGaugePanel rp;
	public static ArrayList<String> subCommands= new ArrayList<String>();
	private static JukeBox theSoundPlayer;
	
	private static MediaPlayer metalKlinkMediaPlayer;
	
	// *********
	// Variables
	// *********
	public static int GAMETIMER= 15;   // 15
	public static int elapsedTime= 0;
	public static int torpedoLaunchCount= 0;
	public static int subDamageLevelCounter= 0;
	public static int numSubs= 4;
	public static int numTorpedoes= 40;
	public static int numBullets= 75;
	public static int numAntiMineOps= 1;
	public static String endOfGameMessage;
	public static int CANVAS_WIDTH=1200;
	public static int CANVAS_HEIGHT=700;
	public final static int HELICOPTER_DISPLAY_ROW= 30;
	public final static int LOADING_MILLIS=200;
	public static Label antiMineOpsIcon;
	
	public static TextField torpedoStatusTextField;
	public static TextField scoreTextField;
	public static TextField bulletsTextField;
	public static TextField modeTextField;
	
	public static Label subIcon1;
	public static Label subIcon2;
	public static Label subIcon3;
	
	public static boolean gameOver= false;
	
	public static Gauge damageGauge;
	static StackPane[] tubeArray= new StackPane[6];
	static boolean awaitingReload= false;
	static int torpedoTube;
	
	static boolean doneOnce;
	
	public static int tBombCount;
	
	public static boolean resetGaugeNeedle;

	
	public static void main(String[] args)
	{
		scoreTextField= new TextField();
		bulletsTextField= new TextField();
		torpedoStatusTextField= new TextField();
		modeTextField= new TextField();
				
		launch(args);
	}
	
	Button btn;
	private Timer weaponReleaseTimer;
	public static TextField torpedosTextField = new TextField();
	public static String torpedoTubesStatus= "NA";

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
		// Create the button
		btn = new Button();
		btn.setText("Click me please");
		btn.setOnAction(e->buttonClick());
		
		initGameData();
		
		// **************************
		// Setup Digital Font for GUI
		// **************************
		String filename="/Fonts/digital-7-italic.ttf";//this is for testing normally we would store the font file in our app (knows as an embedded resource), see this for help on that http://stackoverflow.com/questions/13796331/jar-embedded-resources-nullpointerexception/13797070#13797070

		final Font digitalFont = Font.loadFont(SeaWolf2.class.getResource("/Fonts/digital-7-italic.ttf").toExternalForm(), 20);
		

		HBox northPane= createNorthPane(digitalFont);
		northPane.setStyle("-fx-background-color: #1d00cb;");
		// topStatusPanel.setBackground(topPanelBlue);
		
		// Color green1 = Color.rgb(171, 190, 21);
		HBox southPane= createSouthPane(digitalFont);
		southPane.setStyle("-fx-background-color: rgba(171, 190, 21);");
		
		Canvas canvas= createCenterPanel();
		
		BorderPane pane= new BorderPane();
		
		pane.setTop(northPane);	
		pane.setCenter(canvas);
		pane.setBottom(southPane);
		// Add the layout pane to a scene
		Scene scene = new Scene(pane, CANVAS_WIDTH, CANVAS_HEIGHT);
		
		setupKeyHandlers(canvas, scene);
		
		canvas.requestFocus();
		
		theSoundPlayer= new JukeBox();
		
		metalKlinkMediaPlayer= theSoundPlayer.getMetalKlinkClip();
		
		GameAction gameAction= new GameAction(canvas, scene, theSoundPlayer);
		gameAction.doit();
		
		// Finalize and show the scene
		primaryStage.setScene(scene);
		primaryStage.setTitle("The SeaWolf game");
		primaryStage.show();
		
	}
	
	public static void initGameData()
	{
		GAMETIMER= 15;   // 15
		elapsedTime= 0;
		torpedoLaunchCount= 0;
		subDamageLevelCounter= 0;
		numSubs= 4;
		numTorpedoes= 40;
		numBullets= 75;
		numAntiMineOps= 1;
		gameOver= false;
	}
	
	public static void clearStatusDisplays()
	{
		scoreTextField.clear();
	}
	
	public static void loadSpareSubs()
	{
		
	}
	
	public void setupKeyHandlers(Canvas canvas, Scene scene) 
	{
		// Setup keyhandlers
		KeyCombination moveLeft= new KeyCodeCombination(KeyCode.A);
		KeyCombination moveRight= new KeyCodeCombination(KeyCode.D);
		KeyCombination cycleActiveWeaponRight= new KeyCodeCombination(KeyCode.RIGHT);
		KeyCombination cycleActiveWeaponLeft= new KeyCodeCombination(KeyCode.LEFT);
		KeyCombination changeMode= new KeyCodeCombination(KeyCode.PERIOD);
		KeyCombination fireWeapon= new KeyCodeCombination(KeyCode.SPACE);
		KeyCombination antiMineOps= new KeyCodeCombination(KeyCode.M);
		KeyCombination restartGame= new KeyCodeCombination(KeyCode.Y);
		
		// *******************
		// Begin Test Timeline  - Make two of these.  One for the Gun and one for the Torpedo.
		// *******************
		Duration firingInterval = Duration.millis(500);
		
		Timeline torpedoFireTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> fire()),
                new KeyFrame(firingInterval));
        torpedoFireTimeline.setCycleCount(Animation.INDEFINITE);
        
        Timeline machineGunFireTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> fire()),
                new KeyFrame(Duration.millis(400)));
        torpedoFireTimeline.setCycleCount(Animation.INDEFINITE);
        
//        scene.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.F && firing.getStatus() != Animation.Status.RUNNING) {
//                firing.playFromStart();
//            }
//        });
//
//        scene.setOnKeyReleased(event -> {
//            if (event.getCode() == KeyCode.F) {
//                firing.stop();
//            }
//        });
              
        // *****************
		// End Test Timeline
        // *****************
		
		
		scene.setOnKeyPressed(ke-> {
			if (gameOver== true) { //Need to change this to availRestart to block them till code is ready!!
				if (restartGame.match(ke)) {
					if (!subCommands.contains("restartGame")) {
						subCommands.add("restartGame");
						System.out.println("Restart Game");
					}
				}
			}
			if (ke.getCode() == KeyCode.SPACE && torpedoFireTimeline.getStatus() != Animation.Status.RUNNING) {
				if (GameAction.seaWolfSubmarine.getAAGun().getGunActivationStatus()== true) {
					if (numBullets!= 0) {
						machineGunFireTimeline.playFromStart();
					} else {
						// Play Klink sound
						theSoundPlayer.getMetalKlinkClip().play();
					}
				} else if (GameAction.seaWolfSubmarine.getAAGun().getGunActivationStatus()== false) {
					
					if (awaitingReload== false) {  // If hit this, then in "Sea Mode"
						if (numTorpedoes!= 0) {
							torpedoFireTimeline.playFromStart();
						}
						else {
							// Play Klink sound
							theSoundPlayer.getMetalKlinkClip().play();
						}
					}
				}	
			}
			else if (gameOver== false && SeaWolfSubmarine.shipSunk== false) {
				if (moveLeft.match(ke)) {
					if (!subCommands.contains("moveLeft")) {
						subCommands.add("moveLeft");
						System.out.println("Move Left");
					}
				} else if (moveRight.match(ke)) {
					if (!subCommands.contains("moveRight")) {
						subCommands.add("moveRight");
						System.out.println("Move Right");
					}
				} else if (cycleActiveWeaponRight.match(ke)) {
					if (!subCommands.contains("cycleRight")) {
						subCommands.add("cycleRight");
						System.out.println("Cycle weapon right");
					}
				} else if (cycleActiveWeaponLeft.match(ke)) {
					if (!subCommands.contains("cycleLeft")) {
						subCommands.add("cycleLeft");
						System.out.println("Cycle weapon left");
					}
				} else if (changeMode.match(ke)) {
					if (!subCommands.contains("changeMode")) {
						subCommands.add("changeMode");
						System.out.println("Change Mode");
					}
				} else if (antiMineOps.match(ke)) {
					if (!subCommands.contains("antiMineOps")) {
						subCommands.add("antiMineOps");
						System.out.println("Anti Mine Ops");	
					}
				} else {
					System.out.println("You typed: " + ke);
				}
			}
		});
		
		scene.setOnKeyReleased(ke-> {
			if (gameOver== false) {
				if (ke.getCode() == KeyCode.SPACE) {
	                torpedoFireTimeline.stop();
	                machineGunFireTimeline.stop();
	            }
				else if (moveLeft.match(ke)) {
					subCommands.remove("moveLeft");
				} else if (moveRight.match(ke)) {
					subCommands.remove("moveRight");
				} 
				//			else if (cycleActiveWeaponRight.match(ke)) {
				//				System.out.println("You released Arrow Right");
				//				subCommands.remove("cycleRight");
				//			} else if (cycleActiveWeaponLeft.match(ke)) {
				//				System.out.println("You released Arrow Left");
				//				subCommands.remove("cycleLeft");
				//			}
			}
		});
		
		
	}
	
	class WeaponRelease extends TimerTask
	{
		
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			subCommands.add("fireWeapon");
			weaponReleaseTimer.cancel();
		}
	}
	
	private void fire() {
        // dummy implementation:
        System.out.println("Fire!");
        subCommands.add("fireWeapon");
    }
	
	public void buttonClick()
	{
//		System.out.println("Hello World");
	}
	
	public HBox createNorthPane(Font font)
	{
		// Begin **Create NorthPane display items**	
		Label timeLabel= new Label("TIME:");
		timeLabel.setFont(font);
		timeLabel.setStyle("-fx-text-fill: #ffffff;");
		TextField timeTextField= new TextField();
		timeTextField.setPrefColumnCount(6);
		timeTextField.setFont(font);
		timeTextField.setStyle("-fx-background-color: rgba(0, 0, 0); -fx-text-inner-color: red;");
		HBox timeHBox = new HBox(10, timeLabel, timeTextField);
		timeHBox.setAlignment(Pos.CENTER);
		
		Label flotillaLabel= new Label("FLOTILLA SIZE:");
		flotillaLabel.setFont(font);
		flotillaLabel.setStyle("-fx-text-fill: #ffffff;");
		TextField flotillaTextField= new TextField();
		flotillaTextField.setPrefColumnCount(2);
		flotillaTextField.setFont(font);
		flotillaTextField.setStyle("-fx-background-color: rgba(0, 0, 0); -fx-text-inner-color: red;");
		HBox flotillaHBox = new HBox(10, flotillaLabel, flotillaTextField);
		flotillaHBox.setAlignment(Pos.CENTER);
		
		
		Label remainingShipsLabel= new Label("REMAINING SHIPS:");
		remainingShipsLabel.setFont(font);
		remainingShipsLabel.setStyle("-fx-text-fill: #ffffff;");
		TextField remaininsShipsTextField= new TextField();
		remaininsShipsTextField.setPrefColumnCount(2);
		remaininsShipsTextField.setFont(font);
		remaininsShipsTextField.setStyle("-fx-background-color: rgba(0, 0, 0); -fx-text-inner-color: red;");
		HBox remainingShipsHBox = new HBox(10, remainingShipsLabel, remaininsShipsTextField);
		remainingShipsHBox.setAlignment(Pos.CENTER);
		
		Label scoreLabel= new Label("SCORE:");
		scoreLabel.setFont(font);
		scoreLabel.setStyle("-fx-text-fill: #ffffff;");
		scoreTextField.setPrefColumnCount(7);
		scoreTextField.setFont(font);
		scoreTextField.setStyle("-fx-background-color: rgba(0, 0, 0); -fx-text-inner-color: red;");
		HBox scoreHBox = new HBox(10, scoreLabel, scoreTextField);
		scoreHBox.setAlignment(Pos.CENTER);
		// End **Create NorthPane display items
		
		damageGauge= createDamageGauge();
		
		HBox northPane= new HBox(20, timeHBox, flotillaHBox, damageGauge, remainingShipsHBox, scoreHBox);
		northPane.setAlignment(Pos.CENTER);
		northPane.setMinHeight(70);
		
		return northPane;
	}
	
	public Canvas createCenterPanel()
	{
		Canvas canvas= new Canvas(1200, 550);
		
		return canvas;
	}
	
	public StackPane createATube(String tubeLabel)
	{

		Circle torpedoTube= new Circle(0, 0, 15);
		torpedoTube.setFill(Color.RED);
		Text labelText= new Text(tubeLabel);
		
		StackPane layout= new StackPane();
		layout.getChildren().addAll(
				torpedoTube,
				labelText
		);
		layout.setPadding(new Insets(5));
		
		return layout;
	}
	
	public static void initLaunchTubes()
	{
		for (int index=0; index<6; index++)  {
			StackPane aStackPane= SeaWolf2.tubeArray[index];
			Node node= (Circle) aStackPane.getChildren().get(0);
			if (node instanceof Circle) {
				// Now change it's color
				((Circle) node).setFill(Color.RED);
				
			}
		}
	}
	
	public HBox createSouthPane(Font font)
	{
		Image imageIcon= new Image("/ImagesUserSubs/NewSubLtoRAMini2.png");
		subIcon1= new Label("");
		subIcon1.setGraphic(new ImageView(imageIcon));
		
		subIcon2= new Label("");
		subIcon2.setGraphic(new ImageView(imageIcon));
	
		subIcon3= new Label("");
		subIcon3.setGraphic(new ImageView(imageIcon));
		
		VBox subIconVbox= new VBox();
		subIconVbox.getChildren().add(subIcon1);
		subIconVbox.getChildren().add(subIcon2);
		subIconVbox.getChildren().add(subIcon3);
		subIconVbox.setAlignment(Pos.CENTER);
		
		
		Label statusLabel= new Label("STATUS:");
		statusLabel.setFont(font);
		statusLabel.setStyle("-fx-text-fill: #ffffff;");
				
		torpedoStatusTextField= new TextField();
		torpedoStatusTextField.setPrefColumnCount(7);
		torpedoStatusTextField.setAlignment(Pos.CENTER);
		torpedoStatusTextField.setFont(font);
		torpedoStatusTextField.setStyle("-fx-background-color: rgba(0, 0, 0); -fx-text-inner-color: red;");
		
		HBox statusHBox = new HBox(10, statusLabel, torpedoStatusTextField);
		statusHBox.setAlignment(Pos.CENTER);
		
		
		// Torpedo Tubes
		Circle torpedoTube1= new Circle(0, 0, 15);
		torpedoTube1.setFill(Color.RED);
		Text labelText= new Text("1");
		
		StackPane layout= new StackPane();
		layout.getChildren().addAll(
				torpedoTube1,
				labelText
		);
		layout.setPadding(new Insets(5));
		
		Circle torpedoTube2= new Circle(0, 0, 15);
		torpedoTube2.setFill(Color.RED);
		TextField tube2TextField= new TextField();
		tube2TextField.setPrefColumnCount(1);
		tube2TextField.setAlignment(Pos.CENTER);
		tube2TextField.setFont(font);
		tube2TextField.setStyle("-fx-background-color: rgba(0, 0, 0); -fx-text-inner-color: red;");
		
		Circle torpedoTube3= new Circle(0, 0, 15);
		torpedoTube3.setFill(Color.RED);
		TextField tube3TextField= new TextField();
		tube3TextField.setPrefColumnCount(1);
		tube3TextField.setAlignment(Pos.CENTER);
		tube3TextField.setFont(font);
		tube3TextField.setStyle("-fx-background-color: rgba(0, 0, 0); -fx-text-inner-color: red;");
		
		Circle torpedoTube4= new Circle(0, 0, 15);
		torpedoTube4.setFill(Color.RED);
		TextField tube4TextField= new TextField();
		tube4TextField.setPrefColumnCount(1);
		tube4TextField.setAlignment(Pos.CENTER);
		tube4TextField.setFont(font);
		tube4TextField.setStyle("-fx-background-color: rgba(0, 0, 0); -fx-text-inner-color: red;");
		
		tubeArray[0]= createATube("1");
		tubeArray[1]= createATube("2");
		tubeArray[2]= createATube("3");
		tubeArray[3]= createATube("4");
		tubeArray[4]= createATube("5");
		tubeArray[5]= createATube("6");
		
		HBox tubesHBox= new HBox(10,tubeArray[0], tubeArray[1], tubeArray[2], tubeArray[3], tubeArray[4], tubeArray[5]);
		tubesHBox.setStyle("-fx-background-color: #000000;");
		tubesHBox.setAlignment(Pos.CENTER);
		tubesHBox.setMinHeight(30);
		tubesHBox.setMaxHeight(30);
		tubesHBox.setPrefHeight(30);
		
		
		Label torpedosLabel= new Label("TORPEDOS:");
		torpedosLabel.setFont(font);
		torpedosLabel.setStyle("-fx-text-fill: #ffffff;");
		torpedosTextField.setPrefColumnCount(2);
		torpedosTextField.setFont(font);
		torpedosTextField.setStyle("-fx-background-color: rgba(0, 0, 0); -fx-text-inner-color: red;");
		HBox torpedosHBox = new HBox(10, torpedosLabel, torpedosTextField);
		torpedosHBox.setAlignment(Pos.CENTER);
		
		Label bulletsLabel= new Label("BULLETS:");
		bulletsLabel.setFont(font);
		bulletsLabel.setStyle("-fx-text-fill: #ffffff;");
		bulletsTextField= new TextField();
		bulletsTextField.setPrefColumnCount(2);
		bulletsTextField.setFont(font);
		bulletsTextField.setStyle("-fx-background-color: rgba(0, 0, 0); -fx-text-inner-color: red;");
		HBox bulletsHBox = new HBox(10, bulletsLabel, bulletsTextField);
		bulletsHBox.setAlignment(Pos.CENTER);
		
		Label modeLabel= new Label("MODE:");
		modeLabel.setFont(font);
		modeLabel.setStyle("-fx-text-fill: #ffffff;");
		modeTextField= new TextField();
		modeTextField.setPrefColumnCount(4);
		modeTextField.setFont(font);
		modeTextField.setStyle("-fx-background-color: rgba(0, 0, 0); -fx-text-inner-color: red;");
		HBox modeHBox = new HBox(10, modeLabel, modeTextField);
		modeHBox.setAlignment(Pos.CENTER);
		
		Image antiMineImageIcon= new Image("/ImageAntiMineOps/AntiMineIconSlash.png");
		antiMineOpsIcon= new Label("");
		antiMineOpsIcon.setGraphic(new ImageView(antiMineImageIcon));
		
		HBox southPane= new HBox(20, subIconVbox, modeHBox, statusHBox, tubesHBox, torpedosHBox, bulletsHBox, antiMineOpsIcon);
		southPane.setAlignment(Pos.CENTER);
		southPane.setMinHeight(70);
		
		return southPane;
	}
	
	public Gauge createDamageGauge()
	{
		Gauge gauge = GaugeBuilder.create()
                .prefSize(58,58)
                .title("Damage Meter")
                .valueVisible(true)
                .backgroundPaint(Color.WHITE)
                .scaleDirection(ScaleDirection.CLOCKWISE)
                .tickLabelLocation(TickLabelLocation.OUTSIDE)
                .minValue(0)
                .maxValue(100)
                .startAngle(320)
                .angleRange(280)
                .zeroColor(Color.ORANGE)
                .majorTickMarkType(TickMarkType.TRIANGLE)
                .areasVisible(true)
                .areas(new Section(0, 30, Color.GREEN), new Section(30, 70, Color.YELLOW), new Section(70, 100, Color.RED))
                .markersVisible(true)
                .markers(new Marker(0.75, "Marker 1", Color.MAGENTA))
                .needleColor(Color.BLACK)
                .needleSize(NeedleSize.THICK)
                .build();
		
		
		return gauge;
	}
	
	
	public static void updateNumShipsLeftDisplay()
	{
		
		switch (numSubs) {
		
			case 3:
				subIcon1.setVisible(false);
				break;
			case 2:
				subIcon2.setVisible(false);
				break;
			case 1:
				subIcon3.setVisible(false);
				break;
				
			default:
				break;

		}
	}
	
	public static void resetNumShips()
	{
		subIcon1.setVisible(true);
		subIcon2.setVisible(true);
		subIcon3.setVisible(true);
	}
	
	
	public static void debugOut(String msg) {
		Date date= new Date();
		Timestamp timeStamp= new Timestamp(date.getTime());
		System.out.println(timeStamp + " Msg: " + msg);
	}
	
	public static void updateDamageLevel()
	{
		
		if (subDamageLevelCounter <= 3) {
			debugOut("Incrementing subDamageLevel");
			subDamageLevelCounter++;
		}
		
		switch (subDamageLevelCounter) {
		case 1:
			GameAction.spinDamageGaugeValue= 25;
			break;
		case 2:
			GameAction.spinDamageGaugeValue= 50;
			break;
		case 3:
			GameAction.spinDamageGaugeValue= 75;
			break;
		case 4:
			GameAction.spinDamageGaugeValue= 100;
			break;
			
		default:
			
			System.out.println("Should not be here!");
			System.exit(0);
			
		}			
	
	}
	
	public static void updateBulletCount()
	{
		numBullets--;
		bulletsTextField.setText(Integer.toString(numBullets));
		
	}
	
	public static void updateTorpedoCount()
	{
		numTorpedoes--;
		torpedosTextField.setText(Integer.toString(numTorpedoes));
		
	}
	
	public static void updateTorpedoTubePanel()
	{
		StackPane aStackPane= tubeArray[torpedoTube];
		Node node= (Circle) aStackPane.getChildren().get(0);
		if (node instanceof Circle) {
			// Now change it's color
			((Circle) node).setFill(Color.valueOf("#990000"));
			torpedoTube++;

			if (torpedoTube== 6) {
				// Setup timer to make "Reload" blink
				Timer makeStatusBlinkTimer= new Timer();

				makeStatusBlinkTimer.schedule(new MakeStatusBlinkTask(), LOADING_MILLIS);
				
				SeaWolf2.awaitingReload= true;
			}
		}


	}
	
	public static void deactivateTorpedoTubes()
	{
		for (int index=0; index<6; index++)  {
			StackPane aStackPane= SeaWolf2.tubeArray[index];
			Node node= (Circle) aStackPane.getChildren().get(0);
			if (node instanceof Circle) {
				// Now change it's color
				((Circle) node).setFill(Color.valueOf("#990000"));
				
			}
		}
	}
	
	public static void activateTorpedoTubes()
	{
		// Get all the Torpedo Tubes Ready again (i.e.make them Red)
		for (int index=0; index<6; index++)  {
			StackPane aStackPane= SeaWolf2.tubeArray[index];
			Node node= (Circle) aStackPane.getChildren().get(0);
			if (node instanceof Circle) {
				// Now change it's color
				((Circle) node).setFill(Color.RED);
				
			}
		}
	}
}


class MakeStatusBlinkTask extends TimerTask {
	
	private static int blinkCount;
	
	public MakeStatusBlinkTask() {
		
	}
	
	public void debugOut(String msg) {
		Date date= new Date();
		Timestamp timeStamp= new Timestamp(date.getTime());
		System.out.println(timeStamp + " Msg: " + msg);
	}
	
	// TODO - block launching of future Torpedos until everything is reset!!!!!
	public void run() {
	

		System.out.println("Inside run method");
		System.out.println("value of blinkcount: " + blinkCount);

		if (blinkCount < 6) {
			
			SeaWolf2.torpedoStatusTextField.setStyle("-fx-background-color: rgba(0, 0, 0); -fx-text-inner-color: yellow;");
			
			System.out.println("inside test for blinkCount1");
			
//			System.out.println(0 % 0);
//			System.out.println(0 % 1);
//			System.out.println(0 % 2);
			
			System.out.println("inside test for blinkCount2");

			if (blinkCount % 2== 0) {
				SeaWolf2.torpedoTubesStatus= "Loading";	
				System.out.println("Setting value to Loading");
			} else {
				SeaWolf2.torpedoTubesStatus= "Empty";
				System.out.println("Setting value to Empty");
			}
			
			blinkCount++;

			Timer createBlinkTimer= new Timer();
			createBlinkTimer.schedule(new MakeStatusBlinkTask(), SeaWolf2.LOADING_MILLIS);
		}
		else {
			
			SeaWolf2.torpedoStatusTextField.setStyle("-fx-background-color: rgba(0, 0, 0); -fx-text-inner-color: red;");
			SeaWolf2.torpedoTubesStatus= "Ready";
			
			// Get all the Torpedo Tubes Ready again (i.e.make them Red)
			for (int index=0; index<6; index++)  {
				StackPane aStackPane= SeaWolf2.tubeArray[index];
				Node node= (Circle) aStackPane.getChildren().get(0);
				if (node instanceof Circle) {
					// Now change it's color
					((Circle) node).setFill(Color.RED);
					
				}
			}
			
			SeaWolf2.awaitingReload= false;
			SeaWolf2.torpedoTube= 0;
			blinkCount= 0;
		}

	}

}


