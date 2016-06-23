package client;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/***
 * ���������ҿͻ���
 * @author Administrator
 *
 */
public class ChatClient extends Frame {
	
	private static final long serialVersionUID = 1L;
	private TextField chatInputBox = new TextField();
	private TextArea contentTextArea = new TextArea();
	private Socket s;
	private DataOutputStream socketOutputStream;
	private DataInputStream socketInputStream;
	private boolean bConnected = false;
	private accept accept;
	@SuppressWarnings("unused")
	private Thread acceptThread;
	
	boolean isConnected = false;

	/**
	 * �ڲ���
	 * ���շ��������ص���Ϣ
	 * @author LoDog
	 *
	 */
	class accept implements Runnable {
		Socket s;
		
		accept(Socket s){
			this.s = s;
		}

		@Override
		public void run()
		{
			while(bConnected)
			{
				try {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = new Date();
					String dateStr = format.format(date);
					String str = socketInputStream.readUTF();
					contentTextArea.append("ĳ�� " + dateStr + "\n" + str + "\n\n");
					
				} catch (SocketException e3){
					System.out.println("bye!!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * ��ʼ������
	 */
	public void lunchFrame() 
	{
		
		try {
			s = new Socket("127.0.0.1", 8118);
			socketOutputStream = new DataOutputStream(s.getOutputStream());
			socketInputStream = new DataInputStream(s.getInputStream());
			isConnected = true;
			bConnected = true;
			accept = new accept(s);
			Thread acceptThread = new Thread(accept);
			acceptThread.start();
		} catch (UnknownHostException e1) {
			System.out.println("Miss Server!!");
		} catch (IOException e1) {
			System.out.println("Can't output!!");
		}
		
		this.setTitle("������");
		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		add(chatInputBox, BorderLayout.SOUTH);
		add(contentTextArea, BorderLayout.NORTH);
		pack();

		// ��Ӵ��ڹش����¼�
		this.addWindowListener(new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				try {
					if(isConnected){
						bConnected = false;
						socketInputStream.close();
						socketOutputStream.close();
						s.close();
						System.exit(0);
					}
				} catch (IOException e1) {
					System.out.println("Doesn't connected!!");
				}
				
			}

		});
		//����¼�����
		chatInputBox.addActionListener(new ChatInputBoxListener());
		this.setVisible(true);
	}

	/**
	 * main����
	 * @param args
	 */
	public static void main(String[] args) 
	{
		new ChatClient().lunchFrame();
	}

	/**
	 * �����¼���
	 * @author LoDog
	 *
	 */
	private class ChatInputBoxListener implements ActionListener  {
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			String tempStr;
			//ȡ���ַ���
			tempStr = chatInputBox.getText();
			
			try {
				socketOutputStream.writeUTF(tempStr);
				socketOutputStream.flush();
			} catch (IOException e1) {
				System.out.println("Can't output!!");
			}
			//����
			chatInputBox.setText("");
		}
	}
}
