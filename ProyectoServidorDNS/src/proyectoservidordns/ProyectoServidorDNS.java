/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoservidordns;
import java.net.*;
import java.io.*;
/**
 *
 * @author samyf
 */
public class ProyectoServidorDNS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        servidor server= new servidor();
        server.servidorActivo();
    }
    
}
