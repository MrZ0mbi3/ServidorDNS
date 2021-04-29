package proyectoservidordns;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;


public class ResRR {

	private short nombre;
	private short tipo;
	private short clase;
	private int TLL;
	private short tama;
	private InetAddress address;

	public ResRR(short nombre, short tipo, short clase, int tLL, short tama, InetAddress address) {
		super();
		this.nombre = nombre;
		this.tipo = tipo;
		this.clase = clase;
		this.TLL = tLL;
		this.tama = tama;
		this.address = address;
	}

	public byte[] toByte() {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(out);

		try {
			data.writeShort(nombre);
			data.writeShort(tipo);
			data.writeShort(clase);
			data.writeInt(TLL);
			data.writeShort(tama);
			data.write(address.getAddress());
			return out.toByteArray();
		} catch (IOException e) {
			System.out.println("Error parsendo la respuesta del paquete.");
			return null;
		}


	}

	public short getName() {
		return nombre;
	}

	public void setName(short name) {
		this.nombre = name;
	}

	public short getTipo() {
		return tipo;
	}

	public void setTipo(short tipo) {
		this.tipo = tipo;
	}

	public short getClase() {
		return clase;
	}

	public void setClase(short clase) {
		this.clase = clase;
	}

	public int getTLL() {
		return TLL;
	}

	public void setTLL(int tLL) {
		this.TLL = tLL;
	}

	public short getLength() {
		return tama;
	}

	public void setLength(short length) {
		this.tama = length;
	}

	public InetAddress getAdress() {
		return address;
	}

	public void setAdress(InetAddress adress) {
		this.address = adress;
	}
}
