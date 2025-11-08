package Controlador;

import java.sql.Connection;

public class ProbarConexion {
    public static void main(String[] args) {
        Connection conn = Conexion.getConnection();
        if (conn != null) {
            System.out.println("Conexion exitosa a bd");
        } else {
            System.out.println("No se pudo establecer la conexion");
        }
    }
}