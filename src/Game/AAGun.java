package Game;

import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.image.Image;


public class AAGun {
	
	private int gunImageIndexSelected= 0;
	
	private Image[] gunLtoRImages;
	private Image[] gunRtoLImages;
	
	enum Direction { L_to_R, R_to_L};	
	
	private Direction gunDirection;
	private boolean gunActivated;
	
	private JukeBox jukeBox;
	
	private MediaPlayer machineGunClip;
	
	// Change this to Throw Exceptions if files fail to load
	public AAGun(JukeBox jukeBox) {

		gunLtoRImages= new Image[6];
		gunRtoLImages= new Image[4];

		Image imageIcon= new Image("/ImagesAAGun/AAGunLtoR0.png");
		gunLtoRImages[0]= imageIcon;

		imageIcon= new Image("/ImagesAAGun/AAGunLtoR45.png");
		gunLtoRImages[1]= imageIcon;
		
		imageIcon= new Image("/ImagesAAGun/AAGunLtoR66.png");
		gunLtoRImages[2]= imageIcon;

		imageIcon= new Image("/ImagesAAGun/AAGunLtoR90.png");
		gunLtoRImages[3]= imageIcon;
		
		imageIcon= new Image("/ImagesAAGun/AAGunLtoR114.png");
		gunLtoRImages[4]= imageIcon;

		imageIcon= new Image("/ImagesAAGun/AAGunLtoRN135.png");
		gunLtoRImages[5]= imageIcon;

		imageIcon= new Image("/ImagesAAGun/AAGunRtoL0.png");
		gunRtoLImages[0]= imageIcon;
		
		
		gunDirection= Direction.L_to_R;
		
//		theMediaPlayer= soundPlayer.getMachineGunClip();
		this.jukeBox= jukeBox;
		
//		theMediaPlayer= soundPlayer.getMachineGunClip();
		
		machineGunClip= jukeBox.getMachineGunClip();
		
	}
	
	public Direction getGunDirection() {
		
		return gunDirection;
	}
	
	public Image getAAImageDeActive(Direction gunDirection) {
		
		if (gunDirection== Direction.L_to_R)
			return gunLtoRImages[0];
		else
			return  gunRtoLImages[0];
		
	}
	
	
	public Image getAAImage(Direction gunDirection) {
		
		if (gunActivated== false) {
		 
			if (gunDirection== Direction.L_to_R)
				return gunLtoRImages[0];
			else
				return  gunRtoLImages[0];  // Was R_TO_L
			
		} else {
				
				return  gunLtoRImages[gunImageIndexSelected];  // Was R_TO_L
			
		}
	}
	
	public void setDirectionLeft() {
		if (gunImageIndexSelected== 1)
			gunImageIndexSelected= 5;
		else if (gunImageIndexSelected== 5)
			gunImageIndexSelected= 1;
		else if (gunImageIndexSelected== 2)
			gunImageIndexSelected= 4;
		else if (gunImageIndexSelected== 4)
			gunImageIndexSelected= 2;
		
	}
	
	public void setDirectionRight() {
		if (gunImageIndexSelected== 5)
			gunImageIndexSelected= 1;
		else if (gunImageIndexSelected== 1)
			gunImageIndexSelected= 5;
		else if (gunImageIndexSelected== 4)
			gunImageIndexSelected= 2;
		else if (gunImageIndexSelected== 2)
			gunImageIndexSelected= 4;
	}
	
	public void cycleGunLeft() {
		
		gunImageIndexSelected= gunImageIndexSelected + 1;
		
		if (gunImageIndexSelected== 6) 
			gunImageIndexSelected= 1;
		
	}
	
	public void cycleGunRight() {
		
		gunImageIndexSelected= gunImageIndexSelected - 1;
		
		if (gunImageIndexSelected== 0)
			gunImageIndexSelected= 5;
	}
	
	public int getGunImageIndexSelected() {
		
		return gunImageIndexSelected;
		
	}
	
	public void fireGun() {
//		boolean playing = theMediaPlayer.getStatus().equals(Status.PLAYING);
//		if (playing== false)
		machineGunClip.play();
		machineGunClip.seek(machineGunClip.getStartTime());
	}
	
	public void setSeaMode() {
		gunActivated= false;
		gunImageIndexSelected= 0;
	}
	
	public void toggleGunStatus(Direction gunDirection) {
		
		if (gunActivated== true) {
			gunActivated= false;
			if (gunDirection== Direction.L_to_R)
				gunImageIndexSelected= 0;
			else
				gunImageIndexSelected= 0;
		}
		else
		{
			gunActivated= true;
			gunImageIndexSelected= 3;
		}	
	}
	
	public boolean getGunActivationStatus() {
		
		return gunActivated;
		
	}
	
}
