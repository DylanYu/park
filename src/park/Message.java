package park;

/**
 * // 1:application; 2:reply; 3:release; 4:exit; 5:append;
	// 6:receive append reply;7 Get the unoccupied number from the exit
 * @author hElo
 *
 */
public class Message {
	int msgType = 1;
	int timeStamp = 0;
	int clock = 0;
	int source = 0;
	int portType = 1;

	public Message(int msgType, int timeStamp, int clock, int source,
			int portType) {
		this.msgType = msgType;
		this.timeStamp = timeStamp;
		this.clock = clock;
		this.source = source;
		this.portType = portType;
	}
}
