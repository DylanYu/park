package sample;

import java.util.*;

/*
 * Each port(entry/exit) has an unique mailbox, which is used to 
 * send and receive the message from other port
 * */
public class Mailbox {
	
	private Queue<Message> msgList;
	
	private LinkedList<EntryPort> entryList;
	private LinkedList<ExitPort> exitList;
	
	public Mailbox(InitialView initialView)
	{
		msgList = new LinkedList<Message>();// store all the message received
		entryList = initialView.entryList;
		exitList = initialView.exitList;
	}
	
	public void send(int type, int i, Message msg)
	{
		Mailbox reciever = null;
		if(type == 1)
		{
			reciever = entryList.get(i).mailbox;
		}
		else
		{
			reciever = exitList.get(i).mailbox;
		}
		
		reciever.push(msg);
	}
	
	synchronized public void push(Message msg)
	{
		msgList.add(msg);// add the message to the end of the queue
		notify();// notify the waiting thread
	}
	
	synchronized public Message pop() throws InterruptedException
	{
		while(msgList.size() == 0)
		{
			wait();
		}
		return msgList.remove();// return the front message
	}
}
