package proyectoservidordns;

import java.net.DatagramPacket;

public class HeaderFormat {
    short ID; //2 bytes
    // FLAGS
    boolean QR; // 1 bit            |
    byte   OpCode; // son 4 bits    |
    boolean AA; // 1 bit            |
    boolean TC;// 1 bit             |1 byte
    boolean RD;// 1 bit             |________________
    boolean RA; // 1 bit            |
    byte Z; // 3 bits los 3 en 0    | 1 byte
    byte RCode; //4 bits            |
    /// FLAGS
    short QDCount;
    short ANCount;
    short NSCount;
    short ARCount;
    // Header en byte
    byte[] encabezado;
    public HeaderFormat() {
        //se crea con 12 porque un byte almacena 8 bits y se necesitan 6 espacios de 16 bits
        encabezado= new byte[12];
        this.QR= true;
        this.OpCode=1;
        this.AA=false;
        this.TC=false;
        this.RD=false;
        this.RA=true;
        this.Z=0;
        this.RCode=1;

    }

    
    public void setFlags(byte[] mensaje )
    {
        short aux=(short) (mensaje[2] & 0x80);
        this.QR= aux== 0x80 ? true : false;
        aux= (short) (mensaje[2] &0x78);
        this.OpCode= (byte) (aux>>3);
        System.out.println("prueba" + QR + " flag op code= "+ OpCode);
        
        
        
    }
    public void leerMensajePregunta(DatagramPacket PaqueteMensaje)
    {
        byte[] mensaje= PaqueteMensaje.getData();
        System.out.println("_________________________________________________Mensaje entrante __________________________________________________________");
        System.out.println("se imprime header");
        System.out.println("ID "+ (mensaje[0] | mensaje[1]));
        System.out.println("QR "+  (mensaje[2]& 128) );
        System.out.println("OpCode "+(mensaje[2]& 120) );
        System.out.println("AA "+  (mensaje[2]& 4) );
        System.out.println("TC "+  (mensaje[2]& 2) );
        System.out.println("RD "+  (mensaje[2]& 1) );
        System.out.println("RA "+  (mensaje[3]& 128) );
        System.out.println("Z "+  (mensaje[3]& 112) );
        System.out.println("RCode "+  (mensaje[3]& 15) );
        System.out.println("QDCount "+  (mensaje[4] | mensaje [5]) );
        System.out.println("ANCount "+  (mensaje[6] | mensaje [7]) );;
        System.out.println("NSCount "+  (mensaje[8] | mensaje [9]) );
        System.out.println("ARCount "+  (mensaje[10] | mensaje [11]) );
        System.out.println("Fin del encabezado");
        System.out.println("Inicio de la query");
        for(int i=12; i<PaqueteMensaje.getLength()-5 ; i++)
        {
                System.out.println("linea "+ i + "mensaje "+(char)(mensaje[i])  ) ;

        }
        //Desde 4 antes del tamano final porque hay un byte null despues del mensaje que indica que finalizo este
        System.out.println("Qtype " + (mensaje[PaqueteMensaje.getLength()-4] | mensaje[PaqueteMensaje.getLength()-3]));
        System.out.println("QClass " + (mensaje[PaqueteMensaje.getLength()-2] | mensaje[PaqueteMensaje.getLength()-1]));
        System.out.println(" tamano " + PaqueteMensaje.getLength());
        System.out.println("Mensaje sin partir"+new String (PaqueteMensaje.getData()));


    }

    public void hacerEncabezado( byte[] mensajePregunta)
    {       // guarda el ID del encabezado de la pregunta que le llega
        this.encabezado[0]=mensajePregunta[0];
        this.encabezado[1]=mensajePregunta[1];
        System.out.println(this.encabezado.length );
        //
        //
        this.hacerFlags();



    }
    public void hacerFlags()
    {
        byte aux=0;
        byte aux2=0;
        if(this.QR)
        {
            //1000 0000
            aux=(byte) 128;
        }
        //para que se compare en la posicion correcta
        //1_ _ _  _000
        this.OpCode= (byte) (this.OpCode<<3);

        aux=(byte) (aux|this.OpCode);
        if(this.AA)
        {   //1000 0_00
            aux=(byte)(aux|4);
        }
        if(this.TC)
        {
            //1000 00_0
            aux=(byte)(aux|2);
        }
        if(this.RD)
        {
            //1000 00_0
            aux=(byte)(aux|1);

        }
        System.out.println(aux );
        System.out.println(Long.toBinaryString((long) aux));
        //se guarda el primer byte de los flags desde QR a RD
        this.encabezado[2]=aux;

        if(this.RA)
        {
            aux2=(byte)128;
        }
        aux2=(byte)(aux2 | this.RCode);
        System.out.println(aux2);
        System.out.println(Long.toBinaryString((long) aux2));
        this.encabezado[3]=aux2;
    }

    
}
