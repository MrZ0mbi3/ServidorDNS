package proyectoservidordns;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class HeaderFormat {
	private short ID; // 2 bytes
	// FLAGS
	private boolean QR; // 1 bit |
	private byte OpCode; // son 4 bits |
	private boolean AA; // 1 bit |
	private boolean TC;// 1 bit |1 byte
	private boolean RD;// 1 bit |________________
	private boolean RA; // 1 bit |
	private byte Z; // 3 bits los 3 en 0 | 1 byte
	private byte RCode; // 4 bits |
	private short tipo;
	private short clase;
	private short nombre;
	private int TTL;
	private short tama;
	private InetAddress ip;
	/// FLAGS
	short QDCount;
	short ANCount;
	short NSCount;
	short ARCount;
	// Header en byte
	byte[] encabezado;
	byte[] cuerpo;
	byte[] pregunta;
	String paginaPregunta;

	public HeaderFormat() {
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

	public void setFlags(byte[] mensaje) {
		short aux = (short) (mensaje[2] & 0x80);
		this.QR = aux == 0x80 ? true : false;
		aux = (short) (mensaje[2] & 0x78);
		this.OpCode = (byte) (aux >> 3);
		System.out.println("prueba" + QR + " flag op code= " + OpCode);

	}

	public void leerMensajePregunta(DatagramPacket PaqueteMensaje) {
		paginaPregunta = new String();
		String aux2 = new String();
		char[] aux = new char[1];
		byte[] mensaje = PaqueteMensaje.getData();
		System.out.println(
				"_________________________________________________Mensaje entrante __________________________________________________________");
		System.out.println("se imprime header");
		System.out.println("ID " + (mensaje[0] | mensaje[1]));
		this.ID = (byte)(mensaje[0] | mensaje[1]);
		System.out.println("QR " + (mensaje[2] & 128));
		System.out.println("OpCode " + (mensaje[2] & 120));
		this.OpCode = (byte) (mensaje[2] & 120);
		System.out.println("AA " + (mensaje[2] & 4));
		System.out.println("TC " + (mensaje[2] & 2));
		this.TC = (mensaje[2] & 2) == 1 ? true : false;
		System.out.println("RD " + (mensaje[2] & 1));
		this.RD = (mensaje[2] & 1) == 1 ? true : false;
		System.out.println("RA " + (mensaje[3] & 128));
		this.RA = (mensaje[3] & 128) == 1 ? true : false;
		System.out.println("Z " + (mensaje[3] & 112));
		System.out.println("RCode " + (mensaje[3] & 15));
		this.RCode = (byte) (mensaje[3] & 15);
		System.out.println("QDCount " + (mensaje[4] | mensaje[5]));
		this.QDCount = (short) (mensaje[4] | mensaje[5]);
		System.out.println("ANCount " + (mensaje[6] | mensaje[7]));
		System.out.println("NSCount " + (mensaje[8] | mensaje[9]));
		System.out.println("ARCount " + (mensaje[10] | mensaje[11]));
		System.out.println("Fin del encabezado");
		System.out.println("Inicio de la query");
		this.pregunta=new byte[PaqueteMensaje.getLength()];


		for (int i = 12; i < PaqueteMensaje.getLength() - 5; i++) {

			if ((mensaje[i] > 47 && mensaje[i] < 58) || (mensaje[i] > 64 && mensaje[i] < 91)
					|| (mensaje[i] > 96 && mensaje[i] < 123)) // validar que solo reciba caracteres y numeros
			{
				// System.out.println("linea "+ i + "mensaje "+(char)(mensaje[i]) ) ;
				aux[0] = (char) mensaje[i];
				System.out.println("linea " + i + "mensaje " + aux[0]);
				paginaPregunta = aux2.concat(new String(aux));
				aux2 = paginaPregunta;
			} else {
				if (i != 12) {
					paginaPregunta = aux2.concat(".");
					aux2 = paginaPregunta;
				}

			}

		}

		//guardar la pregunta
		System.arraycopy(mensaje, 12, this.pregunta, 0, PaqueteMensaje.getLength());
		System.out.println("tamano paquete " + PaqueteMensaje.getLength() + "   lo que asignamos a la pregunta " + (PaqueteMensaje.getLength()-10) );

		System.out.println("pagina buscada =" + paginaPregunta); // encuentra la pagina pero sin puntos
		// Desde 4 antes del tamano final porque hay un byte null despues del mensaje
		// que indica que finalizo este
		System.out.println(
				"Qtype " + (mensaje[PaqueteMensaje.getLength() - 4] | mensaje[PaqueteMensaje.getLength() - 3]));
		System.out.println(
				"QClass " + (mensaje[PaqueteMensaje.getLength() - 2] | mensaje[PaqueteMensaje.getLength() - 1]));
		System.out.println(" tamano " + PaqueteMensaje.getLength());
		System.out.println("Mensaje sin partir" + new String(PaqueteMensaje.getData()));

	}

	public void imprimirRespuestaInterna() {
		System.out.println("--------------------Dominio : " + this.getPaginaPregunta() + " -------------------------------");
		System.out.println("Se encontro el dominio en el MasterFile interno");
		System.out.println("Transaction ID: 0x" + String.format("%x",this.ID));
		System.out.println("Flags: " );
		System.out.println("Questions: " + String.format("%x", this.QDCount));
		System.out.println("Answers RR: "  );
		
	}
	public byte[] crearResInterna(HashMap<String, ArrayList<ResRR>> masterFile, DatagramPacket PaqueteMensaje) {
		hacerBodyRespuestaInterna(masterFile);
		hacerEncabezadoRespuesta(PaqueteMensaje);
		imprimirRespuestaInterna();
		System.out.println("aaaaaaaaaaaaaa " + this.cuerpo);
		byte[] combined = new byte[this.encabezado.length + this.pregunta.length + this.cuerpo.length+100];
		System.arraycopy(this.encabezado, 0, combined, 0, this.encabezado.length);
		System.arraycopy(this.pregunta, 0, combined, this.encabezado.length, this.pregunta.length-12);
		System.arraycopy(this.cuerpo, 0, combined, this.pregunta.length, this.cuerpo.length);
		return combined;
	}

	public void hacerBodyRespuestaInterna(HashMap<String, ArrayList<ResRR>> masterFile) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(out);
		//Encuentra las respuestas que coincidan con el dominio
		ArrayList<ResRR> rec = masterFile.get(this.paginaPregunta);
		//Saca todas las respuestas del masterFile
		for (ResRR actual : rec) {
			try {
				
				data.write(actual.toByte());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.ANCount=(short)rec.size();
		System.out.println(out + "+++++++++++++++");
		this.cuerpo = out.toByteArray();
		//this.cuerpo= rec.get(0).toByte();
		System.out.println("tamano cuerpo  " + this.cuerpo.length);
	}

	public void hacerEncabezadoRespuesta(DatagramPacket PaqueteMensaje) { // guarda el ID del encabezado de la pregunta
																			// que le llega
		byte[] mensajePregunta = PaqueteMensaje.getData();
		this.leerMensajePregunta(PaqueteMensaje);
		System.out.println(
				"_________________________________________________Respuesta Mensaje __________________________________________________________");
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
			this.encabezado[3] = (byte) (this.encabezado[3] | 128);// RD
		}
		this.encabezado[3] = (byte) (this.encabezado[3] | this.RCode);// RCode se debe cambiar si depronto
		this.encabezado[4] = mensajePregunta[4]; // QDCount
		this.encabezado[5] = mensajePregunta[5]; // QDCount 2
		this.encabezado[6] = (byte) (this.ANCount & 0xff00); //ANCount 
		this.encabezado[7] = (byte) (this.ANCount & 0xff);//ANCount 2
		this.encabezado[8] = 0;// NSCount
		this.encabezado[9] = 0;//NSCount 2
		this.encabezado[10] = 0;//ARCcount
		this.encabezado[11] = 0;//ARCount2
		// 

		//
		//

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
		System.out.println(aux);
		System.out.println(Long.toBinaryString((long) aux));
		// se guarda el primer byte de los flags desde QR a RD
		this.encabezado[2] = aux;

		if (this.RA) {
			aux2 = (byte) 128;
		}
		aux2 = (byte) (aux2 | this.RCode);
		System.out.println(aux2);
		System.out.println(Long.toBinaryString((long) aux2));
		this.encabezado[3] = aux2;
	}

	public short convertToShort(byte[] name) {

		ByteBuffer buffer = ByteBuffer.wrap(name);
		return buffer.getShort();
	}

	public short getID() {
		return ID;
	}

	public void setID(short iD) {
		ID = iD;
	}

	public boolean isQR() {
		return QR;
	}

	public void setQR(boolean qR) {
		QR = qR;
	}

	public byte getOpCode() {
		return OpCode;
	}

	public void setOpCode(byte opCode) {
		OpCode = opCode;
	}

	public boolean isAA() {
		return AA;
	}

	public void setAA(boolean aA) {
		AA = aA;
	}

	public boolean isTC() {
		return TC;
	}

	public void setTC(boolean tC) {
		TC = tC;
	}

	public boolean isRD() {
		return RD;
	}

	public void setRD(boolean rD) {
		RD = rD;
	}

	public boolean isRA() {
		return RA;
	}

	public void setRA(boolean rA) {
		RA = rA;
	}

	public byte getZ() {
		return Z;
	}

	public void setZ(byte z) {
		Z = z;
	}

	public byte getRCode() {
		return RCode;
	}

	public void setRCode(byte rCode) {
		RCode = rCode;
	}

	public short getQDCount() {
		return QDCount;
	}

	public void setQDCount(short qDCount) {
		QDCount = qDCount;
	}

	public short getANCount() {
		return ANCount;
	}

	public void setANCount(short aNCount) {
		ANCount = aNCount;
	}

	public short getNSCount() {
		return NSCount;
	}

	public void setNSCount(short nSCount) {
		NSCount = nSCount;
	}

	public short getARCount() {
		return ARCount;
	}

	public void setARCount(short aRCount) {
		ARCount = aRCount;
	}

	public byte[] getEncabezado() {
		return encabezado;
	}

	public void setEncabezado(byte[] encabezado) {
		this.encabezado = encabezado;
	}

	public String getPaginaPregunta() {
		return paginaPregunta;
	}

	public void setPaginaPregunta(String paginaPregunta) {
		this.paginaPregunta = paginaPregunta;
	}
}
