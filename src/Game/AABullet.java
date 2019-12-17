package Game;

import javafx.scene.shape.Rectangle;
import java.net.URL;

import javafx.scene.image.Image;

public class AABullet implements ImageDisplayFunctions, ImageMoveFunctions {
	
	private int xLoc;
	private int yLoc;
	
	private boolean aliveStatus;
	private int bulletImageWidth;
	private int bulletImageHeight;
	private Image bulletImage;
	private boolean hitAirborne;
	
	private enum Direction {Degrees135, Degrees90, Degrees45};
	
//	private Direction bulletDirection;
	private int activeGunIndex;
	
	public AABullet(int activeGunIndex, int xLoc, int yLoc, SeaWolfSubmarine.Direction subDirection) {
		
		Image imageIcon= new Image("/ImagesAABullet/DblAABullet.png");
		bulletImage= imageIcon;
		
		this.xLoc= xLoc;
		this.yLoc= yLoc;
		
		bulletImageWidth= (int) bulletImage.getWidth();
		bulletImageHeight= (int) bulletImage.getHeight();
		
		aliveStatus= true;
		
//		bulletDirection= Direction.Degrees45;
		
		this.activeGunIndex= activeGunIndex;
		
		// Offset the X coordinate based on angle of the gun
		
		if (subDirection== SeaWolfSubmarine.Direction.L_to_R)
			switch (activeGunIndex) {
				case 1:
 					this.xLoc+= 83;
					break;
					
				case 2:
					this.xLoc+= 79;
					break;
					
				case 3:  // Was 2
					this.xLoc+= 75;
					break;
					
				case 4:
					this.xLoc+= 73;
					break;
					
				case 5:  // Was 3
					this.xLoc+= 70;
					break;
				}
		else
		{
			switch (activeGunIndex) {
			case 1:
				this.xLoc+= 23;
				break;
				
			case 2:
				this.xLoc+= 17;
				break;
				
			case 3:  // Was 2
				this.xLoc+= 12;
				break;
				
			case 4:
				this.xLoc+= 10;
				break;
				
			case 5:  // Was 3
				this.xLoc+= 8;
				break;
			}
		}
		
	}
	
	public Image getImage() {
		return bulletImage;
	}
	
	public void move() {
		
		if (yLoc<= 9) {
			
			aliveStatus= false;
		}
		else {
			switch (activeGunIndex) {
			case 1:  // 45 
					xLoc = xLoc + 3;
					yLoc = yLoc -3;
				break;
				
			case 2: // 66
					xLoc = xLoc + 3;
					yLoc = yLoc - 6;
				break;
				
			case 3:  // 90  // Was 2
					yLoc= yLoc - 3;
				break;
				
			case 4: // 114
					xLoc = xLoc - 3;
					yLoc= yLoc - 6;
				break;
				
			case 5:  // 135  // Was 3
					xLoc= xLoc - 3;
					yLoc= yLoc - 3;
				break;
			}
		}	
	}
	
	public int getX() {
		return xLoc;
		
	}
	
	public int getY() {
		return yLoc;
	}
	
	public boolean aliveStatus() {
		return aliveStatus;
	}
	
	public void hitAirborne() {
		hitAirborne= true;
	}
	
	public boolean isHitAirborne() {
		return hitAirborne;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(xLoc, yLoc, bulletImageWidth, bulletImageHeight);
	}
	
	private URL getImageURL(String imageFilename) {
		URL aURL= null;
		
//		ClassLoader cl = this.getClass().getClassLoader();
		
		aURL= AABullet.class.getResource(imageFilename);
   
	     return aURL;
	}
	

}
