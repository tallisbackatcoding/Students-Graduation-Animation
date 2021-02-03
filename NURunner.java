import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.text.Position;

class Vector2{
	public double x;
	public double y;
	public Vector2(double X, double Y) {
		x = X;
		y = Y;
	}
	
	public void set(Vector2 v) {
		
	}
	public double distanceTo(Vector2 other) {
		return Math.sqrt((x - other.x)*(x - other.x) + (y - other.y)*(y - other.y));
	}
	public Vector2 normalize() {
		double length = Math.sqrt(x*x + y*y);
		if(length == 0) {
			return new Vector2(1,1);
		}
		return new Vector2(x/length, y/length);
	}
	public Vector2 plus(Vector2 other) {
		return new Vector2(x + other.x, y + other.y);	
	}
	public Vector2 minus(Vector2 other) {
		return new Vector2(x - other.x, y - other.y);	
	}
}

class Entity {
	public String name;
	public Vector2 position;
	public State state;
	public Common common;
	public String className;
	public Entity(){
		state = new ZigZag();
	}
	

	public void draw(Graphics2D g2d) {
	}
	public void step() {
		state.step(this);
	}
}

abstract class State {
	public String StateName;
	
	public boolean isOver = true;
	public boolean isVisible = true;
	public boolean isGraduated;
	public boolean isStationary = false;
	public abstract void step(Entity e);

}

class Invisible extends State{

	public Invisible() {
		StateName = "Invisible";
	}
	public void step(Entity e) {
		
	}
}

class Stationary extends State{
	
	public Stationary() {
		StateName = "Stationary";
	}
	public void step(Entity e) {
	}
	
	
}
class Rest extends State{
	
	public Rest() {
		StateName = "Rest";
	}
	public void step(Entity e) {
	}
	
}
class ZigZag extends State{
	boolean x_right = true;
	boolean y_right = true;
	public double speed;
	
	public ZigZag() {
		if(Common.randomInt(1, 2) == 1) {
			x_right = true;
		}else {
			x_right = false;
		}
		if(Common.randomInt(1, 2) == 1) {
			y_right = true;
		}else {
			y_right = false;
		}
		speed = (double)(Common.randomInt(1, 3));
		speed /= 2;
		StateName = "ZigZag";
	}
	
	public void step(Entity e) {
		if(x_right) {
			e.position.x += speed;
			if(e.position.x > 1150) {
				x_right = false;
			}
		}else {
			e.position.x -= speed;
			if(e.position.x < 0) {
				x_right = true;
			}
		}
		if(y_right) {
			e.position.y += speed;
			if(e.position.y > 550) {
				y_right = false;
			}
		}else {
			e.position.y -= speed;
			if(e.position.y < 0) {
				y_right = true;
			}
		}
		
	}
	
}
class GotoXY extends State{
	private double speed;
	Vector2 destination;
	public GotoXY(Vector2 pos) {
		speed = (double)Common.randomInt(1, 2);
		destination = new Vector2(pos.x, pos.y);
		StateName = "GotoXY";
	}
	
	
	public void step(Entity e) {
		Vector2 norm = destination.minus(e.position).normalize();
		e.position.x += speed * norm.x;
		e.position.y += speed * norm.y;
		if(destination.distanceTo(e.position) < 10) {
			if(e.className.equals("Academician")) {
				if(Common.randomInt(1, 3) == 1) {
					e.state = new ZigZag();
				}else if(Common.randomInt(1, 3) == 2) {
					e.state = new Rest();
				}else {
					destination = new Vector2(Common.randomInt(70, 1130),Common.randomInt(60, 540));
				}
			}else {
				if(((Student) e).isGraduated) {
					e.state = new Stationary();
					return;
				}
				if(Common.randomInt(1, 4) == 1) {
					e.state = new ZigZag();
				}else if(Common.randomInt(1, 4) == 2) {
					e.state = new Rest();
				}else if(Common.randomInt(1, 4) == 3) {
					e.state = new Closest();
				}else {
					destination = new Vector2(Common.randomInt(70, 1130),Common.randomInt(60, 540));
				}
			}
			
		}
		
	}
	
}
class Closest extends State{
	public double speed;
	public Closest() {
		speed = (double)Common.randomInt(1, 3);
		speed = speed * 2 /3;
		StateName = "Closest";
		
	}

	public void step(Entity e) {
		
		double min_dist = Double.MAX_VALUE;
		Vector2 closest_cord = e.position;
		for(Assessment as : Common.assessments) {
			if(e.position.distanceTo(as.position) < min_dist) {
				min_dist = e.position.distanceTo(as.position);
				closest_cord = as.position;
			}
		}
		Vector2 norm = closest_cord.minus(new Vector2(e.position.x + 8, e.position.y + 8)).normalize();
		e.position.x += speed * norm.x;
		e.position.y += speed * norm.y;
		
		if(e.position.distanceTo(closest_cord) < 15) {
			e.state = new Closest();
		}
		
	}
	
}
class Assessment extends Entity{
	public double randnum;
	public int points;
	public Assessment(int Point,int X,int Y) {
		points = Point;
	}
	public void draw(Graphics2D g2d) {
	}
}
abstract class AssessmentFactory{
	public abstract Assessment createAssessment(Vector2 position);
}
class LabFactory extends AssessmentFactory{//red circle

	public Assessment createAssessment(Vector2 position) {
		return new Lab(Common.randomInt(2, 4),(int)position.x,(int)position.y);
	}
	
}

class QuizFactory extends AssessmentFactory{ //blue triangle

	public Assessment createAssessment(Vector2 position) {
		return new Quiz(Common.randomInt(4, 5),(int)position.x,(int)position.y);
	}
	
}
class HomeworkFactory extends AssessmentFactory{//green square

	public Assessment createAssessment(Vector2 position) {
		return new Homework(Common.randomInt(1, 3),(int)position.x,(int)position.y);
	}
	
}
class Lab extends Assessment{
	public int points;
	private double randX,randY;
	private int newX,newY;
	public Lab(int Point, int X, int Y) {
		super(Point, X, Y);
		position = new Vector2(X,Y);
		points = Point;
		randX = Math.random()*1000;
		randY = Math.random()*1000;
		if(Math.random() < 0.5) {
			randX *= -1;
		}
		if(Math.random() < 0.5) {
			randY *= -1;
		}
		newX = (int)randX;
		newY = (int)randY;
		newX %= 60;
		newY %= 75;
		int XX = (int)position.x + 30;
		int YY = (int)position.y + 37;
		XX += newX;
		YY += newY;
		position.x = XX;
		position.y = YY;
	}
	public void draw(Graphics2D g2d) {

		g2d.setColor(Color.RED);
		g2d.fillOval((int)position.x, (int)position.y, 18, 18);
		Font Bold = new Font("Calibri",Font.BOLD, 17);
		g2d.setFont(Bold);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Integer.toString(points), (int)position.x + 5, (int)position.y + 14);
	}

}
class Quiz extends Assessment{
	public int points;
	private double randX,randY;
	private int newX,newY;
	public Quiz(int Point, int X, int Y) {
		super(Point, X, Y);
		position = new Vector2(X,Y);
		points = Point;
		randX = Math.random()*1000;
		randY = Math.random()*1000;
		if(Math.random() < 0.5) {
			randX *= -1;
		}
		if(Math.random() < 0.5) {
			randY *= -1;
		}
		newX = (int)randX;
		newY = (int)randY;
		newX %= 60;
		newY %= 75;
		int XX = (int)position.x + 30;
		int YY = (int)position.y + 37;
		XX += newX;
		YY += newY;
		position.x = XX;
		position.y = YY;
	}
	public void draw(Graphics2D g2d) {
		Color MyColor = new Color(95, 141, 173);
		g2d.setColor(MyColor);
		int x[] = {(int)position.x - 12,(int)position.x + 12,(int)position.x};
		int y[] = {(int)position.y + 21,(int)position.y + 21,(int)position.y};
		g2d.fillPolygon(x, y, 3);
		Font Bold = new Font("Calibri",Font.BOLD, 17);
		g2d.setFont(Bold);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Integer.toString(points), (int)position.x - 4, (int)position.y + 18);
	}
}

class Homework extends Assessment{
	public int points;
	private double randX,randY;
	private int newX,newY;
	public Homework(int Point, int X, int Y) {
		super(Point, X, Y);
		position = new Vector2(X,Y);
		points = Point;
		randX = Math.random()*1000;
		randY = Math.random()*1000;
		if(Math.random() < 0.5) {
			randX *= -1;
		}
		if(Math.random() < 0.5) {
			randY *= -1;
		}
		newX = (int)randX;
		newY = (int)randY;
		newX %= 60;
		newY %= 75;
		int XX = (int)position.x + 30;
		int YY = (int)position.y + 37;
		XX += newX;
		YY += newY;
		position.x = XX;
		position.y = YY;
	}
	public void draw(Graphics2D g2d) {
		g2d.setColor(Color.GREEN);
		g2d.fillRect((int)position.x, (int)position.y, 15, 15);
		Font Bold = new Font("Calibri",Font.BOLD, 17);
		g2d.setFont(Bold);
		g2d.setColor(Color.BLACK);
		g2d.drawString(Integer.toString(points), (int)position.x + 3, (int)position.y + 12);
	}
}


class Academician extends Entity {
	private BufferedImage image;
	public Academician(String AcademicianName, String filename, double A, double B) {
		name = AcademicianName;
		try {
			image = ImageIO.read(new File(filename));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		position = new Vector2(A,B);
		state = new ZigZag();
		className = "Academician";
	}
	public void draw(Graphics2D g2d) {
		Font Bold = new Font("Calibri",Font.BOLD, 18);
		g2d.drawImage(image,(int)position.x ,(int)position.y , 60, 75, null);
		g2d.setPaint(Color.BLACK);
		g2d.setFont(Bold);
		g2d.drawString(name,(int)position.x + (34 - name.length()*5) , (int)position.y);
		g2d.drawString(state.StateName,(int)position.x + (34 - state.StateName.length()*5), (int)position.y + 90);
		
		if(Math.random() > 0.95 && state.StateName != "Rest" && Common.number_of_graduated < Common.students.size() ) {
			double randInt = Math.random();
			if(randInt > 0.66) {
				AssessmentFactory AF = new HomeworkFactory();
				Assessment HW = AF.createAssessment(position);
				Common.assessments.add(HW);
			}else if(randInt> 0.33) {
				AssessmentFactory AF = new LabFactory();
				Assessment L = AF.createAssessment(position);
				Common.assessments.add(L);
			}else {
				AssessmentFactory AF = new QuizFactory();
				Assessment Q = AF.createAssessment(position);
				Common.assessments.add(Q);
			}
		}
	}

	Assessment createAssessment() {
		return null;
	}
}
class Speaker extends Entity{
	private BufferedImage image;
	public String filename;
	public Speaker(String SpeakerName, String Filename, double A, double B) {
		className = "Speaker";
		name = SpeakerName;
		position = new Vector2(A,B);
		state = new Invisible();
		filename = Filename;
		
	}
	public void draw(Graphics2D g2d) {
		if(state.StateName.equals("Stationary")){
			try {
				image = ImageIO.read(new File(filename));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Font Bold = new Font("Calibri",Font.BOLD, 18);
			g2d.drawImage(image,(int)position.x ,(int)position.y , 60, 75, null);
			g2d.setPaint(Color.BLACK);
			g2d.setFont(Bold);
			g2d.drawString(name,(int)position.x + (34 - name.length()*5) , (int)position.y);
			g2d.drawString(state.StateName,(int)position.x + (34 - state.StateName.length()*5), (int)position.y + 90);
		}
	}
	public void step() {
		if(Common.number_of_graduated >= Common.students.size()) {
			state = new Stationary();
		}
		
	}
}
class Student extends Entity{
	public boolean isGraduated = false;
	public boolean isStationary = false;
	public int points;
	public Student(String Name) {
		Vector2 Cords = new Vector2(Common.randomInt(50, 1150),Common.randomInt(50, 1150));
		position = Cords;
		this.name = Name;
		points = 0;
		className = "Student";
	}
	public void draw(Graphics2D g2d) {
		Color LtBlue;
		if(!isGraduated) {
			LtBlue = new Color(100,215,255);
		}else {
			LtBlue = new Color(138,43,226);
		}
		
		g2d.setColor(LtBlue);
		g2d.fillOval((int)position.x,(int)position.y, 36, 36);
		Font Bold = new Font("Calibri",Font.BOLD, 20);
		g2d.setFont(Bold);
		g2d.setColor(Color.BLACK);
		g2d.drawOval((int)position.x,(int)position.y, 36, 36);
		Bold = new Font("Calibri",Font.BOLD, 16);
		g2d.setFont(Bold);
		g2d.drawString(name, (int)position.x + (18 - name.length()*4) , (int)position.y - 2);
		g2d.drawString(state.StateName,(int)position.x + (29 - state.StateName.length()*5), (int)position.y + 48);
		int shift_left_for_points = 0;
		if(points > 10) {
			shift_left_for_points = 4;
		}
		if(points > 100) {
			shift_left_for_points = 9;
		}
		
		
		for(int i = 0; i < Common.assessments.size();i++) {
			Vector2 tempPos = new Vector2(position.x + 15, position.y + 15);
			if(Common.assessments.get(i).position.distanceTo(tempPos) < 20) {
				if(!isGraduated) {
					points += Common.assessments.get(i).points;
					Common.assessments.remove(i);
				}
			}
			if(points >= 100) {
				if(!isGraduated) {
					isGraduated = true;
					state = new GotoXY(Common.GraduationCords);
					System.out.println("GRADUATED");
					Common.number_of_graduated++;
				}
				if(Common.number_of_graduated >= Common.students.size()) {
				}
			}
		}
		g2d.drawString(Integer.toString(points), (int)position.x + 13 - shift_left_for_points, (int)position.y + 23);
	}
}


class Display extends JPanel {
	private BufferedImage imageNU;
	private int counter[] = new int[Common.students.size()];
	private int it[] = new int[Common.students.size()];	
	private boolean next_state[] = new boolean[Common.students.size()];
	
	private int counter_1[] = new int[Common.academicians.length];
	private boolean next_state_1[] = new boolean[Common.academicians.length];
	private int it_1[] = new int[Common.academicians.length];	
	
	boolean next_state1 = false,next_state2 = false, next_state3 = false, next_state4 = false;
	public Display() {
		super();
		setBackground(Color.WHITE);
		for(int i = 0; i < Common.students.size(); i++) {
			counter[i] = Common.randomInt(100, 250);
			it[i] = 0;
			next_state[i] = false;
		}
		for(int i = 0; i < Common.academicians.length; i++) {
			counter_1[i] = Common.randomInt(100, 250);
			it_1[i] = 0;
			next_state_1[i] = false;
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(1200, 600);
	}
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		try {
			imageNU = ImageIO.read(new File("NUMap-Faded.jpg"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		g2d.drawImage(imageNU, 0, 0, 1200, 600, null);

		Common.drawAllEntities(g2d);
		Common.stepAllEntities();
		if(Common.number_of_graduated >= Common.students.size()) {
			Common.assessments.clear();
		}
		double r;
		for(int i = 0; i < Common.students.size(); i++) {
			r = Math.random();
			if(!Common.students.get(i).isGraduated) {
				if(next_state[i] == true) {
					if(r > 0.71) {
						Common.students.get(i).state = new GotoXY(new Vector2(Common.randomInt(30, 1160),Common.randomInt(30, 560)));
					}else if(r > 0.46) {
						Common.students.get(i).state = new ZigZag();
					}else if(r > 0.17) {
						Common.students.get(i).state = new Closest();
					}else {
						Common.students.get(i).state = new Rest();
					}
					next_state[i] = false;
				}
				
				it[i]++;
				if(it[i] > counter[i]) {
					it[i] = 0;
					next_state[i] = true;
					counter[i] = Common.randomInt(100, 250);
				}
			}
			
		}
		for(int i = 0; i < Common.academicians.length; i++) {
			if(Common.number_of_graduated < Common.students.size()){
				r = Math.random();
				if(next_state_1[i] == true) {
					if (r > 0.76) {
						Common.academicians[i].state = new Rest();
					}else if(r > 0.37) {
						Common.academicians[i].state = new ZigZag();
					}else {
						Common.academicians[i].state = new GotoXY(new Vector2(Common.randomInt(70, 1100),Common.randomInt(70, 530)));
					}
					next_state_1[i] = false;
				}
				
			it_1[i]++;
			if(it_1[i] > counter_1[i]) {
				it_1[i] = 0;
				next_state_1[i] = true;
				counter_1[i] = Common.randomInt(100, 250);
			}
			}else {
				Common.academicians[i].state = new Stationary();
				Common.academicians[i].position.x = 670 + i*85;
				Common.academicians[i].position.y = 340;
			}
			
		}
		ActionListener listener1 = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		};
		Timer myTimer = new Timer(100, listener1);
		myTimer.start();		
		
	}
}

class Common extends JFrame {
		private Display draw1;
		private JButton button1;
		public static Academician[] academicians;
		public static LinkedList<Assessment> assessments; 
		public static LinkedList<Student> students;
		public static LinkedList<Speaker> speakers;
		public static int number_of_graduated = 0;
		public static Vector2 GraduationCords = new Vector2(randomInt(822, 832),randomInt(475,485));
		public static void stepAllEntities() {
			for(Academician a: academicians)
	            a.step();

	        for(Speaker a: speakers)
	            a.step();

	        for(Student a: students)
	            a.step();
		}
		public static void drawAllEntities(Graphics2D g2d) {
			for(Assessment a: assessments)
		        a.draw(g2d);

	        for(Speaker a: speakers)
	            a.draw(g2d);

	        for(Student a: students)
	            a.draw(g2d);
	        
	        for(Academician a: academicians)
	            a.draw(g2d);
	        if(number_of_graduated >= students.size()) {
	            Font a = new Font("Calibri",Font.BOLD, 18);
	            g2d.setPaint(Color.BLACK);
	            g2d.setFont(a);
	            g2d.drawString("Graduation Ceremony", 750, 580);
	        }
		}
		public Common() {
			assessments = new LinkedList<Assessment>();
			academicians = new Academician[4];
			academicians[0] = new Academician("Temizer","SelimTemizer.gif",((int)(Math.random()*1234))%1100,((int)(Math.random()*1234))%550);
			academicians[1] = new Academician("Nivelle","HansDeNivelle.gif",((int)(Math.random()*1234))%1100,((int)(Math.random()*1234))%550);
			academicians[2] = new Academician("Tourassis","VassiliosTourassis.gif",((int)(Math.random()*1234))%1100,((int)(Math.random()*1234))%550);
			academicians[3] = new Academician("Katsu","ShigeoKatsu.gif",((int)(Math.random()*1234))%1100,((int)(Math.random()*1234))%550);
			
			students = new LinkedList<Student>();
			students.add(new Student("Alisher"));
			students.add(new Student("Yerzhan"));
			students.add(new Student("Nursultan"));
			students.add(new Student("Aslan"));
			students.add(new Student("Yerken"));
			students.add(new Student("Arman"));
			students.add(new Student("Medet"));
			students.add(new Student("Maksat"));
			students.add(new Student("Adil"));
			students.add(new Student("Nurzhan"));
			
			speakers = new LinkedList<Speaker>();
			speakers.add(new Speaker("Tokayev","KassymJomartTokayev.gif",GraduationCords.x - 115, GraduationCords.y - 20));
			speakers.add(new Speaker("Nazarbayev","NursultanNazarbayev.gif",GraduationCords.x + 70, GraduationCords.y - 20));
			
			setTitle("Graduation Ceremony");
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			Container cp = getContentPane();
			cp.setLayout(new FlowLayout());
			draw1 = new Display();
			
			button1 = new JButton("EXIT");
			ActionListener listener1 = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			};
	
			button1.addActionListener(listener1);
	
			cp.add(draw1);
			cp.add(button1);
	
			pack();
		}
		public static int randomInt(int from, int to) {
			double a = Math.random()*45637613;
			int b = (int) a;
			int result = b%(to - from + 1);
			return result + from;
		}
}

public class NURunner extends JFrame {

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Common().setVisible(true);
			}
		});

	}

}
