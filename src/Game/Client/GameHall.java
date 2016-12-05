package Game.Client;

import Util.GameConstants;
import Util.MyUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class GameHall extends JFrame implements GameConstants {
    private Logger logger=Logger.getLogger("Game.Client.MainClient");
    private GameFrame gameFrame;

    void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }


    private Socket socket;

    private JLabel count;
    private JLabel state;
    private JButton jbutton_create, jbutton_join;

    void setPlayersCount(String count) {
        this.count.setText(count);
    }

    void setGameState(String state) {
        this.state.setText(state);
    }

    void setJbutton_create(boolean bool) {
        this.jbutton_create.setEnabled(bool);
    }

    void setJbutton_join(boolean bool) {
        this.jbutton_join.setEnabled(bool);
    }

    private String username = "";

    private void initGameHall() {
        JLabel jLabel_welcome = new JLabel("欢迎 " + username + " 玩家，祝您游戏愉快！");
        JLabel jLabel_count = new JLabel("游戏在线人数");
        count = new JLabel("0");
        JLabel jLabel_state = new JLabel("游戏状态");
        state = new JLabel("-------");
        jbutton_create = new JButton("创建新的游戏");
        jbutton_join = new JButton("加入已存在的游戏");

        jbutton_create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println(getWidth()+" "+getHeight());

                createEvent();
            }
        });
        jbutton_join.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println(getWidth()+" "+getHeight());
                joinEvent();

            }
        });

        FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 20, 20);
        JPanel p1 = new JPanel();
        p1.add(jLabel_welcome);
        p1.setOpaque(false);

        JPanel p2 = new JPanel(flowLayout);
        p2.add(jLabel_count);
        p2.add(count);
        p2.setOpaque(false);


        JPanel p3 = new JPanel(flowLayout);
        p3.add(jLabel_state);
        p3.add(state);
        p3.setOpaque(false);


        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        jLabel_welcome.setAlignmentX(CENTER_ALIGNMENT);
        jbutton_create.setAlignmentX(CENTER_ALIGNMENT);
        jbutton_join.setAlignmentX(CENTER_ALIGNMENT);

        add(jLabel_welcome);
        add(p2);
        add(p3);
        add(jbutton_create);
        add(Box.createGlue());
        add(jbutton_join);
        add(Box.createGlue());

//        getContentPane().setBackground(Color.green);

    }

    private void joinEvent() {
        //显示游戏窗口

        setVisible(false);
        gameFrame.setVisible(true);
        //播放默认音乐
        gameFrame.playMusic();
        try {
            logger.info("sending join message");
            MyUtils.SendMessage(socket, client_btn_join);
        } catch (IOException e) {
            logger.severe("发送加入游戏的信息时产生异常");
        }
    }

    private void createEvent() {
        //显示游戏窗口
        setVisible(false);
        gameFrame.setVisible(true);
        gameFrame.playMusic();

        /*不显式地请求服务器更新游戏的table表*
        服务器通过处理按钮事件来更新table
         */
//        gameFrame.renewGameFrame();
        try {
            logger.info("sending create message");
            MyUtils.SendMessage(socket, client_btn_create);
        } catch (IOException e) {
            logger.severe("发送创建游戏的信息时产生异常");

        }

    }

    GameHall(Socket socket, String username) {
        setTitle("游戏大厅-" + username);
        this.username = username;
        initGameHall();
        setSize(360, 260);
//        pack();
//        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.socket = socket;

//        MyUtils.setLogger(logger);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            int op = JOptionPane.showConfirmDialog(null, "关闭会退出游戏，是否继续？","退出确认",JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                try {
                    //显式请求退出
                    MyUtils.SendMessage(socket,client_over);
                } catch (IOException e1) {
                    logger.severe("退出时产生异常："+e1.getMessage());
                }
                logger.log(new LogRecord(Level.INFO,"exit"));//刷新日志
                System.exit(3);
            } else return;
        }
        super.processWindowEvent(e);
    }

    //废弃：被动地接收大厅信息，而不是主动请求
//    public void renewGameHall() {
//        //请求更新游戏大厅信息
//        try {
//            /**
//             * 直接发送指令，不附带内容，指令将有服务器的特定输入线程收到，
//             并回馈窗口信息给该线程的输入线程。
//             */
//            //debug
//            System.out.println("开始发送更新hall请求");
//            MyUtils.SendMessage(socket, client_req_renew_hall);
//            System.out.println("发送更新hall请求成功");
//
//        } catch (IOException e) {
//            logger.severe("发送更新游戏大厅的信息时产生异常");
//        }
//    }

    public static void main(String[] args) {
        GameHall gameHall = new GameHall(null, null);
        gameHall.setVisible(true);
    }

}
