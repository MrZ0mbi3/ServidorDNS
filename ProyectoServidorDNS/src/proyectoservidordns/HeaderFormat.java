package proyectoservidordns;

public class HeaderFormat {
    short ID;
    boolean QR;
    byte OpCode;
    boolean AA;
    boolean TC;
    boolean RD;
    boolean RA;
    byte Z;
    byte RCode;
    short QDCount;
    short ANCount;
    short NSCount;
    short ARCount;
    
    public HeaderFormat() {
    }

    public HeaderFormat(short iD, boolean qR, byte opCode, boolean aA, boolean tC, boolean rD, boolean rA, byte z,
            byte rCode, short qDCount, short aNCount, short nSCount, short aRCount) {
        ID = iD;
        QR = qR;
        OpCode = opCode;
        AA = aA;
        TC = tC;
        RD = rD;
        RA = rA;
        Z = z;
        RCode = rCode;
        QDCount = qDCount;
        ANCount = aNCount;
        NSCount = nSCount;
        ARCount = aRCount;
    }
    
}
