package sample;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.LinkedList;

public class PortPanel extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel label;
	private JTextField tfUnoccupiedNum;
	private JButton bEnter, bExit;
	private int portType;
	private ActionListener listener = new OperationListener();
	private InitialView frame;
	private Thread receiveThread;
		
	public LinkedList<EntryPort> entryList;
	public LinkedList<ExitPort> exitList;
	
	private EntryPort entryPort;
	private ExitPort exitPort;

	private class ReceiveThread extends Thread{
		public void run() 
		{		
			while(true)
			{
				if (1 == portType)
				{
					entryPort.receive();
					setUnoccupiedNum(entryPort.unoccupiedNum);
				}
				else
				{
					exitPort.receive();
					setUnoccupiedNum(exitPort.unoccupiedNum);
				}
			}
		}
	}
//	
	private class OperationListener implements ActionListener{
		public void actionPerformed(ActionEvent e)
		{
			Object o = e.getSource();
			if(!(o instanceof JButton)) return;
			JButton btn = (JButton)o;
			if(1 == portType)
			{
				entryPort.apply();
				setUnoccupiedNum(entryPort.unoccupiedNum);
			}
			else if(btn.getText().equals("Car Exit"))
			{
				exitPort.carExit();
				setUnoccupiedNum(exitPort.unoccupiedNum);
			}
		}
	}
	
	public PortPanel(String title, int type, int id, JFrame f){
		super(f, title, false);
		
		this.frame = (InitialView)f;
		this.portType = type;// determine the port type is entry or exit
		this.entryList = frame.entryList;
		this.exitList = frame.exitList;
		
		if(1 == portType)
		{
			entryPort = entryList.get(id);
		}
		else
		{
			exitPort = exitList.get(id);
		}	
		
		Container contentPane = this.getContentPane();
		if(1 == portType)
		{
			this.setLocation(50 * (id + 1), 0);
		}
		else
		{
			this.setLocation(50 * (id + 1), 100);
		}
		GridBagConstraints gbc = new GridBagConstraints();
		contentPane.setLayout(new GridBagLayout());
		
		gbc.anchor = GridBagConstraints.WEST;
		label = new JLabel("UNOCCUPIED NUM: ");
		addComponent(label, contentPane, gbc, 0, 0, 1, 1);
		
		tfUnoccupiedNum = new JTextField();
		tfUnoccupiedNum.setEnabled(false);
		tfUnoccupiedNum.setText(Integer.toString(frame.unoccupiedNum));
		tfUnoccupiedNum.setPreferredSize(new Dimension(100, 20));
		addComponent(tfUnoccupiedNum, contentPane, gbc, 1, 0, 1, 1);
		
		// Add an empty line to separate the button from Text Field
		label = new JLabel(" ");
		addComponent(label, contentPane, gbc, 0, 1, 1, 1);
		
		gbc.anchor = GridBagConstraints.CENTER;
		if(portType == 1)
		{
			bEnter = new JButton("Car Enter");
			bEnter.addActionListener(listener);
			addComponent(bEnter, contentPane, gbc, 0, 2, 2, 1);
		}
		else
		{
			bExit = new JButton("Car Exit");
			bExit.addActionListener(listener);
			addComponent(bExit, contentPane, gbc, 0, 2, 2, 1);
		}
		
		this.setSize(300, 100);
		this.setResizable(false);
		this.setVisible(true);
		receiveThread = new ReceiveThread();
		receiveThread.start();
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
	
	public synchronized void setUnoccupiedNum(int num)
	{
		tfUnoccupiedNum.setText(Integer.toString(num));
	}
}
