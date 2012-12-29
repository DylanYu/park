package park;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class StartPanel extends JFrame {
	private JLabel lblTotalNum = new JLabel("停车位总数：");
	private JLabel lblEntryNum = new JLabel("入口总数：");
	private JLabel lblExitNum = new JLabel("出口总数");
	private JTextField tfTotalNum = new JTextField("1");
	private JTextField tfEntryNum = new JTextField("1");
	private JTextField tfExitNum = new JTextField("1");
	private JButton btnStart = new JButton("启动");
	public int totalParking, unoccupiedNum, entryNum, exitNum;

	public LinkedList<Extrance> entryList = new LinkedList<Extrance>();
	public LinkedList<Exit> exitList = new LinkedList<Exit>();

	private class StartClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String input = tfTotalNum.getText();
			if (!verifyInteger(input)) {
				messageBox();
				return;
			}
			totalParking = Integer.parseInt(input);
			input = tfEntryNum.getText();
			if (!verifyInteger(input)) {
				messageBox();
				return;
			}
			entryNum = Integer.parseInt(input);
			input = tfExitNum.getText();
			if (!verifyInteger(input)) {
				messageBox();
				return;
			}
			exitNum = Integer.parseInt(input);
			unoccupiedNum = totalParking;
			AddEntranceAndExit();

		}
	}

	public StartPanel() {
		this.btnStart.addActionListener(new StartClickListener());

		JPanel panel0 = new JPanel();
		panel0.setLayout(new GridLayout(1, 2));
		panel0.add(lblTotalNum);
		panel0.add(tfTotalNum);
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(1, 2));
		panel1.add(lblEntryNum);
		panel1.add(tfEntryNum);
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(1, 2));
		panel2.add(lblExitNum);
		panel2.add(tfExitNum);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 1));
		panel.add(panel0);
		panel.add(panel1);
		panel.add(panel2);
		panel.add(this.btnStart);

		add(panel);

		this.setTitle("Start Panel");
		this.setLocationRelativeTo(null);
		this.setSize(300, 200);
		this.setResizable(false);
		this.setVisible(true);
	}

	/**
	 * 验证输入车位总数为自然数
	 * 
	 * @param arg
	 * @return
	 */
	public boolean verifyInteger(String arg) {
		try {
			int temp = Integer.parseInt(arg);
			if (temp == 0) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void messageBox() {
		JOptionPane.showMessageDialog(null, "Please input integers!", "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public void AddEntranceAndExit() {
		for (int i = 0; i < entryNum; ++i) {
			entryList.add(new Extrance(i, this));
		}

		for (int i = 0; i < exitNum; ++i) {
			exitList.add(new Exit(i, this));
		}
		new ControlPanel(this);
	}

	public static void main(String args[]) {
		new StartPanel();
	}
}