/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nutricionalappfx.utilidades;

import nutricionalappfx.dominio.pojo.Colaborador;

/**
 *
 * @author chris
 */
public class Sesion {
    
    private static Colaborador colaborador;

    public static Colaborador getColaborador() {
        return colaborador;
    }

    public static void iniciar(Colaborador c) {
        colaborador = c;
    }

    public static void cerrar() {
        colaborador = null;
    }
}
