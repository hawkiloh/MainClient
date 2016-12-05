package Game.Client;

import Util.GameConstants;
import Util.MyUtils;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

/*
不断接受服务器的信息。
 */
class ClientInputThread extends Thread implements GameConstants {
    private Logger logger=Logger.getLogger("Game.Client.MainClient");
    private Socket socket;
    private GameHall gameHall;
    private GameFrame gameFrame;

    void setGameHall(GameHall gameHall) {
        this.gameHall = gameHall;
    }

    void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    ClientInputThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        while (true) {
            try {
                String message = MyUtils.ReceiveMessage(socket);
                Scanner scanner=new Scanner(message);
                while(scanner.hasNext()){
                    cope(scanner.nextLine());
                }
                scanner.close();

            } catch (IOException e) {
                //程序退出
                logger.info("与服务器失去连接");
                JOptionPane.showMessageDialog(null,"与服务器失去连接，程序将退出！");
                System.exit(1);
                break;
            }
        }
    }

     void cope(String message) {
        String cm = MyUtils.DeMessage_cm(message);
        String cont = MyUtils.DeMessage_cont(message);
        //获取多个参数
        ArrayList<String> arrayList=MyUtils.DeMessge_strings(cont);
         if (cm.equals(ser_send_time)) {
             gameFrame.setTime(cont);

         }else {
             //不记录时间日志
             logger.info("coping ："+message);
             if (cm.equals(ser_send_gameState)) {
                 gameHall.setGameState(cont);
                 if (cont.equals(game_not_created)) {
                     gameHall.setJbutton_create(true);
                     gameHall.setJbutton_join(false);
                 } else if (cont.equals(game_created_and_can_join)) {
                     gameHall.setJbutton_create(false);
                     gameHall.setJbutton_join(true);
                 } else if (cont.equals(game_started)) {
                     gameHall.setJbutton_create(false);
                     gameHall.setJbutton_join(false);
                 }

             } else if (cm.equals(ser_send_playerCount)) {
                 gameHall.setPlayersCount(cont);

             } else if (cm.equals(ser_send_4num)) {
                 gameFrame.set4Num(arrayList.get(0), arrayList.get(1), arrayList.get(2), arrayList.get(3));

             } else if (cm.equals(ser_send_chat)) {
                 gameFrame.appendText(cont);

             } else if (cm.equals(ser_table_incr_player)) {
                 gameFrame.increasePlayer(arrayList.get(0), arrayList.get(1), arrayList.get(2));

             } else if (cm.equals(ser_table_remv_player)) {
                 gameFrame.removePlayer(cont);
                 gameFrame.setbtnState(true);

             } else if (cm.equals(ser_table_inc_player_score)) {
                 gameFrame.setTablePlayerScore(arrayList.get(0), arrayList.get(1));

             } else if (cm.equals(ser_send_game_end)) {//游戏结束，重置用户的状态和按钮（准备、时间）
                 gameFrame.gameOver(cont);

             } else if (cm.equals(ser_table_set_player_state)) {
                 gameFrame.updatePlayerState(arrayList.get(0), arrayList.get(1));

             }

         }

    }
}
