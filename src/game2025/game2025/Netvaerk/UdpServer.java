package game2025.game2025.Netvaerk;

import javafx.scene.chart.PieChart;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UdpServer {
    private static DatagramSocket datagramSocket;
    private static Object lock = new Object();

    private static byte[] receiveBuffer = new byte[1024];
    private static byte[] sendBuffer = new byte[1024];

    public static void main(String[] args) {
        try{
            datagramSocket = new DatagramSocket(12_005);

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
        DatagramPacket datagramPacket = new DatagramPacket(receiveBuffer, 0, receiveBuffer.length);

        while(true){
            try {
                datagramSocket.receive(datagramPacket);
                System.out.println("Reading from cliend...");
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

    private static void broadcastToClients(){
        System.out.println("broadcast open");
        try {
            datagramSocket.setBroadcast(true);
            DatagramPacket datagramPacket;

            while (true){
                if (!queue.isEmpty()){
                    String messageFromQueue = removeFromQueue();
                    System.out.println("Broadcasting : " + messageFromQueue);
                    sendBuffer = messageFromQueue.getBytes();

                    datagramPacket =
                            new DatagramPacket(
                                    sendBuffer,
                                    sendBuffer.length,
                                    InetAddress.getByName("255.255.255.255"),
                                    12000
                            );
                    datagramSocket.send(datagramPacket);
                    sendBuffer = new byte[1024];
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
