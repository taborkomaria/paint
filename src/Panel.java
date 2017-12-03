import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Panel extends JPanel {
    public static final int CELL_SIZE = 40; // ������ ������
    public static final int TOOL_BAR_SIZE = 50; // ������ ������ ������������

    private int width; // ������
    private int height; // ������ ���� ��� ���������

    private Color currentColor; // ���� �����
    private Color currentColorRight; // ���� �����
    private Color[] colorPallete;
    private int x, y; // ���������� �������� ������
    private boolean firstDraw = true;
    private boolean drawKey = false;
    private boolean rightButton = false;
    private int toolbarWidth;
    private Graphics2D g2;

    private int xKey, yKey; // ���������� �� ����������

    //�����������
  	//w, h - ������ � ������ ������
    public Panel(int w, int h) {
    	
        width = w;
        height = h;
        System.out.println("size " + width + ", " + height);
        x = y = -1;

        //���������� ������, ��� ������� ��������� � ����������
        xKey = (int)(width / (2 * CELL_SIZE))*CELL_SIZE;
        yKey = (int)(height / (2 * CELL_SIZE))*CELL_SIZE;
        System.out.println("center " + xKey + ", " + yKey);
        
        //�������� �������
        colorPallete = new Color[4];
        colorPallete[0] = Color.blue;
        colorPallete[1] = Color.red;
        colorPallete[2] = Color.green;
        colorPallete[3] = Color.yellow;
        
        //������� �������� �����
        currentColor = colorPallete[0];
        //���� ��� ������ ������ ����
        currentColorRight = Color.white;
        addMouseListener(new Handler());
        addMouseMotionListener(new Handler());
        addKeyListener(new Handler());
        setFocusable(true);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g2 = (Graphics2D)g;

        //��������� ���� � �������� �������
        if(firstDraw) {
            setBackground(new Color(238, 238, 238));
            //�������� ���� ��� ���������
            Rectangle2D background  = new Rectangle2D.Double(0, 0, this.width, this.height);
            g2.setPaint(Color.white);
            g2.fill(background);
            g2.setPaint(Color.black);
            g2.draw(background);

            int k;

            //���-�� �������� � �����
            int rows = height / CELL_SIZE;
            int columns = width / CELL_SIZE;

            for (k = 0; k < rows; k++)
                g2.drawLine(0, k * CELL_SIZE , width, k * CELL_SIZE);

            for (k = 0; k < columns; k++)
                g2.drawLine(k * CELL_SIZE , 0, k * CELL_SIZE , height);
            firstDraw = false;
            //��������� ������� ������
            drawColorPallete();
        }
        //���������� ��������� ����� ��� ���������
        else{
        	//���� ������ ������ �������
        	if(rightButton){
        		//������ ������ ��� ������ ������ ����
        		g2.setPaint(currentColorRight);
        	}
        	//���� ������ ������ �������
        	else{
        		//������ ������, ������� ������ �� �������� �������
        		g2.setPaint(currentColor);
        	}
        	//���� ������������ ������� ��� ���������
        	if(drawKey) {
        		//������ ������������ ��������� ��� ����������
        		g2.fill(new Rectangle2D.Double(xKey+1, yKey+1, CELL_SIZE-1, CELL_SIZE-1));
            }
        	else{
        		//������ ������������ ��������� ��� ����
            	g2.fill(new Rectangle2D.Double(x+1, y+1, CELL_SIZE-1, CELL_SIZE-1));
        	}
        	
        }
    }
    //��������� �������� �������
    private void drawColorPallete() {
    	//����������� ������ ������ ��� �����
    	toolbarWidth = (int)width/4;
        for(int i=0; i<4;i++){
        	g2.setPaint(colorPallete[i]);
        	g2.fill(new Rectangle2D.Double(toolbarWidth*i, height+TOOL_BAR_SIZE, toolbarWidth, TOOL_BAR_SIZE));
        }
    }
    //������� ���������� ��� ��������� � ������� ����
  	//eventX, eventY - ������� ���������� ����
    private void setCell(int eventX, int eventY) {
    	//������� �� ������� ������
    	if(eventY > height+TOOL_BAR_SIZE){
    		int index = (int)eventX/toolbarWidth;
    		//����������� ����� �� �������
    		if(index >= 0 && index <4 ){
    			currentColor = colorPallete[index];	
    		}
    		x = -100;
    		y = -100;
    	}
    	else{
    		//������� � ����� ������ ���� ������
    		x = (eventX / CELL_SIZE ) * CELL_SIZE;
    		y = (eventY > height) ? -100:((eventY / CELL_SIZE ) * CELL_SIZE );
    	}
    }
    //������� ���������� ��� ��������� � ������� ����������
  	//eventX, eventY - ������� ���������� ������� 
    private void setCellKey(int eventX, int eventY) {
    	int widthKey = width - CELL_SIZE;
    	int heightKey = height - CELL_SIZE;
    	//�������� ������ �� ������� ������� ���������
    	if(eventX >0){
    		xKey = (xKey < widthKey)? (xKey + eventX) :  widthKey;
    	}
    	else{
    		xKey = (xKey > 0)? (xKey + eventX) :  0;
    	}
    	if(eventY >0){
    		yKey = (yKey < heightKey) ? (yKey + eventY) : heightKey ;
    	}
    	else{
    		yKey = (yKey > 0) ? (yKey + eventY) : 0 ;
    	}
    }

    private class Handler extends MouseAdapter implements KeyListener{
        @Override
        public void mousePressed(MouseEvent e) {
        	//������ ������ ������ ����
        	if(SwingUtilities.isRightMouseButton(e)){
        		rightButton = true;
        	}
        	//���������� ������ ��� �����������
        	setCell(e.getX(),e.getY());
        	//��������� �������
            paintImmediately(x+1,y+1,CELL_SIZE-1,CELL_SIZE-1);
        	
        }
        @Override
        public void mouseDragged(MouseEvent e) {
        	//������ ������ ������ ����
        	if(SwingUtilities.isRightMouseButton(e)){
        		rightButton = true;
        	}
        	//���������� ������ ��� �����������
        	setCell(e.getX(),e.getY());
        	//��������� �������
            paintImmediately(x+1,y+1,CELL_SIZE-1,CELL_SIZE-1);
        }
        @Override
        public void mouseReleased(MouseEvent e) {
        	rightButton = false;
        }
        @Override
        public void keyReleased(KeyEvent e) {
        	drawKey = false;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        	drawKey = true; 	
        }

        @Override
        public void keyPressed(KeyEvent e) {
        	drawKey = true;
        	int keyCode = e.getKeyCode();
        	switch( keyCode ) {
        		//������� �����
	        	case KeyEvent.VK_UP:
	        		// handle up
	        		//���������� ������ ��� �����������
	                setCellKey(0,-CELL_SIZE);
	              //��������� �������
                	paintImmediately(xKey+1,yKey+1,CELL_SIZE-1,CELL_SIZE-1);
                	//���� ������ ������� shift - ������������ ��� ��� ������
	                if(e.isShiftDown()){
	                	for(int i=2; i<4; i++){
		                	setCellKey(0,-CELL_SIZE);
		                	paintImmediately(xKey+1,yKey+1,CELL_SIZE-1,CELL_SIZE-1);
		                }
	                }
	                break;
	        	case KeyEvent.VK_DOWN:
	        		// handle down
	        		//���������� ������ ��� �����������
	                setCellKey(0,CELL_SIZE);
	                //��������� �������
                	paintImmediately(xKey+1,yKey+1,CELL_SIZE-1,CELL_SIZE-1);
	                if(e.isShiftDown()){
	                	for(int i=2; i<4; i++){
	                		setCellKey(0,CELL_SIZE);
	                		paintImmediately(xKey+1,yKey+1,CELL_SIZE-1,CELL_SIZE-1);
	                	}
	                }
	                break;
	            case KeyEvent.VK_LEFT:
	            	// handle left
	            	//���������� ������ ��� �����������
	                setCellKey(-CELL_SIZE, 0);
	                //��������� �������
                	paintImmediately(xKey+1,yKey+1,CELL_SIZE-1,CELL_SIZE-1);
	                if(e.isShiftDown()){
	                	for(int i=2; i<4; i++){
		                	setCellKey(-CELL_SIZE, 0);
		                	paintImmediately(xKey+1,yKey+1,CELL_SIZE-1,CELL_SIZE-1);
	                	}
	                }
	                break;
	            case KeyEvent.VK_RIGHT :
	            	// handle right
	            	//���������� ������ ��� �����������
	            	setCellKey(CELL_SIZE, 0);
	            	//��������� �������
                	paintImmediately(xKey+1,yKey+1,CELL_SIZE-1,CELL_SIZE-1);
	                if(e.isShiftDown()){
	                	for(int i=2; i<4; i++){
		                	setCellKey(CELL_SIZE, 0);
		                	paintImmediately(xKey+1,yKey+1,CELL_SIZE-1,CELL_SIZE-1);
	                	}
	                }
	                break;
	            case 82 :
	            	//����� �������� ����� ��� ���������
	            	currentColor = Color.red;
	                break;
	            case 71 :
	            	//����� �������� ����� ��� ���������
	            	currentColor = Color.green;
	                break;
	            case 66 :
	            	//����� ������ ����� ��� ���������
	            	currentColor = Color.blue;
	                break;
	            case 89 :
	            	//����� ������� ����� ��� ���������
	            	currentColor = Color.yellow;
	                break;
	    
        	}
            
        }
    }
}
