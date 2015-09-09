
import java.awt.*;
import java.util.concurrent.*;

import TSim.TSimInterface;
import Tsim.SensorEvent; 

public class Train implements Runnable {

	private int id; 
	private int speed; 
	private TSimInterface tsi; 
	private static SensorEvent sEvent; 
	
	
	public Train(int id, int speed){
		this.id = id; 
		this.speed = speed; 
		tsi = TSimInterface.getInstance();
	}

	public void run(){
		try{
			tsi.setSpeed(id, speed);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	Semaphore[] semArray = new Semaphore[]();
	semArray.add(new Semaphore(1));
	semArray.add(new Semaphore(1));
	semArray.add(new Semaphore(1));
	semArray.add(new Semaphore(1));
}
