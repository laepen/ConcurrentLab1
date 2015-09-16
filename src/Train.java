
import java.awt.*;
import java.util.concurrent.*;
 
import org.omg.PortableInterceptor.ACTIVE;
 
import TSim.*;
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
    
    /**
     * Stops the train until it can enter a track and starts it once it can
     * 
     * @param track the number for the track(semaphore) the train is requesting
     * @throws CommandException 
     * @throws InterruptedException 
     */
    private void request(int track) throws CommandException, InterruptedException{
    	tsi.setSpeed(id, 0);
    	semArray[track].acquire();
    	tsi.setSpeed(id, speed);
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
            new Semaphore(1),	//0: Switch closest to bottom station
            new Semaphore(1),	//1: Switch on the left of bottom twin track
            new Semaphore(1),	//2: Switch on the right of bottom twin track
            new Semaphore(1),	//3: Switch on the right of top twin track
            new Semaphore(1),	//4: Railcrossing at top twin track
            new Semaphore(1),	//5: Singletrack Left
            new Semaphore(1),	//6: Bottom fasttrack
            new Semaphore(1),	//7: Singletrack right
            new Semaphore(1)	//8: Top fasttrack
    };
 
    //  protected SensorEvent[] events = new SensorEvent[] {
    //          new SensorEvent(id,4,13,SensorEvent.ACTIVE),
    //          new SensorEvent(id,4,11,SensorEvent.ACTIVE),
    //          new SensorEvent(id,1,9,SensorEvent.ACTIVE)
    //  };
 
 
 
    public void checkEvent() throws CommandException, InterruptedException {
 
        SensorEvent event = tsi.getSensor(id);
        if(event.getStatus() == SensorEvent.ACTIVE){
 
            switch(this.direction){
 
                case (DOWNWARD):{
                	if(event.getXpos() == 14 && event.getYpos() == 3){
                		semArray[8].tryAcquire();	//Tåget som startar uppe claimar sin plats i universum, typ
                	}
                }
     
     
                case(UPWARD): { 
                    if((event.getXpos() == 6 && event.getYpos() == 11)
                    		||(event.getXpos() == 4 && event.getYpos() == 13)) {	//Kommer från botstation
                    	request(5);	//Vänster singelspår
                    	request(0);	//Switch innan singelspår
                        tsi.setSwitch(3, 11, 2);        
                    }
                    
                    else if(event.getXpos() == 1 && event.getYpos() == 10){	//Vill in undre dubbelspåren
                        semArray[0].release();	//Switch innan singelspår
                        semArray[1].acquire();	//Switch innan dubbelspår
                        if(semArray[6].tryAcquire()){	//Undre fasttrack
                        	tsi.setSwitch(4, 9, 2);
                        }
                        else{							//Undre slowtrack
                        	tsi.setSwitch(4, 9, 1);                        	
                        }
                    }
     
                    else if((event.getXpos() == 7 && event.getYpos() == 9)
                    		||(event.getXpos()== 6 && event.getYpos() == 10)){ //Är i undre dubbelspår
                        semArray[5].release();	//Vänster singelspår
                    	semArray[1].release();	//Switch innan dubbelspår
                    }   
                    
                    else if(event.getXpos() == 12 && event.getYpos() == 9){	//Vill ut i höger singletrack från undre fasttrack
                    	request(7);	//Höger singletrack
                    	request(2);	//Switch efter dubbelsår
                    	tsi.setSwitch(15, 9, 2);
                    }
                    
                    else if(event.getXpos() == 13 && event.getYpos() == 10){	//Vill ut i höger singletrack från undre slowtrack
                    	request(7);	//Höger Singletrack
                    	request(2);	//Switch efter dubbelspår
                    	tsi.setSwitch(15, 9, 1);
                    }
                    
                    else if(event.getXpos() ==19 && event.getYpos() == 8){	//Är i höger singletrack
                    	semArray[4].release(); //Undre Fasttrack
                    	semArray[3].acquire(); //Switch innan dubbelspår
                    	if(semArray[8].tryAcquire()){	//Övre fasttrack
                    		tsi.setSwitch(18, 7, 2);
                    	}
                    	else{							//Övre slowtrack
                    		tsi.setSwitch(18, 7, 1);
                    	}
                    }
                    
                    else if((event.getXpos() == 14 && event.getYpos() == 7)
                    		||(event.getXpos() == 15 && event.getYpos() == 8)){	//Är i övre dubbelspår
                    	semArray[2].release();	//Switch efter dubbelspår
                    	semArray[3].release();	//Switch innan dubbelspår
                    	semArray[7].release();	//Höger Singletrack
                    	
                    }
                    else if((event.getXpos() == 12 && event.getYpos() == 7)
                    		||(event.getXpos() == 11 && event.getYpos() == 8)){ //Vill genom korsningen från dubbelspår
                    	request(4);	//Korsningen
                    }
                    
                    else if((event.getXpos() == 6 && event.getYpos() == 5)
                    		||(event.getXpos() == 9 && event.getYpos() == 5)){	//Har lämnat korsningen
                    	semArray[4].release();	//Korsningen
                    }
                    
                    else if((event.getXpos() == 14 && event.getYpos() == 3)
                    		||(event.getXpos() == 14 && event.getYpos() == 5)){	//Har anlänt till övre station
                    	changeDirection();
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