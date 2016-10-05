package com.cameraforensics.imageiotest;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageIOTest {

    public ImageIOTest() throws IOException {
    }

    @Test
    public void can_read_file() throws IOException, InterruptedException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("test.jpg").getFile());

        java.util.List<String> profileNames = new ArrayList<>();
        profileNames.add("ACESCG Linear.icc");
        profileNames.add("AdobeRGB1998.icc");
        profileNames.add("Black & White.icc");
        profileNames.add("Blue Tone.icc");
        profileNames.add("CASIO-PJ-0443CAD9-4B4B-3516-1596-C2E9140C00D3.icc");
        profileNames.add("CP220c-9B738A31-361D-020A-DE87-51E587421F82.icc");
        profileNames.add("Color LCD-0AA6E386-CE2C-5EE2-CC95-F2B67DCDF861.icc");
        profileNames.add("DCI(P3) RGB.icc");
        profileNames.add("DELL P2415Q-C1C4C3E1-E9D8-3D14-9BCD-F6DA2A7E7B16.icc");
        profileNames.add("DELL U2515H-A2C875F7-5EAD-E995-0965-2C3DA97AE162.icc");
        profileNames.add("Display P3.icc");
        profileNames.add("Display-8D79CBC9-A4EC-DDE1-12FC-91BA625B13C2.icc");
        profileNames.add("Generic CMYK Profile.icc");
        profileNames.add("Generic Gray Gamma 2.2 Profile.icc");
        profileNames.add("Generic Gray Profile.icc");
        profileNames.add("Generic Lab Profile.icc");
        profileNames.add("Generic RGB Profile.icc");
        profileNames.add("Generic XYZ Profile.icc");
        profileNames.add("Gray Tone.icc");
        profileNames.add("ITU-2020.icc");
        profileNames.add("ITU-709.icc");
        profileNames.add("LG SIGNAGE-F89847F9-FD21-3765-B9D2-FAD087B57067.icc");
        profileNames.add("Lightness Decrease.icc");
        profileNames.add("Lightness Increase.icc");
        profileNames.add("MB169B+      -79A5DF33-C97B-34FF-E507-0E70DA3D3A21.icc");
        profileNames.add("Optoma XGA-204B4EE6-5226-36A8-1FBD-FB3D0BA774BC.icc");
        profileNames.add("ROMM RGB.icc");
        profileNames.add("SX20-8C39FF0F-26AD-70C1-BE25-ABB14AACEC3B.icc");
        profileNames.add("Sepia Tone.icc");
        profileNames.add("default_cmyk.icc");
        profileNames.add("default_gray.icc");
        profileNames.add("default_rgb.icc");
        profileNames.add("gray_to_k.icc");
        profileNames.add("lab.icc");
        profileNames.add("ps_cmyk.icc");
        profileNames.add("ps_gray.icc");
        profileNames.add("ps_rgb.icc");
        profileNames.add("sRGB Profile.icc");
        profileNames.add("sRGB_ICC_v4_Appearance.icc");
        profileNames.add("sgray.icc");
        profileNames.add("srgb.icc");

        System.out.printf("%65s : [x, y] = [  r,   g,   b]\n", "tool");

        List<String> profileNamesToSkip = new ArrayList<>();
        List<String> inverseProfileNamesToSkip = new ArrayList<>();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int[] imageioRGB = getRGBUsingImageIO(file, x, y);
                int[] pythonRGB = getRGBUsingPython(file, x, y);
                int[] imageMagickRGB = getRGBUsingImageMagick(file, x, y);

                printResults("Image IO", x, y, imageioRGB, pythonRGB);
                printResults("Python", x, y, pythonRGB, pythonRGB);
                printResults("ImageMagick", x, y, imageMagickRGB, pythonRGB);

                for (String profileName : profileNames) {
                    if (!profileNamesToSkip.contains(profileName)) {
                        int[] profileRGB = getRGBUsingProfile(file, x, y, profileName, false);
                        printResults(profileName, x, y, profileRGB, pythonRGB);
                        if (!Arrays.equals(profileRGB, pythonRGB)) {
                            profileNamesToSkip.add(profileName);
                        }
                    }

                    if (!inverseProfileNamesToSkip.contains(profileName)) {
                        int[] profileInverseRGB = getRGBUsingProfile(file, x, y, profileName, true);
                        printResults("inverse " + profileName, x, y, profileInverseRGB, pythonRGB);
                        if (!Arrays.equals(profileInverseRGB, pythonRGB)) {
                            inverseProfileNamesToSkip.add(profileName);
                        }
                    }
                }

                System.out.println();
            }
        }
    }

    private void printResults(String tool, int x, int y, int[] rgb, int[] pythonRGB) {
        String diff = "";
        if (!Arrays.equals(rgb, pythonRGB)) {
            diff = "<--- different to python";
        }
        System.out.printf("%65s : [%d, %d] = [%3d, %3d, %3d] %s\n", tool, x, y, rgb[0], rgb[1], rgb[2], diff);
    }

    private int[] getRGBUsingImageIO(File file, int x, int y) throws IOException {
        BufferedImage image = ImageIO.read(file);

        int javaRGB = image.getRGB(x, y);
        int javaRed = (javaRGB >> 16) & 0xFF;
        int javaGreen = (javaRGB >> 8) & 0xFF;
        int javaBlue = (javaRGB >> 0) & 0xFF;

        return new int[]{javaRed, javaGreen, javaBlue};
    }

    private ICC_Profile sRGBProfile = ICC_Profile.getInstance("src/test/resources/sRGB_ICC_v4_Appearance.icc");
    private ICC_ColorSpace sRGBColorSpace = new ICC_ColorSpace(sRGBProfile);

    private int[] getRGBUsingProfile(File file, int x, int y, String profileName, boolean inverse) throws IOException {
        ICC_Profile cp = ICC_Profile.getInstance("src/test/resources/" + profileName);
        ICC_ColorSpace cs = new ICC_ColorSpace(cp);

        BufferedImage image = ImageIO.read(file);
        ColorConvertOp cco;
        if (inverse) {
            cco = new ColorConvertOp(sRGBColorSpace, cs, null);
        } else {
            cco = new ColorConvertOp(cs, null);
        }
        BufferedImage result = cco.filter(image, null);
        int javaRGB = result.getRGB(x, y);
        int javaRed = (javaRGB >> 16) & 0xFF;
        int javaGreen = (javaRGB >> 8) & 0xFF;
        int javaBlue = (javaRGB >> 0) & 0xFF;

        return new int[]{javaRed, javaGreen, javaBlue};
    }

    private int[] getRGBUsingPython(File image, int x, int y) throws IOException {
        String output = runProcess("python src/test/resources/getRGB.py " + image.getAbsolutePath() + " " + x + " " + y);
        String[] parts = output.replace("(", "").replace(")", "").replaceAll(" ", "").split(",");
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
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
