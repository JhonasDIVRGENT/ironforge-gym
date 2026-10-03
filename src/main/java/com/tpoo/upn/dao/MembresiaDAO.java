package com.tpoo.upn.dao;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.model.TipoMembresia;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Sin actualizar ni eliminar: renovar inserta un periodo nuevo y los anteriores quedan como historial.
public class MembresiaDAO {

    private static final String SQL_SELECT =
            "SELECT m.id_membresia, m.fecha_inicio, m.fecha_fin, "
            + "c.id_cliente, c.dni, c.nombres AS cliente_nombres, "
            + "c.apellidos AS cliente_apellidos, c.telefono, "
            + "t.id_tipo, t.nombre AS tipo_nombre, t.precio "
            + "FROM membresias m "
            + "INNER JOIN clientes c ON m.id_cliente = c.id_cliente "
            + "INNER JOIN tipos_membresia t ON m.id_tipo = t.id_tipo ";

    public boolean insertar(Membresia membresia) throws SQLException {
        String sql = "INSERT INTO membresias (id_cliente, id_tipo, fecha_inicio, fecha_fin) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, membresia.getCliente().getIdCliente());
            ps.setInt(2, membresia.getTipo().getIdTipo());
            ps.setDate(3, Date.valueOf(membresia.getFechaInicio()));
            ps.setDate(4, Date.valueOf(membresia.getFechaFin()));

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                return false;
            }

            try (ResultSet clavesGeneradas = ps.getGeneratedKeys()) {
                if (clavesGeneradas.next()) {
                    membresia.setIdMembresia(clavesGeneradas.getInt(1));
                }
            }
            return true;
        }
    }

    public Membresia buscarPorId(int idMembresia) throws SQLException {
        String sql = SQL_SELECT + "WHERE m.id_membresia = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMembresia);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMembresia(rs);
                }
                return null;
            }
        }
    }

    public List<Membresia> listarPorCliente(String dni) throws SQLException {
        String sql = SQL_SELECT + "WHERE c.dni = ? ORDER BY m.fecha_inicio";

        List<Membresia> membresias = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    membresias.add(mapearMembresia(rs));
                }
            }
        }
        return membresias;
    }

    // Vigentes en "desde" y que vencen entre "desde" y "hasta" (ambos incluidos).
    public List<Membresia> listarPorVencer(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = SQL_SELECT
                + "WHERE m.fecha_fin BETWEEN ? AND ? AND m.fecha_inicio <= ? "
                + "ORDER BY m.fecha_fin";

        List<Membresia> membresias = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));
            ps.setDate(3, Date.valueOf(desde));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    membresias.add(mapearMembresia(rs));
                }
            }
        }
        return membresias;
    }

    private Membresia mapearMembresia(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("dni"),
                rs.getString("cliente_nombres"),
                rs.getString("cliente_apellidos"),
                rs.getString("telefono"));

        TipoMembresia tipo = new TipoMembresia(
                rs.getInt("id_tipo"),
                rs.getString("tipo_nombre"),
                rs.getDouble("precio"));

        return new Membresia(
                rs.getInt("id_membresia"),
                cliente,
                tipo,
                rs.getDate("fecha_inicio").toLocalDate(),
                rs.getDate("fecha_fin").toLocalDate());
    }
}
