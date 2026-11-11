/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package nutricionalappfx.dominio.contrato;

import nutricionalappfx.dominio.pojo.Colaborador;
import nutricionalappfx.dominio.pojo.ColaboradorEstatus;

/**
 *
 * @author chris
 */
public interface IAutenticacionRepositorio {
    
    public int verificarCredenciales(String noPersonal, String contrasenia) throws RuntimeException;
        
    public ColaboradorEstatus verificarEstatusColaborador(int idColaborador) throws RuntimeException;
    
    public Colaborador ObtenerInformacionColaborador(int idColaborador) throws RuntimeException;
}
