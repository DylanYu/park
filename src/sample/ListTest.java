package sample;

import java.util.LinkedList;

class ListTest 
{ 
	LinkedList<Integer> list; 
	
	public ListTest()
	{
	}
	
	public void send(int i, Test t)
	{
		t.push(i);
	}
	
	public synchronized void push(int x) 
	{ 
		list.add(x);
	} 
	
	public void create()
	{
		list = new LinkedList<Integer>(); 
	}
	
	public synchronized int pop() throws InterruptedException 
	{ 
		while( list.size() <= 0 ) 
		{
			wait(); 
		}
		return list.removeLast(); 
	} 
	
	public LinkedList<Integer> getList()
	{
		return this.list;
	}
	
	public static void main(String[] args)
	{
		ListTest a = new ListTest();
		a.create();
		UseClass b = new UseClass(a.list);

		a.push(1);
		b.print();
	}
} 

