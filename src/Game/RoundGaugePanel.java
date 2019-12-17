package Game;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

import javax.swing.*;

import javafx.scene.layout.Pane;

public class RoundGaugePanel extends Pane implements Runnable {

	int targetValue = 0;
	int MAX_VALUE = 100; // default to 100 for now
	int value = 0;
	Thread repaintThread;
	int degreesPerSecond = 1;
	Dimension Size;
	double gaugeWidth;
	double gaugeHeight;
	int    centerX = (int)(gaugeWidth/2.0);
	int    centerY = (int)(gaugeHeight/2.0);
	double zeroAngle = 225.0;
	double maxAngle  = -45; 
	double range = zeroAngle - maxAngle;

	RoundGaugePanel(Dimension size) {
		Size = size;
		gaugeWidth = Size.width  * 0.95;
		gaugeHeight = Size.height * 0.95;
		centerX = (int)(gaugeWidth/2.0);
		centerY = (int)(gaugeHeight/2.0);
		setMinSize(size.width, size.height);
		setMaxSize(size.width, size.height);
		setPrefSize(size.width, size.height);
		repaintThread = new Thread( this );
		repaintThread.setDaemon(true);
		repaintThread.start();
	}

	public void updateValue( int i ){ targetValue = i; }
	public void setMaxValue( int i) { MAX_VALUE = i; }

	private void paintTheBackground( Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		g.setColor(Color.black);
		g.fillRect(0, 0, Size.width, Size.height);
		g.setColor(Color.white);
		g.fillOval(0, 0, (int)gaugeWidth, (int)gaugeHeight);
		// now the lines and the arcs on the gauge
		g.setColor( Color.lightGray);
		g.drawLine(centerX, centerY, (int) gaugeWidth -25, (int)gaugeHeight-25);
		g.drawLine(centerX, centerY, 23, (int)gaugeHeight-25);
		g.setColor( Color.blue);
		g.drawArc( 3, 3, (int)gaugeWidth-7, (int)gaugeHeight-7, -45, 270);
		
		// Begin Add by DMV
		g.drawArc( 10, 10, (int)gaugeWidth-20, (int)gaugeHeight-20, -45, 270);
		g.setColor(Color.red);
		
		
		g2.fill(new Arc2D.Double( 3, 3, (int)gaugeWidth-7, (int)gaugeHeight-7, -45, 90, Arc2D.PIE));  // Added by Dan
		g.setColor(Color.green);
		g2.fill(new Arc2D.Double( 3, 3, (int)gaugeWidth-7, (int)gaugeHeight-7, 135, 90, Arc2D.PIE));  // Added by Dan
		g.setColor(Color.yellow);
		g2.fill(new Arc2D.Double( 3, 3, (int)gaugeWidth-7, (int)gaugeHeight-7, 45, 90, Arc2D.PIE));  // Added by Dan
		// End Add by DMV
		
		// yellow doesn't show up really well on a white background
//		g.setColor( Color.yellow);
//		g.drawArc( 10, 10, (int)gaugeWidth-20, (int)gaugeHeight-20, 0, 45);
		// this red line doesn't add much to the gauge so....
		//g.setColor( Color.red);
		//g.drawArc( 10, 10, (int)gaugeWidth-20, (int)gaugeHeight-20, -45, 45 );
		
//		g2.setPaint(Color.BLUE);
//        g2.fill(new Arc2D.Double(0, 0, 300, 300, 180, 90, Arc2D.PIE));
		
		
		
		g.setColor( Color.black);
		g.drawString("0%", centerX - 45, centerY + 55);
		g.drawString("50%", centerX - 5, centerY - 50);
		g.drawString("100%", centerX + 15, centerY + 55);

	}

	public void paintComponent(Graphics g){
		Color oldColor;
		oldColor = g.getColor();
		paintTheBackground( g);

		g.setColor(Color.black);
		int x1 = centerX, 
				x2 = x1, 
				y1 = centerY, 
				y2 = y1;
		double angleToUse = zeroAngle - 1.0 * range *( value * 1.0 / MAX_VALUE * 1.0);
		x2 += (int)( Math.cos(Math.toRadians(angleToUse))*centerX);
		y2 -= (int)( Math.sin(Math.toRadians(angleToUse))*centerY);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
		g2.drawLine(x1, y1, x2, y2 );
		g2.setColor(Color.black);
		g2.drawString(""+ value, centerX - 10, centerY + 30);
		g2.setColor(oldColor);
	}
	public void run() {
		int oldValue = 32768;
		while ( true ) {
			try {
				Thread.sleep(100);
			} catch( InterruptedException ie) { /**/ }

			if ( targetValue != value ) {
				if ( degreesPerSecond < Math.abs(targetValue - value) ) value = targetValue;
				else if ( targetValue < value ) value -= degreesPerSecond;
				else if ( targetValue > value ) value += degreesPerSecond;
			}

			if( oldValue != value) {
//				repaint();  // This doesn't work and needs to be redesigned using timelines under JavaFX.
				oldValue = value;
			}
		} // close while()
	} // close run()
} // close class RoundGaugePanel