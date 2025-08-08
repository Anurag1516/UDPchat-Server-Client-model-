# UDP Chat Application (Java)

A simple **multi-client UDP chat system** written in Java.  
This project demonstrates **socket programming** using `DatagramSocket` and `DatagramPacket` to send and receive messages between multiple clients through a server.

---

## 📌 Features
- **Server Mode**:
  - Receives messages from clients.
  - Keeps track of all connected clients.
  - Broadcasts incoming messages to all clients except the sender.
- **Client Mode**:
  - Sends messages to the server.
  - Receives broadcast messages from the server in real-time.
- **Multiple Clients Supported** – all clients connected to the server will see each other's messages.
- **Simple Command** to exit chat: type `bye`.

---

## 🛠️ Technologies Used
- **Java** – Core Java networking (`java.net`) and I/O classes.
- **UDP Protocol** – For lightweight, connectionless message exchange.
- **Threads** – To handle sending and receiving messages concurrently.

---

## 📂 Project Structure
