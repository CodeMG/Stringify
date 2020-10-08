import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {

    public static BufferedImage loadImageGrayscale(String text){
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(text));
        } catch (IOException e) {
            System.out.println("Image not found");
        }
        BufferedImage gray = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        for(int i = 0; i < img.getWidth();i++){
            for(int j = 0; j < img.getHeight();j++){
                Color c = new Color(img.getRGB(i,j));
                int sat = (c.getRed() + c.getGreen() + c.getBlue())/3;
                Color newC;
                /*
                if(sat > 255/2){
                    newC = new Color(255,255,255);
                }
                else{
                    newC = new Color(0,0,0);
                }
                */
                newC = new Color(sat,sat,sat);
                gray.setRGB(i,j,newC.getRGB());
            }
        }
        return gray;
    }

    public static BufferedImage sobel(String path){
        BufferedImage image = loadImageGrayscale(path);
        int x = image.getWidth();
        int y = image.getHeight();

        int maxGval = 0;
        int[][] edgeColors = new int[x][y];
        int maxGradient = -1;

        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {

                int val00 = getGrayScale(image.getRGB(i - 1, j - 1));
                int val01 = getGrayScale(image.getRGB(i - 1, j));
                int val02 = getGrayScale(image.getRGB(i - 1, j + 1));

                int val10 = getGrayScale(image.getRGB(i, j - 1));
                int val11 = getGrayScale(image.getRGB(i, j));
                int val12 = getGrayScale(image.getRGB(i, j + 1));

                int val20 = getGrayScale(image.getRGB(i + 1, j - 1));
                int val21 = getGrayScale(image.getRGB(i + 1, j));
                int val22 = getGrayScale(image.getRGB(i + 1, j + 1));

                int gx =  ((-1 * val00) + (0 * val01) + (1 * val02))
                        + ((-2 * val10) + (0 * val11) + (2 * val12))
                        + ((-1 * val20) + (0 * val21) + (1 * val22));

                int gy =  ((-1 * val00) + (-2 * val01) + (-1 * val02))
                        + ((0 * val10) + (0 * val11) + (0 * val12))
                        + ((1 * val20) + (2 * val21) + (1 * val22));

                double gval = Math.sqrt((gx * gx) + (gy * gy));
                int g = (int) gval;

                if(maxGradient < g) {
                    maxGradient = g;
                }

                edgeColors[i][j] = g;
            }
        }

        double scale = 255.0 / maxGradient;

        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {
                int edgeColor = edgeColors[i][j];
                edgeColor = (int)(edgeColor * scale);
                edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;

                image.setRGB(i, j, edgeColor);
            }
        }

        for(int i = 0; i < image.getWidth();i++){
            for(int j = 0;j < image.getHeight();j++){
                Color c = new Color(image.getRGB(i,j));
                c = new Color(255-c.getRed(),255-c.getGreen(),255-c.getBlue());
                image.setRGB(i,j,c.getRGB());
            }
        }

        return image;

    }

    public static int  getGrayScale(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;

        //from https://en.wikipedia.org/wiki/Grayscale, calculating luminance
        int gray = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b);
        //int gray = (r + g + b) / 3;

        return gray;
    }

}
