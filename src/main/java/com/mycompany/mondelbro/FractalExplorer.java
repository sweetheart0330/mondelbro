/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mondelbro;

/**
 *
 * @author sweet
 */
import java.awt.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import javax.swing.JFileChooser.*;
import javax.swing.filechooser.*;
import javax.imageio.ImageIO.*;
import java.awt.image.*;

public class FractalExplorer
{
    /** Целочисленный размер отображения - это ширина и высота отображения в пикселях. **/
    private int displaySize;
    
    /**
     * Ссылка JImageDisplay для обновления отображения с помощью различных методов как
      * фрактал вычислен.
     */
    private JImageDisplay display;
    
    /** Объект FractalGenerator для каждого типа фрактала. **/
    private FractalGenerator fractal;
    
    /**
     * Объект Rectangle2D.Double, который определяет диапазон
      * то, что мы в настоящее время показываем. 
     */
    private Rectangle2D.Double range;
    
    /**
     * Конструктор, который принимает размер отображения, сохраняет его и
      * инициализирует объекты диапазона и фрактал-генератора.
     */
    public FractalExplorer(int size) {
        /** Размер дисплея  **/
        displaySize = size;
        
        /** Инициализирует фрактальный генератор и объекты диапазона. **/
        fractal = new Mondelbrot();
        range = new Rectangle2D.Double();
        fractal.getInitialRange(range);
        display = new JImageDisplay(displaySize, displaySize);
        
    }
    
    /**
     * Этот метод инициализирует графический интерфейс Swing с помощью JFrame, содержащего
      * Объект JImageDisplay и кнопку для очистки дисплея
     */
    public void createAndShowGUI()
    {
        /** Установите для frame использование java.awt.BorderLayout для своего содержимого. **/
        display.setLayout(new BorderLayout());
        JFrame myFrame = new JFrame("Fractal Explorer");
        
        /** Добавьте объект отображения изображения в
         * BorderLayout.CENTER position.
         */
        myFrame.add(display, BorderLayout.CENTER);
        
        /** Создаем кнопку очистки. **/
        JButton resetButton = new JButton("Reset");
        
        /** Экземпляр ButtonHandler на кнопке сброса. **/
        ButtonHandler resetHandler = new ButtonHandler();
        resetButton.addActionListener(resetHandler);
        
        /** Экземпляр MouseHandler в компоненте фрактального отображения. **/
        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);
        
        /** Вызываем операцию закрытия фрейма по умолчанию на "выход".. **/
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
     

        
        /**
         * Создаем новый объект JPanel, и добавляем панель в рамку в NORTH
          * позиции в макете.
         */
        JPanel myPanel = new JPanel();

        myFrame.add(myPanel, BorderLayout.NORTH);
        
        /**
         * Добавляем кнопку очистки
         */
        JPanel myBottomPanel = new JPanel();
        myBottomPanel.add(resetButton);
        myFrame.add(myBottomPanel, BorderLayout.SOUTH);


        
        
        /**
         * Размещаем содержимое фрейма, делаем его видимым и
          * запрещаем изменение размера окна.
         */
        myFrame.pack();
        myFrame.setVisible(true);
        myFrame.setResizable(false);
        
    }
    
    /**
     * Приватный вспомогательный метод для отображения фрактала. Этот метод проходит
      * через каждый пиксель на дисплее и вычисляет количество
      * итераций для соответствующих координат во фрактале
      * Область отображения. Если количество итераций равно -1, установит цвет пикселя.
      * в черный. В противном случае выберет значение в зависимости от количества итераций.
      * Обновит дисплей цветом для каждого пикселя и перекрасит
      * JImageDisplay, когда все пиксели нарисованы.
     */
    public void drawFractal()
    {
        /**Проходим через каждый пиксель на дисплее **/
        for (int x=0; x<displaySize; x++){
            for (int y=0; y<displaySize; y++){
                
                /**
                 * Находим соответствующие координаты xCoord и yCoord
                  * в области отображения фрактала.
                 */
                double xCoord = fractal.getCoord(range.x,
                range.x + range.width, displaySize, x);
                double yCoord = fractal.getCoord(range.y,
                range.y + range.height, displaySize, y);
                
                /**
                 * Вычисляем количество итераций для координат в
                  * область отображения фрактала.
                 */
                int iteration = fractal.numIterations(xCoord, yCoord);
                
                /** If number of iterations is -1, set the pixel to black. **/
                if (iteration == -1){
                    display.drawPixel(x, y, 0);
                }
                
                else {
                    /**
                     * В противном случае выбераем значение оттенка на основе числа
                      * итераций.  
                     */
                    float hue = 0.6f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                
                    /** Обновляем дисплей цветом для каждого пикселя. **/
                    display.drawPixel(x, y, rgbColor);
                }
                
            }
        }
        /**
         * Когда все пиксели будут нарисованы, перекрасим JImageDisplay, чтобы он соответствовал
          * текущее содержимому его изображения.
         */
        display.repaint();
    }
    /**
     * Внутренний класс для обработки событий ActionListener.
     */
    private class ButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            /** Получаем источник действия. **/
            String command = e.getActionCommand();
                      
            /**
             * Если источником является кнопка сброса, сбросьте дисплей и нарисуйте
              * фрактал.
             */
           if (command.equals("Reset")) {
                fractal.getInitialRange(range);
                drawFractal();
            }
           
        }
    }
    
    /**
     * Внутренний класс для обработки событий MouseListener с дисплея.
     */
    private class MouseHandler extends MouseAdapter
    {
        /**
         * Когда обработчик получает событие щелчка мыши, он отображает пиксель-
          * координаты щелчка в области фрактала, который
          * отображается, а затем вызывает функцию RecenterAndZoomRange () генератора
          * метод с координатами, по которым был выполнен щелчок, и шкалой 0,5.
         */
        @Override
        public void mouseClicked(MouseEvent e)
        {
            /** Получаем координату x области отображения щелчка мыши. **/
            int x = e.getX();
            double xCoord = fractal.getCoord(range.x,
            range.x + range.width, displaySize, x);
            
            /** Получаем координату y области отображения щелчка мышью. **/
            int y = e.getY();
            double yCoord = fractal.getCoord(range.y,
            range.y + range.height, displaySize, y);
            
            /**
             * Вызывааем метод генератора RecenterAndZoomRange() с помощью
              * координаты, по которым был выполнен щелчок, и шкала 0,5.
             */
            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);
            
            /**
             * Перерисовываем фрактал после изменения отображаемой области.
             */
            drawFractal();
        }
    }
    
    /**
     * Статический метод main() для запуска FractalExplorer. Инициализирует новый
      * Экземпляр FractalExplorer с размером дисплея 600, вызывает
      * createAndShowGU () в объекте проводника, а затем вызывает
      * drawFractal() в проводнике, чтобы увидеть исходный вид.
     */
}
