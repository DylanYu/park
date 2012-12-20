package sample;


public class ExitPort {
	public int unoccupiedNum, entryNum, exitNum, totalPortNum; 
	public Mailbox mailbox;
	private int logicalTime = 0;
	private int id;
	private int totalParks;
	private boolean entryAdded = false;
		
	public ExitPort(int id, InitialView initialView)
	{
		this.unoccupiedNum = initialView.unoccupiedNum;// unoccupied park place
		this.entryNum = initialView.entryNum;// entry number
		this.exitNum = initialView.exitNum;// exit number
		this.totalPortNum = this.entryNum + this.exitNum;
		this.id = id;
		this.totalParks = initialView.totalNum;// total number of park place
		this.mailbox = new Mailbox(initialView);// mailbox of current exit port
	}

	public void receive(){
		try
		{
			Message msg = mailbox.pop();
			synchronized(this)
			{
				logicalTime = 1 + ((msg.clock > logicalTime)? msg.clock : logicalTime);
				//3: car entered; 4: car exited; 
				switch(msg.msgType)
				{
					case 1: // application
						Message reply = new Message(7, unoccupiedNum, logicalTime, id, 0);
						++logicalTime;
						mailbox.send(1, msg.source, reply);
						break;
					case 2: // reply
						entryAdded = false;
						notify();
						break;
					case 3: // release
						--unoccupiedNum;
						break;
					case 4: // exit
						++unoccupiedNum;
						break;
					case 5: // append
						entryAdded = true;
						++entryNum;
						reply = new Message(6, msg.timeStamp, logicalTime, id, 0);
						++logicalTime;
						mailbox.send(1, msg.source, reply);
						System.out.println("Exit " + id + ": Reply is sent to entry " + msg.source);
						break;						
				}	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(e);
		}
	}
	
	public synchronized void carExit(){
		try
		{
			// wait until the append is finished
			while(entryAdded)
			{
				wait();
			}
			
			if (unoccupiedNum < totalParks)
			{
				Message msg = new Message(4, logicalTime, logicalTime, id, 0);
				for(int i = 0; i < entryNum; ++i)
				{
					msg.clock = logicalTime;
					++logicalTime;
					mailbox.send(1, i, msg);		
				}
				
				for(int i = 0; i < exitNum; ++i)
				{
					msg.clock = logicalTime;
					++logicalTime;
					if (id != i)
					{
						mailbox.send(0, i, msg);
					}
				}
				++unoccupiedNum;
				System.out.println("Exit " + id + ": One car has left the park ground.");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(e);
		}
	}
}
