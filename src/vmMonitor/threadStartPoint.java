package vmMonitor;

public class threadStartPoint {

	public static void main(String[] args) {
		pingThread t1= new pingThread();
		t1.start();
	}

	
}