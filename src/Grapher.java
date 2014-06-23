import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Grapher {
	
	static String initialFunc = "x^2+4";
	
	static Polynomial poly = Polynomial.parse(initialFunc);
	
	public static void main(String args[]) {
		
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension window = new Dimension(scr.width/2, scr.height/2);
		
		JFrame frame = new JFrame("Grapher");
		
		final Screen screen = new Screen();
		
		JPanel bottom = new JPanel();
		
		bottom.setLayout(new BorderLayout());
		
		final JTextField input = new JTextField(initialFunc);
		JLabel yLbl = new JLabel("f(x)=");
		
		input.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					synchronized (this) {
						poly = Polynomial.parse(input.getText());
					}
					System.out.println(poly.toString());
					screen.repaint();
				}
			}
		});
		
		bottom.add(BorderLayout.WEST, yLbl);
		bottom.add(BorderLayout.CENTER, input);
		
		frame.setLayout(new BorderLayout());
		
		frame.add(BorderLayout.CENTER, screen);
		frame.add(BorderLayout.SOUTH, bottom);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(window);
		frame.setLocation(scr.width/2 - window.width/2, scr.height/2 - window.height/2);
		frame.pack();
		frame.setVisible(true);
	}
}

class Screen extends JPanel{
	private static final long serialVersionUID = 1L;
	
	static float pixelSize = 8.0f;	//width and height of pixels
	
	protected void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(Color.WHITE);
		float width = this.getWidth();
		float height = this.getHeight();
		g.fillRect(0, 0, (int)width, (int)height);
		//draw axes
		g.setColor(Color.BLACK);
		g.drawLine(0, (int)(height/2.0f), (int)width, (int)(height/2));//x axis
		g.drawLine((int)(width/2.0f), 0, (int)(width/2.0f), (int)height);//y axis
		g.setColor(Color.RED);
		float offset = -(width/2.0f);//center the graph
		for(float x = 0; x < width; x ++) {
			float y = Grapher.poly.eval(x + offset);
			//negate y axis
			y *= -1.0f;
			y += height/2.0f;//center
			g.fillRect((int)(x - pixelSize/2.0f), (int)(y - pixelSize/2.0f), (int)pixelSize, (int)pixelSize);
		}
	}
}

class Polynomial {
	public ArrayList<Monomial> poly = new ArrayList<Monomial>();
	
	public Polynomial() {
		
	}
	
	//get y from an x
	public float eval(float x) {
		float y = 0.0f;
		for(Monomial m : poly) {
			y += m.eval(x);
		}
		return y;
	}
	
	public static Polynomial parse(String raw) {
		Polynomial p = new Polynomial();
		raw = raw.replaceAll("-", "+-");
		String[] rawMonos = raw.split("\\+");
		for(String m : rawMonos) {
			p.poly.add(Monomial.parse(m));
		}
		return p;
	}
	
	public String toString() {
		String polyStr = "";
		for(Monomial m : poly) {
			polyStr += ("+" + m.toString());
		}
		return polyStr;
	}
}

class Monomial {
	float coefficient = 0.0f;
	float power = 0.0f;
	
	public Monomial(float co, float pow) {
		coefficient = co;
		power = pow;
	}
	
	public float eval(float x) {
		return coefficient * ((float)Math.pow(x, power));
	}
	
	public static Monomial parse(String raw) {
		raw = raw.trim();
		float co = 1.0f, pow = 1.0f;
		boolean hasX = raw.contains("x");
		boolean hasPow = raw.contains("^");
		boolean hasCo = raw.split("x").length > 0 && raw.split("x")[0].length() > 0;
		if(raw.trim().equals("x")) return new Monomial(1.0f, 1.0f);
		if(raw.trim().equals("-x")) return new Monomial(-1.0f, 1.0f);
		if(!hasX && !hasPow) {
			//a number
			co = Float.parseFloat(raw.trim());
			pow = 0.0f;
		}
		else if(hasX && !hasPow) {
			//first order (x to the first), num*x
			pow = 1.0f;
			co = Float.parseFloat(raw.split("\\^")[0].trim());
		}
		else if(hasX && hasPow) {
			//num*x^pow
			pow = Float.parseFloat(raw.split("\\^")[1].trim());
			if(hasCo) co = Float.parseFloat(raw.split("x")[0].trim());
			else co = 1.0f;
		}
		else if(!hasX && hasPow) {
			//num^pow
			String[] tmp = raw.split("\\^");
			pow = 0.0f;
			co = (float) Math.pow(Float.parseFloat(tmp[0].trim()), Float.parseFloat(tmp[1].trim()));
		}
		return new Monomial(co, pow);
	}
	
	public String toString() {
		return coefficient + "x^" + power;
	}
}