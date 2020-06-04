import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPMulticastClient implements Runnable
{

    public static void main(String[] args)
    {
        Thread t = new Thread(new UDPMulticastClient());
        t.start();
    }

    public void receiveUDPMessage(String ip, int port) throws IOException
    {
        byte[] buffer = new byte[1024];
		
        MulticastSocket socket = new MulticastSocket(4321);
		
        InetAddress group = InetAddress.getByName("230.0.0.0");
		
        socket.joinGroup(group);
		
        while(true)
        {
            System.out.println("Waiting for multicast message...");
			
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
            socket.receive(packet);
			
            String msg = new String(packet.getData(), packet.getOffset(),packet.getLength());
			
            System.out.println("[Multicast UDP message received] >> " + msg);
			
            if("OK".equals(msg))
            {
                System.out.println("No more message. Exiting : " + msg);
                break;
            }
        }
		
        socket.leaveGroup(group);
        socket.close();
    }

    @Override
    public void run()
    {
        try
        {
            receiveUDPMessage("230.0.0.0", 4321);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPMulticastServer
{

	public static void sendUDPMessage(String message, String ipAddress, int port) throws IOException 
	{
		DatagramSocket socket = new DatagramSocket();
		
		InetAddress group = InetAddress.getByName(ipAddress);
		
		byte[] msg = message.getBytes();
		
		DatagramPacket packet = new DatagramPacket(msg, msg.length, group, port);
		
		socket.send(packet);
		
		socket.close();
	}

	public static void main(String[] args) throws IOException
	{
		sendUDPMessage("This is a multicast messge", "230.0.0.0", 4321);
		
		sendUDPMessage("This is the second multicast messge", "230.0.0.0", 4321);
		
		sendUDPMessage("This is the third multicast messge", "230.0.0.0", 4321);
		
		sendUDPMessage("OK", "230.0.0.0", 4321);
	}
}

