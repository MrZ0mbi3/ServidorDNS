package proyectoservidordns;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DNSQuery {
	// private short ID; // 2 bytes
	// private boolean QR; // 1 bit |
	// private byte Z; // 3 bits los 3 en 0 | 1 byte

	// FLAGS
	private byte OpCode; // son 4 bits |
	private boolean AA; // 1 bit |
	private boolean TC;// 1 bit |1 byte
	private boolean RD;// 1 bit |________________
	private boolean RA; // 1 bit |
	private byte RCode; // 4 bits |
	private byte Z; // 3 bits los 3 en 0 | 1 byte
	short QDCount;
	short ANCount;
	short NSCount;
	short ARCount;
	private boolean QR; // 1 bit |

	// Respuesta
	byte[] encabezado = new byte[12];
	byte[] cuerpo;
	byte[] pregunta;
	String paginaPregunta;
	
	public DNSQuery() {
		// se crea con 12 porque un byte almacena 8 bits y se necesitan 6 espacios de 16
		// bits
		encabezado = new byte[12];
		this.QR = true;
		this.OpCode = 1;
		this.AA = true;
		this.TC = false;
		this.RD = false;
		this.RA = false;
		this.Z = 0;
		this.RCode = 1;

	}

	public void leerMensajePregunta(DatagramPacket PaqueteMensaje) {
		paginaPregunta = new String();
		String aux2 = new String();
		char[] aux = new char[1];
		byte[] mensaje = PaqueteMensaje.getData();
		// this.ID = (byte) (mensaje[0] | mensaje[1]);
		this.OpCode = (byte) (mensaje[2] & 120);
		this.TC = (mensaje[2] & 2) == 1 ? true : false;
		this.RD = (mensaje[2] & 1) == 1 ? true : false;
		this.RA = (mensaje[3] & 128) == 1 ? true : false;
		this.RCode = (byte) (mensaje[3] & 15);
		this.QDCount = (short) (mensaje[4] | mensaje[5]);
		this.pregunta = new byte[PaqueteMensaje.getLength()];

		for (int i = 12; i < PaqueteMensaje.getLength() - 5; i++) {

			if ((mensaje[i] > 47 && mensaje[i] < 58) || (mensaje[i] > 64 && mensaje[i] < 91)
					|| (mensaje[i] > 96 && mensaje[i] < 123)) // validar que solo reciba caracteres y numeros
			{
				aux[0] = (char) mensaje[i];

				paginaPregunta = aux2.concat(new String(aux));
				aux2 = paginaPregunta;
			} else {
				if (i != 12) {
					paginaPregunta = aux2.concat(".");
					aux2 = paginaPregunta;
				}
			}
		}
		//Elimina puntos extra que manda una maquina linux
		paginaPregunta = paginaPregunta.replace("...........", "");
		// guardar la pregunta
		System.arraycopy(mensaje, 12, this.pregunta, 0, PaqueteMensaje.getLength());
		// encuentra la pagina pero sin puntos
		// Desde 4 antes del tamano final porque hay un byte null despues del mensaje
		// que indica que finalizo este
	}

	public byte[] crearResInterna(HashMap<String, ArrayList<ResRR>> masterFile, DatagramPacket PaqueteMensaje) {
		hacerBodyRespuestaInterna(masterFile);
		hacerEncabezadoRespuesta(PaqueteMensaje);
		imprimirRespuestaInterna(masterFile);
		byte[] combined = new byte[this.encabezado.length + this.pregunta.length + this.cuerpo.length + 100];
		System.arraycopy(this.encabezado, 0, combined, 0, this.encabezado.length);
		System.arraycopy(this.pregunta, 0, combined, this.encabezado.length, this.pregunta.length );
		System.arraycopy(this.cuerpo, 0, combined, this.pregunta.length, this.cuerpo.length);
		return combined;
	}

	public void hacerBodyRespuestaInterna(HashMap<String, ArrayList<ResRR>> masterFile) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(out);
		// Encuentra las respuestas que coincidan con el dominio
		ArrayList<ResRR> rec = masterFile.get(this.paginaPregunta);
		// Saca todas las respuestas del masterFile
		for (ResRR actual : rec) {
			try {

				data.write(actual.toByte());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.ANCount = (short) rec.size();
		this.cuerpo = out.toByteArray();
	}

	public void hacerEncabezadoRespuesta(DatagramPacket PaqueteMensaje) { // guarda el ID del encabezado de la pregunta
																			// // que le llega
		byte[] mensajePregunta = PaqueteMensaje.getData();
		this.leerMensajePregunta(PaqueteMensaje);
		this.encabezado[0] = mensajePregunta[0];
		this.encabezado[1] = mensajePregunta[1];
		this.encabezado[2] = (byte) 128;// QR
		this.encabezado[2] = (byte) (this.encabezado[2] | this.OpCode);// Opcode
		if (this.AA) {
			this.encabezado[2] = (byte) (this.encabezado[2] | 4); // autorithy
		}
		if (this.TC) {
			this.encabezado[2] = (byte) (this.encabezado[2] | 2); // TC
		}
		if (this.RD) {
			this.encabezado[2] = (byte) (this.encabezado[2] | 1);// RD
		}
		if (this.RA) {
			this.encabezado[3] = (byte) (0);// RA
		}
		this.encabezado[3] = (byte) (this.encabezado[3] | this.RCode);// RCode se debe cambiar si depronto
		this.encabezado[4] = mensajePregunta[4]; // QDCount
		this.encabezado[5] = mensajePregunta[5]; // QDCount 2
		this.encabezado[6] = (byte) (this.ANCount & 0xff00); // ANCount
		this.encabezado[7] = (byte) (this.ANCount & 0xff);// ANCount 2
		this.encabezado[8] = 0;// NSCount
		this.encabezado[9] = 0;// NSCount 2
		this.encabezado[10] = 0;// ARCcount
		this.encabezado[11] = 0;// ARCount2
	}

	public void imprimirRespuestaInterna(HashMap<String, ArrayList<ResRR>> masterFile) {
		int i = 1;
		ArrayList<ResRR> rec = masterFile.get(this.paginaPregunta);
		System.out.println("------------------- Dominio: " + this.paginaPregunta + " -------------------");
		System.out.println("Numero de respuestas: " + rec.size());
		for (ResRR actual : rec) {
			System.out.println("------- Respuesta : " + i + " --------");
			System.out.println("Tipo: A");
			System.out.println("Clase: IN");
			System.out.println("TTL: " + actual.getTLL());
			System.out.println("Tamaño: " + actual.getLength());
			System.out.println("IP : " + actual.getAdress());
			i++;
		}
	}
	
	public void hacerFlags() {
		byte aux = 0;
		byte aux2 = 0;
		if (this.QR) {
			// 1000 0000
			aux = (byte) 128;
		}
		// para que se compare en la posicion correcta
		// 1_ _ _ _000
		this.OpCode = (byte) (this.OpCode << 3);

		aux = (byte) (aux | this.OpCode);
		if (this.AA) { // 1000 0_00
			aux = (byte) (aux | 4);
		}
		if (this.TC) {
			// 1000 00_0
			aux = (byte) (aux | 2);
		}
		if (this.RD) {
			// 1000 00_0
			aux = (byte) (aux | 1);

		}
		// se guarda el primer byte de los flags desde QR a RD
		this.encabezado[2] = aux;

		if (this.RA) {
			aux2 = (byte) 128;
		}
		aux2 = (byte) (aux2 | this.RCode);
		this.encabezado[3] = aux2;
	}



	public String getPaginaPregunta() {
		return paginaPregunta;
	}
}
