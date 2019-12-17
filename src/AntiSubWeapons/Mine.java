package AntiSubWeapons;

import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//import Game.CreateMineTask;
import Game.ImageDisplayFunctions;
import Game.ImageMoveFunctions;
import Game.SeaWolf2;
import Game.JukeBox;
import Game.Sprite;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


public class Mine extends Sprite implements ImageDisplayFunctions, ImageMoveFunctions {
	private final static int MOVEMENTINC= 1;
	private boolean aliveStatus;
	
	private static Image mineImageA;
	private static int mineImageAWidth;
	private static int mineImageAHeight;
		
	private Boolean mineHit;
	
	private Image currentImage;
	private static Image[] mineExplodingImages;
	
	private static String[] mineExplodingImageFilenames;
	
	private int movementCounter;
	
	private enum Direction { L_to_R, R_to_L};
	
	private Direction mineDirection;
	
	private static MediaPlayer explosionClipMediaPlayer;
	private static JukeBox jukeBox;
	

	private ScheduledExecutorService scheduledExecutorService;
	private int numTimesExplodeTimerInvoked;
	
	private boolean explosionTimerScheduled;
	
	private final static int MINE_DISPLAY_ROW1X= 103;
	private final static int MINE_DISPLAY_ROW2X= 142;
	private final static int MINE_DISPLAY_ROW3X= 182; // 160;
	private final static int MINE_DISPLAY_ROW4X= 222; // 200;
	private final static int MINE_DISPLAY_ROW5X= 262; // 230;
	private final static int MINE_DISPLAY_ROW6X= 302; // 260;
	private final static int MINE_DISPLAY_ROW7X= 342; // 290;
	
	private final IntegerProperty killFlag= new SimpleIntegerProperty(0);
	private ScheduledFuture<?> scheduledFuture;
	
	private static boolean mineDataInitialized= initMineData();
	
	// Note - there can be multiple mines on a row.
	public Mine(int yloc, int movementInc) {
		
		super(MOVEMENTINC);
		
//		this.soundPlayer= soundPlayer;
		
		xloc= 0;
		this.yloc= yloc;
		
		Image imageIcon= new Image("/ImagesMines/NewMineAG2.png");
		mineImageA= imageIcon;
		mineImageAWidth= (int) mineImageA.getWidth();
		mineImageAHeight= (int) mineImageA.getHeight();
		
		
		mineExplodingImageFilenames= new String[4];
		
		mineExplodingImageFilenames[0]= "/ImagesMineHit/ExplodingMineAG1.png";
		mineExplodingImageFilenames[1]= "/ImagesMineHit/ExplodingMineAG2.png";
		mineExplodingImageFilenames[2]= "/ImagesMineHit/ExplodingMineAG3.png";
		mineExplodingImageFilenames[3]= "/ImagesMineHit/ExplodingMineAG4.png";
		
		mineExplodingImages= new Image[4];
		
		for (int index= 0; index <4; index++) {
			Image aImageIcon= new Image(mineExplodingImageFilenames[index]);
			mineExplodingImages[index]= aImageIcon;
		}
		
		aliveStatus= true;
		
		
		this.movementInc= movementInc;
		
		movementCounter= 0;
		
		switch (yloc) {
		case MINE_DISPLAY_ROW1X:
			// Move L to R
			mineDirection= Direction.L_to_R;
			break;
		case MINE_DISPLAY_ROW2X:
			// Move R to L
			mineDirection= Direction.L_to_R;
			break;
		case MINE_DISPLAY_ROW3X:
			// Move L to R
			mineDirection= Direction.L_to_R;
			break;
		case MINE_DISPLAY_ROW4X:
			// Move R to L
			mineDirection= Direction.L_to_R;
			break;
		case MINE_DISPLAY_ROW5X:
			// Move L to R
			mineDirection= Direction.L_to_R;
			break;
		case MINE_DISPLAY_ROW6X:
			// Move R to L
			mineDirection= Direction.L_to_R;
			break;
		case MINE_DISPLAY_ROW7X:
			// L to R
			mineDirection= Direction.L_to_R;
			break;
		}
		
		if (mineDirection== Direction.L_to_R)
			xloc= 0;
		else
			xloc= 820;
		
		mineHit= false;
		
		// Experiment with ExecutorService
		scheduledExecutorService =
		        Executors.newScheduledThreadPool(5);
		
		currentImage= mineImageA;  // Was mineImageB
	
	}
	
	private static boolean initMineData() {
		
		Image imageIcon= new Image("/ImagesMines/NewMineAG1.png");
		mineImageA= imageIcon;
		mineImageAWidth= (int) mineImageA.getWidth();
		mineImageAHeight= (int) mineImageA.getHeight();
		
		jukeBox= new JukeBox();
		
//		imageIcon= new Image("/ImagesMines/NewMineAG1.png"));
//		mineImageB= imageIcon;
//		mineImageBWidth= mineImageB.getWidth();
//		mineImageBHeight= mineImageB.getHeight();
		
//		SoundPlayer soundPlayer= new SoundPlayer();
		explosionClipMediaPlayer= jukeBox.getExplosionClip();
		
		return true;
	}
	
	
	// Use this function to determine if will be moving left or right
	// depending on the row it was created on.
	public void move() {
		
		movementCounter++;
		
		if (movementCounter== movementInc) {
			
			if (mineDirection== Direction.L_to_R)
			{
				if (xloc== (SeaWolf2.CANVAS_WIDTH-30)) {
					
				  // End of the run for this mine.
				  // Somehow flag it for destruction.
					
					aliveStatus= false;
					
				} else {
		//			xloc+=movementInc;
					xloc+= 2;
				}
			}
			else
			{
				if (xloc== 0) {
					
					// End of the run for this mine.
					// Somehow flag it for destruction.
						
					aliveStatus= false;
					
				} else {
					xloc-=2;
				}
			}
		
			
			movementCounter= 0;
		}
	}
	
	public void setHitByAntiMineSoundWave() {
		
		mineHit= true;
		
	}
	
	public boolean aliveStatus() {
		if (killFlag.get()== 1)
			return false;
		else
			return aliveStatus;
	}
	
	
	public Image getImage() {

		if (mineHit== true)
		{
			if (explosionTimerScheduled== false) {
		
				scheduledFuture =
					scheduledExecutorService.scheduleAtFixedRate(animateMineExplodingTask, 2, 100, TimeUnit.MILLISECONDS);
				explosionTimerScheduled= true;
			}
			
		}

		return currentImage;
	}
	
	public Rectangle getBounds() {

		return new Rectangle(xloc, yloc, mineImageAWidth, mineImageAHeight);

	}
	

	public int getX() {
		return xloc;
	}
	
	Runnable animateMineExplodingTask = new Runnable(){

		public void run() {
//			System.out.println("Hello World"); 
		

			switch (numTimesExplodeTimerInvoked) {
			case 0:
				currentImage= recolorAsRed(currentImage);
				numTimesExplodeTimerInvoked++;
				break;
				
			case 1:				
				currentImage= mineExplodingImages[0];
				currentImage= recolorAsRed(currentImage);
				numTimesExplodeTimerInvoked++;
				break;
				
			case 2:
				explosionClipMediaPlayer.seek(explosionClipMediaPlayer.getStartTime());
				explosionClipMediaPlayer.play();
				currentImage= mineExplodingImages[2];
				currentImage= mineExplodingImages[1];
				currentImage= recolorAsRed(currentImage);
				numTimesExplodeTimerInvoked++;
				break;
				
			case 3:
				currentImage= mineExplodingImages[2];
				currentImage= recolorAsRed(currentImage);
				numTimesExplodeTimerInvoked++;
				break;
				
			case 4:
				currentImage= mineExplodingImages[3];
				currentImage= recolorAsRed(currentImage);
				numTimesExplodeTimerInvoked++;
				
			default:

				// Cancel the Timer
				scheduledFuture.cancel(true);
				
				explosionTimerScheduled= false;
				
				aliveStatus= false;
				break;
			}
		}
	};
		

	public int getY() {
		return yloc;
	}
	
	public void mineHit()
	{
		mineHit= true;
	}
	
	private static URL getImageURL(String imageFilename) {
		URL aURL= null;
		
//		ClassLoader cl = this.getClass().getClassLoader();
		
		aURL= Mine.class.getResource(imageFilename);
   
	     return aURL;
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
	
	public void finalize()
	{
		explosionClipMediaPlayer.dispose();
	}
}
