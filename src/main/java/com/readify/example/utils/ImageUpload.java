package com.readify.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@Slf4j
public class ImageUpload {
    @Async
    public static void saveFile(MultipartFile file, String directoryPath, String filename) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        log.info("File entered for upload..." + directoryPath);

        Path fileNameAndPath = Paths.get(directoryPath, filename);
        Files.write(fileNameAndPath, file.getBytes());

    }
//    @Async
//    public  void saveThumbFile(MultipartFile sourceImage, String directoryPath, String filename) throws IOException {
//
//        int maxWidth = 500;
//        int maxHeight = 500;
//        // Get dimensions of source image.
//        BufferedImage image;
//        try {
//            InputStream inputStream = sourceImage.getInputStream();
//            image = ImageIO.read(inputStream);
//            inputStream.close();//if you are opening inputstream it is mandatory to close orelse unchecked exception will come
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
//        int origWidth = image.getWidth();
//        int origHeight = image.getHeight();
//        int newWidth;
//        int newHeight;
//        if (origWidth<maxWidth&&origHeight<maxHeight){
//            newHeight=origHeight;
//            newWidth=origWidth;
//        }
//        else {
//            maxWidth = maxWidth == 0 ? origWidth : maxWidth;
//            maxHeight = maxHeight == 0 ? origHeight : maxHeight;
//
//            // Calculate ratio of desired maximum sizes and original sizes.
//            double widthRatio = (double) maxWidth / origWidth;
//            double heightRatio = (double) maxHeight / origHeight;
//
//            // Ratio used for calculating new image dimensions.
//            double ratio = Math.min(widthRatio, heightRatio);
//
//            // Calculate new image dimensions.
//            newWidth = (int) (origWidth * ratio);
//            newHeight = (int) (origHeight * ratio);
//
//        }
//        File directory = new File(directoryPath);
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//        log.info("thumbFile entered for upload..." + directoryPath);
//        Path fileNameAndPath = Paths.get(directoryPath, filename);
//        String imagePath=fileNameAndPath.toString();
//
//        Thumbnails.of(imagePath)//the path where we have stored the  image
//                .forceSize(newWidth,newHeight)
//                .toFile(directoryPath+File.separator+"thumb_"+filename);
//        System.out.println(imagePath);
//        System.out.println(directoryPath+File.separator+"thumb_"+filename);
//
//    }
}

