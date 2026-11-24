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

public class PacienteDAO implements IPacienteRepositorio{
    
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
        String sentencia = "INSERT INTO pacientes (codigo, nombre, apellidoPaterno, apellidoMaterno, " +
                "fechaNacimiento, altura, pesoInicial, sexo, telefono, correo, idNutriologo) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement ps = conexion.prepareStatement(sentencia);){
                ps.setString(1, paciente.getCodigo());
                ps.setString(2, paciente.getNombre());
                ps.setString(3, paciente.getApellidoPaterno());
                ps.setString(4, paciente.getApellidoMaterno());
                ps.setString(5, paciente.getFechaNacimiento());
                ps.setInt(6, paciente.getAltura());
                ps.setFloat(7, paciente.getPesoInicial());
                ps.setString(8, "" + paciente.getSexo());
                ps.setString(9, paciente.getTelefono());
                ps.setString(10, paciente.getCorreo());
                ps.setInt(11, paciente.getIdNutriologo());
                int filasAfectadas = ps.executeUpdate();
                if(filasAfectadas > 0 ){
                    resultado.setError(false);
                    resultado.setMensajes("Informacion del paciente, guardada correctamente.");
                } else {
                    resultado.setError(true);
                    resultado.setMensajes("Lo sentimos la informacion del paciente no pudo ser guardada, " +
                            "porfavor revise la informacion he intentelo de nuevo.");
                }
                return resultado;
        } catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException("Error en el almacenamiento.");
        }
    }
    
    @Override
    public OperacionResultado editarPaciente(Paciente paciente) throws RuntimeException {
        return null;
    }
    
    @Override
    public OperacionResultado eliminarPaciente(int idPaciente) throws RuntimeException {
        return null;
    }
    
    @Override
    public boolean verificarExistenciaCodigoPaciente(String codigo) throws RuntimeException {
        return false;
    }

    private static final int ERROR_LLAVE_FORANEA = 1451;

    private static final String COLUMNAS =
            "idPaciente, codigo, nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento, "
            + "altura, pesoInicial, sexo, telefono, correo, idNutriologo";

    public static List<Paciente> obtenerPacientes() throws RuntimeException {
        return buscarPacientes("");
    }

    public static List<Paciente> buscarPacientes(String texto) throws RuntimeException {
        List<Paciente> pacientes = new ArrayList<>();
        String consulta = "SELECT " + COLUMNAS + " FROM pacientes "
                + "WHERE codigo LIKE ? OR CONCAT(nombre, ' ', apellidoPaterno, ' ', IFNULL(apellidoMaterno, '')) LIKE ? "
                + "ORDER BY nombre, apellidoPaterno";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
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
        String insertar = "INSERT INTO pacientes (codigo, nombre, apellidoPaterno, apellidoMaterno, "
                + "fechaNacimiento, altura, pesoInicial, sexo, telefono, correo, idNutriologo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String actualizarCodigo = "UPDATE pacientes SET codigo = ? WHERE idPaciente = ?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement ps = conexion.prepareStatement(insertar, Statement.RETURN_GENERATED_KEYS)) {
                // Código temporal de 7 caracteres; se reemplaza enseguida por P0001, P0002...
                ps.setString(1, String.format("T%06d", Math.abs(System.nanoTime() % 1000000)));
                asignarDatos(ps, p, 2);
                ps.setInt(11, p.getIdNutriologo());
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
            e.printStackTrace();
            throw new RuntimeException("Error de conexión con el almacenamiento.");
        }
    }

    public static void darDeBajaPaciente(int idPaciente) throws RuntimeException {
        String consulta = "DELETE FROM pacientes WHERE idPaciente = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {
            ps.setInt(1, idPaciente);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_LLAVE_FORANEA) {
                throw new RuntimeException("No se puede dar de baja: el paciente tiene registros asociados.");
            }
            e.printStackTrace();
            throw new RuntimeException("Error de conexión con el almacenamiento.");
        }
    }

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