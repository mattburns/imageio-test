package com.cameraforensics.imageiotest;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Arrays;

public class ImageIOTest {

    @Test
    public void can_read_file() throws IOException, InterruptedException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("test.jpg").getFile());

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int[] imageioRGB = getRGBUsingImageIO(file, x, y);
                int[] pythonRGB = getRGBUsingPython(file, x, y);
                int[] imageMagickRGB = getRGBUsingImageMagick(file, x, y);

                String diff = "";
                if (!Arrays.equals(imageioRGB, pythonRGB)) {
                    diff = "<--- different to python";
                }
                System.out.printf("Image IO    : [%d, %d] = %s %s\n", x, y, Arrays.toString(imageioRGB), diff);
                System.out.printf("Python      : [%d, %d] = %s\n", x, y, Arrays.toString(pythonRGB));
                System.out.printf("ImageMagick : [%d, %d] = %s\n\n", x, y, Arrays.toString(imageMagickRGB));
            }
        }
    }

    private int[] getRGBUsingImageIO(File file, int x, int y) throws IOException {
        BufferedImage image = ImageIO.read(file);
        int javaRGB = image.getRGB(x, y);
        int javaRed = (javaRGB >> 16) & 0xFF;
        int javaGreen = (javaRGB >> 8) & 0xFF;
        int javaBlue = (javaRGB >> 0) & 0xFF;

        return new int[]{javaRed, javaGreen, javaBlue};
    }

    private int[] getRGBUsingPython(File image, int x, int y) throws IOException {
        String output = runProcess("python src/test/resources/getRGB.py " + image.getAbsolutePath() + " " + x + " " + y);
        String[] parts = output.replace("(", "").replace(")", "").replaceAll(" ", "").split(",");
        int[] rgb = new int[3];
        for (int i = 0 ; i < 3 ; i++){
            rgb[i] = Integer.parseInt(parts[i]);
        }
        return rgb;
    }

    private int[] getRGBUsingImageMagick(File image, int x, int y) throws IOException, InterruptedException {
        String output = runProcess("convert " + image.getAbsolutePath() + " -crop 1x1+" + x + "+" + y + " -depth 8 txt: ");
        String[] parts = output.split("\\s+");
        String rgbString = parts[parts.length - 2];
        Color color = Color.decode(rgbString);
        return new int[]{color.getRed(), color.getGreen(), color.getBlue()};
    }

    private String runProcess(String command) throws IOException {
        Process p = Runtime.getRuntime().exec(command);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String s;
        StringBuilder sb = new StringBuilder();
        while ((s = stdInput.readLine()) != null) {
            sb.append(s);
        }

        BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String err;
        StringBuilder errBuff = new StringBuilder();
        while ((err = stdErr.readLine()) != null) {
            errBuff.append(err);
        }
        err = errBuff.toString().trim();
        if (!err.isEmpty()) {
            System.err.printf("Problem executing:\n%s\nError:\n%s\n", command, errBuff);
            System.exit(-1);
        }

        return sb.toString().trim();
    }
}
