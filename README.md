# VMAvailability-Manager

To implement the ## Disaster Recovery the following tools are used: 
• VI API
• VMware vSphere 
• Jdk 1.7.0

## APPROACH:
• A thread which pings all the VM’s continuously monitors its liveliness. If the VM is reachable and running fine, the processes running on the VM are monitored.
• Failure detection: When a VM doesn’t respond to the ping it can be either powered off or failed. If it is powered off an Alarm is created to specify that the VM is just in powered off mode and not failed. A Virtual Machine is considered to be ‘failed’ if the network connection is disabled.
• Snapshots are the point in time image of the VMs that includes disk, memory device state at the time the snapshot was taken. If VM’s respond to the ping their snapshot is created and refreshed after every 10 minutes in order to avoid resource crunch.

## DESIGN:

• We have one centralized Data Centre which is hosting 2 Vhosts: T07-vHost04-132.194 and T07-vHost05-132.195 .All Vhosts should have VMware ESXi installed on them.
• Guest OS: Ubuntu and Windows are installed on our virtual machines.
• Vsphere client for monitoring the Vcenter Server.
• The data stores have vHosts attached to them which are used to add the virtual machines. 

## APPROACH:
• One main thread which gathers all statistics related to Virtual Machines .
• This program initially sets the alarm on all the VM’s which is triggered whenever user powers off the VM.
• Once the initial alarms are created on all the VM’s and all VM statistics are displayed, It creates two threads.
**Thread 1:** SnapshotManager
It captures the snapshot for the VM and vHost after configurable amount of time. It also refreshes these snapshots by means of deleting the all previous old snapshots.
**Thread 2:** Disaster recovery thread
This thread recovers the failures in VM and vHost.
It pings all the VM’s after configurable amount of time.
If the VM doesn’t respond to the ping, it checks whether its parents vHost is responds to ping.
If vHost responds then it will revert the VM to its snapshot.
If it doesn’t it will revert vHost to its previous snapshot.