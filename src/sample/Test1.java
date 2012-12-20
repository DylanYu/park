package sample;

import javax.swing.JOptionPane;
public class Test1 {

	int i = 0;
	
	public void print()
	{
		System.out.println(i);
	}
	
	public static void main(String[] args)
	{
		JOptionPane.showMessageDialog(null, "ţ��", 
				"���", JOptionPane.INFORMATION_MESSAGE);
	}
}
