package Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.tree.DefaultMutableTreeNode;

import Backend.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
	private static JFileChooser archivo;
	static String url = "jdbc:mariadb://localhost:3307/tfg";
	static String user = "root";
	static String password = "admin";
	static String tableName;
	private JList<String> list;
	private JComboBox<String> comboBox_1;
	private static JTabbedPane tabbedPane;
	private JPanel panel;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JPanel panel_1;
	private JList<String> list_1;
	private JPanel panel_2;
	private static JTextPane textPane;
	private JComboBox<String> comboBox_1_1;
	private JButton btnpredecir;
	private JTree tree;
	private JButton btnactualizar;
	private JButton btnañadir;
	private JButton btnborrar;
	private JSeparator separator_1;
	private JSeparator separator;
	private JLabel lblSeleccioneLaColumna;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JButton btninfo_1;
	private JButton btnNewButton;

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

	@SuppressWarnings("unchecked")
	public Main() throws SQLException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 768);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(135, 206, 250));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panel_1 = new JPanel();
		panel_1.setBackground(new Color(135, 206, 235));
		panel_1.setBounds(309, 507, 297, 189);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		splist = new JScrollPane(); // Necesita un ScrollPane
		splist.setBounds(10, 31, 108, 158);
		panel_1.add(splist);
		
				list = new JList<String>();
				splist.setViewportView(list);
				list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		comboBox_1 = new JComboBox<String>();
		comboBox_1.setBounds(141, 29, 135, 22);
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
		sptree.setBackground(new Color(135, 206, 235));
		sptree.setBounds(0, 0, 140, 696);
		contentPane.add(sptree);

		panel = new JPanel();
		panel.setBounds(177, 11, 808, 484);
		contentPane.add(panel);
		panel.setLayout(null);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(new Color(135, 206, 235));
		tabbedPane.setBounds(0, 0, 808, 484);
		panel.add(tabbedPane);



		JButton btntable = new JButton("Crear tabla");
		btntable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				if(tableName==null){
					JOptionPane.showMessageDialog(null, "Seleccione una tabla", "Error",
							JOptionPane.ERROR_MESSAGE);
				}else {
				
				
				List<String> selectedValuesList = list.getSelectedValuesList();
				String[] selectedValuesArray = selectedValuesList.toArray(new String[0]);

				// Obtener la columna seleccionada del JComboBox
				int columna = comboBox_1.getSelectedIndex();
				String ncolumna =(String) comboBox_1.getSelectedItem();

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
				createChart(datosFiltradosDouble, selectedValuesArray, datos,tableName,ncolumna);
			}
			}
		});
		btntable.setBounds(141, 62, 135, 33);
		panel_1.add(btntable);
		
		lblNewLabel_2 = new JLabel("Columnas a mostrar");
		lblNewLabel_2.setBounds(23, 6, 95, 14);
		panel_1.add(lblNewLabel_2);
		
		lblNewLabel_3 = new JLabel("Columna a compar");
		lblNewLabel_3.setBounds(156, 6, 92, 14);
		panel_1.add(lblNewLabel_3);
		
		btninfo_1 = new JButton("info");
		btninfo_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
			}
		});
		btninfo_1.setBounds(187, 155, 89, 23);
		panel_1.add(btninfo_1);

		dtm = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override // Método para hacer las celdas no editables con FALSE
			public boolean isCellEditable(int row, int column) {
				return true;
			}

			@Override // Método indica que los datos han cambiado
			public void fireTableDataChanged() {
				super.fireTableDataChanged();
			}

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
				panel_2.setBackground(new Color(135, 206, 235));
				tabbedPane.addTab("Prediccion", null, panel_2, null);
				panel_2.setLayout(null);
				
						textPane = new JTextPane();
						textPane.setEnabled(false);
						textPane.setEditable(false);
						textPane.setBackground(Color.BLACK);
						textPane.setForeground(Color.WHITE);
						textPane.setBounds(412, 10, 381, 435);
						
								JScrollPane scrollPane = new JScrollPane(textPane);
								scrollPane.setBounds(412, 10, 381, 436);
								panel_2.add(scrollPane);
								
										JScrollPane splist_1 = new JScrollPane();
										splist_1.setBounds(21, 46, 108, 158);
										panel_2.add(splist_1);
										
												list_1 = new JList<String>();
												splist_1.setViewportView(list_1);
												
														comboBox_1_1 = new JComboBox<String>();
														comboBox_1_1.setBounds(212, 44, 135, 22);
														panel_2.add(comboBox_1_1);
														
																JButton btninfo = new JButton("info");
																btninfo.addActionListener(new ActionListener() {
																	public void actionPerformed(ActionEvent e) {

																		JOptionPane.showMessageDialog(null, "<html>Para realizar una buena prediccion sin errores, es necesario <br>seleccionar correctamente"
																				+ "las columnas utiles ha predecir.<br>"
																				+ "Este algoritmo solo funciona provisionalmente con valores numericos, <br>para el correcto uso,"
																				+ "seleccione solo las columnas númericas</html>", "Informacion",
																				JOptionPane.INFORMATION_MESSAGE);

																	}
																});
																btninfo.setBounds(228, 101, 89, 23);
																panel_2.add(btninfo);
																
																		btnpredecir = new JButton("Predecir");
																		btnpredecir.addActionListener(new ActionListener() {
																			public void actionPerformed(ActionEvent e) {


																				if(tableName==null) {
																					appendText("Seleccione una tabla\n");
																				}else {
																				
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

																				System.out.println("==========================================================================================================");
																		        System.out.println("Verificacion de dato numerico");
																				System.out.println("==========================================================================================================");

																		        


																		        	
																		        
																		        //comprbar si la tabla tiene numeros
																		        
																		        
																		        System.out.println(tableName);
																		        System.out.println(valorPredecir);
																		        System.out.println(allValuesList);
																		        System.out.println(selectedValuesList);
																		        System.out.println(datos);

																		    	 
																		        List<String> dummies = List.of();
																		        try {
																		        	
																		           // List<String> dummies1 = List.of("Género", "Departamento");
																		        	System.out.println();
																		        	String salida =	defs.sendPostRequest(tableName, valorPredecir, allValuesList, dummies, datos);
																					//String salida = defs.sendPostRequest(tableName, valorPredecir, allValuesList, dummies, datos);
																					 //textPane.setText(salida);
																					 
																					 appendText(salida+"\n");
																					 appendText("Datos totales de la nueva fila:\n");
																					 
																					 
																					 
																					 
																				} catch (Exception e1) {
																					// TODO Auto-generated catch block
																					e1.printStackTrace();
																					appendText("Error al introducir valores. Por favor, intentelo de nuevo\n");
																				}
																		        
																		        
																				}
																		        
																			}
																		});
																		btnpredecir.setBounds(10, 412, 392, 34);
																		panel_2.add(btnpredecir);
																		
																		separator = new JSeparator();
																		separator.setBackground(new Color(230, 230, 250));
																		separator.setBounds(10, 366, 394, 9);
																		separator.setOpaque(true);
																		panel_2.add(separator);
																		
																		JLabel lblNewLabel = new JLabel("Seleccione las columnas \r\n");
																		lblNewLabel.setBounds(10, 10, 145, 14);
																		panel_2.add(lblNewLabel);
																		
																		lblSeleccioneLaColumna = new JLabel("Seleccione la columna a predecir\r\n");
																		lblSeleccioneLaColumna.setBounds(175, 19, 215, 14);
																		panel_2.add(lblSeleccioneLaColumna);
																		
																		btnNewButton = new JButton("Probar conexión");
																		btnNewButton.addActionListener(new ActionListener() {
																			public void actionPerformed(ActionEvent e) {
																				
																				try {
																					String testresult = defs.sendGetRequest();
																					appendText(testresult);

																				} catch (IOException
																						| InterruptedException e1) {
																					appendText("Error al establecer connexion, revise la configuracion y vuelve a intentarlo");
																					//e1.printStackTrace();
																				}
																			}
																		});
																		btnNewButton.setBounds(10, 331, 145, 22);
																		panel_2.add(btnNewButton);
		
		btnactualizar = new JButton("Actualizar tabla");
		btnactualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(tableName==null){
					JOptionPane.showMessageDialog(null, "Seleccione una tabla", "Error",
							JOptionPane.ERROR_MESSAGE);
				}else {
				
				  defs.actualizarTablaDesdeJTable(tableName, tabla,url, user, password);
				}
			}
		});
		btnactualizar.setBounds(804, 525, 127, 28);
		contentPane.add(btnactualizar);
		
		btnañadir = new JButton("Añadir fila");
		btnañadir.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	
		    	if(tableName==null) {
		    		JOptionPane.showMessageDialog(null, "Seleccione una tabla", "Error",
							JOptionPane.ERROR_MESSAGE);
		    	}else {
                Object[] emptyRow;
				try {
					emptyRow = new Object[defs.getColumnNames(url, user, password, tableName).length];
					dtm.addRow(emptyRow);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	}
		    }
		});
		btnañadir.setBounds(804, 567, 127, 28);
		contentPane.add(btnañadir);


		
		btnborrar = new JButton("Eliminar fila");
		btnborrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				defs.deleteSelectedRow(tabla);
			}
		});
		btnborrar.setBounds(804, 610, 127, 28);
		contentPane.add(btnborrar);
		
		separator_1 = new JSeparator();
		separator_1.setBackground(new Color(230, 230, 250));
		separator_1.setBounds(787, 518, 7, 178);
		separator_1.setOpaque(true);
		contentPane.add(separator_1);
		
		lblNewLabel_1 = new JLabel("<HTML>CREACIÓN DE TABLA DE VALORES</HTML>");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 27));
		lblNewLabel_1.setBounds(150, 507, 149, 189);
		contentPane.add(lblNewLabel_1);

		setVisible(true);

		menuBar = new JMenuBar();
		menuBar.setBackground(new Color(135, 206, 235));
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
					
					JOptionPane.showMessageDialog(null, "Reiniciando la aplicacion", "Reinicio requerido",
							JOptionPane.INFORMATION_MESSAGE);
					
					restartApplication();

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
                
                JOptionPane.showMessageDialog(null, "Reiniciando la aplicacion", "Reinicio requerido",
						JOptionPane.INFORMATION_MESSAGE);
				
				restartApplication();
            }
            
            
        });
        fileMenu.add(deleteItem);

		JMenuItem exitItem = new JMenuItem("Salir");
		fileMenu.add(exitItem);

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}

	
    public static void appendText(String text) {
        Document doc = textPane.getDocument();
        try {
            doc.insertString(doc.getLength(), text, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
	
	public static void createChart(Double[][] datosfiltrados, String[] rowLabels, String[] columnLabels,String tablename,String columna) {
		// Crear el dataset para el gráfico
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// Añadir los datos al dataset con etiquetas
		for (int row = 0; row < datosfiltrados.length; row++) {
			for (int col = 0; col < datosfiltrados[row].length; col++) {
				dataset.addValue(datosfiltrados[row][col], rowLabels[row], columnLabels[col]);
			}
		}

		// Crear el gráfico de barras
		JFreeChart barChart = ChartFactory.createBarChart(tablename, // Título del gráfico
				columna, // Etiqueta del eje X
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
	
	public void restartApplication() {
	    // Ocultar la ventana actual
	    setVisible(false);  // Suponiendo que estás en una clase que extiende JFrame o similar
	    
	    // Reiniciar la aplicación
	    try {
	        String javaHome = System.getProperty("java.home");
	        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
	        String className = getClass().getName();

	        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", System.getProperty("java.class.path"), className);
	        Process process = builder.start();
	        process.waitFor();
	        System.exit(0);
	    } catch (IOException | InterruptedException e) {
	        e.printStackTrace();
	    }
	}
}
