import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Image {
    BufferedImage image;
    int width;
    int height;

    public Image() throws IOException {
            File input = new File("space.jpg");
            image = ImageIO.read(input);
            width = image.getWidth();
            height = image.getHeight();
    }
    public Image(String name) throws IOException {
        File input = new File(name);
        image = ImageIO.read(input);
        width = image.getWidth();
        height = image.getHeight();
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BufferedImage processImage(BufferedImage image){
        int[] arr=new int[width*height];
        image.getRGB(0,0,width,height,arr,0,1);
        System.out.println(Arrays.toString(arr));
        return image;
    }
}
