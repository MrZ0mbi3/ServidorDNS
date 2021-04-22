package proyectoservidordns;


import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;



public class MensajeRespuesta 
{
    String nombre;
    short tipo;
    short clase;
    int ttl;
    short RDLength;
    int Rdata;
    




    public MensajeRespuesta()
    {
        nombre="server";
    }

    public MensajeRespuesta(String nombre, short tipo, short clase, int ttl, short rDLength, int rdata) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.clase = clase;
        this.ttl = ttl;
        RDLength = rDLength;
        Rdata = rdata;
        byte prueba = (byte) tipo;
        System.out.println("tipo pasa a byte "+ prueba);
    }
    public String MensajeRespuesta()
    {
        String respuesta="";
        String direccion="192.168.10.0";
        respuesta=this.nombre +"0000000000000001"+"0000000000000001"+"4"+direccion;
        return respuesta;
    }
    
}
