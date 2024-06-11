package test;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TableExample extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JButton printButton;

    public TableExample() {
        setTitle("Ejemplo de Tabla");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        // Crear el modelo de la tabla
        model = new DefaultTableModel(
                new Object[][]{
                        {"John", "Doe", 30, "New York", "USA"},
                        {"Jane", "Smith", 25, "Los Angeles", "USA"},
                        {"Bob", "Johnson", 40, "Chicago", "USA"}
                },
                new String[]{"Nombre", "Apellido", "Edad", "Ciudad", "País"}
        );

        // Crear la tabla con el modelo
        table = new JTable(model);

        // Crear el botón para imprimir las columnas
        printButton = new JButton("Imprimir Columnas 'Apellido' y 'País'");
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener las columnas 'Apellido' y 'País' de la tabla
                String[] columnNames = {"Apellido", "País"};
                Object[][] specificColumns = getSpecificColumns(model, columnNames);

                // Imprimir las columnas en la consola
                for (int i = 0; i < specificColumns.length; i++) {
                    System.out.print("Columna '" + columnNames[i] + "': ");
                    for (int j = 0; j < specificColumns[i].length; j++) {
                        System.out.print(specificColumns[i][j] + " ");
                    }
                    System.out.println();
                }
            }
        });

        // Agregar la tabla y el botón al panel principal
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(printButton, BorderLayout.SOUTH);

        // Agregar el panel al frame
        add(panel);
    }

    // Método para obtener columnas específicas de la tabla por nombre
    private Object[][] getSpecificColumns(DefaultTableModel model, String[] columnNames) {
        int rowCount = model.getRowCount();
        int columnCount = columnNames.length;

        Object[][] specificColumns = new Object[columnCount][rowCount];

        for (int i = 0; i < columnCount; i++) {
            String columnName = columnNames[i];
            int columnIndex = model.findColumn(columnName);
            if (columnIndex != -1) {
                for (int j = 0; j < rowCount; j++) {
                    specificColumns[i][j] = model.getValueAt(j, columnIndex);
                }
            } else {
                System.out.println("No se encontró la columna '" + columnName + "'.");
            }
        }

        return specificColumns;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TableExample example = new TableExample();
                example.setVisible(true);
            }
        });
    }
}
