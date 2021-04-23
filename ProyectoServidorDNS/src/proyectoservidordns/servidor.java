/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoservidordns;

/**
 *
 * @author samyf
 */
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class servidor {

    public servidor() {
    }
    
    
    void servidorActivo()
    {
        try
        {
            DatagramSocket servidorActivo= new DatagramSocket(53, InetAddress.getByName("192.168.0.6"));
            byte[] buffer = new byte[1000];
            HeaderFormat encabezado= new HeaderFormat();
            MensajeRespuesta mensaje = new MensajeRespuesta();
            String respuesta;
            byte[] bMensaje= new byte[1000];
            
            
            
            while(true)   
            {
                DatagramPacket mensajePeticion = new DatagramPacket(buffer, buffer.length);
                
                servidorActivo.receive(mensajePeticion);
                
                System.out.println("mensaje recibido del host = "+ mensajePeticion.getAddress() + "desde el puerto: " + mensajePeticion.getPort()) ;
                //byte[] pruebaMnesaje = mensajePeticion.getData();
                System.out.println("prueba puerto "+ mensajePeticion.getPort() + "tamano del mensaje"+ mensajePeticion.getLength());
                
                HeaderFormat prueba = new HeaderFormat();
                //prueba.setFlags(pruebaMnesaje);
                //System.out.println(pruebaMnesaje[0]);
                prueba.leerMensajePregunta(mensajePeticion);
                //

                //respuesta=encabezado.EncabezadoMensajeRespuestaSinError()+mensaje.MensajeRespuesta();
                //bMensaje=respuesta.getBytes();
                 String resp= "hola1";

                DatagramPacket mensajeRespuesta = new DatagramPacket(resp.getBytes(), resp.length(),mensajePeticion.getAddress(),mensajePeticion.getPort());
                servidorActivo.send(mensajeRespuesta);
            }
            
        }
        catch (SocketException e)
        {
            System.out.println("Socket: " + e.getMessage());
        } catch (UnknownHostException ex) {
            Logger.getLogger(servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
