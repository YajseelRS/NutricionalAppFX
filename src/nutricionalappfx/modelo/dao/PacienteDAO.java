package nutricionalappfx.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import nutricionalappfx.dominio.contrato.IPacienteRepositorio;
import nutricionalappfx.dominio.pojo.OperacionResultado;
import nutricionalappfx.dominio.pojo.Paciente;
import nutricionalappfx.modelo.ConexionBD;

public class PacienteDAO implements IPacienteRepositorio {

    private static final int ERROR_LLAVE_FORANEA = 1451;

    private static final String COLUMNAS
            = "idPaciente, codigo, nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento, "
            + "altura, pesoInicial, sexo, telefono, correo, idNutriologo";

    @Override
    public List<Paciente> obtenerPacientesNutriologo(int idNutriologo) throws RuntimeException {
        List<Paciente> pacientes = new ArrayList<>();
        String consulta = "SELECT " + COLUMNAS + " FROM pacientes "
                + "WHERE idNutriologo = ? "
                + "ORDER BY nombre, apellidoPaterno";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {

            ps.setInt(1, idNutriologo);

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

    @Override
    public OperacionResultado registrarPaciente(Paciente paciente) throws RuntimeException {
        OperacionResultado resultado = new OperacionResultado();
        String insertar = "INSERT INTO pacientes (codigo, nombre, apellidoPaterno, apellidoMaterno, "
                + "fechaNacimiento, altura, pesoInicial, sexo, telefono, correo, idNutriologo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String actualizarCodigo = "UPDATE pacientes SET codigo = ? WHERE idPaciente = ?";

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement ps = conexion.prepareStatement(insertar, Statement.RETURN_GENERATED_KEYS)) {
                // Código temporal de 7 caracteres; se reemplaza enseguida por P0001, P0002...
                ps.setString(1, String.format("T%06d", Math.abs(System.nanoTime() % 1000000)));
                asignarDatos(ps, paciente, 2);
                ps.setInt(11, paciente.getIdNutriologo());
                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas > 0) {
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
                    resultado.setError(false);
                    resultado.setMensajes("Información del paciente guardada correctamente.");
                } else {
                    conexion.rollback();
                    resultado.setError(true);
                    resultado.setMensajes("Lo sentimos, la información del paciente no pudo ser guardada. "
                            + "Por favor revise la información e inténtelo de nuevo.");
                }
                return resultado;
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error de conexión con el almacenamiento.");
        }
    }

    @Override
    public OperacionResultado editarPaciente(Paciente paciente) throws RuntimeException {
        OperacionResultado resultado = new OperacionResultado();
        String consulta = "UPDATE pacientes SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, "
                + "fechaNacimiento = ?, altura = ?, pesoInicial = ?, sexo = ?, telefono = ?, correo = ? "
                + "WHERE idPaciente = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {
            asignarDatos(ps, paciente, 1);
            ps.setInt(10, paciente.getIdPaciente());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                resultado.setError(false);
                resultado.setMensajes("Los datos del paciente fueron actualizados.");
            } else {
                resultado.setError(true);
                resultado.setMensajes("No se encontró el paciente a editar.");
            }
            return resultado;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error de conexión con el almacenamiento.");
        }
    }

    @Override
    public OperacionResultado eliminarPaciente(int idPaciente) throws RuntimeException {
        OperacionResultado resultado = new OperacionResultado();
        String consulta = "DELETE FROM pacientes WHERE idPaciente = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {
            ps.setInt(1, idPaciente);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                resultado.setError(false);
                resultado.setMensajes("El paciente fue dado de baja.");
            } else {
                resultado.setError(true);
                resultado.setMensajes("No se encontró el paciente a eliminar.");
            }
            return resultado;
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_LLAVE_FORANEA) {
                throw new RuntimeException("No se puede dar de baja: el paciente tiene registros asociados.");
            }
            e.printStackTrace();
            throw new RuntimeException("Error de conexión con el almacenamiento.");
        }
    }

    @Override
    public boolean verificarExistenciaCodigoPaciente(String codigo) throws RuntimeException {
        String consulta = "SELECT COUNT(*) FROM pacientes WHERE codigo = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {
            ps.setString(1, codigo);
            try (ResultSet resultado = ps.executeQuery()) {
                return resultado.next() && resultado.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error de conexión con el almacenamiento.");
        }
    }

    // Asigna nombre ... correo (9 campos) a partir de la posición "inicio".
    private static void asignarDatos(PreparedStatement ps, Paciente p, int inicio) throws SQLException {
        ps.setString(inicio, p.getNombre());
        ps.setString(inicio + 1, p.getApellidoPaterno());
        ps.setString(inicio + 2, p.getApellidoMaterno());
        ps.setString(inicio + 3, p.getFechaNacimiento());
        ps.setInt(inicio + 4, p.getAltura());
        ps.setFloat(inicio + 5, p.getPesoInicial());
        ps.setString(inicio + 6, String.valueOf(p.getSexo()));
        ps.setString(inicio + 7, p.getTelefono());
        ps.setString(inicio + 8, p.getCorreo());
    }

    private static Paciente convertirPaciente(ResultSet r) throws SQLException {
        Paciente p = new Paciente();
        p.setIdPaciente(r.getInt("idPaciente"));
        p.setCodigo(r.getString("codigo"));
        p.setNombre(r.getString("nombre"));
        p.setApellidoPaterno(r.getString("apellidoPaterno"));
        p.setApellidoMaterno(r.getString("apellidoMaterno"));
        p.setFechaNacimiento(r.getString("fechaNacimiento"));
        p.setAltura(r.getInt("altura"));
        p.setPesoInicial(r.getFloat("pesoInicial"));
        p.setSexo(r.getString("sexo").charAt(0));
        p.setTelefono(r.getString("telefono"));
        p.setCorreo(r.getString("correo"));
        p.setIdNutriologo(r.getInt("idNutriologo"));
        return p;
    }
}