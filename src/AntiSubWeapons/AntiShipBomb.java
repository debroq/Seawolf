package AntiSubWeapons;

import javafx.scene.shape.Rectangle;
import java.net.URL;


import Aircraft.Airplane;
import Game.GameAction;
import Game.ImageDisplayFunctions;
import Game.ImageMoveFunctions;
import Game.JukeBox;
import Game.Sprite;
import Game.SubmarineStruckByExplosiveCollisionFunctions;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;

//public class AntiShipBomb extends Sprite implements ImageDisplayFunctions, ImageMoveFunctions {
public class AntiShipBomb extends Sprite implements ImageDisplayFunctions, SubmarineStruckByExplosiveCollisionFunctions, ImageMoveFunctions {
	private final static int MOVEMENTINC=3;
	
	private static Image antiShipBombImage;
	
	private static int antiShipBombImageWidth;
	private static int antiShipBombImageHeight;
	
	private boolean hitGun= false;
	
	private static JukeBox jukeBox;
	private static MediaPlayer bombEnteredWaterMediaPlayer;  // Something probably wrong on this line!!
	
	private static boolean antiShipBombDataInitialized= initAntiShipBombData();
	
	public AntiShipBomb(int xloc) {
		
		super(MOVEMENTINC);
		
		Image imageIcon= new Image("/ImagesAntiShipBomb/AntiShipBomb.png");
		antiShipBombImage= imageIcon;
		antiShipBombImageWidth= (int) antiShipBombImage.getWidth();
		antiShipBombImageHeight= (int) antiShipBombImage.getHeight();
		
		this.xloc= xloc;
		yloc= GameAction.AIRPLANE_DISPLAY_ROW+ 40;
		
//		soundPlayer= new SoundPlayer();
//		bombEnteredWaterMediaPlayer= soundPlayer.getAntiShipBombEnteredWaterClip();
	}
	
	private static boolean initAntiShipBombData() {
		
		Image imageIcon= new Image("/ImagesAntiShipBomb/AntiShipBomb.png");
		antiShipBombImage= imageIcon;
		antiShipBombImageWidth= (int) antiShipBombImage.getWidth();
		antiShipBombImageHeight= (int) antiShipBombImage.getHeight();
		
		jukeBox= new JukeBox();
		bombEnteredWaterMediaPlayer= jukeBox.getAntiShipBombEnteredWaterClip();
		
		return true;
	}
	
	public Image getImage() {
		return antiShipBombImage;
	}
	
	public void move() {
		yloc+= movementInc;
	}
	
	public int getX() {
		return xloc;
	}
	
	public int getY() {
		
		if (yloc== 62 )
		{
			bombEnteredWaterMediaPlayer.play();
		}
		return yloc;
	}
	
	public boolean aliveStatus() {
		return true;
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
	
	private static URL getImageURL(String imageFilename) {
		URL aURL= null;
		
//		ClassLoader cl = this.getClass().getClassLoader();
		
		aURL= Airplane.class.getResource(imageFilename);
   
	     return aURL;
	}
	
	// Never used
	public boolean isHit() {
		return false;
	}
	
}
