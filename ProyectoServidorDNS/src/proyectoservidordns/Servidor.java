package proyectoservidordns;
import java.net.*;
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
		int ttl;
		short tipo;
		short clase;
		short len = 4;

		while ((linea = br.readLine()) != null) {

			String[] datos = linea.split(" ");

			if (!datos[0].equalsIgnoreCase("$ORIGIN")) {

				ArrayList<ResRR> ips = new ArrayList<ResRR>();
				dominio = datos[0];
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

	void servidorActivo() {
		try {
			byte[] buffer = new byte[this.udpSize];
			System.out.println("Iniciando servidor");
			while (true) {
				DatagramSocket servidorActivo = new DatagramSocket(this.puerto_udp,
						InetAddress.getByName("10.10.10.88"));// Se conecta a la direccion ip dada y al puerto esto
																// para que no presente conflictos por el uso de la ip
				DatagramPacket mensajePeticion = new DatagramPacket(buffer, buffer.length);

				servidorActivo.receive(mensajePeticion);
				System.out.println("----------------------- Nueva peticion ------------------------------------");
				System.out.println("Mensaje recibido del host = " + mensajePeticion.getAddress().getHostAddress());
				System.out.println("Desde el puerto: " + mensajePeticion.getPort());

				DNSQuery prueba = new DNSQuery();
				prueba.leerMensajePregunta(mensajePeticion);

				byte[] resp = new byte[this.udpSize];
				// Revisa si el dominio esta en el masterFile
				if (this.masterFile.containsKey(prueba.getPaginaPregunta())) {
					System.out.println("El dominio se encuentra en el MasterFile");
					resp = prueba.crearResInterna(masterFile, mensajePeticion);
				} else {
					System.out.println("No mijo, aqui no esta");
					// Metodo para realizar consulta externa
				}
				DatagramPacket paquete = new DatagramPacket(resp, resp.length - 112, mensajePeticion.getAddress(),
						mensajePeticion.getPort());
				try {
					servidorActivo.send(paquete);
					servidorActivo.close();
				} catch (Exception e) {
					System.out.println("Enviando...");
				}
			}

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (UnknownHostException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void main(String[] args) {
		Servidor Servidor = new Servidor();
		try {
			Servidor.servidorActivo();
		} catch (Exception e) {
			System.out.println("Error " + e);
		}
	}

}
