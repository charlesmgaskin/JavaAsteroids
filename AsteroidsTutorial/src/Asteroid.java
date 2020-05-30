import java.awt.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Asteroid {
	double x,y, xVelocity, yVelocity, radius,angle,direction;
	int hitsLeft, numSplit, magnitude;
	boolean finished;
	
	ArrayList<Integer> xPts = new ArrayList<Integer>();
	ArrayList<Integer> yPts = new ArrayList<Integer>();
	
	int[] xPtsArr,yPtsArr;
	
	public Asteroid(double x, double y, double radius, double minVelocity, double maxVelocity, 
					int hitsLeft, int numSplit) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.hitsLeft = hitsLeft;
		this.numSplit = numSplit;
		
		angle =0;
		direction = 0.33 * Math.PI;
		
		double vel = minVelocity + Math.random()*(maxVelocity-minVelocity),
				dir = 2*Math.PI*Math.random();
		xVelocity = vel*Math.cos(dir);
		yVelocity = vel*Math.sin(dir);
		
		finished = false;
		while(!finished) {
			angle = angle + (direction*Math.random());
			if(angle >= 2*Math.PI) {
				finished =true;
				break;
			}
			xPts.add((int)(radius*Math.cos(angle)));
			yPts.add((int)(radius*Math.sin(angle)));
		}
	}
	
	public void move(int scrnWidth, int scrnHeight) {
		x+=xVelocity;
		y+=yVelocity;
		if(x<0-radius) {
			x +=scrnWidth+(2*radius);
		} else if(x>scrnWidth+radius) {
			x -=scrnWidth+(2*radius);
		}
		if(y<0-radius) {
			y +=scrnHeight+(2*radius);
		} else if(y>scrnHeight+radius) {
			y -=scrnHeight+(2*radius);
		}
	}
	
	public void draw(Graphics g) {
		
		
		int[] xPtsArr = new int[xPts.size()];
		int[] yPtsArr = new int[yPts.size()];
		for (int i = 0; i < xPtsArr.length;i++) {
			xPtsArr[i] = (int) (xPts.get(i)+x);
			yPtsArr[i] = (int) (yPts.get(i)+y);
		}
				
		g.setColor(Color.DARK_GRAY);
		//g.drawOval((int)(x-radius+.5), (int)(y-radius-.5), (int)(2*radius),(int)(2*radius));
		g.fillPolygon(xPtsArr, yPtsArr, xPtsArr.length);
	}
	
	public boolean shipCollision(Ship ship) {
		if(Math.pow((radius*0.95)+ship.getRadius(), 2) > Math.pow(ship.getX()-x,2)+Math.pow(ship.getY()-y, 2) && ship.isActive()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean shotCollision(Shot shot) {
		if(Math.pow(radius,2) > Math.pow(shot.getX()-x, 2) + Math.pow(shot.getY()-y, 2)) {
			return true;
		} else {
			return false;
		}
	}
	
	public Asteroid createSplitAsteroid(double minVelocity, double maxVelocity) {
		return new Asteroid(x,y,radius/Math.sqrt(numSplit),minVelocity,maxVelocity,hitsLeft-1,numSplit);
	}
	
	public int getHitsLeft() {
		return hitsLeft;
	}
	
	public int getNumSplit(){
		return numSplit;
	}	
	
}
