package gurinovich.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class UserList extends JFrame
{
    private int WIDTH = 150, HEIGHT = 500;

    private int PORT = 8888;
    private String Address = "224.0.0.3";
    private InetAddress Addr;
    private String LocalAddress;
    private MainFrame parent;

    public UserList(MainFrame mainframe)
    {
        super("idk2");
        setSize(WIDTH, HEIGHT);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        setLocation((toolkit.getScreenSize().width + mainframe.getWIDTH()) / 2, (toolkit.getScreenSize().height - mainframe.getHEIGHT()) / 2);

        parent = mainframe;

        try
        {
            Addr = InetAddress.getByName(Address);
            LocalAddress = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {}

//////////server
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (!Thread.interrupted())
                {
                    try
                    {
                        String msg = mainframe.getUserName();
                        DatagramSocket socket = new DatagramSocket();
                        DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), Addr, PORT);
                        socket.send(packet);
                        //mainframe.AddToHistory("send");
                        socket.close();
                        Thread.sleep(1000);
                    }
                    catch (SocketException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

//////////client
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    MulticastSocket socket = new MulticastSocket(PORT);
                    socket.joinGroup(Addr);
                    byte[] buffer = new byte[1024];
                    while (!Thread.interrupted())
                    {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
                        String From = packet.getAddress().getHostAddress();

                        //if (From.equals(LocalAddress))
                          //  continue;

                        User(msg, From);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

        TimeoutTimer.start();
    }

    private ArrayList<String> Names = new ArrayList<String>();
    private ArrayList<String> Addresses = new ArrayList<String>();
    private ArrayList Timeout = new ArrayList();

    private Timer TimeoutTimer = new Timer(1000, new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            synchronized (Timeout)
            {
                for (int i = 0; i < Timeout.size(); i++)
                {
                    if ((int) Timeout.get(i) == 4)
                    {
                        Names.remove(i);
                        Addresses.remove(i);
                        Timeout.remove(i);
                        repaint();
                    }
                    else
                        Timeout.set(i, (int) Timeout.get(i) + 1);
                }
            }
        }
    });

    public void User(String name, String address)
    {
        boolean Exist = false;

        for (int i = 0; i < Names.size(); i++)
        {
            if (Addresses.get(i).equals(address))
            {
                if (!Names.get(i).equals(name))
                {
                    Names.set(i, name);
                    repaint();
                }
                Exist = true;
                synchronized (Timeout)
                {
                    Timeout.set(i, 0);
                }
            }
        }

        if (!Exist)
        {
            Names.add(name);
            Addresses.add(address);
            Timeout.add(0);
            repaint();
        }
    }

    @Override
    public void repaint()
    {
        super.repaint();

        getContentPane().removeAll();

        JPanel panel = new JPanel();

        Box All = Box.createVerticalBox();

        for (int i = 0; i < Names.size(); i++)
        {
            JLabel label = new JLabel("<html><h4 align=\"center\">" + (int)(i + 1) + ". " + Names.get(i) + "</h4></html>");
            label.addMouseListener(new UserMouseAdapter(Addresses.get(i)));
            All.add(label);
        }

        panel.add(All);

        getContentPane().add(panel);
        getContentPane().revalidate();
    }

    //private UserMouseAdapter adapter = new UserMouseAdapter();

    private class UserMouseAdapter extends MouseAdapter
    {
        private String address;
        public UserMouseAdapter(String address)
        {
            this.address = address;
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            parent.setWhereToText(address);
        }
    }

}
