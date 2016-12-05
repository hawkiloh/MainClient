package Game.Client;

import Util.CalExpress;
import Util.GameConstants;
import Util.MyUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

public class GameFrame extends JFrame implements GameConstants, ActionListener {
    private Logger logger=Logger.getLogger("Game.Client.MainClient");
    private Socket socket;
    private GameHall gameHall;

    private String userName;
    private PlainDocument document = new PlainDocument();
    private SimpleAttributeSet sas = new SimpleAttributeSet();
    private JButton jButton_com;
    private JPanel p1, p2;
    private JTextArea jTextArea;
    private JLabel jlable_time, jlable_timeshow;
    private JTextField jTextField_cal, jTextField_com;
    private DefaultTableModel tableModel = new DefaultTableModel(null, new String[]{"用户", "分数", "状态"});
    private JTable table = new JTable(tableModel);
    private ImageIcon icon = new ImageIcon(this.getClass().getResource("/bgp.jpg"));
    private boolean isGaming = false;
    private JButton jButton_1, jButton_2, jButton_3, jButton_4, jButton_add, jButton_sub, jButton_mul, jButton_div;
    private JButton jButton_left, jButton_right, jButton_clear, jButton_delete, jButton_cal, jButton_ready, jButton_exit;
    private Font font_big = new Font(Font.SERIF, Font.BOLD, 80);
    private Font font_mid = new Font(null, Font.CENTER_BASELINE, 30);
    private Font font_small = new Font(null, Font.BOLD, 20);

    //音乐相关
    private String defaultMusicPath="./music/default.wav";
    private File musicFile=new File(defaultMusicPath);

    private JRadioButton btn_music_ctrl=null;
    private PlayMusicThread pmt;

    GameFrame(Socket socket) {
        this.socket = socket;
//        MyUtils.setLogger(logger);
    }

    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame(null);
        gameFrame.initGameFrame();
        gameFrame.setVisible(true);
    }

    private void registAction(){
        //事件注册
        jButton_1.addActionListener(this);
        jButton_2.addActionListener(this);
        jButton_3.addActionListener(this);
        jButton_4.addActionListener(this);

        jButton_ready.addActionListener(this);
        jButton_exit.addActionListener(this);

        jButton_add.addActionListener(this);
        jButton_sub.addActionListener(this);
        jButton_mul.addActionListener(this);
        jButton_div.addActionListener(this);
        jButton_left.addActionListener(this);
        jButton_right.addActionListener(this);

        jButton_clear.addActionListener(this);
        jButton_delete.addActionListener(this);
        jButton_cal.addActionListener(this);

        jButton_com.addActionListener(this);

        jTextField_cal.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyEvent(e);
            }
        });
        jTextField_com.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyEvent(e);
            }
        });

    }
    private  void newComponents() {

        jButton_1 = new JButton("-");
        jButton_2 = new JButton("-");
        jButton_3 = new JButton("-");
        jButton_4 = new JButton("-");

        jButton_add = new JButton("+");
        jButton_sub = new JButton("-");
        jButton_mul = new JButton("*");
        jButton_div = new JButton("/");
        jButton_left = new JButton("(");
        jButton_right = new JButton(")");

        jButton_clear = new JButton("clear");
        jButton_delete = new JButton("delete");
        jButton_cal = new JButton("calculate");
        jButton_ready = new JButton("准备");
        jButton_exit = new JButton("退出");

        jlable_time = new JLabel("倒计时");
        jlable_timeshow = new JLabel(total_time + "");

        jButton_com = new JButton("send");
        jTextArea = new JTextArea(13, 8);
        jTextField_cal = new JTextField(20);
        jTextField_com = new JTextField(20);
    }

    void initGameFrame() {
        newComponents();

        jButton_1.setFont(font_big);
        jButton_2.setFont(font_big);
        jButton_3.setFont(font_big);
        jButton_4.setFont(font_big);

        jButton_add.setFont(font_small);
        jButton_sub.setFont(font_small);
        jButton_mul.setFont(font_small);
        jButton_div.setFont(font_small);
        jButton_left.setFont(font_small);
        jButton_right.setFont(font_small);
        jButton_delete.setFont(font_small);
        jButton_clear.setFont(font_small);
        jButton_cal.setFont(font_small);

        jlable_time.setFont(font_small);
        jlable_timeshow.setFont(font_small);

        registAction();

        jTextField_cal.setFont(font_mid);
        jTextField_com.setFont(new Font(null,Font.TRUETYPE_FONT,17));
//        jButton_com.setFont(font_small);

        jTextField_cal.setMaximumSize(new Dimension(400, 0));
        jTextField_com.setMaximumSize(new Dimension(400, 0));

        jTextField_cal.setDocument(document);
        jTextField_cal.requestFocus();


        Caret caret = jTextField_cal.getCaret();
        caret.setVisible(true);

        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        jTextArea.setEditable(false);


        p1 = new JPanel(new BorderLayout());

        Component v = Box.createHorizontalBox();
        Box box_time = Box.createHorizontalBox();
        box_time.add(v);
        box_time.add(jlable_time);
        box_time.add(Box.createHorizontalStrut(20));
        box_time.add(jlable_timeshow);
        box_time.add(v);
        p1.add(box_time, BorderLayout.NORTH);

        JPanel p1_cen = new JPanel();
        p1_cen.setLayout(new BoxLayout(p1_cen, BoxLayout.Y_AXIS));
        Box box_card = Box.createHorizontalBox();
        box_card.add(Box.createHorizontalStrut(80));
        box_card.add(jButton_1);
        box_card.add(Box.createHorizontalStrut(50));
        box_card.add(jButton_2);
        box_card.add(Box.createHorizontalStrut(50));
        box_card.add(jButton_3);
        box_card.add(Box.createHorizontalStrut(50));
        box_card.add(jButton_4);
        box_card.add(Box.createHorizontalStrut(80));

        JPanel p1_op = new JPanel();
        p1_op.setOpaque(false);
        p1_op.add(jButton_ready);
        p1_op.add(jButton_exit);

//        JPanel p1_b1=new JPanel();
        Box p1_b1 = Box.createHorizontalBox();
        p1_b1.add(jButton_add);
        p1_b1.add(Box.createHorizontalStrut(20));
        p1_b1.add(jButton_sub);
        p1_b1.add(Box.createHorizontalStrut(20));
        p1_b1.add(jButton_mul);
        p1_b1.add(Box.createHorizontalStrut(20));
        p1_b1.add(jButton_div);
        p1_b1.add(Box.createHorizontalStrut(20));
        p1_b1.add(jButton_left);
        p1_b1.add(Box.createHorizontalStrut(20));
        p1_b1.add(jButton_right);

//        JPanel p1_b2=new JPanel();
        Box p1_b2 = Box.createHorizontalBox();
        p1_b2.add(jButton_clear);
        p1_b2.add(Box.createHorizontalStrut(20));
        p1_b2.add(jButton_delete);
        p1_b2.add(Box.createHorizontalStrut(20));
        p1_b2.add(jButton_cal);

        p1_cen.add(Box.createVerticalStrut(30));
        p1_cen.add(box_card);
        p1_cen.add(Box.createVerticalStrut(30));
        p1_cen.add(p1_op);
        p1_cen.add(Box.createVerticalStrut(20));
        p1_cen.add(jTextField_cal);
        p1_cen.add(Box.createVerticalStrut(5));

        p1_cen.add(p1_b1);
        p1_cen.add(p1_b2);
        p1_cen.add(Box.createVerticalGlue());
        p1_cen.add(Box.createVerticalStrut(50));

        p1.add(p1_cen, BorderLayout.CENTER);

        JPanel p1_south = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p1_south.setOpaque(false);
        JLabel jLabel_userName = new JLabel("玩家：\t" + userName);
        p1_south.add(jLabel_userName);
        p1.add(p1_south, BorderLayout.SOUTH);

        p2 = new JPanel();
        p2.setLayout(new BorderLayout(50, 50));

        table.setPreferredScrollableViewportSize(new Dimension(350, 200));

        Box box_table = Box.createVerticalBox();
        box_table.add(Box.createVerticalStrut(10));
        box_table.add(new JScrollPane(table));

        JPanel p2_text = new JPanel();
        p2_text.setOpaque(false);
        p2_text.add(jTextField_com);
        p2_text.add(jButton_com);

        Box box2_south = Box.createVerticalBox();
        box2_south.add(new JScrollPane(jTextArea));
        box2_south.add(p2_text);

        Box box21 = Box.createHorizontalBox();
        Box box22 = Box.createHorizontalBox();

        box21.add(box_table);
        box21.add(Box.createHorizontalStrut(20));

        box22.add(box2_south);
        box22.add(Box.createHorizontalStrut(20));

        p2.add(box21, BorderLayout.NORTH);
        p2.add(box22, BorderLayout.SOUTH);

        add(p1, BorderLayout.CENTER);
        add(p2, BorderLayout.EAST);

        p1_cen.setOpaque(false);

        setMenu();
        setlook();

    }

    private void setMenu() {
        JMenuBar jMenuBar=new JMenuBar();
        JMenu jMenu_option=new JMenu("设置");
        JMenu jMenu_help=new JMenu("帮助");
        JMenu jMenu_about=new JMenu("关于");
        jMenuBar.add(jMenu_option);
        jMenuBar.add(jMenu_help);
        jMenuBar.add(jMenu_about);

/*
“设置”菜单
* */
        JMenu jMenu_view=new JMenu("窗口调整");
        JRadioButtonMenuItem radio1=new JRadioButtonMenuItem("全屏",true);
        JRadioButtonMenuItem radio2=new JRadioButtonMenuItem("非全屏");

        JMenuItem jMenuItem_setMusic=new JMenuItem("背景音乐");

        //窗口菜单
        ButtonGroup group=new ButtonGroup();
        group.add(radio1);
        group.add(radio2);
        radio1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setScreen(true);
            }
        });
        radio2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setScreen(false);
            }
        });
        jMenu_view.add(radio1);
        jMenu_view.add(radio2);
//        JMenuItem jMenuItem_view =new JMenuItem("窗口调整");
        jMenu_option.add(jMenu_view);

        //音乐菜单
        //音乐设置面板：音量，音乐选择
        final JPanel panel_music=new JPanel();
        final JButton btn_music=new JButton("选择音乐");

        btn_music_ctrl = new JRadioButton("音乐开",true);

        //选择播放音乐
        btn_music.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseMusicAndPlay();
            }
        });

        //打开或关闭音乐
        btn_music_ctrl.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                   if(btn_music_ctrl.isSelected()){
                        if(pmt!=null)return;//已开启

                       playMusic();
                       btn_music.setEnabled(true);

                   }else {

                       if(pmt==null)return;//音乐未开启
                        stopMusic();
                        btn_music_ctrl.setText("音乐关");
                        btn_music.setEnabled(false);
                   }
            }
        });
        panel_music.add(btn_music);
        panel_music.add(btn_music_ctrl);
//        //创建dialog
//        JOptionPane myDialog=new JOptionPane(panel_music);

        jMenuItem_setMusic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,panel_music,"设置音乐",JOptionPane.PLAIN_MESSAGE);
            }
        });
        jMenu_option.add(jMenuItem_setMusic);

        /*
        * "帮助"菜单
        * */
        final JMenuItem jMenuItem_guide=new JMenuItem("游戏指引");
        final JTextArea jTextArea_guide=new JTextArea();
        Scanner scanner=new Scanner(this.getClass().getResourceAsStream("/guide.txt"),"UTF-8");
        while (scanner.hasNext()){
            jTextArea_guide.append(scanner.nextLine()+"\n");
        }
        scanner.close();
        jTextArea_guide.setEditable(false);
        final JOptionPane pane_guide=new JOptionPane(new JScrollPane(jTextArea_guide));
        final JDialog dialog_guide=pane_guide.createDialog("游戏指引");
        dialog_guide.setSize(480,300);

        jMenuItem_guide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog_guide.setVisible(true);
            }
        });
        jMenu_help.add(jMenuItem_guide);

        /*"关于"菜单
        * */
        JMenuItem jMenuItem_about=new JMenuItem("软件说明");
        final JLabel jLabel_about=new JLabel();
        jLabel_about.setText("<html>软件名称：24点游戏多人版<br>" +
                "开发作者：广东工业大学15计科（1）班霍启龙<br>软件说明：java大作业游戏程序" +
                "<br>注：如果游戏出现问题，可反馈给作者邮箱1070506780@qq.com</html>");

        jMenuItem_about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,jLabel_about,"关于",JOptionPane.PLAIN_MESSAGE);
            }
        });
        jMenu_about.add(jMenuItem_about);

        setJMenuBar(jMenuBar);


    }

    private void chooseMusicAndPlay(){
        JFileChooser mus_chooser=new JFileChooser("./music");
//        mus_chooser.setFileFilter(new FileNameExtensionFilter("mp3文件","mp3"));
        mus_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result=mus_chooser.showDialog(null,"确认");
        if(result==JFileChooser.APPROVE_OPTION){
            musicFile= mus_chooser.getSelectedFile();
            //处理已在播放的音乐
            if(pmt!=null){
                stopMusic();
            }
            //新线程播放选择的音乐
            playMusic();
        }
    }
    private void setlook() {
//        p1.setBorder(BorderFactory.createLoweredSoftBevelBorder());
//        p2.setBorder(BorderFactory.createRaisedBevelBorder());
//        pack();
//        setVisible(true);
//        setMinimumSize(new Dimension(800, 725));

        //设置菜单

        setExtendedState(JFrame.MAXIMIZED_BOTH);

//        setSize(1366, 768);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        p2.setOpaque(false);
        p1.setOpaque(false);

        ((JPanel) getContentPane()).setOpaque(false);

        Dimension screenSize = MyUtils.GetParentSize();

        //限制界面大小
//        Image temp = icon.getImage().getScaledInstance(1366, 768,
//                icon.getImage().SCALE_DEFAULT);

        icon.getImage();
        Image temp = icon.getImage().getScaledInstance(screenSize.width, screenSize.height,
                Image.SCALE_DEFAULT);
        icon = new ImageIcon(temp);
        JLabel lp = new JLabel(icon);
//        lp.setBounds(0, 0, 1366, 768);
        lp.setBounds(0, 0, screenSize.width, screenSize.height);
        getLayeredPane().add(lp, new Integer(Integer.MIN_VALUE));


        table.setEnabled(false);

//        Color color = new Color(195,188,160);
//        Color color =Color.lightGray;
//        Container c = table.getParent();
//        if (c instanceof JViewport) {
//            JViewport jp = (JViewport) c;
//            jp.setBackground(color);
//        }
//        jTextArea.setBackground(color);
        setResizable(false);

        setUndecorated(true);

    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    void setGameHall(GameHall gameHall) {
        this.gameHall = gameHall;
    }

    void set4Num(String num1, String num2, String num3, String num4) {
        jButton_1.setText(num1);
        jButton_2.setText(num2);
        jButton_3.setText(num3);
        jButton_4.setText(num4);

        //设置游戏状态为开始
        isGaming = true;
        setTableState(player_gaming);
    }

    void appendText(String text) {
        jTextArea.append(text + "\n");
        jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
    }

    void increasePlayer(String username, String score, String playerState) {
        tableModel.addRow(new String[]{username, score, playerState});
    }

    void removePlayer(String username) {
        tableModel.removeRow(SearchUser(username));
    }

    //设置table上的特定玩家的分数
    void setTablePlayerScore(String username, String score) {
        int userIndex = SearchUser(username);

        tableModel.setValueAt(score, userIndex, 1);

    }

    //更新特定玩家的游戏状态
    void updatePlayerState(String username, String playerState) {
        int userIndex = SearchUser(username);
        tableModel.setValueAt(playerState, userIndex, 2);
    }


    private int SearchUser(String userName) {
        int rows = tableModel.getRowCount();
        for (int i = 0; i < rows; i++) {
            String name = (String) tableModel.getValueAt(i, 0);
            if (name.equals(userName)) {
                return i;
            }
        }
        return -1;
    }

    //重设用户状态，设准备按钮可用
    void gameOver(String answer) {
        setbtnState(true);//设置按钮可用
        jlable_timeshow.setText(total_time + "");
        isGaming = false;
        appendText("系统消息：游戏结束，系统的答案是  " + answer);
        //改变一列的用户状态
        setTableState(player_not_ready);
    }

    //改变一列的用户状态
    private void setTableState(String playerState) {
        int rows = tableModel.getRowCount();
        for (int i = 0; i < rows; i++) {
            tableModel.setValueAt(playerState, i, 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //处理四个按钮
        Object source = e.getSource();
        Caret caret = jTextField_cal.getCaret();


        //处理发送聊天的文本框
        if (source == jButton_com) {
            try {
                MyUtils.SendMessage(socket, MyUtils.BuildComm(client_btn_send_chat, jTextField_com.getText()));
                jTextField_com.setText("");
                return;
            } catch (IOException e1) {
                logger.severe("发送聊天信息时发生异常");

            }
        }
        jTextField_cal.requestFocus();
        //处理计算表达式的文本框
        try {
            if (source == jButton_1) {
                caret.setVisible(true);
                document.insertString(caret.getDot(), jButton_1.getText(), sas);

            } else if (source == jButton_2) {
                caret.setVisible(true);
                document.insertString(caret.getDot(), jButton_2.getText(), sas);
            } else if (source == jButton_3) {
                caret.setVisible(true);
                document.insertString(caret.getDot(), jButton_3.getText(), sas);
            } else if (source == jButton_4) {
                caret.setVisible(true);
                document.insertString(caret.getDot(), jButton_4.getText(), sas);
            } else {
                String actionCommand = e.getActionCommand();

                if (actionCommand.equals("+")) {
                    document.insertString(caret.getDot(), "+", sas);

                } else if (actionCommand.equals("-")) {
                    document.insertString(caret.getDot(), "-", sas);

                } else if (actionCommand.equals("*")) {
                    document.insertString(caret.getDot(), "*", sas);

                } else if (actionCommand.equals("/")) {
                    document.insertString(caret.getDot(), "/", sas);

                } else if (actionCommand.equals("(")) {
                    document.insertString(caret.getDot(), "(", sas);

                } else if (actionCommand.equals(")")) {
                    document.insertString(caret.getDot(), ")", sas);

                } else if (actionCommand.equals("clear")) {
                    jTextField_cal.setText("");

                } else if (actionCommand.equals("delete")) {
                    int caretIndex = caret.getDot();
                    String text = jTextField_cal.getText();
                    if (caretIndex == 0) return;
                    String text1 = text.substring(0, caretIndex - 1);
                    String text2 = text.substring(caretIndex);
                    jTextField_cal.setText(text1 + text2);
                    caret.setDot(caretIndex - 1);
                    caret.setVisible(true);

                } else if (actionCommand.equals("calculate")) {
                    calculate();


                } else if (actionCommand.equals("准备")) {
                    logger.info("ready");
                    try {
                        MyUtils.SendMessage(socket, client_btn_ready);
                        //清空输入框的信息
                        jTextField_cal.setText("");
                    } catch (IOException e1) {
                        logger.severe("发送准备信息时发生异常");
                    }
                    setbtnState(false);//设置准备和退出按钮不可用

                } else if (actionCommand.equals("退出")) {//清除table
                    try {
                        btnExit();
                    } catch (IOException e1) {
                        logger.severe("退出时发生异常"+e1.getMessage());
                    }
                }

            }

        } catch (BadLocationException e1) {
            logger.severe("插入文本是发生异常"+e1.getMessage());
        }
    }

    private void btnExit() throws IOException {
        logger.info("player exit game frame");
        setVisible(false);
        if(gameHall==null)System.exit(2);
        gameHall.setVisible(true);
        //移除table
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        //恢复按钮状态
        setbtnState(true);

        MyUtils.SendMessage(socket, client_btn_exit);
        stopMusic();
    }

    private void calculate() {
         /*计算表达式
         * 1.表达式格式不正确
            2.计算结果不是24
           * 3.计算结果是24*/

        try {
            if(!isGaming){
                JOptionPane.showMessageDialog(null,"游戏未开始");
                return ;
            }
            ArrayList<String> numlist=new ArrayList();
            numlist.add(jButton_1.getText());
            numlist.add(jButton_2.getText());
            numlist.add(jButton_3.getText());
            numlist.add(jButton_4.getText());
            int result = CalExpress.calculate(jTextField_cal.getText(),numlist);
            if (result == div_error) {
                JOptionPane.showMessageDialog(this, "表达式中存在整除问题，请检查表达式", "error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(result==no_use_number_given||result==overuse||result==lackuse){
                JOptionPane.showMessageDialog(null,"要求用且仅用给出的4个数一次，请检查表达式！","error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(result==op_error){
                JOptionPane.showMessageDialog(null,"系统检测到表达式中有不合法的字符，请检查表达式！","error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (result == 24) {
                //仅当游戏中时发送增加分数的命令
                if (isGaming) MyUtils.SendMessage(socket, MyUtils.BuildComm(client_req_inc_score, getTime()));
                JOptionPane.showMessageDialog(this, "恭喜你成功解出了24！");

            } else JOptionPane.showMessageDialog(this, "表达式计算结果不是24，请检查表达式！");
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(this, "表达式计算发生错误，请检查表达式！", "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getTime() {
        return jlable_timeshow.getText();
    }

    void setbtnState(boolean isable){
        if(isable){
            jButton_ready.setEnabled(true);
            jButton_exit.setEnabled(true);
        }else {
            jButton_ready.setEnabled(false);
            jButton_exit.setEnabled(false);
        }
    }
    void setTime(String time) {
        jlable_timeshow.setText(time);
    }


    private void keyEvent(KeyEvent e){
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (jTextField_cal.isFocusOwner()) {
                calculate();
            } else if (jTextField_com.isFocusOwner()) {
                try {
                    MyUtils.SendMessage(socket, MyUtils.BuildComm(client_btn_send_chat, jTextField_com.getText()));
                    jTextField_com.setText("");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void setScreen(boolean full){
        if(full){
            if(getExtendedState()==MAXIMIZED_BOTH)return;//已经全屏
            dispose();
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setUndecorated(true);
            setVisible(true);
        }else {
            if(getExtendedState()!=MAXIMIZED_BOTH)return;//已经没全屏
            dispose();
            setUndecorated(false);
            setSize(1100,650);
            setVisible(true);
        }
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if(e.getID()==WindowEvent.WINDOW_CLOSING){
            if(isGaming)return;//游戏中不能退出
            try {
                btnExit();
                return;
            } catch (IOException e1) {
                logger.severe("退出异常："+e1.getMessage());
                System.exit(2);
            }
        }
        super.processWindowEvent(e);
    }
    void playMusic(){
        pmt=new PlayMusicThread(musicFile);
        pmt.start();
    }
    private void stopMusic(){
        if(pmt==null)return;
        pmt.interrupt();
        pmt=null;
    }

}
