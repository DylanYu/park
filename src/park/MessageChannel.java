package park;

import java.util.*;

/**
 * MessageChannel used to send messages between Entrance and Exit
 * 
 * @author hElo
 * 
 */
public class MessageChannel {

	private Queue<Message> msgList;
	private LinkedList<Extrance> entryList;
	private LinkedList<Exit> exitList;

	public MessageChannel(StartPanel initialView) {
		msgList = new LinkedList<Message>();
		entryList = initialView.entryList;
		exitList = initialView.exitList;
	}

	public void send(int type, int portID, Message msg) {
		MessageChannel reciever = null;
		if (type == 1) {
			reciever = entryList.get(portID).messageChannel;
		} else {
			reciever = exitList.get(portID).mailbox;
		}

		reciever.push(msg);
	}

	synchronized public void push(Message msg) {
		msgList.add(msg);
		notify();
	}

	synchronized public Message pop() throws InterruptedException {
		while (msgList.size() == 0) {
			wait();
		}
		return msgList.remove();
	}
}
