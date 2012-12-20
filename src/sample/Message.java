package sample;

public class Message {
	int msgType = 1;// message type
	int timeStamp = 0;// time of application
	int clock = 0;// logical time of the message
	int source = 0;// the sender id 
	int portType = 1;//the type of sender port 

	public Message(int msgType, int timeStamp, int clock, int source, int portType)
	{
		this.msgType = msgType;
		this.timeStamp = timeStamp;
		this.clock = clock;
		this.source = source;
		this.portType = portType;
	}
}
