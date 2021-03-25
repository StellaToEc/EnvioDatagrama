import java.net.*;
import java.io.*;
import java.util.Arrays;

public class CecoD {

    public static void main(String[] args){

        try{
            int aux=0;
            int pto=8002;
            String dir="127.0.0.1";
            InetAddress dst= InetAddress.getByName(dir);
            int tam = 10;
            BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket cl = new DatagramSocket();
            while(true){
                System.out.println("Escribe un mensaje, <Enter> para enviar, \"salir\" para terminar");
                String msj = br.readLine();
                if(msj.compareToIgnoreCase("salir")==0){
                    System.out.println("termina programa");
                    br.close();
                    cl.close();
                    System.exit(0);
                }else{
                    byte[]b = msj.getBytes();
                    if(b.length>tam){
                        byte[]b_eco = new byte[b.length];
                        System.out.println("b_eco: "+b_eco.length+" bytes");
                        int tp = b.length/tam;

                        for(int j=0;j<tp;j++){
                            byte []tmp=Arrays.copyOfRange(b, j*tam, ((j*tam)+(tam)));
                            System.out.println("\ntmp tam "+tmp.length);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            DataOutputStream dos = new DataOutputStream(baos);
                            dos.writeInt(j+1);
                            dos.writeInt(tmp.length);
                            dos.write(tmp);
                            dos.flush();
                            byte[] b1 = baos.toByteArray();
                            DatagramPacket pack= new DatagramPacket(b1, b1.length, dst , pto);
                            cl.send(pack);
                            System.out.println("Enviando fragmento "+(j+1)+" de "+tp+"\ndesde:"+(j*tam)+" hasta "+((j*tam)+(tam)));

                            String tama  = new String (tmp);
                            System.out.println("Segmento enviado: " + tama);

                            byte[] sr= recibirDatos(cl);

                            for(int i=0; i<tam;i++){
                                b_eco[(j*tam)+i]=sr[i];
                            }//for

                            aux=j+2;
                        }//for
                        if(b.length%tam>0){ //bytes sobrantes
                            int sobrantes = b.length%tam;
                            System.out.println("\nsobrantes:"+sobrantes);

                            System.out.println("ultimo pedazo desde "+tp*tam+" hasta "+((tp*tam)+sobrantes));
                            byte[] tmp = Arrays.copyOfRange(b, tp*tam, ((tp*tam)+sobrantes));

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            DataOutputStream dos = new DataOutputStream(baos);
                            dos.writeInt(aux);
                            dos.writeInt(tmp.length);
                            dos.write(tmp);
                            dos.flush();
                            byte[] b1 = baos.toByteArray();
                            DatagramPacket pack= new DatagramPacket(b1, b1.length, dst , pto);
                            cl.send(pack);

                            System.out.println("tmp tam "+tmp.length);
                            byte[] sr = recibirDatos(cl);
                            for(int i=0; i<sobrantes;i++){
                                b_eco[(tp*tam)+i]=sr[i];
                            }//for
                        }//if

                        String eco = new String(b_eco);

                        System.out.println("\nEco recibido: "+eco);
                    }else{
                        DatagramPacket p=new DatagramPacket(b,b.length,dst,pto);
                        cl.send(p);
                        DatagramPacket p1 = new DatagramPacket(new byte[65535],65535);
                        cl.receive(p1);
                        String eco = new String(p1.getData(),0,p1.getLength());
                        System.out.println("\nEco recibido: "+eco);
                    }//else
                }//else
            }//while
        }catch(Exception e){
            e.printStackTrace();
        }//catch
    }//main

    public static byte[] recibirDatos(DatagramSocket cl) throws IOException{
        DatagramPacket p = new DatagramPacket(new byte[65535],65535);
        cl.receive(p);
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(p.getData()));
        int num = dis.readInt();
        int tam = dis.readInt();
        String eco = new String(p.getData(), 0, p.getLength());
        byte[] data = new byte[tam];
        dis.read(data);
        return data;
    }
}
