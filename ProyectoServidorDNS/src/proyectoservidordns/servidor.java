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
            DatagramSocket servidorActivo= new DatagramSocket(53, InetAddress.getByName("192.168.0.5"));
            byte[] buffer = new byte[1000];
            while(true)   
            {
                DatagramPacket mensajePeticion = new DatagramPacket(buffer, buffer.length);
                
                servidorActivo.receive(mensajePeticion);
                System.out.println("mensaje recibido del host = "+ mensajePeticion.getAddress() + "desde el puerto: " + mensajePeticion.getPort() + "  data= "+ new String (mensajePeticion.getData()));
                
                DatagramPacket mensajeRespuesta = new DatagramPacket(mensajePeticion.getData(), mensajePeticion.getLength(),mensajePeticion.getAddress(),mensajePeticion.getPort());
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
