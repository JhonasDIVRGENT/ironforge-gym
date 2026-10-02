package com.tpoo.upn.app;

import java.sql.Connection;
import java.sql.SQLException;

import com.tpoo.upn.dao.ConexionDB;

/**
 * Clase pequeña para comprobar que la conexion a MySQL funciona.
 * abre y cierra conexion 
 */
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
