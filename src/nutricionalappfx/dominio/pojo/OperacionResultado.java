/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nutricionalappfx.dominio.pojo;

/**
 *
 * @author chris
 */
public class OperacionResultado {
    
    private boolean error;
    private String mensajes;

    public OperacionResultado() {
    }

    public OperacionResultado(boolean error, String mensajes) {
        this.error = error;
        this.mensajes = mensajes;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMensajes() {
        return mensajes;
    }

    public void setMensajes(String mensajes) {
        this.mensajes = mensajes;
    }
}
