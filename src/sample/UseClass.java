package sample;

import java.util.LinkedList;

public class UseClass
{
	LinkedList<Integer> l;
	public UseClass(LinkedList<Integer> right)
	{
		this.l = right;
	}
	
	public synchronized int pop()
	{ 
		while( l.size() <= 0 ) 
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		return l.remove(); 
	} 
	
	public synchronized void print()
	{
		System.out.println("Size: " + l.size());
		for(int i = 0; i < l.size(); ++i)
		{
			System.out.println(l.get(i));
		}
	}
}