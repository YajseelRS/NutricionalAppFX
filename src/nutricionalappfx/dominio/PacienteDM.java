package nutricionalappfx.dominio;

import java.util.List;
import nutricionalappfx.dominio.contrato.IPacienteRepositorio;
import nutricionalappfx.dominio.pojo.OperacionResultado;
import nutricionalappfx.dominio.pojo.Paciente;

public class PacienteDM {

    private IPacienteRepositorio contrato;

    public PacienteDM(IPacienteRepositorio contrato) {
        this.contrato = contrato;
    }

    public List<Paciente> obtenerPacientesNutriologo(int idNutriologo) throws RuntimeException {
        if(idNutriologo < 0){
            throw new RuntimeException("Error al obtener la informacion.");
        }
        return contrato.obtenerPacientesNutriologo(idNutriologo);
    } 

    public OperacionResultado registrarPaciente(Paciente paciente) throws RuntimeException {
        return contrato.registrarPaciente(paciente);
    }

    public OperacionResultado editarPaciente(Paciente paciente) throws RuntimeException {
        return contrato.editarPaciente(paciente);
    }

    public OperacionResultado eliminarPaciente(int idPaciente) throws RuntimeException {
        return contrato.eliminarPaciente(idPaciente);
    }

    public boolean verificarExistenciaCodigoPaciente(String codigo) throws RuntimeException {
        return contrato.verificarExistenciaCodigoPaciente(codigo);
    }
}