package test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MariaDBReader {

    // Método para obtener la conexión a la base de datos MariaDB
    private static Connection getConnection() throws SQLException {
    	String url = "jdbc:mariadb://localhost:3307/tfg";
    	String user = "root";
    	String password = "admin";
        return DriverManager.getConnection(url, user, password);
    }

    // Método para determinar si los datos en una columna especificada son numéricos
    public static boolean isColumnNumeric(String tableName, String columnName) {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT " + columnName + " FROM " + tableName + " LIMIT 1";
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                Object value = resultSet.getObject(columnName);
                return isNumeric(value); // Devuelve true si el valor es numérico, de lo contrario false
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // En caso de error o si no hay resultados, devuelve false
    }

    // Método para verificar si un valor es numérico
    private static boolean isNumeric(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Number) {
            return true;
        }
        try {
            Double.parseDouble(value.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        String tableName = "profesiones";
        String column = "Edad";

        boolean isNumeric = isColumnNumeric(tableName, column);
        System.out.println("Column " + column + " is numeric: " + isNumeric);
    }
}