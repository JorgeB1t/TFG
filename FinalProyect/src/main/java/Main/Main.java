package Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import Backend.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane sp;
	private JScrollPane sptree;
	private JScrollPane splist;
	private static JTable tabla;
	private static DefaultTableModel dtm;
	static String path;
	private JButton testboton;
	private static JFileChooser archivo;
	static String url = "jdbc:mariadb://localhost:3307/tfg";
	static String user = "root";
	static String password = "admin";
	static String tableName;
	private JList<String> list;
	private JComboBox comboBox_1;
	private static JTabbedPane tabbedPane;
	private JPanel panel;
	private JComboBox comboBox;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JPanel panel_1;
	private JList list_1;
	private JPanel panel_2;
	private JTextPane textPane;
	private JComboBox<String> comboBox_1_1;
	private JButton btnpredecir;
	private JTree tree;

	public static void writetable(String path) {
		try {
			String tablename = defs.getTableName(path);
			String columNames[] = defs.readExcelColumnNames(path);

			System.out.println(tablename);
			for (String string : columNames) {
				System.out.println(string);
				if (string == null) {
					System.out.println("es nulo");
				}
			}

			List<Map<String, Object>> ExcelData = defs.readExcel(path);
			Map<String, String> columntype = defs.getColumnTypes(ExcelData);
			defs.createTable(url, user, password, tablename, columntype);
			defs.insertDataIntoDatabase(url, user, password, tablename, columNames, ExcelData);

		} catch (IOException | SQLException e) {
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

	public Main() throws SQLException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 768);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panel_1 = new JPanel();
		panel_1.setBounds(177, 518, 297, 178);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		splist = new JScrollPane(); // Necesita un ScrollPane
		splist.setBounds(10, 10, 108, 158);
		panel_1.add(splist);

		list = new JList<String>();
		splist.setViewportView(list);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		comboBox_1 = new JComboBox<String>();
		comboBox_1.setBounds(141, 11, 135, 22);
		panel_1.add(comboBox_1);

		tree = defs.createDatabaseTree(url, user, password, "tfg");
		
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (selectedNode != null && !selectedNode.isRoot()) {
					tableName = selectedNode.toString();

					
					
					try {
						dtm.setColumnCount(0);
						dtm.setRowCount(0);
						Object[][] tableData = defs.readTableData(url, user, password, tableName);
						String[] tableColumns = defs.getColumnNames(url, user, password, tableName);
						// Add columns to the model
						for (String column : tableColumns) {
							dtm.addColumn(column);
						}

						// Add rows to the model
						for (Object[] row : tableData) {
							dtm.addRow(row);
						}

						list.setListData(tableColumns);
						comboBox_1.setModel(new DefaultComboBoxModel<String>(tableColumns));

						list_1.setListData(tableColumns);
						comboBox_1_1.setModel(new DefaultComboBoxModel<String>(tableColumns));

						
						
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});
		tree.setBounds(0, 0, 140, 696);
		sptree = new JScrollPane(tree); // Necesita un ScrollPane
		sptree.setBounds(0, 0, 140, 696);
		contentPane.add(sptree);

		panel = new JPanel();
		panel.setBounds(177, 11, 808, 484);
		contentPane.add(panel);
		panel.setLayout(null);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 808, 484);
		panel.add(tabbedPane);


//
//		// Crear el gráfico utilizando los datos del conjunto de datos
//		grafico = ChartFactory.createBarChart("Mi Gráfico", "Día", "Valor", dcds, PlotOrientation.VERTICAL, true, false,
//				false);
//		grafico.setBackgroundPaint(Color.WHITE); // Cambia el color de fondo del gráfico a blanco
//
//		ChartPanel chartPanel = new ChartPanel(grafico);
//		chartPanel.setBackground(Color.WHITE);
//		tabbedPane.addTab("Tabla", null, chartPanel, null);

		JButton btntable = new JButton("Mostrar tabla");
		btntable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> selectedValuesList = list.getSelectedValuesList();
				String[] selectedValuesArray = selectedValuesList.toArray(new String[0]);

				// Obtener la columna seleccionada del JComboBox
				int columna = comboBox_1.getSelectedIndex();

				// Crear un array para almacenar los datos como cadenas de texto
				String[] datos = new String[dtm.getRowCount()];

				// Recorrer las filas de la tabla y obtener los valores de la columna
				// seleccionada
				for (int i = 0; i < dtm.getRowCount(); i++) {
					// Obtener el valor en la fila i y columna seleccionada
					Object value = dtm.getValueAt(i, columna);
					// Convertir el valor a String (si no es null)
					datos[i] = (value != null) ? value.toString() : null;
				}

				// Obtener los datos filtrados como Object[][]
				Object[][] datosFiltrados = defs.getSpecificColumns(dtm, selectedValuesArray);

				// Convertir los datos filtrados a Double[][]
				Double[][] datosFiltradosDouble = new Double[datosFiltrados.length][];
				for (int i = 0; i < datosFiltrados.length; i++) {
					datosFiltradosDouble[i] = new Double[datosFiltrados[i].length];
					for (int j = 0; j < datosFiltrados[i].length; j++) {
						try {
							// Intentar convertir cada elemento a Double
							datosFiltradosDouble[i][j] = Double.parseDouble(String.valueOf(datosFiltrados[i][j]));
						} catch (NumberFormatException e1) {
							// En caso de error, asignar null o un valor predeterminado
							datosFiltradosDouble[i][j] = null; // O asigna un valor predeterminado, dependiendo de tus
																// necesidades
						}
					}
				}

				// Ahora puedes usar datosFiltradosDouble en createChart
				createChart(datosFiltradosDouble, selectedValuesArray, datos);
			}

		});
		btntable.setBounds(141, 135, 135, 33);
		panel_1.add(btntable);

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
		tabla.getTableHeader().setReorderingAllowed(false); // No permite arrastrar columnas
		// datos

		sp = new JScrollPane(tabla); // Necesita un ScrollPane
		sp.setBounds(20, 20, 550, 90);
		tabbedPane.addTab("Tabla", null, sp, null); // Agrega el sp

		panel_2 = new JPanel();
		tabbedPane.addTab("Prediccion", null, panel_2, null);
		panel_2.setLayout(null);

		textPane = new JTextPane();
		textPane.setBackground(Color.BLACK);
		textPane.setForeground(Color.WHITE);
		textPane.setBounds(412, 10, 381, 435);

		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setBounds(412, 10, 381, 436);
		panel_2.add(scrollPane);

		JScrollPane splist_1 = new JScrollPane();
		splist_1.setBounds(10, 11, 108, 158);
		panel_2.add(splist_1);

		list_1 = new JList();
		splist_1.setViewportView(list_1);

		comboBox_1_1 = new JComboBox<String>();
		comboBox_1_1.setBounds(10, 232, 135, 22);
		panel_2.add(comboBox_1_1);

		JButton btninfo = new JButton("info");
		btninfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JOptionPane.showMessageDialog(null, "Este es un mensaje informativo", "Mensaje",
						JOptionPane.INFORMATION_MESSAGE);

			}
		});
		btninfo.setBounds(10, 265, 89, 23);
		panel_2.add(btninfo);

		btnpredecir = new JButton("Predecir");
		btnpredecir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				/*
				 * datos necesarios:
				 * 
				 * dicionario con la nueva columna lista o array con las columnas utiles para
				 * predecir columna a predecir
				 * 
				 */
				 // Obtener el índice seleccionado en el ComboBox
			    String valorPredecir = comboBox_1_1.getSelectedItem().toString();

			    // Obtener el modelo de la JList
			    ListModel<String> model = list_1.getModel();

			    // Obtener todos los valores disponibles en la JList
			    List<String> allValuesList = new ArrayList<>();
			    for (int i = 0; i < model.getSize(); i++) {
			        allValuesList.add(model.getElementAt(i));
			        System.out.println(allValuesList.get(i));
			    }
			    String[] allValuesArray = allValuesList.toArray(new String[allValuesList.size()]);
			    
			    
			    // Obtener los valores seleccionados en la JList
			    List<String> selectedValuesList = new ArrayList<>();
			    int[] selectedIndices = list_1.getSelectedIndices();
			    for (int index : selectedIndices) {
			        selectedValuesList.add(model.getElementAt(index));
			    }

			    // Eliminar los valores seleccionados en la JList de la lista de todos los valores disponibles
			    allValuesList.removeAll(selectedValuesList);

			    // Verificar si el valor seleccionado en el JComboBox no está seleccionado en la JList
			    if (!selectedValuesList.contains(valorPredecir)) {
			        // Agregar el valor seleccionado en el JComboBox a la lista de valores no seleccionados
			        allValuesList.add(valorPredecir);
			    }

			    // Imprimir los valores seleccionados en la lista
			    System.out.println("Valores seleccionados en la lista:");
			    for (String value : selectedValuesList) {
			        System.out.println(value);
			    }

			    // Imprimir los valores no seleccionados en la JList (incluyendo el seleccionado en el JComboBox si es necesario)
			    System.out.println("Valores no seleccionados en la JList:");
			    for (String value : allValuesList) {
			        System.out.println(value);
			    }
			    



			    Map<String, Object> datos = new HashMap<>();
			    
			    // Imprimir el índice seleccionado en el ComboBox
			    System.out.println("Índice seleccionado en el ComboBox: " + valorPredecir);
				System.out.println("=====================================================================");
				for (String value : allValuesArray) {
					if (!value.equals(valorPredecir)) {
					
						System.out.println(value);

						Object input =JOptionPane.showInputDialog("dime un valor para el dato "+value);
						
						datos.put(value, input);
					}
				}
				System.out.println(datos);
				System.out.println("==========================================================================================================");
		        System.out.println("Datos ingresados:");
		        for (Entry<String, Object> entry : datos.entrySet()) {
		            System.out.println(entry.getKey() + ": " + entry.getValue());
		           
		        }
				/*
				 * valor a predecir = selectedComboBoxItem 
				 * valor utiles en la aprediccion = selectedValuesList
				 *  valores no utiles en la prediccion = notSelectedValuesList
				 * diccionario con todos lo valores nuevos
				 * nombre de la tabla
				 * 
				 * 
				 */
				System.out.println("==========================================================================================================");
		        System.out.println("Verificacion de dato numerico");
				System.out.println("==========================================================================================================");

		        List<String> dummies = new ArrayList<>();
		        
		        for (int i = 0; i < selectedValuesList.size(); i++) {
					if(!defs.isColumnNumeric("profesiones",selectedValuesList.get(i))) {
						System.out.println("la columna "+selectedValuesList.get(i)+" no es numerica");
						dummies.add(selectedValuesList.get(i));
					}
				}

		        	
		        
		        //comprbar si la tabla tiene numeros
		        
		        
		        
		    	  String tablename = "salarios";
		          String dfcolumn = "Salario";
		          List<String> columndrop = List.of("Nombre", "Salario");
		          List<String> dummies1 = List.of("Género", "Departamento");
		          Map<String, Object> newdata = Map.of(
		              "Nombre", "Nuevo",
		              "Edad", 36,
		              "Género", "Femenino",
		              "Departamento", "Finanzas"
		          );
		        
		        try {
		        	
		           // List<String> dummies1 = List.of("Género", "Departamento");
		        	System.out.println();
		        	String salida =	defs.sendPostRequest(tablename, dfcolumn, columndrop, dummies1, newdata);
					//String salida = defs.sendPostRequest(tableName, valorPredecir, allValuesList, dummies, datos);
					 textPane.setText(salida);
					 
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        
		        
		        
		        
			}
		});
		btnpredecir.setBounds(10, 412, 392, 34);
		panel_2.add(btnpredecir);
		
		JScrollPane splist_1_1 = new JScrollPane();
		splist_1_1.setBounds(222, 32, 108, 158);
		panel_2.add(splist_1_1);

		setVisible(true);

		comboBox = new JComboBox();
		comboBox.setBounds(781, 506, 204, 22);
		contentPane.add(comboBox);

		testboton = new JButton("test");
		testboton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
  
				
				
				

			}
		});
		testboton.setBounds(874, 552, 89, 23);
		contentPane.add(testboton);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		
	  

		fileMenu = new JMenu("Archivo");
		menuBar.add(fileMenu);

		JMenuItem newItem = new JMenuItem("Nuevo");
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				archivo = new JFileChooser();
				archivo.setCurrentDirectory(new File(".")); // Elegimos directorio a abrir
				archivo.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				// Tipo archivos
				FileNameExtensionFilter filtro1 = new FileNameExtensionFilter( // Filtros
						"Documentos xlsx", "xlsx");
				archivo.setFileFilter(filtro1);
				archivo.setFileFilter(null); // Ninguno de los anteriores es el predeterminado
				int r = archivo.showOpenDialog(null); // Opciones del cuadro de diálogo
				if (r == JFileChooser.APPROVE_OPTION) { // Si se Abre
					String path = archivo.getSelectedFile().getPath();

					dtm.setColumnCount(0);
					dtm.setRowCount(0);

					writetable(path);
					
					tree = defs.createDatabaseTree(url, user, password, "tfg");

				} else if (r == JFileChooser.CANCEL_OPTION) { // Si se cancela
					JOptionPane.showMessageDialog(null, "Ha cancelado su selección");
				} else {
					JOptionPane.showMessageDialog(null, "Error en la selección");
				}
			}
		});
		fileMenu.add(newItem);

		JMenuItem deleteItem = new JMenuItem("Eliminar tabla");
		deleteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tableName = JOptionPane.showInputDialog("Introduce el nombre de la tabla a eliminar:");
                if (tableName != null && !tableName.trim().isEmpty()) {
                    try {
                        defs.dropTable(url, user, password, tableName);
                        JOptionPane.showMessageDialog(null, "Tabla " + tableName + " eliminada exitosamente.");
                       
                        tree = defs.createDatabaseTree(url, user, password, "tfg");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error al eliminar la tabla: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "El nombre de la tabla no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        fileMenu.add(deleteItem);

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

	

	
	public static void createChart(Double[][] datosfiltrados, String[] rowLabels, String[] columnLabels) {
		// Crear el dataset para el gráfico
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// Añadir los datos al dataset con etiquetas
		for (int row = 0; row < datosfiltrados.length; row++) {
			for (int col = 0; col < datosfiltrados[row].length; col++) {
				dataset.addValue(datosfiltrados[row][col], rowLabels[row], columnLabels[col]);
			}
		}

		// Crear el gráfico de barras
		JFreeChart barChart = ChartFactory.createBarChart("Array Bidimensional", // Título del gráfico
				"Categoría", // Etiqueta del eje X
				"Valor", // Etiqueta del eje Y
				dataset, // Datos
				PlotOrientation.VERTICAL, true, // Incluir leyenda
				true, false);

		// Crear el panel del gráfico
		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setPreferredSize(new Dimension(500, 270));

		// Agregar el gráfico de barras al ChartPanel existente
		chartPanel.setChart(barChart);

		// Agregar el ChartPanel al tabbedPane
		tabbedPane.addTab("Gráfico de Barras", chartPanel);
	}
}
