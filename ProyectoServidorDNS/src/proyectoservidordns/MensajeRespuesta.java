package proyectoservidordns;


import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;



public class MensajeRespuesta 
{
    byte nombre;
    short tipo;
    short clase;
    int ttl;
    short RDLength;
    int Rdata;
    




    public MensajeRespuesta() 
    }

    public MensajeRespuesta(byte nombre, short tipo, short clase, int ttl, short rDLength, int rdata) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.clase = clase;
        this.ttl = ttl;
        RDLength = rDLength;
        Rdata = rdata;
        byte prueba = (byte) tipo;
        System.out.println("tipo pasa a byte "+ prueba);
    }
    
}
