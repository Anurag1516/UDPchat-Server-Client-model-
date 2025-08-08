import java.net.*;
import java.io.*;
import java.util.*;

class UDPChatServer {
    private final int port;
    private final List<InetAddress> clientAddresses = new ArrayList<>();
    private final List<Integer> clientPorts = new ArrayList<>();

    public UDPChatServer(int port) {
        this.port = port;
    }

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(port);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            Thread messageReceiver = new Thread(() -> receiveMessages(socket));
            messageReceiver.start();

            String message;
            while (!(message = userInput.readLine()).equalsIgnoreCase("bye")) {
                broadcastMessage(socket, message, null, -1);
            }

            messageReceiver.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages(DatagramSocket socket) {
        byte[] data = new byte[1024];

        while (!Thread.currentThread().isInterrupted()) {
            try {
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                int length = packet.getLength();

                String message = new String(data, 0, length);
                System.out.println("Received: " + message + " from " + address + ":" + port);

                updateClientList(address, port);
                broadcastMessage(socket, message, address, port);
            } catch (IOException e) {
                if (Thread.currentThread().isInterrupted()) break;
                e.printStackTrace();
            }
        }
    }

    private void updateClientList(InetAddress address, int port) {
        // FIX: Ensure both address and port are matched before adding
        if (!(clientAddresses.contains(address) && clientPorts.contains(port))) {
            clientAddresses.add(address);
            clientPorts.add(port);
        }
    }

    private void broadcastMessage(DatagramSocket socket, String message, InetAddress senderAddress, int senderPort) {
        try {
            byte[] data = message.getBytes();
            for (int i = 0; i < clientAddresses.size(); i++) {
                if (senderAddress == null || !clientAddresses.get(i).equals(senderAddress) || clientPorts.get(i) != senderPort) {
                    DatagramPacket broadcastPacket = new DatagramPacket(data, data.length, clientAddresses.get(i), clientPorts.get(i));
                    socket.send(broadcastPacket);
                }
            }
            System.out.println("Broadcasted: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class UDPChatClient {
    private final InetAddress serverAddress;
    private final int serverPort;

    public UDPChatClient(InetAddress serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in);
             DatagramSocket socket = new DatagramSocket()) {

            Thread receiverThread = new Thread(() -> receiveBroadcast(socket));
            receiverThread.start();

            while (true) {
                System.out.print("Enter a message to send to the server: ");
                String message = scanner.nextLine();

                if ("bye".equalsIgnoreCase(message)) break;

                byte[] data = message.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, serverPort);
                socket.send(packet);
            }

            receiverThread.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveBroadcast(DatagramSocket socket) {
        byte[] data = new byte[1024];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(data, data.length);
                socket.receive(receivePacket);

                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Broadcast from server: " + receivedMessage);
            } catch (IOException e) {
                if (Thread.currentThread().isInterrupted()) break;
                e.printStackTrace();
            }
        }
    }
}

public class UDPChatApp {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter 1 for server mode or 2 for client mode:");
            int mode = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (mode == 1) {
                UDPChatServer server = new UDPChatServer(3000);
                System.out.println("Chat server is started - Waiting for messages from clients!!");
                server.run();
            } else if (mode == 2) {
                System.out.println("Enter server IP address:");
                String serverIP = scanner.nextLine();
                InetAddress serverAddress = InetAddress.getByName(serverIP);
                UDPChatClient client = new UDPChatClient(serverAddress, 3000);
                System.out.println("Chat client is started.");
                client.run();
            } else {
                System.out.println("Invalid Choice. Exiting the Program.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
