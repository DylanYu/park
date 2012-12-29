package park;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ControlPanel extends JFrame {
	public int totalParking, unoccupiedNum, entryNum, exitNum;
	private StartPanel originPanel;

	private JLabel lblEntry = new JLabel("入口:");
	private JTextField tfEntry = new JTextField("0");
	private JButton btnEntry = new JButton("进入");

	private JLabel lblExit = new JLabel("出口:");
	private JTextField tfExit = new JTextField("0");
	private JButton btnExit = new JButton("离开");

	private JLabel lblEntryNumLabel = new JLabel("入口总数:");
	private JLabel lblEntryNum = new JLabel();
	private JLabel lblExitNumLabel = new JLabel("出口总数:");
	private JLabel lblExitNum = new JLabel();
	
	private JLabel lblUnoccupiedLabel = new JLabel("空闲车位数:");
	private JLabel lblUnoccupiedNum = new JLabel();
	
	private JButton btnAdd = new JButton("增加入口");

	public LinkedList<Extrance> entryList = new LinkedList<Extrance>();
	public LinkedList<Exit> exitList = new LinkedList<Exit>();

	public LinkedList<EntryReceiveThread> entryThreadList = new LinkedList<EntryReceiveThread>();
	public LinkedList<ExitReceiveThread> exitThreadList = new LinkedList<ExitReceiveThread>();

	public ControlPanel(JFrame f) {
		originPanel = (StartPanel) f;
		this.entryNum = originPanel.entryNum;
		this.exitNum = originPanel.exitNum;
		this.totalParking = originPanel.totalParking;
		this.unoccupiedNum = originPanel.unoccupiedNum;
		this.entryList = originPanel.entryList;
		this.exitList = originPanel.exitList;
		
		this.lblEntryNum.setText(String.valueOf(entryNum));
		this.lblExitNum.setText(String.valueOf(exitNum));
		this.lblUnoccupiedNum.setText(String.valueOf(unoccupiedNum));

		addThreads();

		btnEntry.addActionListener(new EntryClickListener());
		btnExit.addActionListener(new ExitClickListener());
		btnAdd.addActionListener(new AddEntryClickListener());

		JPanel panel0 = new JPanel();
		panel0.setLayout(new GridLayout(1, 3));
		panel0.add(lblEntry);
		panel0.add(tfEntry);
		panel0.add(btnEntry);
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(1, 3));
		panel1.add(lblExit);
		panel1.add(tfExit);
		panel1.add(btnExit);
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(1, 4));
		panel2.add(lblEntryNumLabel);
		panel2.add(lblEntryNum);
		panel2.add(lblExitNumLabel);
		panel2.add(lblExitNum);
		JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayout(1, 3));
		panel3.add(lblUnoccupiedLabel);
		panel3.add(lblUnoccupiedNum);
		panel3.add(btnAdd);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 1));
		panel.add(panel0);
		panel.add(panel1);
		panel.add(panel2);
		panel.add(panel3);

		add(panel);
		this.setLocationRelativeTo(null);
		this.setTitle("Control Panel");
		this.setSize(300, 200);
		this.setResizable(false);
		this.setVisible(true);
	}

	private class EntryClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String input = tfEntry.getText();
			int entryID = -1;
			if (!verifyInteger(input)) {
				messageBox("Please input integers!");
				return;
			}
			else {
				entryID = Integer.parseInt(input);
				if(entryID < 0 || entryID > entryList.size() - 1) {
					messageBox(String.format("ID out of range [%d, %d]", 0, entryList.size() - 1) );
					return;
				}
			}
			Extrance entry = null;
			for (int i = 0; i < entryList.size(); i++) {
				if (entryList.get(i).id == entryID) {
					entry = entryList.get(i);
					break;
				}
			}
			entry.carEnter();
			setUnoccupiedNum(entry.unoccupiedNum);
		}
	}

	private class ExitClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String input = tfExit.getText();
			int exitID = -1;
			if (!verifyInteger(input)) {
				messageBox("Please input integers!");
				return;
			}
			else {
				exitID = Integer.parseInt(input);
				if(exitID < 0 || exitID > exitList.size() - 1) {
					messageBox(String.format("ID out of range [%d, %d]", 0, exitList.size() - 1) );
					return;
				}
			}
			Exit exit = null;
			for (int i = 0; i < exitList.size(); i++) {
				if (exitList.get(i).id == exitID) {
					exit = exitList.get(i);
					break;
				}
			}
			exit.carExit();
			setUnoccupiedNum(exit.unoccupiedNum);
		}
	}
	
	private class AddEntryClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int id = entryNum;
			++entryNum;
			Extrance entryPort = new Extrance(id, originPanel);
			entryList.add(entryPort);
			EntryReceiveThread thread = new EntryReceiveThread(entryList.get(entryList.size() - 1));
			entryThreadList.add(thread);
			thread.start();
			entryPort.increase();
			lblEntryNum.setText(String.valueOf(entryNum));
		}
	}
	
	/**
	 * 验证输入车位总数为整数
	 * 
	 * @param arg
	 * @return
	 */
	private boolean verifyInteger(String arg) {
		try {
			Integer.parseInt(arg);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private void messageBox(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private class EntryReceiveThread extends Thread {
		private Extrance entryPort;

		public EntryReceiveThread(Extrance entryPort) {
			this.entryPort = entryPort;
		}

		public void run() {
			while (true) {
				entryPort.receiveMsg();
				setUnoccupiedNum(entryPort.unoccupiedNum);
			}
		}
	}

	private class ExitReceiveThread extends Thread {
		private Exit exitPort;

		public ExitReceiveThread(Exit exitPort) {
			this.exitPort = exitPort;
		}

		public void run() {
			while (true) {
				exitPort.receiveMsg();
				setUnoccupiedNum(exitPort.unoccupiedNum);

			}
		}
	}

	public void addThreads() {
		for (int i = 0; i < entryNum; ++i) {
			EntryReceiveThread thread = new EntryReceiveThread(entryList.get(i));
			this.entryThreadList.add(thread);
			thread.start();
		}
		for (int i = 0; i < exitNum; ++i) {
			ExitReceiveThread thread = new ExitReceiveThread(exitList.get(i));
			this.exitThreadList.add(thread);
			thread.start();
		}
	}

	public synchronized void setUnoccupiedNum(int num) {
		lblUnoccupiedNum.setText(Integer.toString(num));
	}
}
