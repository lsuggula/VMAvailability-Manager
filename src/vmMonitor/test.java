package vmMonitor;

public class test 
{

	public static void main(String[] args) 
	{
		final MyVM myvm = new MyVM("T07-VM-UBU");
		final MyHost myhost= new MyHost("130.65.132.195");
		myvm.addNewHost();
		
	}

}
