package game2025.game2025.serverSide;

public class ServerController {
    public static void main(String[] args) {
        Thread tcpConnectionThread = new Thread(TcpConnection::tcpThread);
        Thread writeThread = new Thread();
        //Thread udpSendAndReceive = new Thread(UdpServer::udpSendAndReceive);

        tcpConnectionThread.start();
        //udpSendAndReceive.start();
    }

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
}
