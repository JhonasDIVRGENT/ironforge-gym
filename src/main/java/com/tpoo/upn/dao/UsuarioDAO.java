package com.tpoo.upn.dao;

import com.tpoo.upn.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public boolean insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombres, apellidos, username, password, rol, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNombres());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getUsername());
            ps.setString(4, usuario.getPassword());
            ps.setString(5, usuario.getRol());
            ps.setBoolean(6, usuario.isActivo());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                return false;
            }

            // Se copia al objeto el id que genero MySQL (AUTO_INCREMENT).
            try (ResultSet clavesGeneradas = ps.getGeneratedKeys()) {
                if (clavesGeneradas.next()) {
                    usuario.setIdUsuario(clavesGeneradas.getInt(1));
                }
            }
            return true;
        }
    }

    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT id_usuario, nombres, apellidos, username, password, rol, activo "
                + "FROM usuarios WHERE username = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
                return null;
            }
        }
    }

    public List<Usuario> listar() throws SQLException {
        String sql = "SELECT id_usuario, nombres, apellidos, username, password, rol, activo "
                + "FROM usuarios ORDER BY apellidos, nombres";

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }
        return usuarios;
    }

    public boolean actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombres = ?, apellidos = ?, username = ?, "
                + "password = ?, rol = ?, activo = ? WHERE id_usuario = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombres());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getUsername());
            ps.setString(4, usuario.getPassword());
            ps.setString(5, usuario.getRol());
            ps.setBoolean(6, usuario.isActivo());
            ps.setInt(7, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeUsername(String username) throws SQLException {
        String sql = "SELECT id_usuario FROM usuarios WHERE username = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("rol"),
                rs.getBoolean("activo"));
    }
}
