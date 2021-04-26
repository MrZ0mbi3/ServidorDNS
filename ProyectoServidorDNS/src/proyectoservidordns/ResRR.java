package proyectoservidordns;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;


public class ResRR {

	private short name;
	private short tipo;
	private short clase;
	private int TLL;
	private short length;
	private InetAddress address;

	public ResRR(short name, short tipo, short clase, int tLL, short length, InetAddress address) {

		super();
		this.name = name;
		this.tipo = tipo;
		this.clase = clase;
		this.TLL = tLL;
		this.length = length;
		this.address = address;
	}

	public byte[] toByte() {

		/*ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(out);

		try {
			data.writeShort(name);
			data.writeShort(tipo);
			data.writeShort(clase);
			data.writeInt(TLL);
			data.writeShort(length);
			data.write(address.getAddress());

			return out.toByteArray();
		} catch (IOException e) {
			System.out.println("Error parsendo la respuesta del paquete.");
			return null;
		}*/

		byte[] devolver = new byte[16];
		devolver[0]=(byte) (this.name |0xf0);
		devolver[1]= (byte) (this.name | 0xf);//nombre
		devolver[2]=(byte) (this.tipo |0xf0);
		devolver[3]=(byte) (this.tipo |0xf);//tipo
		devolver[4]= (byte) (this.clase | 0xf0);
		devolver[5]=(byte) (this.clase | 0xf);//clase
		devolver[6]=(byte) (this.TLL | 0xf000);
		devolver[7]=(byte) (this.TLL | 0xf00);
		devolver[8]=(byte) (this.TLL | 0xf0);
		devolver[9]=(byte) (this.TLL | 0xf);//ttl
		devolver[10]=(byte) (this.length |0xf0);
		devolver[11]=(byte) (this.length |0xf);//lenght
		System.arraycopy(this.address.getAddress(), 0, devolver, 12, 4);
		return devolver;

	}

	public short getName() {
		return name;
	}

	public void setName(short name) {
		this.name = name;
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
		return length;
	}

	public void setLength(short length) {
		this.length = length;
	}

	public InetAddress getAdress() {
		return address;
	}

	public void setAdress(InetAddress adress) {
		this.address = adress;
	}
}
