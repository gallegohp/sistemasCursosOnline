package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Conexion{
    // ruta y credenciales de la base de datos
    //                                     motor -
    private static final String URL = "jdbc:mysql://localhost:3306/cursosonline";
    public static final String USER = "root";
    public static final String PASSWORD = "";
    //creacion del usuario

    //aca creamos nuestra conexion, el metodo tiene que lanzar una excepcion throws SQLException. que es si llega a
    //pasar un error, que es lo que lanza despues del parametro

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }

}