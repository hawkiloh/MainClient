package Game.Client;

import Util.GameConstants;
import Util.MyUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class MainClient extends JFrame implements GameConstants {
    private Logger logger=Logger.getLogger("Game.Client.MainClient");

    private JTextField userName, address, field_port;


    private MainClient() {
        setTitle("游戏登陆");
        initComponents();
        setSize(320, 320);
        setVisible(true);
        setResizable(false);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        MyUtils.setLogger(logger);
    }

    private void initComponents() {
        JButton jbutton = new JButton("登陆");
        JLabel jLabel_userName = new JLabel("用户名");
        JLabel jLabel_address = new JLabel("服务器地址");
        JLabel jLabel_port = new JLabel("服务器端口");
        userName = new JTextField(10);
//        address = new JTextField("192.168.199.222", 10);
//        address = new JTextField("139.199.182.101", 10);
        address = new JTextField("139.199.67.77", 10);

        field_port = new JTextField("5000", 10);

        userName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    land();
                }
            }
        });

        jbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                land();
            }
        });
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,50,10));
        GridLayout gridLayout = new GridLayout(3, 1, 80, 50);

        JPanel p1 = new JPanel(gridLayout);
        p1.add(jLabel_userName);
        p1.add(jLabel_address);
        p1.add(jLabel_port);

        JPanel p2 = new JPanel(gridLayout);
        p2.add(userName);
        p2.add(address);
        p2.add(field_port);


        jPanel.add(p1);
        jPanel.add(p2);
        add(Box.createGlue());
        add(jPanel);
        jbutton.setAlignmentX(CENTER_ALIGNMENT);
        add(jbutton);
        add(Box.createVerticalStrut(10));

    }

    private void land() {
        String username = userName.getText().trim();//去除空格
        if (username.equals("")) {
            JOptionPane.showMessageDialog(getContentPane(), "请输入有效的用户名！");
            return;
        }
        int port = Integer.parseInt(field_port.getText());
        Socket socket;
        try {

            InetAddress inetAddress = InetAddress.getByName(address.getText());
            //尝试连接服务器
            socket = new Socket(inetAddress, port);
            socket.setTcpNoDelay(true);
            logger.info("连接成功");
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(getContentPane(), "连接失败，请检查服务器地址和端口！");
            logger.warning("连接端口失败"+e1.getMessage());
            return;
        }
        //连接成功
        //判断名字是否重复
        try {
            logger.info("发送名字:"+username);
            MyUtils.SendMessage(socket, MyUtils.BuildComm(client_req_checkNameRepeat, username));
        } catch (IOException e1) {
            logger.severe("发送名字时产生异常");
        }
        String isNameRepeat = null;
        Scanner scanner=null;
        try {
            //接收的直接是#内容（已换行）
            logger.info("waiting for the result of name check");
            isNameRepeat = MyUtils.ReceiveMessage(socket);
            scanner = new Scanner(isNameRepeat);
            isNameRepeat=scanner.nextLine();
            logger.info("suc recv："+isNameRepeat);

        } catch (IOException e1) {
            logger.severe("接收名字检查结果时产生异常");
        }


        if(isNameRepeat==null)return;
        if (isNameRepeat.equals(TRUE)) {
            JOptionPane.showMessageDialog(getContentPane(), "用户名已存在！");
        } else {
            //启动新窗口

            logger.info("setting gamehall and gameframe");
            GameHall gameHall = new GameHall(socket, username);

            GameFrame gameFrame=new GameFrame(socket);
            gameFrame.setTitle("24点游戏-"+username);
            gameFrame.setUserName(username);

            setVisible(false);
            gameHall.setVisible(true);

            gameHall.setGameFrame(gameFrame);
            gameFrame.setGameHall(gameHall);

            logger.info("gameframe is initializing");
            gameFrame.initGameFrame();

            //与服务器的通信线程（不断监控来自服务器的输入）
            ClientInputThread cit = new ClientInputThread(socket);
//            gameFrame.set
            cit.setGameHall(gameHall);
            cit.setGameFrame(gameFrame);
            cit.start();
            logger.info("input thread started");
            while (scanner.hasNext()){
                logger.info("coping remain message");
                cit.cope(scanner.nextLine());
            }
            scanner.close();

            //在游戏大厅建立后，向服务器请求更新内容
//            gameHall.renewGameHall();//废弃

            logger.info("玩家"+username+"登陆成功");
        }
    }

    public static void main(String[] args) {
        new MainClient();
    }
}
