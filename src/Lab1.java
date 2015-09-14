import TSim.*;

public class Lab1 {

	public static void main(String[] args) {
		new Lab1(args);
	}

	public Lab1(String[] args) {
		TSimInterface tsi = TSimInterface.getInstance();

		Train train1 = new Train(1,10, 1);
		Train train2 = new Train(2,5, -1);
		
		try {
			train1.run();
			train2.run();
			
		}
		catch (Exception e) {
			e.printStackTrace();    // or only e.getMessage() for the error
			System.exit(1);
		}
	}
}
