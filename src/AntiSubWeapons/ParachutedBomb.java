package AntiSubWeapons;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
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
import javafx.scene.shape.Rectangle;
import Game.AircraftStruckByBulletCollisionFunctions;
import Game.ImageDisplayFunctions;
import Game.ImageMoveFunctions;
import Game.SeaWolfSubmarine;
import Game.JukeBox;
import Game.Sprite;
import Game.SubmarineStruckByExplosiveCollisionFunctions;
import Game.SeaWolf2;



public class ParachutedBomb extends Sprite implements ImageDisplayFunctions, SubmarineStruckByExplosiveCollisionFunctions, ImageMoveFunctions,AircraftStruckByBulletCollisionFunctions  {
	public final static int MOVEMENTINC=2;
	private boolean aliveStatus;
	
	private Image currentImage;
	private static Image[][] parachutedBombImages;
	
	private static String[] parachutedBombExplodingImageFilenames;
	
	private static Image[] parachutedBombExplodingImages;
	
	private static int parachutedBombImageWidth;
	private static int parachutedBombImageHeight;

	
	// Maybe create 5 MediaPlayer and load as necessary?
	private static MediaPlayer mediaBombEnteredWaterClip;
	private static MediaPlayer parachuteOpenedClip;
	private static MediaPlayer explosionClip;
	
	private class Point {
		private int xloc;
		private int yloc;
		
		public Point(int xloc, int yloc) {
			
			this.xloc= xloc;
			this.yloc= yloc;
			
		}
		
		public int getX() {
			return xloc;
		}
		
		public int getY() {
			return yloc;
		}
	}
	
	private ArrayList<Point> wayPoints= new ArrayList<Point>();
	
	private boolean hitGun= false;
	
	private JukeBox jukeBox;
	
	private int numTimesShown;

	private int wayPointsListCounter;

	private boolean beenHereBefore;

	
	private boolean parachutedBombHit;
	
	ScheduledFuture scheduledFuture;
	private ScheduledExecutorService scheduledExecutorService;
	
	
	private boolean explosionTimerScheduled;
	
	
	private static boolean parachutedBombDataInitialized= initParachutedBombData();
	
	public ParachutedBomb(Direction direction, int xloc) {
		
		super(MOVEMENTINC);
		
		this.xloc= xloc;
		this.yloc= SeaWolf2.HELICOPTER_DISPLAY_ROW+ 5;
		
		this.direction= direction;
		
//		this.soundPlayer= soundPlayer;
	
		
		parachutedBombHit= false;
		
		parachutedBombExplodingImageFilenames= new String[4];
		
		parachutedBombExplodingImageFilenames[0]= "/ImagesParachutedMineHit/CruiseMissileDown1x.png";
		parachutedBombExplodingImageFilenames[1]= "/ImagesParachutedMineHit/CruiseMissileDown2x.png";
		parachutedBombExplodingImageFilenames[2]= "/ImagesParachutedMineHit/CruiseMissileDown3x.png";
		parachutedBombExplodingImageFilenames[3]= "/ImagesParachutedMineHit/CruiseMissileDown4x.png";
		
		parachutedBombExplodingImages= new Image[4];
		
		for (int index= 0; index< 4; index++) {
			Image aImageIcon= new Image(parachutedBombExplodingImageFilenames[index]);
			parachutedBombExplodingImages[index]= aImageIcon;
		}
		
		// Experiment with ExecutorService
		scheduledExecutorService =
		        Executors.newScheduledThreadPool(5);
		
		aliveStatus= true;
		
		parachuteOpenedClip.seek(parachuteOpenedClip.getStartTime());
        parachuteOpenedClip.play();
	}
	
	private static boolean initParachutedBombData() {
		
		parachutedBombImages= new Image[2][4];
		
		Image imageIcon= new Image("/ImagesCruiseMissile/CruiseMissileLtoR.png");
		parachutedBombImages[0][0]= imageIcon;
				
		imageIcon= new Image("/ImagesCruiseMissile/CruiseMissileLtoR30.png");
		parachutedBombImages[0][1]= imageIcon;	
			
		imageIcon= new Image("/ImagesCruiseMissile/CruiseMissileLtoR60.png");
		parachutedBombImages[0][2]= imageIcon;	
				
		imageIcon= new Image("/ImagesCruiseMissile/CruiseMissileDown.png");
		parachutedBombImages[0][3]= imageIcon;	
			
		imageIcon= new Image("/ImagesCruiseMissile/CruiseMissileRtoL.png");
		parachutedBombImages[1][0]= imageIcon;
				
		imageIcon= new Image("/ImagesCruiseMissile/CruiseMissileRtoL30.png");
		parachutedBombImages[1][1]= imageIcon;	
			
		imageIcon= new Image("/ImagesCruiseMissile/CruiseMissileRtoL60.png");
		parachutedBombImages[1][2]= imageIcon;	
			
		imageIcon= new Image("/ImagesCruiseMissile/CruiseMissileDown.png");
		parachutedBombImages[1][3]= imageIcon;	
		
		parachutedBombImageWidth= (int) parachutedBombImages[1][3].getWidth();
		parachutedBombImageHeight= (int) parachutedBombImages[1][3].getHeight();
		
		JukeBox jukeBox= new JukeBox();
			
		mediaBombEnteredWaterClip= jukeBox.getAntiShipBombEnteredWaterClip();
		parachuteOpenedClip= jukeBox.getParachuteOpenClip();
		explosionClip= jukeBox.getExplosionClip();
		
		return true;
	}
	
	public Image getImage() {

		int imageIndex= 3;

		if (parachutedBombHit== true) {

			scheduledFuture =
					scheduledExecutorService.scheduleAtFixedRate(animateParachutedBombExplodingTask, 2, 5000, TimeUnit.MILLISECONDS);

			explosionTimerScheduled= true;
		}

		if (explosionTimerScheduled== false) {

			if (numTimesShown < 6)
			{
				imageIndex= 0;
				numTimesShown++;
			}
			else if (numTimesShown >= 6 && numTimesShown < 12)
			{
				imageIndex= 1;
				numTimesShown++;
			}
			else if (numTimesShown >= 12 && numTimesShown < 18 )
			{

				imageIndex= 2;
				numTimesShown++;

				if(numTimesShown== 17) {
					// Only want to do this once
					wayPointsListCounter= 0;
					try {
						generateWayPoints2();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else {

				imageIndex= 3;

			}


			if (direction== Direction.L_TO_R) {

				return currentImage= parachutedBombImages[0][imageIndex];

			} else {

				return currentImage= parachutedBombImages[1][imageIndex];

			}	

		} else {

			return currentImage;
		}

	}
	
	public void stopAudio() {
		
	}
	
	public int getPoints() {
		return 0;
	}
	
	public void move() {

		if (yloc> 513) {  // 473

			aliveStatus= false;

			//			soundPlayer.getCruiseMissileTravelClip().stop();
		}
		else {
			// Begin of if
			if (parachutedBombHit== false) {
				
				if (numTimesShown== 18) {
					// Start cruise missile descent

					if (wayPointsListCounter< wayPoints.size())
					{
						if (yloc== 197) {
							if (beenHereBefore== true)
							{
								System.out.println("About to exit!");
								System.exit(0);
							}
							beenHereBefore= true;
//							System.out.println("**just rebuilt waypoints list**");
							try {
								generateWayPoints2();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							wayPointsListCounter= 0;
						}
//						System.out.println("**waypointslistcounter** is: " + wayPointsListCounter);
//						System.out.println("**size of waypoints list: " + wayPoints.size());
						Point aPoint= wayPoints.get(wayPointsListCounter);
						xloc= aPoint.getX();
						yloc= aPoint.getY();
						wayPointsListCounter++;
					}

					yloc+= 2;
				}
			}
			else {
				int x= 1;
			// End of if
			} 
		}
	}
	
	Runnable animateParachutedBombExplodingTask = new Runnable(){
		private int numTimesExpired;

		public void run() {
//			System.out.println("Hello World"); 
		

			switch (numTimesExpired) {
			case 0:
				currentImage= recolorAsRed(currentImage);
				numTimesExpired++;
				break;
				
			case 1:
				explosionClip.play();  // Rewind
				
				currentImage= parachutedBombExplodingImages[0];
				currentImage= recolorAsRed(currentImage);
				numTimesExpired++;
				break;
				
			case 2:
				currentImage= parachutedBombExplodingImages[1];
				currentImage= recolorAsRed(currentImage);
				numTimesExpired++;
				break;
				
			case 3:
				currentImage= parachutedBombExplodingImages[2];
				currentImage= recolorAsRed(currentImage);
				numTimesExpired++;
				break;
				
			case 4:
				currentImage= parachutedBombExplodingImages[3];
				currentImage= recolorAsRed(currentImage);
				numTimesExpired++;
				break;
				
				
			default:

				// Cancel the Timer
				scheduledFuture.cancel(true);
 
				
				explosionTimerScheduled= false;
				
				aliveStatus= false;
				break;
			}
		}
	};
	
	public int getX() {
		
		return xloc;
	}

	public int getY() {
		
		if (yloc== 62 )
		{
			// Play the bomb entered water track
			mediaBombEnteredWaterClip.seek(mediaBombEnteredWaterClip.getStartTime());
			mediaBombEnteredWaterClip.play();
	
		}
		return yloc;
	}	
	
	
	private void generateWayPoints2() throws IOException
	{
		boolean verticalLine= false;
		float m= 0;
		int b= 0;
		
		// Get coordinates of GunBase.
		int gunBaseXcoord= SeaWolfSubmarine.getXMidPoint();
		int gunBaseYcoord= SeaWolfSubmarine.getY();
		
		// Make sure not going to have a divide by zero error.
		
		   if ((yloc - gunBaseYcoord)== 0)
		   {
//			   horizontalLine= true;
//			   System.out.println("Horizontal Line");
			   throw new IOException("bad");
			   
		   } else if ((xloc - gunBaseXcoord) == 0)
		   {
			   verticalLine= true;
//			   System.out.println("Vertical Line");
			   
		   }
		   else
		   {
//			   System.out.println("calculating slope");
//			   System.out.println("xloc= " + xloc + " gunBaseXcoord= " + gunBaseXcoord);
//			   System.out.println("yloc= " + yloc + " gunBaseYcoord= " + gunBaseYcoord);
			   // Calculate slope
			   m= ((float)(yloc - gunBaseYcoord))/(xloc - gunBaseXcoord);
			   if (m== 0)
				   System.out.println("*******Slope of 0!***********");
		   }
			
			b= (int) (yloc - (m * xloc));
			
//			System.out.println("m= " + m + " xloc= " + xloc +" yloc= " + yloc);

		
		// Clear out previous contents of this list.
//		System.out.println("Just cleared size of WayPoints List");
		wayPoints.clear();
			
		
		for (int ycoord= yloc; ycoord <= gunBaseYcoord; ycoord++)
		{
			int xcoord;
			
			if (verticalLine== true)
				xcoord= xloc;
			else {
				
//				System.out.println("ycoord= " + ycoord);
//				System.out.println("b= " + b);
//				System.out.println("m= " + m);
				
				xcoord= (int) ((ycoord - b) / m);  // Possible Divide by Zero issue here!
			}
//			System.out.println("X coord: " + xcoord + " Y coord: " + ycoord);
			
			Point aPoint= new Point(xcoord, ycoord);
			wayPoints.add(aPoint);
		}

	}
	
	
	public boolean aliveStatus() {
		return aliveStatus;
	}
	
	public Rectangle getBounds() {
		// Fix this!
		return new Rectangle(xloc, yloc+12, parachutedBombImageWidth, parachutedBombImageHeight-12 );
	}
	
	public boolean getHitGun() {
		
		return hitGun;
	}
	
	public void setHitByBullet() {
		parachutedBombHit= true;
	}
	
	// Todo- Use history to see what this method originally was!!!  Need to restore it!!
	public void setHitGun() {
		hitGun= true;
		
		aliveStatus= false;  // Do this so cruise missile now that has hit target can be taken out of the drawable list
	}
	
	private static URL getImageURL(String imageFilename) {
		URL aURL= null;
		
//		ClassLoader cl = this.getClass().getClassLoader();
		
		aURL= ParachutedBomb.class.getResource(imageFilename);
   
	     return aURL;
	}
	
//	private Image recolorAsBlack(Image sourceImage)
//	{
//
//        ImageFilter colorfilter = new AllBackFilter();
//        ImageProducer producer= new FilteredImageSource(sourceImage.getSource(),colorfilter);
//        Image img = Toolkit.getDefaultToolkit().createImage(producer);
//          
//        return img;	 
//	}

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
	
	
//	private Image recolorAsRed(Image sourceImage)
//	{	
//        ImageFilter colorfilter = new AllRedFilter();
//        ImageProducer producer= new FilteredImageSource(sourceImage.getSource(),colorfilter);
//        Image img = Toolkit.getDefaultToolkit().createImage(producer);
//        
//        return img;
//		 
//	}
	
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

	@Override
	// Never used
	public boolean isHitByBullet() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void finalize()
	{
		mediaBombEnteredWaterClip.dispose();
		parachuteOpenedClip.dispose();
		explosionClip.dispose();
	}

}
