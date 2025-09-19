package game2025.game2025.serverSide;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;

import static game2025.game2025.serverSide.ServerController.BLUE;
import static game2025.game2025.serverSide.ServerController.RESET;

public class TcpWrite {
    private static DatagramSocket datagramSocket;
    private static Object lock = new Object();

    private static byte[] receiveBuffer = new byte[1024];
    private static byte[] sendBuffer = new byte[1024];

    public static void udpSendAndReceive(){

    }

    private static LinkedList<Object> queue = new LinkedList();

    private synchronized static void addToQueue(String message){
        synchronized (lock){
            queue.addFirst(message);
        }
    }

    private synchronized static Object removeFromQueue(){
        synchronized (lock){
            return queue.removeLast();
        }
    }

    private static void writeToClient(){
        try {
            while (true){
                if (!queue.isEmpty()){
                    Object messageFromQueue = removeFromQueue();
                    System.out.println(BLUE + "Broadcasting : " + messageFromQueue + RESET);


                }else {
                    Thread.sleep(1);
                }
            }


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
