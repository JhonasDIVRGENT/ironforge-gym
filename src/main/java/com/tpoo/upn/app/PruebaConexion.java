package com.tpoo.upn.app;

import com.tpoo.upn.dao.ConexionDB;
import java.sql.Connection;
import java.sql.SQLException;

// Abre y cierra una conexion para comprobar que MySQL responde.
public class PruebaConexion {

    public static void main(String[] args) {
        try (Connection conexion = ConexionDB.getConexion()) {
            System.out.println("Conexion exitosa a la base de datos ironforge_gym");
        } catch (SQLException e) {
            System.out.println("No se pudo conectar a la base de datos");
            System.out.println("Detalle del error: " + e.getMessage());
        }
    }
}
