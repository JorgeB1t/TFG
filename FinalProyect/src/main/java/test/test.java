package test;

import java.io.IOException;
import java.sql.SQLException;

import Backend.*;

public class test {

	
    static String filePath = "C:/Users/Jorge/Desktop/ejemplo.xlsx";
    static String url = "jdbc:mariadb://localhost:3307/tfg";
    static String user = "root";
    static String password = "admin";
	public static void main(String[] args) {
	try {
		ExcellToMariaDB.insertExcelDataIntoDatabase(filePath, url, user, password, "nombre_de_tu_tabla");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
	}
	
