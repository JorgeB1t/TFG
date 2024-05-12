package Pruebas;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;

public class P2 extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JFreeChart grafico;
    private DefaultCategoryDataset dcds;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    P2 frame = new P2();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public P2() {
        getContentPane().setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 766, 529);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);

        dcds = new DefaultCategoryDataset();
        dcds.addValue(10, "patata", "lunes");
        dcds.addValue(20, "patata", "martes");
        dcds.addValue(30, "patata", "miércoles");
        dcds.addValue(40, "patata", "jueves");
        dcds.addValue(50, "patata", "viernes");
        dcds.addValue(60, "patata", "sábado");
        dcds.addValue(70, "patata", "domingo");

        // Crear el gráfico utilizando los datos del conjunto de datos
        grafico = ChartFactory.createBarChart("Mi Gráfico", "Día", "Valor", dcds, PlotOrientation.VERTICAL, true, false, false);
        grafico.setBackgroundPaint(Color.WHITE); // Cambia el color de fondo del gráfico a blanco
        
        
        /*
         * CAMBIAR COLOR BARRAS
         * 
        CategoryPlot plot = (CategoryPlot) grafico.getPlot();
        plot.getRenderer().setSeriesPaint(0, Color.BLUE);
*/
     // Añade una leyenda en la parte inferior del gráfico
        LegendTitle legend = grafico.getLegend();
        legend.setPosition(RectangleEdge.BOTTOM);




        // Crear el ChartPanel y añadirlo al JPanel principal
        ChartPanel chartPanel = new ChartPanel(grafico);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBounds(10, 10, 600, 350);
        contentPane.add(chartPanel);
    }
}
