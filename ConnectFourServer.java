import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.net.*;

public class ConnectFourServer extends JFrame{

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {new ConnectFourServer();}
	
	public ConnectFourServer(){
		
		// set up frame
		super("Connect Four Server");
		
		try{
			setIconImage(ImageIO.read(new File("checkersIcon.jpg")));
		} catch (IOException e){
			System.err.println("Cannot change icon image.");
		}
		
		setSize(700,735);
		setLocation(400,0);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		ConnectFourServerPanel panel = new ConnectFourServerPanel();
		setContentPane(panel);
		setResizable(false);
		setVisible(true);
		
	}

}

class ConnectFourServerPanel extends ConnectFourPanel{
	
	private static final long serialVersionUID = 1L;
	private Socket s;
	
	public ConnectFourServerPanel(){
		
		super();
		
		Scanner xterm = new Scanner(System.in);
		System.out.print("Enter server's port: ");
		int port = xterm.nextInt();
		xterm.close();
		
		System.out.println("Waiting for connection at port "+port+"...");
		
		try{
			ServerSocket ss = new ServerSocket(port);
			s = ss.accept();
		} catch (IOException e){
			System.err.println("Cannot set up server. Quitting...");
			System.exit(1);
		}
		
		new ReceiveThread().start();
		
	}
	
	public void paintComponent(Graphics g){
		
		super.paintComponent(g);
		
		if (!redTurn){
			g.setColor(Color.WHITE);
			g.fillOval(col*100+10,10,80,80);
		}

	}
	
	public void send(String msg){
		
		PrintWriter pw = null;
		
		try{
			pw = new PrintWriter(s.getOutputStream());
		} catch (IOException e){
			System.err.println("Failed to send information to client. Quitting...");
			System.exit(1);
		}
		
		pw.println(msg);
		pw.flush();
		
	}
	
	public void receive(String msg){
		
		int x,y;
		if (!msg.equals("Reset")){
			x = Integer.parseInt(msg.charAt(1)+"");
			y = Integer.parseInt(msg.charAt(3)+"");
			
			board[x][y] = new ConnectFourPiece(x,y,false);
			redTurn = true;
		} else reset();
		
		repaint();
		
	}
	
	public void mouseClicked(MouseEvent e){
		
		int row = 0,col = e.getX()/100;
		
		for (row = 5;row >= 0;row--){
			try{
				board[col][row].equals(null);
			} catch (NullPointerException exc){break;}
		}
		
		if (row != -1 && redTurn){
			board[col][row] = new ConnectFourPiece(col,row,true);
			send(board[col][row]+"");
			redTurn = false;
		}
		
		repaint();
		
	}
	
	public void mouseMoved(MouseEvent e){
		
		if (redTurn) super.mouseMoved(e);
		
	}
	
	public void actionPerformed(ActionEvent e){
		
		super.actionPerformed(e);
		
		if (e.getActionCommand().equals("Reset"))
			send("Reset");
		
	}
	
	class ReceiveThread extends Thread{
		
		public void run(){
			
			Scanner sc = null;
			try{
				sc = new Scanner(s.getInputStream());
			} catch (IOException e){
				System.err.println("Cannot receive messages from client. Quitting...");
				System.exit(1);
			}
			
			while (true){
				String line = null;
				try{
					line = sc.nextLine();
				} catch (NoSuchElementException e){
					System.out.println("Server terminated connection. Quitting...");
					System.exit(0);
				}
				System.out.println(line);
				receive(line);
			}
			
		}
		
	}
	
}
