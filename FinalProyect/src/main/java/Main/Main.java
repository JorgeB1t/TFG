package Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import Backend.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class Main extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private JScrollPane sp;
    private JScrollPane sptree;
    private static JTable tabla;
    private static DefaultTableModel dtm;
    static String path;
    private JButton testboton;
    private static JFileChooser archivo;
    
    public static void writetable() {
    	try {
			String columNames[]=defs.readExcelColumnNames("C:\\Users\\Jorge\\eclipse-workspace\\Proyecto\\Data\\ejemplo2.xlsx");
			List<Map<String, Object>> ExcelData = defs.readExcel("C:\\Users\\Jorge\\eclipse-workspace\\Proyecto\\Data\\ejemplo2.xlsx");

			  for (String columnName : columNames) {
	                dtm.addColumn(columnName);
	            }
			dtm.setRowCount(0);
			 for (Map<String, Object> rowDataMap : ExcelData) {
	                Object[] rowData = new Object[columNames.length];
	                for (int i = 0; i < columNames.length; i++) {
	                    rowData[i] = rowDataMap.get(columNames[i]);
	                }
	                dtm.addRow(rowData);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } 
    
    
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
        setBounds(100, 100, 1024, 768);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null); 
        
        JTree tree = new JTree();
        tree.setBounds(0, 0, 140, 696);       
        sptree = new JScrollPane(tree); // Necesita un ScrollPane
    	sptree.setBounds(0, 0, 140, 696);
        contentPane.add(sptree);
        
        JPanel panel = new JPanel();
        panel.setBounds(177, 11, 808, 484);
        contentPane.add(panel);
        panel.setLayout(null);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(0, 0, 808, 484);
        panel.add(tabbedPane);
        
        ChartPanel chartPanel = new ChartPanel((JFreeChart) null);
        tabbedPane.addTab("New tab", null, chartPanel, null);
        
        dtm = new DefaultTableModel() {
        	private static final long serialVersionUID = 1L;
        	@Override // Método para hacer las celdas no editables con FALSE
        	public boolean isCellEditable(int row, int column) {
        	return false;
        	}
        	@Override // Método indica que los datos han cambiado
        	public void fireTableDataChanged() {
        	super.fireTableDataChanged();
        	}
        	@SuppressWarnings("unchecked")
        	@Override // Método que transforma los booleanos en JCheckBox
        	public Class<Object> getColumnClass(int c) {
        	return (Class<Object>) getValueAt(0, c).getClass();
        	}
        	@Override // Método que devuelve el nº filas
        	public int getRowCount() {
        	return super.getRowCount();
        	}
        	@Override // Método que elimina fila
        	public void removeRow(int row) {
        	super.removeRow(row);
        	}
        	@Override // Método indica estructura tabla ha cambiado
        	public void fireTableStructureChanged() {
        	super.fireTableStructureChanged();
        	}
        	};
        	tabla = new JTable(dtm);
        	tabla.setShowVerticalLines(true); // Si muestra líneas verticales
        	tabla.setShowHorizontalLines(false); // No muestra líneas horizontales
        	tabla.setRowSelectionAllowed(true); // Permitida selección filas
        	tabla.setColumnSelectionAllowed(false); // No permite selección columnas
        	tabla.setSelectionForeground(Color.blue); // Color letras seleccionadas
        	tabla.setSelectionBackground(Color.pink); // Color fondo seleccionadas
        	tabla.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        	tabla.setVisible(true);
        	tabla.getTableHeader().setReorderingAllowed(false); //No permite arrastrar columnas
        	// datos
        	
        	sp = new JScrollPane(tabla); // Necesita un ScrollPane
        	sp.setBounds(20, 20, 550, 90);
        	 tabbedPane.addTab("Tabla", null, sp, null); // Agrega el sp
        	 
        	 JComboBox comboBox = new JComboBox();
        	 comboBox.setBounds(177, 506, 204, 22);
        	 contentPane.add(comboBox);
        	 
        	 testboton = new JButton("test");
        	 testboton.addActionListener(new ActionListener() {
        	 	public void actionPerformed(ActionEvent e) {
        	 		writetable();
        	 	}
        	 });
        	 testboton.setBounds(177, 552, 89, 23);
        	 contentPane.add(testboton);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Archivo");
        menuBar.add(fileMenu);

        JMenuItem newItem = new JMenuItem("Nuevo");
        newItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		 
                archivo = new JFileChooser();
                archivo.setCurrentDirectory(new File(".")); //Elegimos directorio a abrir
                archivo.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                //Tipo archivos
                FileNameExtensionFilter filtro1 = new FileNameExtensionFilter( // Filtros
                "Documentos xlsx", "xlsx");
                archivo.setFileFilter(filtro1);
                archivo.setFileFilter(null); //Ninguno de los anteriores es el predeterminado
                int r = archivo.showOpenDialog(null); // Opciones del cuadro de diálogo
                if (r == JFileChooser.APPROVE_OPTION) { // Si se Abre
                String path = archivo.getSelectedFile().getPath();
                
                
              dtm.setColumnCount(0);
              dtm.setRowCount(0);
                	 
                	writetable();
                
                } else if (r == JFileChooser.CANCEL_OPTION) { // Si se cancela
                JOptionPane.showMessageDialog(null, "Ha cancelado su selección");
                } else {
                JOptionPane.showMessageDialog(null,"Error en la selección");
                }
        	}
        });
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
