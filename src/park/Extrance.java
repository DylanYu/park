package park;

import java.util.LinkedList;
import java.util.ListIterator;

public class Extrance {
	private int logicalTime = 0;
	private int replyCount = 0;
	private int joinReply;
	public int unoccupiedNum, entryNum, exitNum, totalPortNum;
	public MessageChannel messageChannel;
	public int id;
	private boolean CSEntered = false;
	private Message executionMsg = null;
	private LinkedList<Message> msgList;

	public Extrance(int id, StartPanel startPanel) {
		this.unoccupiedNum = startPanel.unoccupiedNum;
		this.entryNum = startPanel.entryNum;
		this.exitNum = startPanel.exitNum;
		this.totalPortNum = this.entryNum + this.exitNum;
		this.id = id;
		this.messageChannel = new MessageChannel(startPanel);
		msgList = new LinkedList<Message>();
	}

	public synchronized void carEnter() {
		try {
			if (unoccupiedNum == 0) {
				System.out
						.println("Unoccupied number is 0! Please wait for vacancy");
				return;
			}

			Message applyMsg = new Message(1, logicalTime, 0, id, 1);
			++logicalTime;

			replyCount = entryNum - 1;

			for (int i = 0; i < entryNum; ++i) {
				applyMsg.clock = logicalTime;
				++logicalTime;
				if (id != i) {
					messageChannel.send(1, i, applyMsg);
				}
			}
			while (replyCount != 0) {
				wait();
			}
			while (unoccupiedNum == 0) {
				wait();
			}
			CSEntered = true;
			release();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public synchronized void increase() {
		try {
			joinReply = entryNum - 1;
			Message msg = new Message(5, -1, 0, id, 1);
			for (int i = 0; i < entryNum; ++i) {
				msg.clock = logicalTime;
				++logicalTime;
				if (id != i) {
					messageChannel.send(1, i, msg);
				}
			}

			while (joinReply != 0) {
				wait();
			}

			CSEntered = true;
			joinReply = exitNum;
			for (int i = 0; i < exitNum; ++i) {
				msg.clock = logicalTime;
				++logicalTime;
				messageChannel.send(0, i, msg);
			}
			while (joinReply != 0) {
				wait();
			}
			msg = new Message(1, logicalTime, logicalTime, id, 1);
			for (int i = 0; i < exitNum; ++i) {
				msg.clock = logicalTime;
				++logicalTime;
				messageChannel.send(0, i, msg);
			}

			msg = new Message(2, logicalTime, logicalTime, id, 1);
			ListIterator<Message> iter = msgList.listIterator();
			while (iter.hasNext()) {
				msg.clock = logicalTime;
				++logicalTime;
				messageChannel.send(1, msg.source, msg);
				iter.remove();
			}
			executionMsg = null;
			CSEntered = false;
			System.out.println("A new entrance added!");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void receiveMsg() {
		try {
			Message msg = messageChannel.pop();
			synchronized (this) {
				logicalTime = 1 + ((msg.clock > logicalTime) ? msg.clock
						: logicalTime);
				// 1:execute; 2:reply; 3:release; 4:exit; 5:append
				switch (msg.msgType) {
				case 1:
					System.out.println("...Entrance [" + id
							+ "]: Message received from entrance " + msg.source);
					Message reply = new Message(2, msg.timeStamp, logicalTime,
							id, 1);
					++logicalTime;
					if (null == executionMsg) {
						messageChannel.send(1, msg.source, reply);
						System.out.println("...Entrance [" + id + "]: Reply delivered to entrance " + msg.source);
					} else if (CSEntered) {
						this.msgList.add(msg);
					} else {
						if (msg.timeStamp < executionMsg.timeStamp
								|| (msg.timeStamp == executionMsg.timeStamp && msg.source < id)) {
							messageChannel.send(1, msg.source, reply);
							System.out.println("...Entrance [" + id + "]: Reply delivered to entrance " + msg.source);
						} else {
							this.msgList.add(msg);
						}
					}
					break;
				case 2:
					System.out.println("...Entrance [" + id + "]: Reply received from entrance " + msg.source);
					--replyCount;
					notify();
					break;
				case 3:
					--unoccupiedNum;
					break;
				case 4:
					++unoccupiedNum;
					notify();
					break;
				case 5:
					if (replyCount > 0) {
						++replyCount;
					}
					++entryNum;
					if (null == executionMsg) {
						reply = new Message(6, logicalTime, logicalTime, id, 1);
						++logicalTime;
						messageChannel.send(1, msg.source, reply);
						System.out.println("...Entrance [" + id
								+ "]: Reply delivered to entrance " + msg.source);
					} else if (CSEntered) {
						this.msgList.add(msg);
					} else {
						reply = new Message(6, -2, logicalTime, id, 1);
						++logicalTime;
						messageChannel.send(1, msg.source, reply);
					}
					break;
				case 6:
					if (-2 == msg.timeStamp){
						this.msgList.add(msg);
					}
					--joinReply;
					notify();
					break;
				case 7:
					unoccupiedNum = msg.timeStamp;
					reply = new Message(2, logicalTime, logicalTime, id, 1);
					++logicalTime;
					messageChannel.send(0, msg.source, reply);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	public void release() {
		try {
			Message msg = new Message(3, logicalTime, logicalTime, id, 1);
			for (int i = 0; i < entryNum; ++i) {
				msg.clock = logicalTime;
				++logicalTime;
				if (id != i) {
					messageChannel.send(1, i, msg);
				}
			}
			msg = new Message(2, logicalTime, logicalTime, id, 1);
			ListIterator<Message> iter = msgList.listIterator();
			while (iter.hasNext()) {
				msg.clock = logicalTime;
				++logicalTime;
				messageChannel.send(1, msg.source, msg);
				iter.remove();
			}

			msg = new Message(3, logicalTime, logicalTime, id, 1);
			for (int i = 0; i < exitNum; ++i) {
				msg.clock = logicalTime;
				++logicalTime;
				messageChannel.send(0, i, msg);
			}
			--unoccupiedNum;
			executionMsg = null;
			CSEntered = false;
			System.out.println("Entrance [" + id + "]: Car entered.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}
