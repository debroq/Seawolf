package Game;

import java.io.File;

import javafx.scene.media.*;


public class JukeBox {
//	private String backgroundTrackClip;
	private Media backgroundTrackClip;
	private Media torpedoLaunchClip;
	private Media torpedoHitClip;
	private Media airplaneFlyByClip;
	private Media explosionClip;
	private Media mineHitClip;
	private Media torpedoDestructClip;
	private Media prepareNextLevelClip;
	private Media antiShipBombEnteredWaterClip;
	private Media helicopterInFlightClip;
	private Media gameOverTrackClip;
	private Media parachuteOpenClip;
	private Media machineGunClip;
	private Media aircraftHitClip;
	private Media ptBoatAlarmClip;
	private Media submarineSunkClip;
	private Media metalKlinkClip;
	private Media mineSubmergingClip;
	private Media freighterLoadedWithAmmoHitClip;
	private Media awaitNewRoundClip;
	private Media waterBubblingClip;
	private Media fireBurningClip;
	private Media typeWriterClip;
	private Media cruiseMissileTravelClip;
	private Media typeWriterKeyStrokeClip;
	
	private boolean helicopterSoundTrackPlaying= false;
	
	public JukeBox() {
		init();
	}
	
	void init() {
		
		String sonarSoundFileName = "SoundDir/sonar-beep.wav";
		String torpedoLaunchFileName = "SoundDir/Torpedo_Launch.wav";
		String torpedoImpactFileName = "SoundDir/Torpedo_Impact.wav";
		String airplaneFlybyFileName = "SoundDir/Jetflyby.wav";
		String explosionFileName = "SoundDir/explosion.wav";
		String mineImpactFileName = "SoundDir/Minehit.wav";
		String torpedoDestructFileName = "SoundDir/TorpedoDestruct.wav";
		String prepareNextLevelFileName = "SoundDir/BudWin.wav";
		String antiShipBombEnteredWaterFileName = "SoundDir/waterSPLASH.WAV";
		String helicopterInFlightFileName = "SoundDir/HelicopterInFlight.wav";
		String gameOverSoundFileName = "SoundDir/smb_gameover.wav";
		String parachuteOpenFileName= "SoundDir/Parachut-Texavery-8936_hifi.wav";
		String cruiseMissileTravelFileName= "SoundDir/Jaws_Theme_T01.wav";
		String machineGunFileName= "SoundDir/Machinegun.wav";
		String aircraftHitFileName= "SoundDir/AircraftHit.wav";
		String ptBoatAlarmFileName= "SoundDir/SubmarineDiveHorn.wav";
		String submarineSunkFileName= "SoundDir/SubmarineSunkTrimmed.wav";
		String metalKlinkFileName= "SoundDir/MetalKlink.wav";
		String mineSubmergingFileName= "SoundDir/SingleWaterDroplet-SoundBible.com-425249738.wav";
//		File freighterLoadedWithAmmoHitStream= new File("/SoundDir/Cartoon_sound_effects_4.wav");
//		File freighterLoadedWithAmmoHitStream= new File("/SoundDir/253887__themusicalnomad__positive-beeps.wav");
		String awaitNewRoundFileName= "SoundDir/MilitaryDrumRoll1.wav";
		String freighterLoadedWithAmmoHitFileName= "SoundDir/HarpRun1.wav";
		String waterBubblingFileName= "SoundDir/Large_Bubble-SoundBible.com-1084083477.wav";
		String fireBurningFileName= "SoundDir/burning.wav";
		String typeWriterFileName= "SoundDir/typeWriter.wav";
		String typeWriterKeyStrokeFileName= "SoundDir/typewriter_click.wav";
		

		backgroundTrackClip = new Media(SeaWolf2.class.getClassLoader().getResource(sonarSoundFileName).toExternalForm());
//		MediaPlayer mplayer= new MediaPlayer(backgroundTrackClip);
//		mplayer.play();

		
		torpedoLaunchClip = new Media(SeaWolf2.class.getClassLoader().getResource(torpedoLaunchFileName).toExternalForm());
		torpedoHitClip = new Media(SeaWolf2.class.getClassLoader().getResource(torpedoImpactFileName).toExternalForm());
		airplaneFlyByClip = new Media(SeaWolf2.class.getClassLoader().getResource(airplaneFlybyFileName).toExternalForm());
		explosionClip = new Media(SeaWolf2.class.getClassLoader().getResource(explosionFileName).toExternalForm());
		mineHitClip = new Media(SeaWolf2.class.getClassLoader().getResource(mineImpactFileName).toExternalForm());
		torpedoDestructClip = new Media(SeaWolf2.class.getClassLoader().getResource(torpedoDestructFileName).toExternalForm());
		prepareNextLevelClip = new Media(SeaWolf2.class.getClassLoader().getResource(prepareNextLevelFileName).toExternalForm());
		antiShipBombEnteredWaterClip = new Media(SeaWolf2.class.getClassLoader().getResource(antiShipBombEnteredWaterFileName).toExternalForm());
		helicopterInFlightClip = new Media(SeaWolf2.class.getClassLoader().getResource(helicopterInFlightFileName).toExternalForm());
		gameOverTrackClip = new Media(SeaWolf2.class.getClassLoader().getResource(gameOverSoundFileName).toExternalForm());
		parachuteOpenClip= new Media(SeaWolf2.class.getClassLoader().getResource(parachuteOpenFileName).toExternalForm());
		cruiseMissileTravelClip= new Media(SeaWolf2.class.getClassLoader().getResource(cruiseMissileTravelFileName).toExternalForm());
		machineGunClip= new Media(SeaWolf2.class.getClassLoader().getResource(machineGunFileName).toExternalForm());
		aircraftHitClip= new Media(SeaWolf2.class.getClassLoader().getResource(aircraftHitFileName).toExternalForm());
		ptBoatAlarmClip= new Media(SeaWolf2.class.getClassLoader().getResource(ptBoatAlarmFileName).toExternalForm());
		submarineSunkClip= new Media(SeaWolf2.class.getClassLoader().getResource(submarineSunkFileName).toExternalForm());
		metalKlinkClip= new Media(SeaWolf2.class.getClassLoader().getResource(metalKlinkFileName).toExternalForm());
		mineSubmergingClip= new Media(SeaWolf2.class.getClassLoader().getResource(mineSubmergingFileName).toExternalForm());
//			Media freighterLoadedWithAmmoHitStream= new Media("/SoundDir/Cartoon_sound_effects_4.wav");
//			Media freighterLoadedWithAmmoHitStream= new Media("/SoundDir/253887__themusicalnomad__positive-beeps.wav");
		awaitNewRoundClip= new Media(SeaWolf2.class.getClassLoader().getResource(awaitNewRoundFileName).toExternalForm());
		freighterLoadedWithAmmoHitClip= new Media(SeaWolf2.class.getClassLoader().getResource(freighterLoadedWithAmmoHitFileName).toExternalForm());
		waterBubblingClip= new Media(SeaWolf2.class.getClassLoader().getResource(waterBubblingFileName).toExternalForm());
		fireBurningClip= new Media(SeaWolf2.class.getClassLoader().getResource(fireBurningFileName).toExternalForm());
		typeWriterClip= new Media(SeaWolf2.class.getClassLoader().getResource(typeWriterFileName).toExternalForm());
		typeWriterKeyStrokeClip= new Media(SeaWolf2.class.getClassLoader().getResource(typeWriterKeyStrokeFileName).toExternalForm());
	
	}
	
//	public AudioClip getBackgrooundClip()
//	{
//		AudioClip audioClip= new AudioClip(backgroundTrackClip);
//		audioClip.setCycleCount(MediaPlayer.INDEFINITE);
//		return audioClip;
//	}
	
	public MediaPlayer getBackgrooundClip()
	{
		MediaPlayer mediaPlayer= new MediaPlayer(backgroundTrackClip);
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		return mediaPlayer;
	}
	
	public MediaPlayer getTorpedoLaunchClip()
	{
		return new MediaPlayer(torpedoLaunchClip);
	}
	
	public MediaPlayer getTorpedoHitClip()
	{
		return new MediaPlayer(torpedoHitClip);
	}
	
	public MediaPlayer getAirplaneFlyByClip()
	{
		return new MediaPlayer(airplaneFlyByClip);
	}
	
	public MediaPlayer getExplosionClip()
	{
		return new MediaPlayer(explosionClip);
	}
	
	public MediaPlayer getMineHitClip()
	{
		return new MediaPlayer(mineHitClip);
	}
	
	public MediaPlayer getTorpedoDestructClip()
	{
		return new MediaPlayer(torpedoDestructClip);
	}
	
	public MediaPlayer getPrepareNextLevelClip()
	{
		return new MediaPlayer(prepareNextLevelClip);
	}
	
	public MediaPlayer getAntiShipBombEnteredWaterClip()
	{
		return new MediaPlayer(antiShipBombEnteredWaterClip);
	}
	
	// Need to add code to stop this sound track one number of helicopters
	// in list are down to zero.
	public MediaPlayer getHelicopterInFlightClip()
	{
			MediaPlayer mediaPlayer= new MediaPlayer(helicopterInFlightClip);
			mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
			
			helicopterSoundTrackPlaying= true;
			
			return mediaPlayer;
	}
	
	
	public MediaPlayer getGameOverSoundClip()
	{
		return new MediaPlayer(gameOverTrackClip);
	}
	
	public MediaPlayer getParachuteOpenClip()
	{
		return new MediaPlayer(parachuteOpenClip);
	}
	
	public MediaPlayer getCruiseMissileTravelClip()
	{
		return new MediaPlayer(cruiseMissileTravelClip);
	}
	
	public MediaPlayer getMachineGunClip()
	{
		return new MediaPlayer(machineGunClip);
	}
	
	public MediaPlayer getAircraftHitClip()
	{
		return new MediaPlayer(aircraftHitClip);
	}
	
	public MediaPlayer getPTboatClip()
	{
		return new MediaPlayer(ptBoatAlarmClip);
	}
	
	public MediaPlayer getSubmarineSunkClip()
	{
		return new MediaPlayer(submarineSunkClip);
	}
	
	public MediaPlayer getMetalKlinkClip()
	{
		return new MediaPlayer(metalKlinkClip);
	}
	
	public MediaPlayer getMineSubmergingClip()
	{
		return new MediaPlayer(mineSubmergingClip);
	}
	
	public MediaPlayer getFreightWithAmmoHitClip()
	{
		return new MediaPlayer(freighterLoadedWithAmmoHitClip);
	}
	
	public MediaPlayer getAwaitNewRoundClip()
	{
		return new MediaPlayer(awaitNewRoundClip);
	}
	
	public MediaPlayer getWaterBubblingClip()
	{
		return new MediaPlayer(waterBubblingClip);
	}
	
	public MediaPlayer getFireBurningClip()
	{
		return new MediaPlayer(fireBurningClip);
	}
	
	public MediaPlayer getTypeWriterClip()
	{
		return new MediaPlayer(typeWriterClip);
	}
	
	public MediaPlayer getTypeWriterKeyStrokeClip()
	{
		return new MediaPlayer(typeWriterKeyStrokeClip);
	}
//	public void stopAllClips()
//	{
////		torpedoLaunchClip.drain();
//		torpedoLaunchClip.stop();
//		
////		torpedoHitClip.drain();
//		torpedoHitClip.stop();
//		
////		airplaneFlyByClip.drain();
//		airplaneFlyByClip.stop();
//		
////		explosionClip.drain();
//		explosionClip.stop();
//		
////		mineHitClip.drain();
//		mineHitClip.stop();
//		
////		torpedoDestructClip.drain();
//		torpedoDestructClip.stop();
//		
////		prepareNextLevelClip.drain();
//		prepareNextLevelClip.stop();
//		
////		antiShipBombEnteredWaterClip.drain();
//		antiShipBombEnteredWaterClip.stop();
//		
////		helicopterInFlightClip.drain();
//		helicopterInFlightClip.stop();
//		
////		parachuteOpenClip.drain();
//		parachuteOpenClip.stop();
//		
////		machineGunClip.drain();
//		machineGunClip.stop();
//		
////		aircraftHitClip.drain();
//		aircraftHitClip.stop();
//		
////		ptBoatAlarmClip.drain();
//		ptBoatAlarmClip.stop();
//		
////		mineSubmergingClip.drain();
//		mineSubmergingClip.stop();
//		
////		freighterLoadedWithAmmoHitClip.drain();
//		freighterLoadedWithAmmoHitClip.stop();
//		
////		fireBurningClip.drain();
//		fireBurningClip.stop();
//		
////		cruiseMissileTravelClip.drain();
//		cruiseMissileTravelClip.stop();
//		
//	}

}