
import java.awt.*;
import java.util.concurrent.*;

import org.omg.PortableInterceptor.ACTIVE;

import TSim.CommandException;
import TSim.SensorEvent;
import TSim.TSimInterface; 

public class Train implements Runnable {

	private int id; 
	private int speed; 
	private int direction; 
	private TSimInterface tsi; 
	private static SensorEvent e; 

	public static final int DOWNWARD = 1;
	public static final int UPWARD = -1;


	/**
	 * @param id: train id
	 * @param speed: train speed, maxspeed is 100
	 * @param direction: 1 = DOWNWARD, -1 = UPWARD 
	 */
	public Train(int id, int speed, int direction){
		this.id = id; 
		this.speed = speed; 
		this.direction = direction; 
		tsi = TSimInterface.getInstance();
	}

	public int getID() {
		return id; 
	}

	public void run(){
		try{
			tsi.setSpeed(id, speed);
			while(true) {
				checkEvent();
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

	//	protected SensorEvent[] events = new SensorEvent[] {
	//			new SensorEvent(id,4,13,SensorEvent.ACTIVE),
	//			new SensorEvent(id,4,11,SensorEvent.ACTIVE),
	//			new SensorEvent(id,1,9,SensorEvent.ACTIVE)
	//	};



	public void checkEvent() throws CommandException, InterruptedException {

		SensorEvent event = tsi.getSensor(id);
		if(event.getStatus() == SensorEvent.ACTIVE){

			switch(this.direction){

				case (DOWNWARD):{}
	
	
				case(UPWARD): {	
					if(event.getXpos() == 4 && event.getYpos() == 11) {
	
						semArray[0].acquire();
						tsi.setSwitch(3, 13, 2);		
					}	
					if(event.getXpos() == 3 && event.getYpos() == 9){
						semArray[0].release();
					}
	
					if(event.getXpos() == 4 && event.getYpos() == 13){
						semArray[0].acquire();
						tsi.setSwitch(3, 13, 1);		
					}	
					if(event.getXpos() == 1 && event.getYpos() == 10){
						semArray[0].release();
						if(!semArray[1].tryAcquire()){
							tsi.setSwitch(4, 9, 2);
							tsi.setSwitch(15, 9, 2);
						}
						else{
							tsi.setSwitch(4, 9, 1);
							tsi.setSwitch(15, 9, 1);
						}
					}
	
				}
			}
		}
	}
	public void changeDirection() { 
		try {
			speed = -speed; 
			direction = -direction; 

			tsi.setSpeed(id, 0);
			Thread.sleep(1000);

			tsi.setSpeed(id, speed);


		} catch (CommandException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}







}
