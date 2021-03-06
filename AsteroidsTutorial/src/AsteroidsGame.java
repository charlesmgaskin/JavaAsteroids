import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ThreadLocalRandom;

public class AsteroidsGame extends Applet implements KeyListener, Runnable{

	int height,width,numShots,numAsteroids,astNumHits,astNumSplit,level;
	double astRadius,minAstVel,maxAstVel;
	long startTime, endTime,framePeriod;
	boolean paused,shooting;
	Thread thread;
	Dimension dim;
	Image img;
	Graphics g;
	Ship ship;
	Shot[] shots;
	Asteroid[] asteroids;
	
	private static final long serialVersionUID = 6099393013309081816L;

	public void init() {
		width = 1440;
		height = (width/16)*9;
		resize(width,height);	
		shots=new Shot[41];
		numAsteroids = 0;
		level=9;
		astRadius=60;
		minAstVel=.5;
		maxAstVel=5;
		astNumHits=5;
		astNumSplit=2;
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
	
	public void setUpNextLevel() {
		level++;
		ship=new Ship(width/2,height/2,0,.35,.98,.1,12);
		numShots = 0;
		paused=true;
		shooting= false;
		asteroids=new Asteroid[level*(int)Math.pow(astNumSplit,astNumHits-1)+1];
		numAsteroids=level;
		for(int i = 0; i<numAsteroids;i++) {
			asteroids[i] = new Asteroid(Math.random()*dim.width,Math.random()*dim.height,astRadius,minAstVel,maxAstVel,astNumHits,astNumSplit);
		}
	}
	
	public void paint(Graphics gfx) {
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		ship.draw(g);
		for(int i=0;i<numShots;i++) {
			shots[i].draw(g);
		}
		for(int i=0;i<numAsteroids;i++) {
			asteroids[i].draw(g);
		}
		g.setColor(Color.cyan);
		g.drawString("Level "+ level,20,20);
		gfx.drawImage(img,0,0,this);
	}
	
	public void update(Graphics gfx) {
		paint(gfx);
	}
	
	public void run() {
		for(;;) {
			startTime=System.currentTimeMillis();
			if(numAsteroids<=0) {
				setUpNextLevel();
			}
			if(!paused) {
				ship.move(dim.width,dim.height);
				for(int i=0; i<numShots;i++) {
					shots[i].move(dim.width,dim.height);
					if (shots[i].getLifeLeft()<=0) {
						deleteShot(i);
						i--;
					}
				}
				updateAsteroids();
				if(shooting && ship.canShoot()) {
					shots[numShots]=ship.shoot();
					numShots++;
				}
			}
			repaint();
			try {
				endTime=System.currentTimeMillis();
				if(framePeriod-(endTime-startTime)>0) {
					Thread.sleep(framePeriod-(endTime-startTime));}
				}catch(InterruptedException e) {
			}
		}
	}
	
	private void deleteShot(int index) {
		for(int i=index;i<numShots;i++) {
			shots[i] = shots[i+1];
			shots[numShots]=null;
		}
		numShots--;
	}
	
	private void deleteAsteroid(int index) {
		for(int i=index;i<numAsteroids;i++) {
			asteroids[i]=asteroids[i+1];
			asteroids[numAsteroids]=null;
		}
		numAsteroids--;
	}

	private void addAsteroid(Asteroid ast) {
		asteroids[numAsteroids] = ast;
		numAsteroids++;
	}
	
	private void updateAsteroids() {
		for(int i=0; i<numAsteroids;i++) {
			asteroids[i].move(dim.width, dim.height);
			if(asteroids[i].shipCollision(ship)) {
				level--;
				numAsteroids=0;
				return;
			}
			for(int j=0;j<numShots;j++) {
				if(asteroids[i].shotCollision(shots[j])) {
					deleteShot(j);
					if(asteroids[i].getHitsLeft()>1) {
						for(int k=0;k<asteroids[i].getNumSplit();k++) {
							addAsteroid(asteroids[i].createSplitAsteroid(minAstVel, maxAstVel));
						}
					}
					deleteAsteroid(i);
					j=numShots;
					i--;
				}
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
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			shooting = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_Z) {
			ship.teleport(ThreadLocalRandom.current().nextInt(0,dim.width+1), ThreadLocalRandom.current().nextInt(0,dim.height+1));
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
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			shooting = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
