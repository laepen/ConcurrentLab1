
import java.awt.*;
import java.util.concurrent.*;

import org.omg.PortableInterceptor.ACTIVE;

import TSim.SensorEvent;
import TSim.TSimInterface; 

public class Train implements Runnable {

	private int id; 
	private int speed; 
	private TSimInterface tsi; 
	private static SensorEvent e; 


	public Train(int id, int speed){
		this.id = id; 
		this.speed = speed; 
		tsi = TSimInterface.getInstance();
	}
	
	public int getID() {
		return id; 
	}

	public void run(){
		try{
			tsi.setSpeed(id, speed);
			while(true) {
				meeh();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	static Semaphore[] semArray = new Semaphore[] {
		new Semaphore(1),
		new Semaphore(1),
		new Semaphore(1), 
		new Semaphore(1),
		new Semaphore(1),
		new Semaphore(1)
	};
	
	protected SensorEvent[] events = new SensorEvent[] {
		new SensorEvent(id,4,13,SensorEvent.ACTIVE),
		new SensorEvent(id,4,11,SensorEvent.ACTIVE),
		new SensorEvent(id,3,9,SensorEvent.ACTIVE)
	};
	
	
	
	public void meeh() {
		
		
		
		
		
		
	}







	}
