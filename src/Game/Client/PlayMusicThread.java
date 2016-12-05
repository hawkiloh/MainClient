package Game.Client;

import sun.audio.*;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

class PlayMusicThread extends Thread {
    private Logger logger = Logger.getLogger("Game.Client.MainClient");
    private File music = null;
    private AudioStream as;

    PlayMusicThread(File music) {
        this.music = music;

    }

    private void play() {
        try {

            as = new AudioStream(new FileInputStream(music));
            AudioPlayer.player.start(as);
        } catch (IOException e) {
            logger.severe("读取音乐文件失败"+e.getMessage());
            JOptionPane.showMessageDialog(null,"读取音乐文件失败！");
        }

    }

    @Override
    public void run() {
        while (true) {
//            logger.info("循环播放");
            play();
            while (true){
                try {
                    if(as.available()==0)break;
                    sleep(5000);
                } catch (InterruptedException e) {
                    interrupt();
                } catch (IOException e) {
                    interrupt();
                }
            }
        }

    }


    @Override
    public void interrupt() {
        AudioPlayer.player.stop(as);
        super.interrupt();
    }
}
