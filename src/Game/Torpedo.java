package Game;

import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;

import java.net.URL;

public class Torpedo extends Sprite implements ImageDisplayFunctions, ImageMoveFunctions {

	private final static int MOVEMENTINC= -9;
	private boolean aliveStatus;
	private boolean hitMine= false;
	private boolean hitShip= false;
	
	private boolean torpedoDestroyed;
	
	private static Image torpedoNorthImage;
	
	
	private static Image torpedoNorthImageA;
	private static Image torpedoNorthImageB;
	
	private static int torpedoNorthImageWidth;
	private static int torpedoNorthImageWidthA;
	private static int torpedoNorthImageWidthB;
	
	private static int torpedoNorthImageHeight;
	private static int torpedoNorthImageHeightA;
	private static int torpedoNorthImageHeightB;
	
	private static Image torpedoLeftTiltImage;
	private static Image torpedoLeftTiltImageA;
	private static Image torpedoLeftTiltImageB;
	
//	private static int torpedoLeftTiltImageWidth;
	private static int torpedoLeftTiltImageWidthA;
	private static int torpedoLeftTiltImageWidthB;
	
//	private static int torpedoLeftTiltImageHeight;
	private static int torpedoLeftTiltImageHeightA;
	private static int torpedoLeftTiltImageHeightB;
	
	private static Image torpedoRightTiltImage;
	private static Image torpedoRightTiltImageA;
	private static Image torpedoRightTiltImageB;
	
//	private static int torpedoRightTiltImageWidth;
	private static int torpedoRightTiltImageWidthA;
	private static int torpedoRightTiltImageWidthB;
	
//	private static int torpedoRightTiltImageHeight;
	private static int torpedoRightTiltImageHeightA;
	private static int torpedoRightTiltImageHeightB;
	
	private Image currentImage;

	private static JukeBox jukeBox= new JukeBox();
	
	private static String[] torpedoNorthFracImagesFilenames;
	private static String[] torpedoLeftTiltFracImagesFilenames;
	private static String[] torpedoRightTiltFracImagesFilenames;
	
	private static Image[] torpedoNorthFracImages;
	private static Image[] torpedoLeftTiltFracImages;
	private static Image[] torpedoRightTiltFracImages;
	
	enum Direction {Left_Tilt, North, Right_Tilt};
	
	private int numTimesExplisionShown;
	
	private int torpedoMovesSinceHit;
	
	private int torpedoSoundEffectDelay= 0;
	
	
	enum TorpedoDirection { TorpedoLeft, TorpedoNorth, TorpedoRight };
	TorpedoDirection torpedoDirection;
	
	private int movementCounter;
	
	private int movementUpdateCount;
	
	private static MediaPlayer torpedoDestructClip;
	
	private static boolean torpedoDataInitialized= initTorpedoData(); 
	
	public Torpedo(int gumImageIndexSelected, int movementUpdateCount, int xloc, int yloc, int nozzleOffset, SeaWolfSubmarine.Direction subDirection) {
		
		super(MOVEMENTINC);
		
		aliveStatus= true;
		
		switch (gumImageIndexSelected) {

		case 0:
			if (subDirection== SeaWolfSubmarine.Direction.L_to_R)
				this.xloc= xloc + nozzleOffset;
			else
				this.xloc= xloc + nozzleOffset + 31;
			
			this.yloc= yloc + 7;
			
			currentImage= torpedoLeftTiltImageA;
			torpedoDirection= TorpedoDirection.TorpedoLeft;
			break;
		case 1:
			if (subDirection== SeaWolfSubmarine.Direction.L_to_R)
				this.xloc= xloc + nozzleOffset;
			else
				this.xloc= xloc + nozzleOffset + 31;
			
			this.yloc= yloc + 7;
			
			currentImage= torpedoNorthImageA;
			torpedoDirection= TorpedoDirection.TorpedoNorth;
			break;
		case 2:
			if (subDirection== SeaWolfSubmarine.Direction.L_to_R)
				this.xloc= xloc + nozzleOffset;
			else
				this.xloc= xloc + nozzleOffset + 31;
			
			this.yloc= yloc + 7;
			
			currentImage= torpedoRightTiltImageB;
			torpedoDirection= TorpedoDirection.TorpedoRight;
			break;

		default:
			System.out.println("Should not be here!");
				
		}
		
		torpedoDestroyed= false;
		
		numTimesExplisionShown= 0;
		
		torpedoMovesSinceHit= 0;
		
//		this.soundPlayer= soundPlayer;
		
		movementCounter= 0;
		
		this.movementUpdateCount= movementUpdateCount;	
	}

	
	private static boolean initTorpedoData() {
		
		Image image= new Image("/TorpedoImages/TorpedoNorth.png");
		torpedoNorthImage= image;
		torpedoNorthImageWidth= (int) torpedoNorthImage.getWidth();
		torpedoNorthImageHeight= (int) torpedoNorthImage.getHeight();

		image= new Image("/TorpedoImages/TorpedoNorthExh1.png");
		torpedoNorthImageA= image;
		torpedoNorthImageWidthA= (int) torpedoNorthImageA.getWidth();
		torpedoNorthImageHeightA= (int) torpedoNorthImageA.getHeight();

		image= new Image("/TorpedoImages/TorpedoNorthExh2.png");
		torpedoNorthImageB= image;
		torpedoNorthImageWidthB= (int) torpedoNorthImageB.getWidth();
		torpedoNorthImageHeightB= (int) torpedoNorthImageB.getHeight();

		image= new Image("/TorpedoImages/TorpedoLeftTiltExh1.png");
		torpedoLeftTiltImageA= image;	
		torpedoLeftTiltImageWidthA= (int) torpedoLeftTiltImageA.getWidth();
		torpedoLeftTiltImageHeightA= (int) torpedoLeftTiltImageA.getHeight();

		image= new Image("/TorpedoImages/TorpedoLeftTiltExh2.png");
		torpedoLeftTiltImageB= image;	
		torpedoLeftTiltImageWidthB= (int) torpedoLeftTiltImageB.getWidth();
		torpedoLeftTiltImageHeightB= (int) torpedoLeftTiltImageB.getHeight();

		image= new Image("/TorpedoImages/TorpedoRightTiltExh1.png");
		torpedoRightTiltImageA= image;
		torpedoRightTiltImageWidthA= (int) torpedoRightTiltImageA.getWidth();
		torpedoRightTiltImageHeightA= (int) torpedoRightTiltImageA.getHeight();

		image= new Image("/TorpedoImages/TorpedoRightTiltExh2.png");
		torpedoRightTiltImageB= image;
		torpedoRightTiltImageWidthB= (int) torpedoRightTiltImageB.getWidth();
		torpedoRightTiltImageHeightB= (int) torpedoRightTiltImageB.getHeight();

		torpedoNorthFracImagesFilenames= new String[3];
		torpedoNorthFracImagesFilenames[0]= "/TorpedoFracImages/TorpedoNorthFrac1C.png";
		torpedoNorthFracImagesFilenames[1]= "/TorpedoFracImages/TorpedoNorthFrac2C.png";
		torpedoNorthFracImagesFilenames[2]= "/TorpedoFracImages/TorpedoNorthFrac3C.png";

		torpedoLeftTiltFracImagesFilenames= new String[3];
		torpedoLeftTiltFracImagesFilenames[0]= "/TorpedoFracImages/TorpedoLeftTiltFrac1C.png";
		torpedoLeftTiltFracImagesFilenames[1]= "/TorpedoFracImages/TorpedoLeftTiltFrac2C.png";
		torpedoLeftTiltFracImagesFilenames[2]= "/TorpedoFracImages/TorpedoLeftTiltFrac3C.png";

		torpedoRightTiltFracImagesFilenames= new String[3];
		torpedoRightTiltFracImagesFilenames[0]= "/TorpedoFracImages/TorpedoRightTiltFrac1C.png";
		torpedoRightTiltFracImagesFilenames[1]= "/TorpedoFracImages/TorpedoRightTiltFrac2C.png";
		torpedoRightTiltFracImagesFilenames[2]= "/TorpedoFracImages/TorpedoRightTiltFrac3C.png";


		torpedoNorthFracImages= new Image[3];
		int index1= 0;
		for (String aTorpedoNorthFracImage: torpedoNorthFracImagesFilenames )
		{
			Image aImageIcon= new Image(aTorpedoNorthFracImage);
			Image aTorpedoImage= aImageIcon;
			torpedoNorthFracImages[index1++]= aTorpedoImage;
		}


		torpedoLeftTiltFracImages= new Image[3];
		index1= 0;
		for (String aTorpedoLeftTiltFracImage: torpedoLeftTiltFracImagesFilenames )
		{
			Image aImageIcon= new Image(aTorpedoLeftTiltFracImage);
			Image aTorpedoImage= aImageIcon;
			torpedoLeftTiltFracImages[index1++]= aTorpedoImage;
		}

		torpedoRightTiltFracImages= new Image[3];
		index1= 0;
		for (String aTorpedoRightTiltFracImage: torpedoRightTiltFracImagesFilenames )
		{
			Image aImageIcon= new Image(aTorpedoRightTiltFracImage);
			Image aTorpedoImage= aImageIcon;
			torpedoRightTiltFracImages[index1++]= aTorpedoImage;
		}
		
//		SoundPlayer soundPlayer= new SoundPlayer();
		torpedoDestructClip= jukeBox.getTorpedoDestructClip();
	
		
		return true;
	}
	
	
	public void move() {
		
		movementCounter++;
		
		if (movementCounter== movementUpdateCount) {
			if (yloc<= (GameAction.SEA_LANES_START_Y + GameAction.SEA_LANE_ROW_HEIGHT) + 10) {   // Was 70
				
				// End of the run for this mine.
				// Somehow flag it for destruction.
	
				aliveStatus= false;
			}
			else {
				switch (torpedoDirection) {
				
				case TorpedoLeft:
	
					yloc+=movementInc;
					xloc+= -3;
	
					break;
					
				case TorpedoNorth:
	
					yloc+=movementInc;
		
					break;
					
				case TorpedoRight:
	
					yloc+=movementInc;
					xloc+= 3;
					
					break;
				
				default:
					break;
		
				}
			}
			
			if (hitMine== true)
			{
				torpedoSoundEffectDelay+=1;
				if (torpedoSoundEffectDelay== 2)
				{
					torpedoDestructClip.play();
//					soundPlayer.getTorpedoDestructClip().setFramePosition(0);  // Rewind
//					soundPlayer.getTorpedoDestructClip().start();
					torpedoSoundEffectDelay= 0;
				}
			} else if (hitShip== true)
			{
				torpedoDestructClip.play();
//				soundPlayer.getTorpedoHitClip().setFramePosition(0); //Rewind
			}
			
			movementCounter= 0;
		}
	}	
		
	public void hitMine() {
		hitMine= true;
	}
	
	public void hitShip() {
		hitShip= true;
	}
	
	public boolean aliveStatus() {
		return aliveStatus;
	}
	
	public void killTorpedo() {
		aliveStatus= false;
	}
	
	// Use a timer for this??
	public Image getImage() {
		if (hitMine== true)
		{
			switch (torpedoDirection) {
			case TorpedoLeft:
				if (numTimesExplisionShown < 2)
				{
					torpedoLeftTiltImage= torpedoLeftTiltFracImages[0];
					numTimesExplisionShown++;
				}
				else if (numTimesExplisionShown < 4)
				{
					torpedoLeftTiltImage= torpedoLeftTiltFracImages[1];
					numTimesExplisionShown++;
				}
				else if (numTimesExplisionShown < 6)
				{
					torpedoLeftTiltImage= torpedoLeftTiltFracImages[2];
					numTimesExplisionShown++;
				}
				else
				{
					killTorpedo();
					torpedoDestructClip.stop();
//					soundPlayer.getTorpedoDestructClip().stop();
				}
				// break;
				
				return torpedoLeftTiltImage;
				
			case TorpedoNorth:
				if (numTimesExplisionShown < 2)
				{
					torpedoNorthImage= torpedoNorthFracImages[0];
					numTimesExplisionShown++;
				}
				else if (numTimesExplisionShown < 4)
				{
					torpedoNorthImage= torpedoNorthFracImages[1];
					numTimesExplisionShown++;
				}
				else if (numTimesExplisionShown < 6)
				{
					torpedoNorthImage= torpedoNorthFracImages[2];
					numTimesExplisionShown++;
				}
				else
				{
					killTorpedo();
					torpedoDestructClip.stop();
//					soundPlayer.getTorpedoDestructClip().stop();
				}
				
				return torpedoNorthImage;
				
			case TorpedoRight:
				if (numTimesExplisionShown < 2)
				{
					torpedoRightTiltImage= torpedoRightTiltFracImages[0];
					numTimesExplisionShown++;
				}
				else if (numTimesExplisionShown < 4)
				{
					torpedoRightTiltImage= torpedoRightTiltFracImages[1];
					numTimesExplisionShown++;
				}
				else if (numTimesExplisionShown < 6)
				{
					torpedoRightTiltImage= torpedoRightTiltFracImages[2];
					numTimesExplisionShown++;
				}
				else
				{
					killTorpedo();
//					soundPlayer.getTorpedoDestructClip().stop();
					torpedoDestructClip.stop();
				}
				
				return torpedoRightTiltImage;
			}
			return currentImage;  // Never gets executed
		}
		else if (hitShip== true) {
			
			if (torpedoMovesSinceHit < 2)
			{
				torpedoDestructClip.play();
//				soundPlayer.getTorpedoDestructClip().start();
				torpedoMovesSinceHit++;
			}
			else
			{
				killTorpedo();
			}
		}
		
		if (hitMine== false && hitShip== false) {
			switch (torpedoDirection) {
			case TorpedoNorth:
				if (currentImage== torpedoNorthImageA)
				{
					currentImage= torpedoNorthImageB;
				}
				else if (currentImage== torpedoNorthImageB)
				{
					currentImage= torpedoNorthImageA;
				}
				break;
				
			case TorpedoLeft:	
				if (currentImage== torpedoLeftTiltImageA)
				{
					currentImage= torpedoLeftTiltImageB;
				}
				else if (currentImage== torpedoLeftTiltImageB)
				{
					currentImage= torpedoLeftTiltImageA;
				}
				break;
				
			case TorpedoRight:
				if (currentImage== torpedoRightTiltImageA)
				{
					currentImage= torpedoRightTiltImageB;
				}
				else if (currentImage== torpedoRightTiltImageB)
				{
					currentImage= torpedoRightTiltImageA;
				}
				break;
			}

		}
		
		return currentImage;
	}

	public int getX() {
   		return xloc;
	}

	public int getY() {
		return yloc;
	}
	
	public Rectangle getBounds() {
		Rectangle aRectangle= null;
		
		switch (torpedoDirection) {
		
		case TorpedoLeft:
			
			if (currentImage== torpedoLeftTiltImageA) {
				
				aRectangle= new Rectangle(xloc, yloc, torpedoLeftTiltImageWidthA, torpedoLeftTiltImageHeightA);
			} 
			else if (currentImage== torpedoLeftTiltImageB) {
				
				aRectangle= new Rectangle(xloc, yloc, torpedoLeftTiltImageWidthB, torpedoLeftTiltImageHeightB);
			}
			
			break;

			
		case TorpedoNorth:
			
			aRectangle= new Rectangle(xloc, yloc, torpedoNorthImageWidth, torpedoNorthImageHeight);
			break;
	
			
		case TorpedoRight:
			
			if (currentImage== torpedoRightTiltImageA) {
				
				aRectangle= new Rectangle(xloc, yloc, torpedoRightTiltImageWidthA, torpedoRightTiltImageHeightA);
				
			}
			else if (currentImage== torpedoRightTiltImageB) {
				
				aRectangle= new Rectangle(xloc, yloc, torpedoRightTiltImageWidthB, torpedoRightTiltImageHeightB);
				
			}
			
			break;
		   
		}
		return aRectangle;
	}
	
	public boolean isTorpedoDestroyed()
	{
		return torpedoDestroyed;
	}
	
	private static URL getImageURL(String imageFilename) {
		URL aURL= null;
		
		aURL= Torpedo.class.getResource(imageFilename);
   
	     return aURL;
	}
	
	public void finalize()
	{
		torpedoDestructClip.dispose();
	}
}
