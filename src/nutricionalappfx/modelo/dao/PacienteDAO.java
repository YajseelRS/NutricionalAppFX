/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nutricionalappfx.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import nutricionalappfx.dominio.pojo.Paciente;
import nutricionalappfx.modelo.ConexionBD;

/**
 *
 * @author chris
 */
public class PacienteDAO {
    private static final int ESTATUS_ACTIVO = 1;
    private static final int ESTATUS_BAJA = 2;
    private static final int ERROR_DUPLICADO = 1062;

    private static final String COLUMNAS =
            "idPaciente, codigoPaciente, nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento, "
            + "telefono, sexo, altura, pesoInicial, correo, id_Estatus";

    public static List<Paciente> obtenerPacientes() throws RuntimeException {
        return buscarPacientes("");
    }

    public static List<Paciente> buscarPacientes(String texto) throws RuntimeException {
        List<Paciente> pacientes = new ArrayList<>();
        String consulta = "SELECT " + COLUMNAS + " FROM pacientes WHERE id_Estatus = ? "
                + "AND (codigoPaciente LIKE ? OR CONCAT(nombre, ' ', apellidoPaterno, ' ', IFNULL(apellidoMaterno, '')) LIKE ?) "
                + "ORDER BY nombre, apellidoPaterno";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {
            ps.setInt(1, ESTATUS_ACTIVO);
            ps.setString(2, "%" + texto + "%");
            ps.setString(3, "%" + texto + "%");
            try (ResultSet resultado = ps.executeQuery()) {
                while (resultado.next()) {
                    pacientes.add(convertirPaciente(resultado));
                }
            }
            return pacientes;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error de conexión con el almacenamiento.");
        }
    }

    public static void insertarPaciente(Paciente p) throws RuntimeException {
        String insertar = "INSERT INTO pacientes (codigoPaciente, nombre, apellidoPaterno, apellidoMaterno, "
                + "fechaNacimiento, telefono, sexo, altura, pesoInicial, correo, id_Estatus) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String actualizarCodigo = "UPDATE pacientes SET codigoPaciente = ? WHERE idPaciente = ?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement ps = conexion.prepareStatement(insertar, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "TMP" + System.currentTimeMillis());
                asignarDatos(ps, p, 2);
                ps.setInt(11, ESTATUS_ACTIVO);
                ps.executeUpdate();
                try (ResultSet claves = ps.getGeneratedKeys()) {
                    claves.next();
                    int id = claves.getInt(1);
                    try (PreparedStatement psCodigo = conexion.prepareStatement(actualizarCodigo)) {
                        psCodigo.setString(1, String.format("P%04d", id));
                        psCodigo.setInt(2, id);
                        psCodigo.executeUpdate();
                    }
                }
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw traducirError(e);
        }
    }

    public static void editarPaciente(Paciente p) throws RuntimeException {
        String consulta = "UPDATE pacientes SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, "
                + "fechaNacimiento = ?, telefono = ?, sexo = ?, altura = ?, pesoInicial = ?, correo = ? "
                + "WHERE idPaciente = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {
            asignarDatos(ps, p, 1);
            ps.setInt(10, p.getIdPaciente());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw traducirError(e);
        }
    }

    public static void darDeBajaPaciente(int idPaciente) throws RuntimeException {
        String consulta = "UPDATE pacientes SET id_Estatus = ? WHERE idPaciente = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {
            ps.setInt(1, ESTATUS_BAJA);
            ps.setInt(2, idPaciente);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error de conexión con el almacenamiento.");
        }
    }

    // Asigna nombre ... correo (9 valores) empezando en la posición "inicio"
    private static void asignarDatos(PreparedStatement ps, Paciente p, int inicio) throws SQLException {
        ps.setString(inicio, p.getNombre());
        ps.setString(inicio + 1, p.getApellidoPaterno());
        ps.setString(inicio + 2, p.getApellidoMaterno());
        ps.setString(inicio + 3, p.getFechaNacimiento());
        ps.setString(inicio + 4, p.getTelefono());
        ps.setString(inicio + 5, p.getSexo());
        ps.setDouble(inicio + 6, p.getAltura());
        ps.setDouble(inicio + 7, p.getPesoInicial());
        ps.setString(inicio + 8, p.getCorreo());
    }

    private static RuntimeException traducirError(SQLException e) {
        if (e.getErrorCode() == ERROR_DUPLICADO) {
            return new RuntimeException("El correo electrónico ya está registrado en otro paciente.");
        }
        e.printStackTrace();
        return new RuntimeException("Error de conexión con el almacenamiento.");
    }

    private static Paciente convertirPaciente(ResultSet r) throws SQLException {
        Paciente p = new Paciente();
        p.setIdPaciente(r.getInt("idPaciente"));
        p.setCodigoPaciente(r.getString("codigoPaciente"));
        p.setNombre(r.getString("nombre"));
        p.setApellidoPaterno(r.getString("apellidoPaterno"));
        p.setApellidoMaterno(r.getString("apellidoMaterno"));
        p.setFechaNacimiento(r.getString("fechaNacimiento"));
        p.setTelefono(r.getString("telefono"));
        p.setSexo(r.getString("sexo"));
        p.setAltura(r.getDouble("altura"));
        p.setPesoInicial(r.getDouble("pesoInicial"));
        p.setCorreo(r.getString("correo"));
        p.setIdEstatus(r.getInt("id_Estatus"));
        return p;
    }
}
