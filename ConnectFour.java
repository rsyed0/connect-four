import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;

public class ConnectFour extends JFrame{

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {new ConnectFour();}
	
	public ConnectFour(){
		
		// set up frame
		super("Connect Four");
		
		try{
			setIconImage(ImageIO.read(new File("checkersIcon.jpg")));
		} catch (IOException e){
			System.err.println("Cannot change icon image.");
		}
		
		setSize(700,735);
		setLocation(400,0);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		ConnectFourPanel panel = new ConnectFourPanel();
		setContentPane(panel);
		setResizable(false);
		setVisible(true);
		
	}

}

class ConnectFourPanel extends JPanel implements MouseListener,MouseMotionListener,ActionListener{
	
	private static final long serialVersionUID = 1L;
	protected boolean redTurn,moveAllowed;
	protected ConnectFourPiece[][] board;
	protected int col;
	protected JButton reset,quit;
	
	public ConnectFourPanel(){
		
		setBackground(Color.WHITE);
		setLayout(null);
		
		reset = new JButton("Reset");
		reset.setBounds(0,0,350,100);
		reset.addActionListener((ActionListener)this);
		reset.setVisible(false);
		reset.setFont(new Font("Narkisim",Font.BOLD,50));
		add(reset);
		
		quit = new JButton("Quit");
		quit.setBounds(350,0,350,100);
		quit.addActionListener((ActionListener)this);
		quit.setVisible(false);
		quit.setFont(new Font("Narkisim",Font.BOLD,50));
		add(quit);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		redTurn = moveAllowed = true;
		board = new ConnectFourPiece[7][6];
		col = 0;
		
		new CheckGameOverThread().start();
		
	}
	
	public void reset(){
		
		redTurn = moveAllowed = true;
		board = new ConnectFourPiece[7][6];
		col = 0;
		reset.setVisible(false);
		quit.setVisible(false);
		
		repaint();
		
	}
	
	public void paintComponent(Graphics g){
		
		super.paintComponent(g);
		
		if (redTurn) g.setColor(Color.RED);
		else g.setColor(Color.YELLOW);
		
		g.fillOval(col*100+10,10,80,80);
		
		g.setColor(Color.BLUE);
		g.fillRect(0,100,700,600);
		
		for (int y=0;y<6;y++){
			for (int x=0;x<7;x++){
			
				try{
					board[x][y].draw(g);
				} catch (NullPointerException e){
					g.setColor(Color.WHITE);
					g.fillOval(x*100+10,y*100+110,80,80);
				}
				
			}
		}
		
	}
	
	class ConnectFourPiece{
		
		private boolean red;
		private int x,y;
		
		public ConnectFourPiece(int x,int y,boolean red){
			
			this.setX(x);
			this.setY(y);
			this.red = red;
			
		}
		
		public void draw(Graphics g){
			
			if (red) g.setColor(Color.RED);
			else g.setColor(Color.YELLOW);
			
			g.fillOval(getX()*100+10,getY()*100+110,80,80);
			
		}
		
		public String toString(){return "("+x+","+y+")";}
		
		public boolean isRed(){return red;}

		public int getX() {return x;}
		public void setX(int x) {this.x = x;}

		public int getY() {return y;}
		public void setY(int y) {this.y = y;}
		
	}
	
	public boolean gameOver(){
		
		int consec = 0;
		boolean parsingRed = false;
		
		// vertical parser
		for (int c=0;c<7;c++){
			for (int r=0;r<6;r++){
				try{
					if (board[c][r].isRed() == parsingRed) consec++;
					else{
						consec = 1;
						parsingRed = board[c][r].isRed();
					}
				} catch (NullPointerException e){
					consec = 0;
				}
				if (consec == 4) return true;
			}
		}
		
		consec = 0;
		
		// horizontal parser
		for (int r=0;r<6;r++){
			for (int c=0;c<7;c++){
				try{
					if (board[c][r].isRed() == parsingRed) consec++;
					else{
						consec = 1;
						parsingRed = board[c][r].isRed();
					}
				} catch (NullPointerException e){
					consec = 0;
				}
				if (consec == 4) return true;
			}
		}
		
		// diagonal parser
		
		return false;
		
	}

	public void mouseClicked(MouseEvent e) {
		
		int row = 0,col = e.getX()/100;
		
		for (row = 5;row >= 0;row--){
			try{
				board[col][row].equals(null);
			} catch (NullPointerException exc){break;}
		}
		
		//System.out.println("R: "+(row+1)+", C: "+(col+1));
		
		if (row != -1 && moveAllowed){
			board[col][row] = new ConnectFourPiece(col,row,redTurn);
			redTurn = !redTurn;
		}
		
		repaint();
		
	}
	
	class CheckGameOverThread extends Thread{
		
		public void run(){
			
			while (true){
				if (gameOver()){
					reset.setVisible(true);
					quit.setVisible(true);
					moveAllowed = false;
				}
			}
			
		}
		
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {
		
		col = e.getX()/100;
		repaint();
		
	}

	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equals("Reset"))
			reset(); 
		else if (e.getActionCommand().equals("Quit"))
			System.exit(0);
		
	}
	
}