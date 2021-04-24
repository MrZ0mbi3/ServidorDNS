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
import java.nio.ByteBuffer;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

	
	private final int puerto_udp = 53;
	private final int udpSize = 512;
	private HashMap<String, ArrayList<ResRR>> masterFile;

	public Servidor() {
		this.masterFile = new HashMap<String, ArrayList<ResRR>>();
		try {
			// Saca la informacion del MasterFile para poderla mantener en memoria
			System.out.println("Empezando a sacar datos del MasterFile");
			obtenerMasterFileData();
			System.out.println(this.masterFile);
			

		} catch (Exception e) {
			System.out.println("Error, no se pudo obtener la informacion del MasterFile");
		}
	}

	public void obtenerMasterFileData() throws Exception {
		System.out.println("Entre");
		BufferedReader br = new BufferedReader(
				//new FileReader("F:\\Desktop\\anaconda\\ServidorDNS\\ProyectoServidorDNS\\src\\proyectoservidordns\\MasterFile.txt"));
				new FileReader("D:\\universdad\\semestre 7\\Redes\\proyecto servidor DNS\\proyecto de redes con el git\\ServidorDNS\\ProyectoServidorDNS\\src\\proyectoservidordns\\MasterFile.txt"));
				
				
		System.out.println("Entre2");
		String linea;
		String dominio = "";
		InetAddress ip;
		byte[] name;
		int ttl;
		short tipo;
		short clase;
		short len = 4;

		while ((linea = br.readLine()) != null) {

			String[] datos = linea.split(" ");

			if (!datos[0].equalsIgnoreCase("$ORIGIN")) {

				ArrayList<ResRR> ips = new ArrayList<ResRR>();

				dominio = datos[0];
				name = datos[0].getBytes();
				ttl = Integer.parseInt(datos[1]);
				tipo = 0x0001;
				clase = 0x0001;
				ip = InetAddress.getByName(datos[4]);
				ResRR resp = new ResRR(convertToShort(name), tipo, clase, ttl, len, ip);

				if (this.masterFile.containsKey(dominio)) {
					this.masterFile.get(dominio).add(resp);
				} else {
					ips.add(resp);
					this.masterFile.put(dominio, ips);
				}
			}
		}

		br.close();

	}

	public short convertToShort(byte[] name) {

		ByteBuffer buffer = ByteBuffer.wrap(name);
		return buffer.getShort();
	}
    
    
    void servidorActivo()
    {
        try
        {
        	DatagramSocket servidorActivo = new DatagramSocket(this.puerto_udp,InetAddress.getByName("192.168.0.6") );//Se conecta a lka direccion ip dada y al puerto esto para que no presente conflictos por el uso de la ip
            byte[] buffer = new byte[this.udpSize];
            HeaderFormat encabezado= new HeaderFormat();
            MensajeRespuesta mensaje = new MensajeRespuesta();
            String respuesta;
            byte[] bMensaje= new byte[this.udpSize];
            System.out.println("Iniciando servidor");
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
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String[] args) {
		Servidor servidor = new Servidor();
		try {
			servidor.servidorActivo();
		} catch (Exception e) {
			System.out.println("Error " + e);
		}
	}
    
}
