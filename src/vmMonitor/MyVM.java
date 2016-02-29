package vmMonitor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.rmi.RemoteException;

//import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx.Snapshot;
import com.vmware.vim25.Action;
import com.vmware.vim25.AlarmAction;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.AlarmState;
import com.vmware.vim25.AlarmTriggeringAction;
import com.vmware.vim25.ComputeResourceConfigSpec;
import com.vmware.vim25.DatastoreInfo;
import com.vmware.vim25.DuplicateName;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.FileFault;
import com.vmware.vim25.GuestNicInfo;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.InsufficientResourcesFault;
import com.vmware.vim25.InvalidName;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.MethodAction;
import com.vmware.vim25.MethodActionArgument;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.VirtualMachineCapability;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.VirtualMachineNetworkInfo;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRelocateDiskMoveOptions;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.VirtualMachineRelocateTransformation;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSnapshotInfo;
import com.vmware.vim25.VirtualMachineSnapshotTree;
import com.vmware.vim25.VirtualMachineSummary;
import com.vmware.vim25.VmConfigFault;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.AlarmManager;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostDatastoreBrowser;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.InventoryView;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Network;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServerConnection;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.VirtualMachineSnapshot;

import CONFIG.*;

public class MyVM
{
    // instance variables 
    private String vmname ;
    private String currentHostIp;
    private Folder folder;
    private ServiceInstance si ;
    private static VirtualMachine vm ;
    private static HostSystem hs;
    private static boolean flag=true;
    static String snapshotname;
    private static ResourcePool[] rp;
    
    /**
     * Constructor for objects of class MyVM
     */
        
    //constructor with no params
    public MyVM( ) 
    {
        // initialise instance variables
        try 
        {
            // your code here
        	this.si=new ServiceInstance(new URL(SJSULAB.getVmwareHostURL()),
					SJSULAB.getVmwareLogin(), SJSULAB.getVmwarePassword(), true);
        } 
        catch ( Exception e ) 
        { 
        	System.out.println( e.toString() ) ; 
        }

    }
    
    //constructor with 1 param
    public MyVM(String virtual_machine_name)
    {
    	try
    	{
    		this.vmname= virtual_machine_name;
    		this.si=new ServiceInstance(new URL(SJSULAB.getVmwareHostURL()),
					SJSULAB.getVmwareLogin(), SJSULAB.getVmwarePassword(), true);
    		
    		this.folder = si.getRootFolder();
    		this.vm=(VirtualMachine) new InventoryNavigator(folder).searchManagedEntity("VirtualMachine",
    				this.vmname);
    		System.out.println("MyVM, vm value : "+vm);
    		
    		this.hs= (HostSystem) new InventoryNavigator(folder).searchManagedEntity("HostSystem", "130.65.132.194");
    		System.out.println("MyVM, HS value : "+hs);
    		this.snapshotname="snap2";
    		
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    }
    
   

    /**
     * Destructor for objects of class MyVM
     */
    protected void finalize() throws Throwable
    {
       // your code here
    	this.si.getServerConnection().logout();
    	super.finalize();
    } 
    

    public boolean getalarm()
    {
    	boolean res=false;
    	AlarmManager a = si.getAlarmManager();

    	try
    	{
    		Alarm [] alarms= a.getAlarm(vm);
    		System.out.println("Number of alarms set for this vm is : "+ alarms.length);
    		if(alarms.length>0)
    		{
    			
    			boolean al= false;
    			for(int i=0;i<alarms.length;i++)
    			{
    				String name=alarms[i].getAlarmInfo().getName();
    				if(name.equalsIgnoreCase("PoweredOff"))
    				{
    					System.out.println("Required Alarm Found");
    					
    					al=true;
    					res=true;
    					//System.out.println("Res = " + res);
    					return res;
    				}
      			}
    			
    			if(al==false)
    			{
    				System.out.println("no alarms match the required one is found");
    				return res=false;

    			}
    			
    		}
    		else
    		{
    			
    			System.out.println("No alarms is set for this particular VM");
    			return res=false;
    		
    		}
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
  
    	return res;
    }
    
 public boolean getAlarmTriggerStatus()
 {
	AlarmState [] as=  vm.getTriggeredAlarmState();
	System.out.println("number of triggers: "+ as.length +" " );
	if(as.length>0)
		return true;
	else return false;
 }
 public void setalarm()
{
	
	  AlarmManager alarmMgr = si.getAlarmManager();
	  AlarmSpec spec = new AlarmSpec(); 
	  StateAlarmExpression expression =  createStateAlarmExpression();
	  // AlarmAction emailAction = createAlarmTriggerAction(createEmailAction());
	//  AlarmAction methodAction = createAlarmTriggerAction(createPowerOnAction());
	  spec.setExpression(expression);
	  
	  
	  spec.setName("PoweredOff");
	  
	  spec.setDescription("Monitor VM state");
	  spec.setEnabled(true);  
	  try {
		alarmMgr.createAlarm(vm, spec);
		
		System.out.println("New alarm is set for : "+alarmMgr.getMOR().get_value());
		
	} catch (InvalidName e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (DuplicateName e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (RuntimeFault e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
static AlarmTriggeringAction createAlarmTriggerAction(Action action) 
{
  AlarmTriggeringAction alarmAction =  new AlarmTriggeringAction();
  alarmAction.setYellow2red(true);
  
  alarmAction.setAction(action);
  return alarmAction;
}
static MethodAction createPowerOnAction() 
{
  MethodAction action = new MethodAction();
  action.setName("PowerOnVM_Task");
  MethodActionArgument argument = new MethodActionArgument();
  argument.setValue(null);
  action.setArgument(new MethodActionArgument[] { argument });
  return action;
}
static StateAlarmExpression createStateAlarmExpression()
{
  StateAlarmExpression expression = 
    new StateAlarmExpression();
  expression.setType("VirtualMachine");
  expression.setStatePath("runtime.powerState");
  expression.setOperator(StateAlarmOperator.isEqual);
  expression.setRed("poweredOff");
  //expression.setYellow("poweredOff");
  
  return expression;
}
    /**
     * Power On the Virtual Machine
     */
    public void powerOn() 
    {
        try 
        {
              // your code here
        	System.out.println("Powering on virtual machine '"+vm.getName() +"'. Please wait...");     
        	Task t=vm.powerOnVM_Task(null);
        	if(t.waitForTask()== Task.SUCCESS)
        	{
	        	System.out.println("Virtual machine powered on.");
	        	System.out.println("====================================");
        	}
        	else
        		System.out.println("Power on failed / VM already powered on...");
        } 
        catch ( Exception e ) 
        { 
        	System.out.println( e.toString() ) ;
        }
    }

    /**
     * Power Off the Virtual Machine
     */
    public void powerOff() 
    {
        try 
        {
             // your code here
        	System.out.println("Powering off virtual machine '"+vm.getName() +"'. Please wait...");     
        	Task t=vm.powerOffVM_Task();
        	if(t.waitForTask()== Task.SUCCESS)
        	{
	        	System.out.println("Virtual machine powered off.");
	        	System.out.println("====================================");
        	}
        	else
        		System.out.println("Power off failed / VM already powered on...");
        	
        } catch ( Exception e ) 
        { 
        	System.out.println( e.toString() ) ; 
        }
    }

     /**
     * Reset the Virtual Machine
     */

    public void reset() 
    {
        try 
        {
              // your code here
        	System.out.println("Resetting virtual machine '"+vm.getName() +"'. Please wait...");     
        	Task t=vm.resetVM_Task();
        	if(t.waitForTask()== Task.SUCCESS)
        	{
	        	System.out.println("Virtual machine reset.");
	        	System.out.println("====================================");
        	}
        	else
        		System.out.println("Reset failed...");
        } 
        catch ( Exception e ) 
        { 
        	System.out.println( e.toString() ) ; 
        }
    }

    public void createOneSnapshot()
    {
    	try
    	{
    		System.out.println("Please wait.. your snapshot is being created....");
    		
    		if(snapshotname.equalsIgnoreCase("snap2"))
    		{
    			snapshotname="snap1";
    		}
    		else 
    		{
    			snapshotname="snap2";
    		}
    		Task t = vm.createSnapshot_Task(snapshotname, "my vm 's snapshot", false, false);
    		
    
    		if(t.waitForTask()==t.SUCCESS)
    		{
    			System.out.println("snapshot created");
    			//ServerConnection sc = new ServerConnection(url, vimService, serviceInstance)
    			if(snapshotname.equalsIgnoreCase("snap1"))
    			{
    				//delete old snapshot snap2
    				deleteLastSnapshot("snap2");
    				   				
    			}
    			else
    			{
    				deleteLastSnapshot("snap1");
    			}
    		}
    		else
    		{
    			System.out.println("Snapshot not yet created........................");
    		}
    		
    
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		//System.out.println(e.toString());
    	}
    }
     
    public boolean revertToSnapshot()
    {
    	System.out.println("Reverting the Vm since vHost is alive and VM is not reachable.");
    	try
    	{
   
    		Task t1 = vm.getCurrentSnapShot().revertToSnapshot_Task(null);
    		vm.getCurrentSnapShot().toString();
    		if(t1.waitForTask()==t1.SUCCESS)
    			return true;
    		else return false;
    	
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	return false;
    	
    }
    
    public  String addNewHost()
    {
    	String ret = "";
    	try 
		{	
				
			 ManagedEntity [] mes =  new InventoryNavigator(folder).searchManagedEntities("Datacenter");
			 Datacenter dc = new Datacenter(folder.getServerConnection(),  mes[0].getMOR());
			 System.out.println("The value of dc is "+dc);
			 HostConnectSpec hs = new HostConnectSpec();
			 System.out.println("The value of hs is"+hs);
			 String ip= "130.65.132.194";
			 hs.hostName= ip;
			 hs.userName ="root";
			 hs.password = "12!@qwQW";
			 hs.managementIp = "130.65.132.107";
			 //hs.setSslThumbprint("C5:EF:CA:98:96:80:6D:2E:46:CB:B1:D2:BB:87:4A:18:AF:26:83:20");
			 //hs.setSslThumbprint("90:BD:8C:C1:4E:F6:E9:A3:1A:DF:4B:FA:16:6B:9A:0D:73:DC:6A:F7");
			 hs.setSslThumbprint("5E:E1:39:AA:60:0A:64:5E:36:CF:1E:10:2D:8E:A9:CF:BD:37:00:5D");
			 ComputeResourceConfigSpec crcs = new ComputeResourceConfigSpec();
			 Task t = dc.getHostFolder().addStandaloneHost_Task(hs, crcs, true);
			 if(t.waitForTask() == t.SUCCESS)
			 {
				 ret = ip;
			 }
			 else
			 {
				 ret = "not working";
			 }
				
	 
		}   
		catch (Exception re)
		{
			System.out.println(re.toString());
			System.out.println("Unable to connect to Vsphere server");
		}
    	return ret;
	}
   
    static VirtualMachineSnapshot deleteLastSnapshot(String sname)
    {
    	//System.out.println("in delete snapshot");
    	VirtualMachineSnapshotTree[] vmst= vm.getSnapshot().getRootSnapshotList();
    	if(vmst!=null)
    	{
    		ManagedObjectReference mor=getSnapshotFromTree(vmst, sname);
    		if(mor!=null)
    		{
    			VirtualMachineSnapshot vms=new  VirtualMachineSnapshot(vm.getServerConnection(), mor);
    			
    			try 
    			{
					Task t = vms.removeSnapshot_Task(false);
					if(t.waitForTask()==Task.SUCCESS)
					{
						System.out.println("old snapshot deleted");
					}
					else
						System.out.println("not able to delete old snapshot");
				} 
    			catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			catch(InterruptedException e)
    			{
    				e.printStackTrace();
    			}
    		}
    		
    	}
    	return null;
    
    }
    
    static ManagedObjectReference getSnapshotFromTree(VirtualMachineSnapshotTree[] vmst, String s)
    {
    	//ManagedObjectReference mor;
    //	System.out.println("in get tree");
    	for(int i=0; i <vmst.length; i++) 
	    {
	      VirtualMachineSnapshotTree node = vmst[i];
	      if(s.equals(node.getName()))
	      {
	        return node.getSnapshot();
	        
	      } 
	      else 
	      {
	        VirtualMachineSnapshotTree[] childTree = 
	            node.getChildSnapshotList();
	        if(childTree!=null)
	        {
	           ManagedObjectReference mor = getSnapshotFromTree(childTree, s);
	          if(mor!=null)
	          {
	            return mor;
	          }
	        }
	      
	      }
	    }
    	return null;
    }
    
    public void suspend() 
    {
        try 
        {
        	// your code here
        	System.out.println("Suspending virtual machine '"+vm.getName() +"'. Please wait...");     
        	Task t=vm.suspendVM_Task();
        	if(t.waitForTask()== Task.SUCCESS)
        	{
	        	System.out.println("Virtual machine suspended.");
	        	System.out.println("====================================");
        	}
        	else
        		System.out.println("Suspend failed...");
        }
        catch ( Exception e ) 
        { 
        	System.out.println( e.toString() ) ; 
        }
    }

    public void migrateToNewHost( String ip)
    {
    	String newHostIp= ip;
    	HostSystem newHost;
        	
		try
		{
			VirtualMachine vm = (VirtualMachine) new InventoryNavigator(folder).searchManagedEntity("VirtualMachine", vmname);
			
			newHost = (HostSystem) new InventoryNavigator(folder).searchManagedEntity("HostSystem",newHostIp);
			ComputeResource cr = (ComputeResource) newHost.getParent();
			
			Task task = vm.migrateVM_Task(cr.getResourcePool(),newHost,	VirtualMachineMovePriority.highPriority,
					VirtualMachinePowerState.poweredOff);
			
			if(task.waitForTask() == task.SUCCESS)
			{
				System.out.println("Migration to new host completed.");
				VirtualMachine oldvm = (VirtualMachine) new InventoryNavigator(folder).searchManagedEntity("VirtualMachine", this.vmname);
				
				Task t1= oldvm.powerOffVM_Task();
				if(t1.waitForTask()==t1.SUCCESS)
				{
					Task t2= oldvm.destroy_Task();
					if(t2.waitForTask()==t2.SUCCESS)
						System.out.println("Old VM deleted successfully");
					else
						System.out.println("Could not delete old VM.");
					t2= vm.rename_Task(this.vmname);
					if(t2.waitForTask()==t2.SUCCESS)
					{
						System.out.println("Current VM renamed to original name");
						System.out.println("Powering on this VM on new host");
						Task t3= vm.powerOnVM_Task(newHost);
						if(t3.waitForTask()==t3.SUCCESS)
							System.out.println("Powered on VM!");
					}
					else
						System.out.println("Cannot rename VM");
				}
				else
					System.out.println("Could  not power off old VM");
			}
	    	
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	
    }
    
    
    synchronized String  snapshotToClone()
    {
    	String cloneName = null;
    	try{
    	VirtualMachine vm = (VirtualMachine) new InventoryNavigator(folder).searchManagedEntity("VirtualMachine", vmname);
    	cloneName= vm.getName()+ "__SnapShot";
    	VirtualMachineCloneSpec cspec= new VirtualMachineCloneSpec();
    	
    	VirtualMachineRelocateSpec vmrs = new VirtualMachineRelocateSpec();
    ManagedEntity hosts;
		try {
			hosts = new InventoryNavigator(folder).searchManagedEntity("HostSystem", "130.65.132.194");
			
   	vmrs.setHost(hosts.getMOR());
   	vmrs.setPool(rp[0].getMOR());
//vmrs.diskMoveType= VirtualMachineRelocateDiskMoveOptions.createNewChildDiskBacking.toString();
	    	cspec.setLocation(vmrs);
		    cspec.setPowerOn(false);
		    cspec.setTemplate(false);
		
	    	cspec.snapshot=vm.getCurrentSnapShot().getMOR();
	    	
	    	   System.out.println("Parent : " + vm.getParent().toString()); 
	    	
	    	   Task task = vm.cloneVM_Task( (Folder) vm.getParent() ,cloneName, cspec);
	    	
		   
		    System.out.println("Creating clone from previous snapshot. Please wait.. ");

		    String status = task.waitForTask();
		    if(status==Task.SUCCESS)
		    {
		      System.out.println("VM got cloned successfully FROM SNAPSHOT....");
		      notifyAll();

		      return cloneName;
		    
		    }
		    else
		    {
		      System.out.println("Failure -: VM cannot be cloned FROM SNAPSHOT...");
		      cloneName="";
		    }
	    }
	    catch(Exception e)
	    {
	    	System.out.println(e.toString());
	    }
    	}catch(Exception e)
	    {
	    	System.out.println(e.toString());
	    }
    	return cloneName;
    }
    /*Print details of all VMs present on the vCenter (multiple hosts included) */
    public void helloVM()
    {
    	try {

			
			VirtualMachineConfigInfo vminfo = vm.getConfig();
			VirtualMachineCapability vmc = vm.getCapability();
			VirtualMachineRuntimeInfo vmri = vm.getRuntime();
			VirtualMachineSummary  vmsum = vm.getSummary();

			
			System.out.println("------------------------------------------");
			System.out.println("VM Information : ");
			
			System.out.println("VM Name: " + vminfo.getName());
			System.out.println("VM OS: " + vminfo.getGuestFullName());
			System.out.println("VM ID: " + vminfo.getGuestId());
			System.out.println("VM Guest IP Address is " +vm.getGuest().getIpAddress());
			

			System.out.println("------------------------------------------");
			System.out.println("Resource Pool Informtion : ");
			
			System.out.println("Resource pool: " +vm.getResourcePool());
			
			System.out.println("VM Parent: " +vm.getParent());
			//System.out.println("VM Values: " +vm.getValues());
			System.out.println("Multiple snapshot supported: "	+ vmc.isMultipleSnapshotsSupported());
			System.out.println("Powered Off snapshot supported: "+vmc.isPoweredOffSnapshotsSupported());
			System.out.println("Connection State: " + vmri.getConnectionState());
			System.out.println("Power State: " + vmri.getPowerState());
			

			//CPU Statistics

			System.out.println("------------------------------------------");
			System.out.println("CPU and Memory Statistics" );
			
			System.out.println("CPU Usage: " +vmsum.getQuickStats().getOverallCpuUsage());
			System.out.println("Max CPU Usage: " + vmri.getMaxCpuUsage());
			System.out.println("Memory Usage: "+vmsum.getQuickStats().getGuestMemoryUsage());
			System.out.println("Max Memory Usage: " + vmri.getMaxMemoryUsage());
			System.out.println("------------------------------------------");

		} catch (InvalidProperty e) {
			e.printStackTrace();
		} catch (RuntimeFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }
    
    public String lookForAnotherHost()
    {
    	//boolean present=false;
    	String ip="";
    	try
    	{
    		ManagedEntity[] hosts = new InventoryNavigator(folder).searchManagedEntities("HostSystem");
    		if(hosts.length <=1) 
    		{
    			System.out.println("no more hosts present.. Should create new one..");
    			//return false;
    		}
    		else
    		{
    			System.out.println("Multiple hosts present.. Searching in vCenter..");
    		}
    		
    		
    		VirtualMachineRuntimeInfo vmri = vm.getRuntime();
    		String hostB=vmri.getHost().get_value();
    		System.out.println("Lists of new hosts: ");
			for(int i=0;i<hosts.length;i++)
			{
				String hostA=hosts[i].getMOR().get_value();
				
				if(!(hostA.equalsIgnoreCase(hostB)))
				{ 
					String hostIp= hosts[i].getName();
					System.out.println("Host "+(i+1) + " : "+hostIp);
					System.out.println("Trying to ping new vHost...");
					boolean res = pingAll(hostIp);
					if(res)
					{
						System.out.println("Your new host is live! Migrating now...");
						ip=hostIp;
						return ip;
					}
					else
					{
						System.out.println("New host not live.. Should try to ping another host...");
					}
				}
				
			}
    		
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	
    	return ip;
    	
    	
    }
    
    public String returnCurrentHost()
    {
    	String host_name="";
    	try
    	{
    		ManagedEntity[] hosts = new InventoryNavigator(folder).searchManagedEntities("HostSystem");
    		VirtualMachineRuntimeInfo vmri = vm.getRuntime();
    		String hostB=vmri.getHost().get_value();
    		System.out.println("returnCurrentHost, hostB value : "+hostB);
			for(int i=0;i<hosts.length;i++)
			{
				String hostA=hosts[i].getMOR().get_value();
				if(hostA.equalsIgnoreCase(hostB))
				{ 
					host_name= hosts[i].getName();
				}
			}
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	System.out.println("returnCurrentHost, Final Host_name value : "+host_name);
    	return host_name;
    }
    
    
    public boolean pingHost(String ip)
    {
    	boolean res=false;
    	
    	try
    	{
 					System.out.println("Host Ip is " + ip);
					System.out.println("Trying to ping vHost...");
					res=pingAll(ip);
	 	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	
    	return res;
    	
    }
    public boolean pingAll(String ip)
    {
    	boolean res= false;
    	String cmd = "ping "+ ip;
    	String consoleResult="";
    	try
    	{
	   		if(ip!=null)
			{
				Runtime r=Runtime.getRuntime();
				Process p= r.exec(cmd);
			
				BufferedReader input= new BufferedReader(new InputStreamReader(p.getInputStream()));
				while(input.readLine()!=null)
				{
					System.out.println(input.readLine());
					consoleResult+=input.readLine();	    				
				}
				input.close();
			
				if(consoleResult.contains("Request timed out"))
				{
					System.out.println("Packets Dropped");
					res=false;
					//flag=false;
				}
				else
				{
					//ping successful
					System.out.println("ping success in vhost");
					res=true;
					//notifyAll();
				}
				
			} 
			else 
			{
				System.out.println("IP is not found!");
				res = false; //ip = null
				//flag=false;
			}
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
		return res;
	}
	
	
    public boolean  stateOfVM()
    {
    	boolean res=false;
    	Folder folder=si.getRootFolder();
    	try{
    	ManagedEntity mes = new InventoryNavigator(folder).searchManagedEntity("VirtualMachine", vmname);
    	VirtualMachine vm= (VirtualMachine) mes;
    	
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
    public boolean pingMyVM()
    {
    	
    	boolean result=false;
    	
    	String vmIp;
    	try
    	{
    		ManagedEntity mes = new InventoryNavigator(folder).searchManagedEntity("VirtualMachine", vmname);
    		VirtualMachine vm= (VirtualMachine) mes;
    		vmIp=vm.getGuest().getIpAddress();
    		System.out.println("VM IP = "+vmIp);
    		result = pingAll(vmIp);
    		
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	return result;
    }
}