import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class AsteroidsGame extends Applet implements KeyListener, Runnable{

	int height,width;
	long startTime, endTime, framePeriod;
	boolean paused;
	Thread thread;
	Dimension dim;
	Image img;
	Graphics g;
	Ship ship;
	
	
	private static final long serialVersionUID = 6099393013309081816L;

	public void init() {
		width = 1440;
		height = (width/16)*9;
		resize(width,height);
		ship=new Ship(width/2,height/2,0,.35,.98,.1,12);
		paused=false;
		startTime=0;
		endTime=0;
		framePeriod=25;
		dim=getSize();
		img=createImage(dim.width,dim.height);
		g=img.getGraphics();
		thread=new Thread(this);
		thread.start();
		addKeyListener(this);
	}
	
	public void paint(Graphics gfx) {
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		ship.draw(g);
		gfx.drawImage(img,0,0,this);
	}
	
	public void update(Graphics gfx) {
		paint(gfx);
	}
	
	public void run() {
		for(;;) {
			startTime=System.currentTimeMillis();
			if(!paused) {
				ship.move(dim.width,dim.height);
			repaint();
			}
			try {
				endTime=System.currentTimeMillis();
				if(framePeriod-(endTime-startTime)>0) {
					Thread.sleep(framePeriod-(endTime-startTime));}
				}catch(InterruptedException e) {
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(!ship.isActive() && !paused) {
				ship.setActive(true);
			} else {
				paused=!paused;
				if(paused) {
					ship.setActive(false);
				} else {
					ship.setActive(true);
				}
			}
		}else if(paused || ship.isActive()==false) {
			return;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			ship.setAccelerating(true);
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			ship.setTurningLeft(true);
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			ship.setTurningRight(true);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			ship.setAccelerating(false);
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			ship.setTurningLeft(false);
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			ship.setTurningRight(false);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
