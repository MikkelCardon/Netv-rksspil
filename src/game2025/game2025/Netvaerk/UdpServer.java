package game2025.game2025.Netvaerk;

import javafx.scene.chart.PieChart;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UdpServer {
    private static DatagramSocket datagramSocket;

    public static void main(String[] args) {
        try{
            datagramSocket = new DatagramSocket(12_000);

            Thread readTråd = new Thread(UdpServer::readFromClient);
            Thread writeTråd = new Thread(UdpServer::broadcastToClients);

            readTråd.start();
            writeTråd.start();

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static LinkedList<String> queue = new LinkedList();

    private static void readFromClient(){
        System.out.println("Now reading from client");
        byte[] buffer = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, buffer.length);

        while(true){
            try {
                datagramSocket.receive(datagramPacket);
                System.out.println("Reading from cliend...");
                String messageFromClient = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                System.out.println(messageFromClient);
                addToQueue(messageFromClient);
                buffer = new byte[1024];
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private synchronized static void addToQueue(String message){
        queue.addFirst(message);
    }

    private synchronized static String removeFromQueue(){
        return queue.removeLast();
    }

    private static void broadcastToClients(){
        System.out.println("broadcast open");
        try {
            datagramSocket.setBroadcast(true);
            byte[] buffer = new byte[1024];
            DatagramPacket datagramPacket =
                    new DatagramPacket(
                            buffer,
                            buffer.length,
                            InetAddress.getByName("255.255.255.255"),
                            12000
                    );

            while (true){
                if (!queue.isEmpty()){
                    String messageFromQueue = removeFromQueue();
                    System.out.println("Broadcasting : " + messageFromQueue);
                    buffer = messageFromQueue.getBytes();
                    datagramSocket.send(datagramPacket);
                    buffer = new byte[1024];
                }
            }


        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
