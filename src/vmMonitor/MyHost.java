package vmMonitor;

import java.net.URL;
import java.rmi.RemoteException;

import CONFIG.SJSULAB;

import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.HostCapability;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.HostHardwareInfo;
import com.vmware.vim25.HostSystemInfo;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class MyHost 
{
	private static String vmname ;
    private String currentHostIp;
    private Folder folder;
    private Folder folder1;
    private static ServiceInstance si ;
    private static ServiceInstance si1 ;
    private static VirtualMachine vm ;
    private static HostSystem hs;
    private static boolean flag=true;
    static String snapshotname;
    
    public MyHost()
    {
    	try
    	{
    		this.si=new ServiceInstance(new URL("https://130.65.132.19/sdk"),
					"student@vsphere.local", SJSULAB.getVmwarePassword(), true);
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    }
    
    public MyHost(String host_name)
    {
    	try
    	{
    		this.vmname= host_name;
    		this.si=new ServiceInstance(new URL("https://130.65.132.19/sdk"),
    			"student@vsphere.local", SJSULAB.getVmwarePassword(), true);
    		this.folder = si.getRootFolder();
    		this.vm=(VirtualMachine) new InventoryNavigator(folder).searchManagedEntity("VirtualMachine",
    			"T07-vHost04_132.194");
    		System.out.println("MyHost VM value : "+vm);
    		this.si1=new ServiceInstance(new URL("https://130.65.132.107/sdk"),
        			"administrator", SJSULAB.getVmwarePassword(), true);
    		this.folder1 = si1.getRootFolder();
    		this.hs= (HostSystem) new InventoryNavigator(folder1).searchManagedEntity("HostSystem", "130.65.132.194");
    		System.out.println("MyHost value for HS : "+hs);
    		this.snapshotname="host2";
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    }
    
       
    public boolean revertHostSnapshot()
    {
    	System.out.println("Reverting Host Snapshot");
    	try
    	{
   
    		Task t1 = vm.getCurrentSnapShot().revertToSnapshot_Task(null);
    		vm.getCurrentSnapShot().toString();
    		if(t1.waitForTask()==t1.SUCCESS)

				return true;
    		else
    			return false;
    	
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	return false;
    }
    
    
    public void takeHostSnapshot()
    {
    	try
    	{
    		System.out.println("Please wait.. your vhost snapshot is being created....");
    		//System.out.println("current name : "+ snapshotname);
    		if(snapshotname.equalsIgnoreCase("host2"))
    		{
    			snapshotname="host1";
    			//System.out.println("came to if");
    		}
    		else 
    		{
    			snapshotname="host2";
    		}
    		//System.out.println("now name is : "+ snapshotname);
    		System.out.println("TakeHostSnapshot, VM parameter value :"+vm);
    		
    		Task t1 = vm.createSnapshot_Task(snapshotname, "my snap", false, false);
    		if(t1.waitForTask()==t1.SUCCESS)
    		{
    			System.out.println("Initial host snapshot created");
    			
       		}
    		else
    		{
    			System.out.println("Host Snapshot not yet created........................");
    		}
    		
    
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		System.out.println(e.toString());
    	}
    }
    public boolean  stateOfVhost()
    {
    	boolean res=false;
    	
    	try{
    	
    	VirtualMachineRuntimeInfo vmri=vm.getRuntime();
    	String state=vmri.getPowerState().toString();
    	if(state.contains("poweredOn"))
    		res=true;
    	else res=false;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	
    return res;
    	
    }    
    public void powerOn() 
    {
        try 
        {
        	System.out.println("Powering on vHost '"+vm.getName() +"'. Please wait...");     
        	Task t=vm.powerOnVM_Task(null);
        	if(t.waitForTask()== Task.SUCCESS)
        	{
	        	System.out.println("vHost powered on.");
	        	System.out.println("====================================");
	        
        	}
        	else
        		System.out.println("Power on failed / vHost already powered on...");
        	System.out.println("PowerOn : The value of HS is : "+hs);
        	Thread.sleep(5*60*1000);
        	Task reconnectTask=hs.reconnectHost_Task(null);
       if(reconnectTask.waitForTask()==Task.SUCCESS)
       {
    	   System.out.println("vhost is connected");
       }
       else
       {
    	   System.out.println("vhost is still not connected");
       }
        } 
        catch ( Exception e ) 
        { 
        	System.out.println( e.toString() ) ;
        }
    }
}