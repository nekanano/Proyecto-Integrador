package DAO;

import Controlador.Conexion;
import Modelo.Usuario;
import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsuarioDAO {
    private static final Logger log = LoggerFactory.getLogger(UsuarioDAO.class);

    public Usuario obtenerPorCorreo(String correo) throws SQLException {
        String sql = "SELECT idUsuario, nombre, apellido, correo, dni, contraseña FROM usuario WHERE correo = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("idUsuario"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("correo"),
                    rs.getInt("dni")
                );
            }
        }
        return null;
    }

    public String obtenerHashPorCorreo(String correo) throws SQLException {
        String sql = "SELECT contraseña FROM usuario WHERE correo = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("contraseña");
            }
        }
        return null;
    }
}