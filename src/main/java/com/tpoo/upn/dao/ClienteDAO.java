package com.tpoo.upn.dao;

import com.tpoo.upn.model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla clientes.
 */
public class ClienteDAO {

    public boolean insertar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (dni, nombres, apellidos, telefono) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getDni());
            ps.setString(2, cliente.getNombres());
            ps.setString(3, cliente.getApellidos());
            // setString acepta null y guarda NULL, que es lo que corresponde al telefono opcional.
            ps.setString(4, cliente.getTelefono());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                return false;
            }

            try (ResultSet clavesGeneradas = ps.getGeneratedKeys()) {
                if (clavesGeneradas.next()) {
                    cliente.setIdCliente(clavesGeneradas.getInt(1));
                }
            }
            return true;
        }
    }

    public Cliente buscarPorDni(String dni) throws SQLException {
        String sql = "SELECT id_cliente, dni, nombres, apellidos, telefono "
                + "FROM clientes WHERE dni = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
                return null;
            }
        }
    }

    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT id_cliente, dni, nombres, apellidos, telefono "
                + "FROM clientes ORDER BY apellidos, nombres";

        List<Cliente> clientes = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        }
        return clientes;
    }

    public boolean actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET dni = ?, nombres = ?, apellidos = ?, telefono = ? "
                + "WHERE id_cliente = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getDni());
            ps.setString(2, cliente.getNombres());
            ps.setString(3, cliente.getApellidos());
            ps.setString(4, cliente.getTelefono());
            ps.setInt(5, cliente.getIdCliente());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idCliente) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeDni(String dni) throws SQLException {
        String sql = "SELECT id_cliente FROM clientes WHERE dni = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Reconstruye un Cliente con el constructor que recibe el id. */
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("dni"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("telefono"));
    }
}
