package sample;

import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.JOptionPane;

public class EntryPort {
	private int logicalTime = 0, replyCount = 0, joinReply;
	public int unoccupiedNum, entryNum, exitNum, totalPortNum; 
	public Mailbox mailbox;
	private int id;
	private boolean criticalEntered = false;
	private Message applyMsg = null;
	private LinkedList<Message> appList;

	public EntryPort(int id, InitialView initialView)
	{
		this.unoccupiedNum = initialView.unoccupiedNum;//��λ��
		this.entryNum = initialView.entryNum;
		this.exitNum = initialView.exitNum;
		this.totalPortNum = this.entryNum + this.exitNum;
		this.id = id;//����ID��
		this.mailbox = new Mailbox(initialView);
		appList = new LinkedList<Message>();
	}
	
	public synchronized void apply(){
		try
		{		
			if(unoccupiedNum == 0){
				JOptionPane.showMessageDialog(null,"There is no space left now. Please wait...",
						"Info",JOptionPane.INFORMATION_MESSAGE);
				return ;
			}	
			
			Message applyMsg = new Message(1, logicalTime, 0, id, 1);
			++logicalTime;
			
			replyCount = entryNum - 1;
			
			for(int i = 0; i < entryNum; ++i)
			{
				applyMsg.clock = logicalTime;
				++logicalTime;
				if (id != i)
				{
					mailbox.send(1, i, applyMsg);
				}
			}
			
			while(replyCount != 0)
			{
				wait();
			}
			
			while (unoccupiedNum == 0)
			{
				wait();
			}
			
			criticalEntered = true;
			
			release();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	public synchronized void jion(){
		try
		{	
			joinReply = entryNum - 1;
			Message msg = new Message(5, -1, 0, id, 1);	
			
			for(int i = 0; i < entryNum; ++i)
			{
				msg.clock = logicalTime;
				++logicalTime;
				if (id != i)
				{
					mailbox.send(1, i, msg);
				}
			}
			
			while(joinReply != 0)
			{
				wait();
			}
			
			criticalEntered = true;
			
			joinReply = exitNum;
			for(int i = 0; i < exitNum; ++i)
			{
				msg.clock = logicalTime;
				++logicalTime;
				mailbox.send(0, i, msg);
			}
			
			while(joinReply != 0)
			{
				wait();
			}	

			msg = new Message(1, logicalTime, logicalTime, id, 1);
			for(int i = 0; i < exitNum; ++i)
			{
				msg.clock = logicalTime;
				++logicalTime;
				mailbox.send(0, i, msg);
			}
			
			msg = new Message(2, logicalTime, logicalTime, id, 1);
			ListIterator<Message> iter = appList.listIterator();
			while(iter.hasNext())
			{
				msg.clock = logicalTime;
				++logicalTime;
				mailbox.send(1, msg.source, msg);
				iter.remove();
			}
			applyMsg = null;
			criticalEntered = false;
			System.out.println("A new entry has been added successfully!");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	public  void receive(){
		try
		{
			Message msg = mailbox.pop();			
			synchronized(this)
			{
				logicalTime = 1 + ((msg.clock > logicalTime)? msg.clock : logicalTime);
				//1:application; 2:reply; 3:release; 4:exit; 5:append
				switch(msg.msgType)
				{
					case 1: // application
						System.out.println("Entry " + id + ": Message received from entry " + msg.source);
						Message reply = new Message(2, msg.timeStamp, logicalTime, id, 1);
						++logicalTime;
						if (null == applyMsg)
						{
							mailbox.send(1, msg.source, reply);
							System.out.println("Entry " + id + ": Reply is sent to entry " + msg.source);
						}
						else if (criticalEntered)
						{
							this.appList.add(msg);
						}
						else
						{	
							if (msg.timeStamp < applyMsg.timeStamp ||
								(msg.timeStamp == applyMsg.timeStamp && msg.source < id))
							{
								mailbox.send(1, msg.source, reply);
								System.out.println("Entry " + id + ": Reply is sent to entry " + msg.source);
							}
							else
							{
								this.appList.add(msg);
							}
						}
						break;
					case 2: // reply
						System.out.println("Entry " + id + ": Reply arrived from entry " + msg.source);
						--replyCount;
						notify();
						break;
					case 3: // release
						--unoccupiedNum;
						break;
					case 4: // exit
						++unoccupiedNum;
						notify();
						break;
					case 5: // append
						if (replyCount > 0)
						{
							++replyCount;
						}
						++entryNum;
						if (null == applyMsg)
						{
							reply = new Message(6, logicalTime, logicalTime, id, 1);
							++logicalTime;
							mailbox.send(1, msg.source, reply);
							System.out.println("Entry " + id + ": Reply is sent to entry " + msg.source);
						}
						else if (criticalEntered)
						{
							this.appList.add(msg);
						}
						else
						{
							reply = new Message(6, -2, logicalTime, id, 1);
							++logicalTime;
							mailbox.send(1, msg.source, reply);
						}
						break;
					case 6: // receive append reply
						if (-2 == msg.timeStamp)// -2 means the sender is waiting for critical section
						{
							this.appList.add(msg);
						}
						--joinReply;
//						System.out.println("received reply!" + " " + joinReply);
						notify();
						break;
					case 7: // Get the unoccupied number from the exit
						unoccupiedNum = msg.timeStamp;
						reply = new Message(2, logicalTime, logicalTime, id, 1);
						++logicalTime;
						mailbox.send(0, msg.source, reply);
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

	public void release(){
		try
		{
			Message msg = new Message(3, logicalTime, logicalTime, id, 1);
			for(int i = 0; i < entryNum; ++i)
			{
				msg.clock = logicalTime;
				++logicalTime;
				if (id != i)
				{
					mailbox.send(1, i, msg);
				}
			}
			
			msg = new Message(2, logicalTime, logicalTime, id, 1);
			ListIterator<Message> iter = appList.listIterator();
			// notify all the waiting thread that the critical section has been released
			while(iter.hasNext())
			{
				msg.clock = logicalTime;
				++logicalTime;
				mailbox.send(1, msg.source, msg);
				iter.remove();
			}
			
			msg = new Message(3, logicalTime, logicalTime, id, 1);
			for(int i = 0; i < exitNum; ++i)
			{
				msg.clock = logicalTime;
				++logicalTime;
				mailbox.send(0, i, msg);
			}
			
			--unoccupiedNum;
			applyMsg = null;
			criticalEntered = false;
			System.out.println("Entry " + id + ": One car has entered the park ground.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(e);
		}
	}
}
