package Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class Main extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main frame = new Main();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1031, 669);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JTree tree = new JTree();
        tree.setBounds(0, 0, 140, 533);
        contentPane.add(tree);
        
        ChartPanel chartPanel = new ChartPanel((JFreeChart) null);
        chartPanel.setBounds(156, 11, 849, 414);
        contentPane.add(chartPanel);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Archivo");
        menuBar.add(fileMenu);

        JMenuItem newItem = new JMenuItem("Nuevo");
        fileMenu.add(newItem);

        JMenuItem openItem = new JMenuItem("Abrir");
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Guardar");
        fileMenu.add(saveItem);

        JMenuItem exitItem = new JMenuItem("Salir");
        fileMenu.add(exitItem);

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}
