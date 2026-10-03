package com.tpoo.upn.dao;

import com.tpoo.upn.model.Cliente;
import com.tpoo.upn.model.Ingreso;
import com.tpoo.upn.model.Membresia;
import com.tpoo.upn.model.TipoMembresia;
import com.tpoo.upn.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// Sin actualizar ni eliminar: los ingresos son historial.
public class IngresoDAO {

    // c = cliente del ingreso, cm = cliente de la membresia.
    private static final String SQL_SELECT =
            "SELECT i.id_ingreso, i.fecha_hora, "
            + "c.id_cliente, c.dni, c.nombres AS cliente_nombres, "
            + "c.apellidos AS cliente_apellidos, c.telefono, "
            + "m.id_membresia, m.fecha_inicio, m.fecha_fin, "
            + "cm.id_cliente AS mem_id_cliente, cm.dni AS mem_dni, "
            + "cm.nombres AS mem_nombres, cm.apellidos AS mem_apellidos, "
            + "cm.telefono AS mem_telefono, "
            + "t.id_tipo, t.nombre AS tipo_nombre, t.precio, "
            + "u.id_usuario, u.nombres AS usuario_nombres, u.apellidos AS usuario_apellidos, "
            + "u.username, u.password, u.rol, u.activo "
            + "FROM ingresos i "
            + "INNER JOIN clientes c ON i.id_cliente = c.id_cliente "
            + "INNER JOIN membresias m ON i.id_membresia = m.id_membresia "
            + "INNER JOIN clientes cm ON m.id_cliente = cm.id_cliente "
            + "INNER JOIN tipos_membresia t ON m.id_tipo = t.id_tipo "
            + "INNER JOIN usuarios u ON i.id_usuario = u.id_usuario ";

    public boolean insertar(Ingreso ingreso) throws SQLException {
        String sql = "INSERT INTO ingresos (id_cliente, id_membresia, id_usuario, fecha_hora) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, ingreso.getCliente().getIdCliente());
            ps.setInt(2, ingreso.getMembresia().getIdMembresia());
            ps.setInt(3, ingreso.getUsuario().getIdUsuario());
            ps.setTimestamp(4, Timestamp.valueOf(ingreso.getFechaHora()));

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                return false;
            }

            try (ResultSet clavesGeneradas = ps.getGeneratedKeys()) {
                if (clavesGeneradas.next()) {
                    ingreso.setIdIngreso(clavesGeneradas.getInt(1));
                }
            }
            return true;
        }
    }

    public Ingreso buscarPorId(int idIngreso) throws SQLException {
        String sql = SQL_SELECT + "WHERE i.id_ingreso = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idIngreso);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearIngreso(rs);
                }
                return null;
            }
        }
    }

    public List<Ingreso> listarPorCliente(String dni) throws SQLException {
        String sql = SQL_SELECT + "WHERE c.dni = ? ORDER BY i.fecha_hora DESC";

        List<Ingreso> ingresos = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ingresos.add(mapearIngreso(rs));
                }
            }
        }
        return ingresos;
    }

    private Ingreso mapearIngreso(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("dni"),
                rs.getString("cliente_nombres"),
                rs.getString("cliente_apellidos"),
                rs.getString("telefono"));

        Cliente clienteMembresia = new Cliente(
                rs.getInt("mem_id_cliente"),
                rs.getString("mem_dni"),
                rs.getString("mem_nombres"),
                rs.getString("mem_apellidos"),
                rs.getString("mem_telefono"));

        TipoMembresia tipo = new TipoMembresia(
                rs.getInt("id_tipo"),
                rs.getString("tipo_nombre"),
                rs.getDouble("precio"));

        Membresia membresia = new Membresia(
                rs.getInt("id_membresia"),
                clienteMembresia,
                tipo,
                rs.getDate("fecha_inicio").toLocalDate(),
                rs.getDate("fecha_fin").toLocalDate());

        Usuario usuario = new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("usuario_nombres"),
                rs.getString("usuario_apellidos"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("rol"),
                rs.getBoolean("activo"));

        return new Ingreso(
                rs.getInt("id_ingreso"),
                cliente,
                membresia,
                usuario,
                rs.getTimestamp("fecha_hora").toLocalDateTime());
    }
}
