package gurinovich.java;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class MainFrame extends JFrame
{
    private JTextArea History;
    private JTextField WhoText;
    private JLabel WhereToText;
    private JTextArea Message;

    private int PORT = 4567;

    private int WIDTH = 400, HEIGHT = 500;

    public int getWIDTH()
    {
        return WIDTH;
    }
    public int getHEIGHT()
    {
        return HEIGHT;
    }
    public String getUserName()
    {
        return WhoText.getText();
    }
    public void AddToHistory(String s)
    {
        History.append(s + "\n");
    }

    public void setWhereToText(String whereToText)
    {
        WhereToText.setText(whereToText);
    }

    public MainFrame()
    {
        super("idk");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        setSize(WIDTH, HEIGHT);
        setLocation((toolkit.getScreenSize().width - WIDTH) / 2, (toolkit.getScreenSize().height - HEIGHT) / 2);

//////////
        History = new JTextArea();
        History.setEditable(false);
        History.setLineWrap(true);
        ((DefaultCaret)History.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        History.setMaximumSize(History.getPreferredSize());

        JScrollPane HistoryScroll = new JScrollPane(History);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Message"));

        JLabel WhoLabel = new JLabel("Who ");
        WhoText = new JTextField(9);
        WhoText.setMaximumSize(WhoText.getPreferredSize());
        WhoText.setText("username");

        JLabel WhereToLabel = new JLabel("Where To ");
        WhereToText = new JLabel();
        WhereToText.setText("Choose one user");
        WhereToText.setMaximumSize(WhereToText.getPreferredSize());

        Message = new JTextArea();
        Message.setLineWrap(true);

        JScrollPane MessageScroll = new JScrollPane(Message);

        JButton SendButton = new JButton("Send");

        SendButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                SendMessage();
            }
        });


//////////View
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(HistoryScroll)
                .addComponent(panel));
        layout.setVerticalGroup(layout
                .createSequentialGroup()
                .addComponent(HistoryScroll)
                .addComponent(panel));

        GroupLayout MessageLayout = new GroupLayout(panel);
        panel.setLayout(MessageLayout);

        MessageLayout.setAutoCreateGaps(true);
        MessageLayout.setAutoCreateContainerGaps(true);

        MessageLayout.setHorizontalGroup(MessageLayout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(MessageLayout
                        .createSequentialGroup()
                        .addComponent(WhoLabel)
                        .addComponent(WhoText)
                        .addComponent(WhereToLabel)
                        .addComponent(WhereToText))
                .addComponent(MessageScroll)
                .addComponent(SendButton));

        MessageLayout.setVerticalGroup(MessageLayout
                .createSequentialGroup()
                .addGroup(MessageLayout
                        .createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(WhoLabel)
                        .addComponent(WhoText)
                        .addComponent(WhereToLabel)
                        .addComponent(WhereToText))
                .addComponent(MessageScroll)
                .addComponent(SendButton));

//////////handler
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    ServerSocket SS = new ServerSocket(PORT);

                    while(!Thread.interrupted())
                    {
                        Socket socket = SS.accept();

                        DataInputStream in = new DataInputStream(socket.getInputStream());

                        String From = in.readUTF();
                        String Msg = in.readUTF();

                        in.close();
                        socket.close();

                        String AdressFrom = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().getHostAddress();

                        History.append(From + " -> " + Msg + "\n");
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

        }).start();

        UserList frame2 = new UserList(this);
        frame2.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame2.setVisible(true);

    }

    public void SendMessage()
    {
        String Who = WhoText.getText();
        String Where = WhereToText.getText();
        String Msg = Message.getText();

        if (Who.isEmpty())
        {
            Who = JOptionPane.showInputDialog(this, "Who?", "Who?", JOptionPane.QUESTION_MESSAGE);
            WhoText.setText(Who);
        }
        if (Where.equals("Choose one user"))
        {
            JOptionPane.showMessageDialog(this, "Choose one of the users", "Recipient not chosen", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (Msg.isEmpty())
        {
            return;
        }

        if (Who.length() > 13)
        {
            JOptionPane.showMessageDialog(this, "You cannot use name taller than 13 characters", "Wrong length", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Msg = Msg.trim();

        try
        {
            Socket socket = new Socket(Where, PORT);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(Who);
            out.writeUTF(Msg);

            out.close();
            socket.close();

            History.append(Who + " -> " + Msg + "\n");
            Message.setText("");
        }
        catch(IOException e)
        {
            JOptionPane.showMessageDialog(this, "Not possible to send to " + Where, "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args)
    {
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}




