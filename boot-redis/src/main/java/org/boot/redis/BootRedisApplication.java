package org.boot.redis;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author luoliang
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class BootRedisApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BootRedisApplication.class, args);
        processImg("/Users/luoliang/Downloads/视频/35w888piCxTA.mp4", "/Users/luoliang/Downloads/ffmpeg-macos/bin/ffmpeg");
//        generateFixedSizeImage();
    }

    private static void processImg(String vodFilePath, String ffmpegPath) throws InterruptedException, IOException {
        File file = new File(vodFilePath);
        if (!file.exists()) {
            System.err.println("路径[" + vodFilePath + "]对应的视频文件不存在!");
        }
        int d = 1;
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
            String path = "/Users/luoliang/Downloads/screenshot/" + UUID.randomUUID() + ".jpg";
            commands.add(path);
            System.out.println(commands.toString().replaceAll(",", " "));
            try {
                ProcessBuilder builder = new ProcessBuilder();
                builder.command(commands);
                builder.start();
                System.out.println("截取成功");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(1000);
//            System.out.println(getBytes(path));
//            System.out.println("删除文件");
//            Files.delete(Paths.get(path));
        }
    }

    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer;
    }

    /**
     * 使用给定的图片生成指定大小的图片
     */
    private static void generateFixedSizeImage() {
        try {
            Thumbnails.of("/Users/luoliang/Pictures/壁纸/02786_lakefornight_2560x1600.jpg")
                    .sourceRegion(Positions.CENTER, 500, 500)
                    .size(478, 500)
                    .keepAspectRatio(false)
                    .toFile("/Users/luoliang/Downloads/ssss.jpg");
        } catch (IOException e) {
            System.out.println("原因: " + e.getMessage());
        }
        System.out.println(getBytes("/Users/luoliang/Downloads/ssss.jpg"));
    }
}
