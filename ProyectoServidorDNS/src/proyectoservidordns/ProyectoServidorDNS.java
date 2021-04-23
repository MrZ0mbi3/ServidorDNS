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
        Servidor server= new Servidor();
        //iniciador del servidor se activa cuando se necesita probar el servidor
        server.servidorActivo();
        
        


        //ejemplo para convertir un numero a un string a binario 
        /*short n = 30000;
    
        String cadena = new String(Long.toBinaryString((long) n ));
        System.out.println(cadena);*/
        
        
        //Prueba
        
       /* byte[] mensajePrueba = new byte[2];
        mensajePrueba[0]=(byte) 0xB5;
        mensajePrueba[1]=15;
        System.out.println("prueba byte" + mensajePrueba[0]);*/
        //HeaderFormat prueba = new HeaderFormat();
        //prueba.setFlags(mensajePrueba);
        
        HeaderFormat prueba=new HeaderFormat();
        prueba.hacerFlags();
    }
    
}
