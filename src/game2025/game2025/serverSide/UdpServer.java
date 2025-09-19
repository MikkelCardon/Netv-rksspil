package game2025.game2025.serverSide;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import static game2025.game2025.serverSide.ServerController.*;

public class UdpServer {
    private static DatagramSocket datagramSocket;
    private static Object lock = new Object();

    private static byte[] receiveBuffer = new byte[1024];
    private static byte[] sendBuffer = new byte[1024];

    public static void udpSendAndReceive(){
        try{
            datagramSocket = new DatagramSocket(10_005);

            Thread readTråd = new Thread(UdpServer::readFromClient);
            Thread writeTråd = new Thread(UdpServer::sendToClient);

            readTråd.start();
            writeTråd.start();

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
    private static List<String> clientIps = new ArrayList<>();
    private static LinkedList<String> queue = new LinkedList();

    public static void addClient(InetAddress inetAddress){
        String ip = inetAddress.toString().substring(1);
        if (!clientIps.contains(ip)){
            clientIps.add(ip);
        }
    }

    private static void readFromClient(){
        System.out.println("UDP reading thread listening");
        DatagramPacket datagramPacket = new DatagramPacket(receiveBuffer, 0, receiveBuffer.length);

        while(true){
            try {
                datagramSocket.receive(datagramPacket);

                System.out.println("Reading from client...");

                String messageFromClient = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                System.out.println(messageFromClient);
                addToQueue(messageFromClient);
                receiveBuffer = new byte[1024];
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private synchronized static void addToQueue(String message){
        synchronized (lock){
            queue.addFirst(message);
        }
    }

    private synchronized static String removeFromQueue(){
        synchronized (lock){
            return queue.removeLast();
        }
    }

    private static void sendToClient(){
        System.out.println("broadcast open");
        try {
            DatagramPacket datagramPacket;

            while (true){
                if (!queue.isEmpty()){
                    String messageFromQueue = removeFromQueue();
                    System.out.println(BLUE + "Broadcasting : " + messageFromQueue + RESET);
                    sendBuffer = messageFromQueue.getBytes();

                    for (String clientIp : clientIps) {
                        datagramPacket =
                                new DatagramPacket(
                                        sendBuffer,
                                        sendBuffer.length,
                                        InetAddress.getByName(clientIp),
                                        10000
                                );
                        datagramSocket.send(datagramPacket);
                    }

                    sendBuffer = new byte[1024];
                }else {
                    Thread.sleep(1);
                }
            }


        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
