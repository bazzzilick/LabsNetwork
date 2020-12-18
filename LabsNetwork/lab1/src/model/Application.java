package model;

import java.io.IOException;
import java.net.*;

public class Application implements Runnable{

    private MulticastSocket multicastSocketRecv = null;
    private MulticastSocket multicastSocketSend = null;
    private String adress;
    private int port = 1234;
    private InetAddress group;
    private byte[] bufSend = new byte[256];
    private long timeout = 3000;
    private byte[] bufRecv = new byte[256];
    private Storage storage = new Storage();


    Application(String adress) {
        this.adress = adress;
    }

    public static void main(String[] args) {
        Application application = new Application(args[0]);
        new Thread(application).start();
    }

    @Override
    public void run() {
        try {
            multicastSocketRecv = new MulticastSocket(port);
            group = InetAddress.getByName(adress);
            multicastSocketRecv.joinGroup(group);
            multicastSocketRecv.setSoTimeout(2000);

            multicastSocketSend = new MulticastSocket();
            String message = "Hello, group!";
            bufSend = message.getBytes();

            while (true) {
                DatagramPacket packetSend = new DatagramPacket(bufSend, bufSend.length, group, port);
                multicastSocketSend.send(packetSend);

                long startTime = System.currentTimeMillis();
                long endTime = System.currentTimeMillis();
                while (endTime - startTime <= timeout) {
                    DatagramPacket packetRecv = new DatagramPacket(bufRecv, bufRecv.length);
                    try {
                        multicastSocketRecv.receive(packetRecv);
                    } catch (SocketTimeoutException ign) {
                        endTime = System.currentTimeMillis();
                        continue;
                    }
                    storage.add(packetRecv.getAddress().getHostAddress() + ":" + packetRecv.getPort());
                    endTime = System.currentTimeMillis();
                }

                storage.checkStorage();
                storage.clean();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                multicastSocketRecv.leaveGroup(group);
            } catch (IOException e) {
                e.printStackTrace();
            }
            multicastSocketRecv.close();
            multicastSocketSend.close();
        }
    }
}
