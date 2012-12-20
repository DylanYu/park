package park;

public class Entry implements Runnable{
	int _ID;
	int _type;
	int _throughput;
	
	public Entry(int _ID, int _type, int _throughput) {
		super();
		this._ID = _ID;
		this._type = _type;
		this._throughput = _throughput;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
