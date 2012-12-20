package sample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class InitialView extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel label;
	private JTextField tfTotalNum, tfEntryNum, tfExitNum;
	private JButton bStart, bAppend;
	private ActionListener listener = new MyBtnListener();
	public int totalNum, unoccupiedNum, entryNum, exitNum;
	
	public LinkedList<EntryPort> entryList = new LinkedList<EntryPort>();
	public LinkedList<ExitPort> exitList = new LinkedList<ExitPort>();
	
	private class MyBtnListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			Object o = e.getSource();
			if(o instanceof JButton){
				JButton btn=(JButton)o;
				
				if(btn.getText().equals("Start")){
					// Get all the user input
					String input = tfTotalNum.getText();
					if(!verifyInput(input)){
						messageBox();
						return;
					}
					totalNum = Integer.parseInt(input);
					
					input = tfEntryNum.getText();
					if(!verifyInput(input)){
						messageBox();
						return;
					}
					entryNum = Integer.parseInt(input);
					
					input = tfExitNum.getText();
					if(!verifyInput(input)){
						messageBox();
						return;
					}
					exitNum = Integer.parseInt(input);
					
					unoccupiedNum = totalNum;
					// Start all the port thread
					readyToWork();
				}
				else if(btn.getText().equals("Append"))
				{
					int id = entryNum;
					++entryNum;
					EntryPort entryPort = new EntryPort(id, InitialView.this);
					entryList.add(entryPort);
					PortPanel entryPanel = new PortPanel("Entry Port " + id, 1, id, InitialView.this);
					entryPort.jion();
					entryPanel.setUnoccupiedNum(entryPort.unoccupiedNum);
				}
			}
		}
	}
	
	public InitialView(){
		super("Initial View");
		Container contentPane = this.getContentPane();
		GridBagLayout gbl = new GridBagLayout();
		contentPane.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.anchor = GridBagConstraints.WEST;
		
		label = new JLabel("TOTAL NUM: ");
		addComponent(label, contentPane, gbc, 0, 0, 1, 1);// first row first column
		
		tfTotalNum = new JTextField();
		tfTotalNum.setPreferredSize(new Dimension(100, 20));
		addComponent(tfTotalNum, contentPane, gbc, 1, 0, 1, 1);// first row second column		
		
		label = new JLabel("ENTRY NUM: ");
		this.addComponent(label, contentPane, gbc, 0, 1, 1, 1);
		
		tfEntryNum = new JTextField();
		tfEntryNum.setPreferredSize(new Dimension(100, 20));
		this.addComponent(tfEntryNum, contentPane, gbc, 1, 1, 1, 1);
		
		label = new JLabel("EXIT NUM: ");
		this.addComponent(label, contentPane, gbc, 0, 2, 1, 1);
		
		tfExitNum = new JTextField();
		tfExitNum.setPreferredSize(new Dimension(100, 20));
		this.addComponent(tfExitNum, contentPane, gbc, 1, 2, 1, 1);
		
		// Add an empty line to separate button from text field
		label = new JLabel(" ");
		this.addComponent(label, contentPane, gbc, 0, 3, 1, 1);
		
		gbc.anchor = GridBagConstraints.CENTER;
		
		bAppend = new JButton("Append");
		bAppend.addActionListener(listener);
		this.addComponent(bAppend, contentPane, gbc, 1, 4, 1, 1);
		
		bStart = new JButton("Start");
		bStart.setPreferredSize(bAppend.getPreferredSize());
		bStart.addActionListener(listener);
		this.addComponent(bStart, contentPane, gbc, 0, 4, 1, 1);
		


		this.setSize(300, 200);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public void addComponent(Component com, 
								Container pane, 
								GridBagConstraints gbc, 
								int x, int y, int w, int h)
	{
		gbc.gridx = x;// column index
		gbc.gridy = y;// row index
		gbc.gridwidth = w;// total column
		gbc.gridheight = h;// total row
		pane.add(com, gbc);
	}
	
	public boolean verifyInput(String arg){
		try
		{
			int temp = Integer.parseInt(arg);
			if(temp == 0)
			{
				return false;
			}
		}catch(Exception e){
			return false;
		}
		return true;
	}

	public void messageBox(){
		JOptionPane.showMessageDialog(null, "The input values should be integers!", 
				"Error", JOptionPane.ERROR_MESSAGE);
	}
	public void readyToWork(){
		for(int i = 0; i < entryNum; ++i)
		{
			entryList.add(new EntryPort(i, this));
		}
		
		for(int i = 0; i < exitNum; ++i)
		{
			exitList.add(new ExitPort(i, this));
		}
		
		for(int i = 0; i < entryNum; ++i)
		{
			new PortPanel("Entry Port " + i, 1, i, this);
		}

		for(int i = 0; i < exitNum; ++i)
		{
			new PortPanel("Exit Port " + i, 0, i, this);
		}
	}
	
	public static void main(String args[]){
		new InitialView();
	}
}