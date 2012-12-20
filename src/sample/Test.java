package sample;

import java.util.LinkedList;

class Test 
{ 
	LinkedList<Integer> list = new LinkedList<Integer>(); 
	MyThread1 mt1 = new MyThread1();
	MyThread1 mt2 = new MyThread1();
	MyThread1 mt3 = new MyThread1();
	MyThread mt = new MyThread();
	
	public Test()
	{
		mt1.start();
		mt.start();
		mt2.start();
	}
	
	public void send(int i, Test t)
	{
		t.push(i);
	}
	
	public synchronized void push(int x) 
	{ 
		list.addLast( x ); 
		notify(); 
		
	} 
	
	public synchronized int pop() throws InterruptedException 
	{ 
		while( list.size() <= 0 ) 
		{
			wait(); 
		}
		return list.removeLast(); 
	} 

	public class MyThread extends Thread
	{
		public void run()
		{
			push(1);
		}
	}
	public class MyThread1 extends Thread
	{
		public void run()
		{
			try {
				pop();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Test();
	}
} 

