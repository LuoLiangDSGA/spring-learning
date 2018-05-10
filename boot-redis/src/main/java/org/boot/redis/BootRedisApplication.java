package org.boot.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author luoliang
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class BootRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootRedisApplication.class, args);
        processImg("/Users/luoliang/Movies/JUFD-409.mp4/JUFD-409.mp4", "/Users/luoliang/Downloads/ffmpeg-20180508-293a6e8-macos64-static/bin/ffmpeg");
    }

    private static void processImg(String vodFilePath, String ffmpegPath) {
        File file = new File(vodFilePath);
        if (!file.exists()) {
            System.err.println("路径[" + vodFilePath + "]对应的视频文件不存在!");
        }
        int d = 5;
        for (int i = 1; i <= 6; i++) {
            List<String> commands = new ArrayList<>();
            commands.add(ffmpegPath);
            commands.add("-i");
            commands.add(vodFilePath);
            commands.add("-y");
            commands.add("-f");
            commands.add("image2");
            commands.add("-ss");
            // 这个参数是设置截取视频多少秒时的画面
            commands.add(String.valueOf(d * i));
            commands.add("-aspect");
            commands.add("16:9");
            commands.add(System.getProperty("user.dir") + "/" + UUID.randomUUID() + ".jpg");
            System.out.println(commands.toString().replaceAll(",", " "));
            try {
                ProcessBuilder builder = new ProcessBuilder();
                builder.command(commands);
                builder.start();
                System.out.println("截取成功");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
