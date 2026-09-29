package com.tpoo.upn.dao;

import com.tpoo.upn.model.TipoMembresia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla tipos_membresia.
 */
public class TipoMembresiaDAO {

    public boolean insertar(TipoMembresia tipo) throws SQLException {
        String sql = "INSERT INTO tipos_membresia (nombre, precio) VALUES (?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, tipo.getNombre());
            ps.setDouble(2, tipo.getPrecio());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                return false;
            }

            try (ResultSet clavesGeneradas = ps.getGeneratedKeys()) {
                if (clavesGeneradas.next()) {
                    tipo.setIdTipo(clavesGeneradas.getInt(1));
                }
            }
            return true;
        }
    }

    public TipoMembresia buscarPorId(int idTipo) throws SQLException {
        String sql = "SELECT id_tipo, nombre, precio FROM tipos_membresia WHERE id_tipo = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTipo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearTipo(rs);
                }
                return null;
            }
        }
    }

    public List<TipoMembresia> listar() throws SQLException {
        String sql = "SELECT id_tipo, nombre, precio FROM tipos_membresia ORDER BY nombre";

        List<TipoMembresia> tipos = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tipos.add(mapearTipo(rs));
            }
        }
        return tipos;
    }

    public boolean actualizar(TipoMembresia tipo) throws SQLException {
        String sql = "UPDATE tipos_membresia SET nombre = ?, precio = ? WHERE id_tipo = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipo.getNombre());
            ps.setDouble(2, tipo.getPrecio());
            ps.setInt(3, tipo.getIdTipo());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idTipo) throws SQLException {
        String sql = "DELETE FROM tipos_membresia WHERE id_tipo = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTipo);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT id_tipo FROM tipos_membresia WHERE nombre = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Reconstruye un TipoMembresia con el constructor que recibe el id. */
    private TipoMembresia mapearTipo(ResultSet rs) throws SQLException {
        // La columna precio es DECIMAL(10,2); getDouble la entrega como double, que es el tipo del modelo.
        return new TipoMembresia(
                rs.getInt("id_tipo"),
                rs.getString("nombre"),
                rs.getDouble("precio"));
    }
}
