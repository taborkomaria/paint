import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Panel extends JPanel {
    public static final int CELL_SIZE = 40; // размер ячейки
    public static final int TOOL_BAR_SIZE = 50; // высота панели инструментов

    private int width; // ширина
    private int height; // высота поля для рисования

    private Color currentColor; // цвет кисти
    private Color currentColorRight; // цвет кисти
    private Color[] colorPallete;
    private int x, y; // координаты рисуемой клетки
    private boolean firstDraw = true;
    private boolean drawKey = false;
    private boolean rightButton = false;
    private int toolbarWidth;
    private Graphics2D g2;

    private int xKey, yKey; // координаты на клавиатуре

    //конструктор
  	//w, h - ширина и высота панели
    public Panel(int w, int h) {
    	
        width = w;
        height = h;
        System.out.println("size " + width + ", " + height);
        x = y = -1;

        //координаты центра, для позиции рисования с клавиатуры
        xKey = (int)(width / (2 * CELL_SIZE))*CELL_SIZE;
        yKey = (int)(height / (2 * CELL_SIZE))*CELL_SIZE;
        System.out.println("center " + xKey + ", " + yKey);
        
        //цветовая палитра
        colorPallete = new Color[4];
        colorPallete[0] = Color.blue;
        colorPallete[1] = Color.red;
        colorPallete[2] = Color.green;
        colorPallete[3] = Color.yellow;
        
        //задание текущего цвета
        currentColor = colorPallete[0];
        //цвет для правой кнопки мыши
        currentColorRight = Color.white;
        addMouseListener(new Handler());
        addMouseMotionListener(new Handler());
        addKeyListener(new Handler());
        setFocusable(true);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g2 = (Graphics2D)g;

        //отрисовка поля и цветовой палитры
        if(firstDraw) {
            setBackground(new Color(238, 238, 238));
            //создание поля для рисования
            Rectangle2D background  = new Rectangle2D.Double(0, 0, this.width, this.height);
            g2.setPaint(Color.white);
            g2.fill(background);
            g2.setPaint(Color.black);
            g2.draw(background);

            int k;

            //кол-во столбцов и строк
            int rows = height / CELL_SIZE;
            int columns = width / CELL_SIZE;

            for (k = 0; k < rows; k++)
                g2.drawLine(0, k * CELL_SIZE , width, k * CELL_SIZE);

            for (k = 0; k < columns; k++)
                g2.drawLine(k * CELL_SIZE , 0, k * CELL_SIZE , height);
            firstDraw = false;
            //отрисовка палитры цветов
            drawColorPallete();
        }
        //обновление отдельных полей при рисовании
        else{
        	//если нажата правая клавиша
        	if(rightButton){
        		//рисуем цветом для правой кнопки мыши
        		g2.setPaint(currentColorRight);
        	}
        	//если нажата правая клавиша
        	else{
        		//рисуем цветом, который выбран на цветовой палитре
        		g2.setPaint(currentColor);
        	}
        	//если используются клавиши для рисования
        	if(drawKey) {
        		//рисуем относительно координат для клавиатуры
        		g2.fill(new Rectangle2D.Double(xKey+1, yKey+1, CELL_SIZE-1, CELL_SIZE-1));
            }
        	else{
        		//рисуем относительно координат для мыши
            	g2.fill(new Rectangle2D.Double(x+1, y+1, CELL_SIZE-1, CELL_SIZE-1));
        	}
        	
        }
    }
    //отрисовка цветовой палитры
    private void drawColorPallete() {
    	//определение ширины ячейки для цвета
    	toolbarWidth = (int)width/4;
        for(int i=0; i<4;i++){
        	g2.setPaint(colorPallete[i]);
        	g2.fill(new Rectangle2D.Double(toolbarWidth*i, height+TOOL_BAR_SIZE, toolbarWidth, TOOL_BAR_SIZE));
        }
    }
    //задание координаты для рисования с помощью мыши
  	//eventX, eventY - текущие координаты мыши
    private void setCell(int eventX, int eventY) {
    	//нажатие на палтиру цветов
    	if(eventY > height+TOOL_BAR_SIZE){
    		int index = (int)eventX/toolbarWidth;
    		//определение цвета из палитры
    		if(index >= 0 && index <4 ){
    			currentColor = colorPallete[index];	
    		}
    		x = -100;
    		y = -100;
    	}
    	else{
    		//рассчет в какую ячейку поля попали
    		x = (eventX / CELL_SIZE ) * CELL_SIZE;
    		y = (eventY > height) ? -100:((eventY / CELL_SIZE ) * CELL_SIZE );
    	}
    }
    //задание координаты для рисования с помощью клавиатуры
  	//eventX, eventY - текущие координаты курсора 
    private void setCellKey(int eventX, int eventY) {
    	int widthKey = width - CELL_SIZE;
    	int heightKey = height - CELL_SIZE;
    	//проверка выхода за границы области рисования
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
        	//зажата правая кнопка мыши
        	if(SwingUtilities.isRightMouseButton(e)){
        		rightButton = true;
        	}
        	//определяем ячейку для перерисовки
        	setCell(e.getX(),e.getY());
        	//обновляем область
            paintImmediately(x+1,y+1,CELL_SIZE-1,CELL_SIZE-1);
        	
        }
        @Override
        public void mouseDragged(MouseEvent e) {
        	//зажата правая кнопка мыши
        	if(SwingUtilities.isRightMouseButton(e)){
        		rightButton = true;
        	}
        	//определяем ячейку для перерисовки
        	setCell(e.getX(),e.getY());
        	//обновляем область
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
        		//стрелка вверх
	        	case KeyEvent.VK_UP:
	        		// handle up
	        		//определяем ячейку для перерисовки
	                setCellKey(0,-CELL_SIZE);
	              //обновляем область
                	paintImmediately(xKey+1,yKey+1,CELL_SIZE-1,CELL_SIZE-1);
                	//если зажата клавиша shift - перерисовать еще две ячейки
	                if(e.isShiftDown()){
	                	for(int i=2; i<4; i++){
		                	setCellKey(0,-CELL_SIZE);
		                	paintImmediately(xKey+1,yKey+1,CELL_SIZE-1,CELL_SIZE-1);
		                }
	                }
	                break;
	        	case KeyEvent.VK_DOWN:
	        		// handle down
	        		//определяем ячейку для перерисовки
	                setCellKey(0,CELL_SIZE);
	                //обновляем область
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
	            	//определяем ячейку для перерисовки
	                setCellKey(-CELL_SIZE, 0);
	                //обновляем область
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
	            	//определяем ячейку для перерисовки
	            	setCellKey(CELL_SIZE, 0);
	            	//обновляем область
                	paintImmediately(xKey+1,yKey+1,CELL_SIZE-1,CELL_SIZE-1);
	                if(e.isShiftDown()){
	                	for(int i=2; i<4; i++){
		                	setCellKey(CELL_SIZE, 0);
		                	paintImmediately(xKey+1,yKey+1,CELL_SIZE-1,CELL_SIZE-1);
	                	}
	                }
	                break;
	            case 82 :
	            	//выбор красного цвета для рисования
	            	currentColor = Color.red;
	                break;
	            case 71 :
	            	//выбор зеленого цвета для рисования
	            	currentColor = Color.green;
	                break;
	            case 66 :
	            	//выбор синего цвета для рисования
	            	currentColor = Color.blue;
	                break;
	            case 89 :
	            	//выбор желтого цвета для рисования
	            	currentColor = Color.yellow;
	                break;
	    
        	}
            
        }
    }
}
