package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 转发信息到多个客户端
 * @author LoDog
 *
 */
public class ChatServer {
	
	//从客户端接收到的数据
	private String dataStringFromClient;
	//判断服务器是否已经被启动
	private boolean isStarted;
	//服务端对象
	private ServerSocket serverWithoutUI;
	//储存客户端的list
	List<Client> clientList = new ArrayList<Client>();
	
	public ChatServer() {
		super();
	}

	/**
	 * 启动服务器方法
	 */
	public void startServer() {
		try {
			serverWithoutUI = new ServerSocket(8118);
			isStarted = true;
			//服务器开启提示
			System.out.println("Server Started!！");
			// 不停的接收
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
	 * 内部类，用户接收相应的客户端对象
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
				
				//接收是启动的，开始读取文本内容
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
				System.out.println("对方退出了");
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

	//运行启动服务器
	public static void main(String[] args) 
	{
		new ChatServer().startServer();
	}
	
}
