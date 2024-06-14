package Backend;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.data.category.DefaultCategoryDataset;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class defs {

	public static void createAndShowChart() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		try {
			Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/nombre_de_tu_base_de_datos",
					"usuario", "contraseña");
			String query = "SELECT fecha, monto FROM ventas";
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				String fecha = resultSet.getString("fecha");
				double monto = resultSet.getDouble("monto");
				dataset.addValue(monto, "Ventas", fecha);
			}

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**********************************************************************************************************************************/

	public static void createTable(String dbUrl, String dbUser, String dbPassword, String tableName,
			Map<String, String> columnstype) throws SQLException {
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
		try (FileInputStream fis = new FileInputStream(new File(filePath)); Workbook wb = new XSSFWorkbook(fis)) {

			return wb.getSheetName(0); // Obtener el nombre de la primera hoja (índice 0)
		} catch (FileNotFoundException e) {
			// Si el archivo no se encuentra, lanzar la excepción para que se maneje en otro
			// lugar
			throw new FileNotFoundException("Archivo no encontrado: " + e.getMessage());
		} catch (IOException e) {
			// Si hay un error de E/S, lanzar la excepción para que se maneje en otro lugar
			throw new IOException("Error de E/S: " + e.getMessage());
		}
	}

	/**********************************************************************************************************************************/

	public static Map<String, String> getColumnTypes(String url, String user, String password, String tableName)
			throws SQLException {
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

	public static String[] readExcelColumnNames(String filePath) throws IOException {
		try (FileInputStream fis = new FileInputStream(new File(filePath)); Workbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheetAt(0); // Obtener la primera hoja del libro
			Row firstRow = sheet.getRow(0); // Obtener la primera fila (cabecera)

			// Crear un array para los nombres de las columnas
			String[] columnNames = new String[firstRow.getPhysicalNumberOfCells()];
			int i = 0;
			// Recorrer las celdas de la primera fila para obtener los nombres de las
			// columnas
			for (Cell cell : firstRow) {
				columnNames[i++] = cell.getStringCellValue();

			}

			return columnNames; // Devolver los nombres de las columnas
		}
	}

	/**********************************************************************************************************************************/

	public static List<Map<String, Object>> readExcel(String filePath) throws IOException {
		List<Map<String, Object>> data = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {
			Sheet sheet = workbook.getSheetAt(0);
			Row headerRow = sheet.getRow(0);
			int numColumns = headerRow.getPhysicalNumberOfCells();
			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue; // Saltar la fila de encabezado
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
		if (data.isEmpty())
			return columnTypes;
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

	public static void insertDataIntoDatabase(String url, String user, String password, String tableName,
			String[] columnNames, List<Map<String, Object>> data) throws SQLException {
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

//    public static JTree createDatabaseTree(String url, String user, String password, String databaseName) {
//        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Tablas");
//        try (Connection connection = DriverManager.getConnection(url, user, password)) {
//            DatabaseMetaData metaData = connection.getMetaData();
//            ResultSet tables = metaData.getTables(databaseName, null, null, null);
//            while (tables.next()) {
//                String tableName = tables.getString("TABLE_NAME");
//                String tableType = tables.getString("TABLE_TYPE");
//                if ("TABLE".equalsIgnoreCase(tableType)) {
//                    DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(tableName);
//                    root.add(tableNode);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return new JTree(root);
//    }
//    
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

	public static Object[][] readTableData(String url, String user, String password, String tableName)
			throws SQLException {
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

	public static String[] getColumnNames(String url, String user, String password, String tableName)
			throws SQLException {
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

	public static Object[][] getSpecificColumns(DefaultTableModel model, String[] columnNames) {
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

	 public static String sendGetRequest() throws IOException, InterruptedException {
	        // Crear una instancia de HttpClient
	        HttpClient client = HttpClient.newHttpClient();

	        // Crear una solicitud HttpRequest
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create("http://127.0.0.1:8000/test"))
	                .build();

	        // Enviar la solicitud y recibir la respuesta
	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	        // Retornar el cuerpo de la respuesta
	        return response.body();
	    }
	
	public static String sendPostRequest(String tablename, String dfcolumn, List<String> columndrop,
			List<String> dummies, Map<String, Object> newdata) throws Exception {
		// Verificar que las cadenas lleguen correctamente

		// URL de la API FastAPI
		String apiUrl = "http://localhost:8000/api/echo";

		URL url = new URL(apiUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// Configurar el encabezado y el método de solicitud
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");

		// Convertir las listas a nuevas instancias de ArrayList para evitar problemas
		// con el tipo de lista
		List<String> newcolumndrop = new ArrayList<>(columndrop);
		List<String> newdummies = new ArrayList<>(dummies);

		// Datos que enviarás a la API
		Map<String, Object> postData = new HashMap<>();
		postData.put("textData1", tablename);
		postData.put("textData2", dfcolumn);
		postData.put("listData1", newcolumndrop);
		postData.put("listData2", newdummies);
		postData.put("mapData", newdata);

		// Convertir el objeto a formato JSON
		Gson gson = new Gson();
		String jsonData = gson.toJson(postData);

		// Enviar datos a la API
		con.setDoOutput(true);
		try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
			wr.write(jsonData.getBytes(StandardCharsets.UTF_8));
			wr.flush();
		}

		// Leer la respuesta de la API
		try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			System.out.println(response.toString());
			return response.toString();
		}
	}

	public static void dropTable(String url, String user, String password, String tableName) throws SQLException {
		// Verificar que los parámetros no sean nulos ni vacíos
		if (url == null || url.isEmpty() || user == null || user.isEmpty() || password == null || password.isEmpty()
				|| tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException(
					"La URL de la base de datos, el usuario, la contraseña y el nombre de la tabla son requeridos.");
		}

		// Conectar a la base de datos
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			// Crear la declaración SQL para eliminar la tabla
			String sql = "DROP TABLE " + tableName;

			// Crear un Statement y ejecutar la declaración SQL
			try (Statement statement = connection.createStatement()) {
				statement.executeUpdate(sql);
				System.out.println("Tabla " + tableName + " eliminada exitosamente.");
			}
		} catch (SQLException e) {
			// Manejar errores de SQL
			System.err.println("Error al eliminar la tabla: " + e.getMessage());
			throw e;
		}
	}

	public static void actualizarTablaDesdeJTable(String nombreTabla, JTable table, String url, String user,
			String password) {

		Connection conn = null;
		PreparedStatement deleteStatement = null;
		PreparedStatement insertStatement = null;

		try {
			// Establecer la conexión JDBC
			conn = DriverManager.getConnection(url, user, password);
			conn.setAutoCommit(false); // Deshabilitar la confirmación automática

			// Eliminar todos los registros de la tabla
			String deleteSql = "DELETE FROM " + nombreTabla;
			deleteStatement = conn.prepareStatement(deleteSql);
			deleteStatement.executeUpdate();

			// Insertar los nuevos datos en la tabla
			String insertSql = generarInsertStatement(nombreTabla, table);
			insertStatement = conn.prepareStatement(insertSql);
			insertStatement.executeUpdate();

			// Confirmar la transacción
			conn.commit();
			System.out.println("Tabla actualizada correctamente.");

		} catch (SQLException e) {
			// Manejar excepciones de SQL
			e.printStackTrace();
			try {
				// Revertir la transacción en caso de error
				if (conn != null) {
					conn.rollback();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} finally {
			// Cerrar los recursos
			try {
				if (deleteStatement != null) {
					deleteStatement.close();
				}
				if (insertStatement != null) {
					insertStatement.close();
				}
				if (conn != null) {
					conn.setAutoCommit(true); // Restaurar la configuración de autoCommit por defecto
					conn.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	// Método para generar la consulta de inserción SQL desde una JTable
	private static String generarInsertStatement(String tableName, JTable table) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int rowCount = model.getRowCount();
		int columnCount = model.getColumnCount();
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(tableName).append(" VALUES ");

		// Construir la consulta de inserción
		for (int i = 0; i < rowCount; i++) {
			sql.append("(");
			for (int j = 0; j < columnCount; j++) {
				if (j > 0) {
					sql.append(",");
				}
				Object value = model.getValueAt(i, j);
				if (value instanceof String) {
					sql.append("'").append(value).append("'");
				} else {
					sql.append(value);
				}
			}
			sql.append(")");
			if (i < rowCount - 1) {
				sql.append(",");
			}
		}

		return sql.toString();
	}


	public static void deleteSelectedRow(JTable table) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int selectedRowIndex = table.getSelectedRow();

		if (selectedRowIndex != -1) { // Verifica si hay una fila seleccionada
			model.removeRow(selectedRowIndex);
		} else {
			JOptionPane.showMessageDialog(null, "Por favor, selecciona una fila para eliminar.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
