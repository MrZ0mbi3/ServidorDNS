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

public class servidor {

	
	private final int puerto_udp = 53;
	private final int udpSize = 512;
	private HashMap<String, ArrayList<ResRR>> masterFile;

	public servidor() {
		this.masterFile = new HashMap<String, ArrayList<ResRR>>();
		try {
			// Saca la informacion del MasterFile para poderla mantener en memoria
			obtenerMasterFileData();
			

		} catch (Exception e) {
			System.out.println("Error, no se pudo obtener la informacion del MasterFile");
		}
	}

	public void obtenerMasterFileData() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(new File("MasterFile.txt")));
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
				ResRR resp = new ResRR((short) 0xc00c, tipo, clase, ttl, len, ip);

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
                prueba.leerMensajePregunta(mensajePeticion);


                
                
                
                byte[] resp = new byte[this.udpSize];
                //Revisa si el dominio esta en el masterFile
                if(this.masterFile.containsKey(prueba.getPaginaPregunta())) {
                	System.out.println("El dominio se encuentra en el MasterFile");
                	resp = prueba.crearResInterna(masterFile,mensajePeticion);
                	//Aqui va metodo para realizar consulta interna
                }
                else {
                	System.out.println("No mijo, aqui no esta");
                	//Metodo para realizar consulta externa
                }
                 DatagramPacket paquete = new DatagramPacket(resp,resp.length-112, mensajePeticion.getAddress(), this.puerto_udp);
         		try {
         			servidorActivo.send(paquete);
         			servidorActivo.close();
         		} catch (Exception e) {
         			System.out.println("Enviando...");
         		}
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
    public static void main(String[] args) {
		servidor servidor = new servidor();
		try {
			servidor.servidorActivo();
		} catch (Exception e) {
			System.out.println("Error " + e);
		}
	}
    
}
