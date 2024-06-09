package Backend;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class defs {

    public static void main(String[] args) {
        String filePath = "C:/Users/Jorge/eclipse-workspace/Proyecto/Data/ejemplo3.xlsx";
        String url = "jdbc:mariadb://localhost:3307/tfg";
        String user = "root";
        String password = "admin";

    
       
//        try {
//			//getColumnTypes(url, user, password, getTableName(filePath));
//            String tableName = getTableName(filePath);
//            Map<String, String> columnTypes = getColumnTypes(readExcel(filePath));
//			 createTable(url, user, password, tableName, columnTypes);
//		} catch (IOException | SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
			try {

	            // Leer los nombres de las columnas y los datos del archivo Excel
	            String[] columnNames = readExcelColumnNames(filePath);
	            List<Map<String, Object>> excelData = readExcel(filePath);

	            // Convertir los datos a una lista de arrays de objetos
	            List<Object[]> data = new ArrayList<>();
	            
	            for (Map<String, Object> row : excelData) {
	            	
	                Object[] rowData = row.values().toArray();
	               
	                data.add(rowData);
	               
	            }

	            // Crear la tabla en la base de datos
	            Map<String, String> columns = getColumnTypes(excelData);
	            createTable(url, user, password, getTableName(filePath), columns);
	            
	            // Insertar los datos en la base de datos
	        } catch (IOException | SQLException e) {
	            e.printStackTrace();
	        }
	    }
        
//        try {
//            // Llamar al método para leer los datos de la tabla
//            Object[][] tabla =  readTableData(url, user, password,  getTableName(filePath));
//            for (int i = 0; i < tabla.length; i++) {
//                for (int j = 0; j < tabla[i].length; j++) {
//                    System.out.println(tabla[i][j]);
//                }
//            }
//        } catch (SQLException | IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**********************************************************************************************************************************/


   
    public static void createTable(String dbUrl, String dbUser, String dbPassword, String tableName, Map<String, String> columnstype) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        for (Map.Entry<String, String> column : columnstype.entrySet()) {
            sb.append(column.getKey()).append(" ").append(column.getValue()).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(")");

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(sb.toString())) {
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla: " + e.getMessage());
            throw e;
        }
    }
    
    /**********************************************************************************************************************************/
    public static String getTableName(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
                Workbook wb = new XSSFWorkbook(fis)) {
        	
        	return wb.getSheetName(0); // Obtener el nombre de la primera hoja (índice 0)
        } catch (FileNotFoundException e) {
            // Si el archivo no se encuentra, lanzar la excepción para que se maneje en otro lugar
            throw new FileNotFoundException("Archivo no encontrado: " + e.getMessage());
        } catch (IOException e) {
            // Si hay un error de E/S, lanzar la excepción para que se maneje en otro lugar
            throw new IOException("Error de E/S: " + e.getMessage());
        }
    }

    /**********************************************************************************************************************************/

    public static Map<String, String> getColumnTypes(String url, String user, String password, String tableName) throws SQLException {
        Map<String, String> columnTypes = new LinkedHashMap<>();
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String dataType = resultSet.getString("TYPE_NAME");
                columnTypes.put(columnName, dataType);
            }
        }
        return columnTypes;
    }



    /**********************************************************************************************************************************/

    public static  String[] readExcelColumnNames(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Obtener la primera hoja del libro
            Row firstRow = sheet.getRow(0); // Obtener la primera fila (cabecera)

            // Crear un array para los nombres de las columnas
            String[] columnNames = new String[firstRow.getPhysicalNumberOfCells()];
            int i = 0;
            // Recorrer las celdas de la primera fila para obtener los nombres de las columnas
            for (Cell cell : firstRow) {
                columnNames[i++] = cell.getStringCellValue();
                
            }

            return columnNames; // Devolver los nombres de las columnas
        }
    }
    
    /**********************************************************************************************************************************/

    
    public static List<Map<String, Object>> readExcel(String filePath) throws IOException {
        List<Map<String, Object>> data = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            int numColumns = headerRow.getPhysicalNumberOfCells();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Saltar la fila de encabezado
                Map<String, Object> rowData = new HashMap<>();
                for (int i = 0; i < numColumns; i++) {
                    Cell cell = row.getCell(i);
                    rowData.put(headerRow.getCell(i).getStringCellValue(), getCellValue(cell));
                }
                data.add(rowData);
            }
        }
        return data;
    }
    
    /**********************************************************************************************************************************/

    
    
    private static Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**********************************************************************************************************************************/
    
    public static Map<String, String> getColumnTypes(List<Map<String, Object>> data) {
        Map<String, String> columnTypes = new LinkedHashMap<>();
        if (data.isEmpty()) return columnTypes;
        Map<String, Object> firstRow = data.get(0);
        for (Map.Entry<String, Object> entry : firstRow.entrySet()) {
            String columnName = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                columnTypes.put(columnName, "VARCHAR(255)");
            } else if (value instanceof Integer) {
                columnTypes.put(columnName, "INT");
            } else if (value instanceof Double) {
                columnTypes.put(columnName, "DOUBLE PRECISION");
            } else if (value instanceof Date) {
                columnTypes.put(columnName, "DATE");
            } else if (value instanceof Boolean) {
                columnTypes.put(columnName, "BOOLEAN");
            } else {
                columnTypes.put(columnName, "VARCHAR(255)");
            }
        }
        return columnTypes;
    }

    /**********************************************************************************************************************************/




    	
    public static void insertDataIntoDatabase(String url, String user, String password, String tableName, String[] columnNames, List<Map<String, Object>> data) throws SQLException {
    	 try (Connection connection = DriverManager.getConnection(url, user, password)) {
    	        for (Map<String, Object> row : data) {
    	            StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
    	            sqlBuilder.append(tableName).append(" (");

    	            List<Object> values = new ArrayList<>();
    	            for (Map.Entry<String, Object> entry : row.entrySet()) {
    	                sqlBuilder.append(entry.getKey()).append(", ");
    	                values.add(entry.getValue());
    	            }
    	            sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());
    	            sqlBuilder.append(") VALUES (");

    	            for (int i = 0; i < values.size(); i++) {
    	                sqlBuilder.append("?, ");
    	            }
    	            sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());
    	            sqlBuilder.append(")");

    	            String sql = sqlBuilder.toString();

    	            try (PreparedStatement statement = connection.prepareStatement(sql)) {
    	                for (int i = 0; i < values.size(); i++) {
    	                    statement.setObject(i + 1, values.get(i));
    	                }
    	                statement.executeUpdate();
    	            }
    	        }
    	    }
        
    }
    private static ChartFactory showChart(){
		return null;
    	
    }
    public static JTree createDatabaseTree(String url, String user, String password, String databaseName) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Tablas");
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(databaseName, null, null, null);
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                String tableType = tables.getString("TABLE_TYPE");
                if ("TABLE".equalsIgnoreCase(tableType)) {
                    DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(tableName);
                    root.add(tableNode);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new JTree(root);
    }
    
    
    public static Object[][] readTableData(String url, String user, String password, String tableName) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM " + tableName;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    // Contar el número de filas en el ResultSet
                    int rowCount = 0;
                    while (resultSet.next()) {
                        rowCount++;
                    }
                    
                    // Volver al inicio del ResultSet
                    resultSet.beforeFirst();
                    
                    // Crear una matriz bidimensional para almacenar los datos
                    Object[][] data = new Object[rowCount][columnCount];
                    
                    // Procesar los resultados de la consulta y almacenarlos en la matriz
                    int row = 0;
                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = resultSet.getObject(i);
                            data[row][i - 1] = value;
                        }
                        row++;
                    }
                    
                    return data;
                }
            }
        }
    }
    public static String[] getColumnNames(String url, String user, String password, String tableName) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM " + tableName;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    // Obtener los nombres de las columnas
                    String[] columnNames = new String[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames[i - 1] = metaData.getColumnName(i);
                    }
                    return columnNames;
                }
            }
        }
    }

}
    

