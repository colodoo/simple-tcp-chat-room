package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * ת����Ϣ������ͻ���
 * @author LoDog
 *
 */
public class ChatServer {
	
	//�ӿͻ��˽��յ�������
	private String dataStringFromClient;
	//�жϷ������Ƿ��Ѿ�������
	private boolean isStarted;
	//����˶���
	private ServerSocket serverWithoutUI;
	//����ͻ��˵�list
	List<Client> clientList = new ArrayList<Client>();
	
	public ChatServer() {
		super();
	}

	/**
	 * ��������������
	 */
	public void startServer() {
		try {
			serverWithoutUI = new ServerSocket(8118);
			isStarted = true;
			//������������ʾ
			System.out.println("Server Started!��");
			// ��ͣ�Ľ���
			while(isStarted)
			{
				Client st = new Client(serverWithoutUI.accept());
				new Thread(st).start();
				clientList.add(st);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �ڲ��࣬�û�������Ӧ�Ŀͻ��˶���
	 * @author Administrator
	 *
	 */
	class Client implements Runnable {
		private Socket s;
		private DataInputStream dis;
		private DataOutputStream dos;
		private boolean isAcceptStarted = false;
		
		public Client(Socket s)
		{
			this.s = s;
		}
		
		public void send(String str) 
		{
			try {
				dos.writeUTF(str);
			} catch (IOException e) {}
		}
		
		@Override
		public void run() 
		{
			try {
				System.out.println("Connect successed!!");
				isAcceptStarted = true;
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				
				//�����������ģ���ʼ��ȡ�ı�����
				while(isAcceptStarted)
				{
					dataStringFromClient = dis.readUTF();
					System.out.println(dataStringFromClient);
					for(int index = 0; index < clientList.size(); index++)
					{
						Client tempClient = clientList.get(index);
						tempClient.send(dataStringFromClient);
					}
				}
				
			} catch (Exception e) {
				clientList.remove(this);
				System.out.println("�Է��˳���");
			}
			finally{
				try {
					if(s != null) s.close();
					if(dis != null) dis.close();
					if(dos != null) dos.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	//��������������
	public static void main(String[] args) 
	{
		new ChatServer().startServer();
	}
	
}
