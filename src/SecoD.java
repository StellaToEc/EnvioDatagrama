import java.net.*;
import java.io.*;

public class SecoD {
    public static void main(String[] args){
        try{
            int pto=8002;
            String msj;
            DatagramSocket s = new DatagramSocket(pto);
            s.setReuseAddress(true);
            System.out.println("Servidor iniciado... espedando datagramas..");
            for(;;){
                byte[] b = new byte[65535];
                DatagramPacket p = new DatagramPacket(b,b.length);
                s.receive(p);
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(p.getData()));
                int n = dis.readInt();
                int tam = dis.readInt();
                byte[] b1 = new byte[tam];
                dis.read(b1);
                msj = new String(b1);
                System.out.println("Se ha recibido datagrama " +n+ " desde "+p.getAddress()+":"+p.getPort()+" con el mensaje:"+msj);
                s.send(p);
                dis.close();
            }//for
        }catch(Exception e){
            e.printStackTrace();
        }//catch
    }//main
}








