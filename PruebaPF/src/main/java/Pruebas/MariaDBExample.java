package Pruebas;
public static void main(String[] args) {
    String url = "jdbc:mysql://localhost:3306/nombre_base_de_datos";
    String user = "usuario";
    String password = "contraseña";
    String tableName = "nombre_tabla";

    try {
        // Obtener datos de la JTable usando readTableData
        Object[][] data = readTableData(url, user, password, tableName);

        // Actualizar la tabla en MariaDB con los datos de la JTable
        updateTable(url, user, password, tableName, data);

        System.out.println("Datos actualizados exitosamente en la tabla " + tableName);
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// Método para actualizar una tabla con nuevos datos
public static void updateTable(String url, String user, String password, String tableName, Object[][] newData) throws SQLException {
    try (Connection connection = DriverManager.getConnection(url, user, password)) {
        // Eliminar los datos existentes en la tabla
        String deleteSql = "DELETE FROM " + tableName;
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
            deleteStatement.executeUpdate();
        }

        // Insertar los nuevos datos en la tabla
        String insertSql = generateInsertStatement(tableName, newData);
        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
            insertStatement.executeUpdate();
        }
    }
}

// Método para generar la sentencia SQL de inserción
private static String generateInsertStatement(String tableName, Object[][] data) {
    StringBuilder sql = new StringBuilder();
    sql.append("INSERT INTO ").append(tableName).append(" VALUES ");

    for (int i = 0; i < data.length; i++) {
        sql.append("(");
        for (int j = 0; j < data[i].length; j++) {
            if (j > 0) {
                sql.append(",");
            }
            if (data[i][j] instanceof String) {
                sql.append("'").append(data[i][j]).append("'");
            } else {
                sql.append(data[i][j]);
            }
        }
        sql.append(")");
        if (i < data.length - 1) {
            sql.append(",");
        }
    }

    return sql.toString();
}
