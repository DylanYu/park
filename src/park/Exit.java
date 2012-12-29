package park;

public class Exit {
	public int unoccupiedNum, entryNum, exitNum, totalPortNum;
	public MessageChannel mailbox;
	private int logicalTime = 0;
	public int id;
	private int totalParks;
	private boolean entryAdded = false;

	public Exit(int id, StartPanel startPanel) {
		this.unoccupiedNum = startPanel.unoccupiedNum;
		this.entryNum = startPanel.entryNum;
		this.exitNum = startPanel.exitNum;
		this.totalPortNum = this.entryNum + this.exitNum;
		this.id = id;
		this.totalParks = startPanel.totalParking;
		this.mailbox = new MessageChannel(startPanel);
	}

	public synchronized void carExit() {
		try {
			while (entryAdded) {
				wait();
			}

			if (unoccupiedNum < totalParks) {
				Message msg = new Message(4, logicalTime, logicalTime, id, 0);
				for (int i = 0; i < entryNum; ++i) {
					msg.clock = logicalTime;
					++logicalTime;
					mailbox.send(1, i, msg);
				}

				for (int i = 0; i < exitNum; ++i) {
					msg.clock = logicalTime;
					++logicalTime;
					if (id != i) {
						mailbox.send(0, i, msg);
					}
				}
				++unoccupiedNum;
				System.out.println("Exit [" + id + "]: Car left.");
			} else {
				System.out.println("No car to exit!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	public void receiveMsg() {
		try {
			Message msg = mailbox.pop();
			synchronized (this) {
				logicalTime = 1 + ((msg.clock > logicalTime) ? msg.clock
						: logicalTime);
				switch (msg.msgType) {
				case 1:
					Message reply = new Message(7, unoccupiedNum, logicalTime,
							id, 0);
					++logicalTime;
					mailbox.send(1, msg.source, reply);
					break;
				case 2:
					entryAdded = false;
					notify();
					break;
				case 3:
					--unoccupiedNum;
					break;
				case 4:
					++unoccupiedNum;
					break;
				case 5:
					entryAdded = true;
					++entryNum;
					reply = new Message(6, msg.timeStamp, logicalTime, id, 0);
					++logicalTime;
					mailbox.send(1, msg.source, reply);
					System.out.println("...Exit [" + id
							+ "]: Reply delivered to entrance " + msg.source);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}
