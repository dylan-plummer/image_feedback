import javafx.scene.effect.BlendMode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;

import java.util.Arrays;

import static java.awt.Color.black;
import static java.awt.Color.white;


/**
 * Created by jumpr on 8/19/2016.
 */
public class Main {
    public static void main(String args[]) throws Exception {
        BufferedImage img = ImageIO.read(new File("image.png"));
        BufferedImage output=copyImage(img);
        int iter=1000;
        int rand=(int)(Math.random()*1000);
        feedback(img,output,iter,0,rand);
        File outputfile = new File("output.jpg");
        ImageIO.write(output, "png", outputfile);
    }
    public static BufferedImage feedback(BufferedImage img, BufferedImage output, int iter,int j, int rand){
        int w=img.getWidth();
        int h=img.getHeight();
        System.out.println("j="+j);
        double scale=1.0-getAngleCoefficient(iter,j,8,true)*(0.5+j)/iter;
        if((int)(w*scale)==0||(int)(h*scale)==0){
            return output;
        }
        BufferedImage temp=copyImage(output);
        AffineTransform at = new AffineTransform();
        at.translate(w/2,h/2);
        at.scale(scale,scale);
        at.rotate(Math.PI/2/iter*j);

        at.translate(-w/2,-h/2);
        AffineTransformOp scaleOp =
                new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        temp=scaleOp.filter(output,temp);
        Graphics2D g2=output.createGraphics();
        g2.drawImage(temp,0,0,null);
        mirrorImage(output,false,rand);
        mirrorImage(output,true,rand);
        File outputfile = new File("C:\\tmp\\Feedback\\"+j+".png");
        try {
            ImageIO.write(output, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return feedback(img,output,iter,(j+1),rand);
    }
    public static double getAngleCoefficient(int iter,int j,int max, boolean inverse){
        if(inverse){
            return max*(1.0*j/iter);
        }
        return max-max*(1.0*j/iter);
    }
    public static void mirrorImage(BufferedImage img, boolean vert, int rand){
        int h;
        int w;
        if(!vert){
            h=img.getHeight();
            w=img.getWidth();
        }
        else{
            h=img.getWidth();
            w=img.getHeight();
        }
        //create mirror image pixel by pixel
        Byte alpha = 124%0xff;
        for(int y = 0; y < h; y++){
            for(int lx = 0, rx = w - 1; lx < w/2; lx++, rx--){
                //lx starts from the left side of the image
                //rx starts from the right side of the image

                //get source pixel value
                int mc = (alpha << 27) | 0x00ffffff;
                int p;
                int o;
                if (vert){
                    o = img.getRGB(y, rx);
                    p = img.getRGB(y, rx) - rand;
                }
                else {
                    o = img.getRGB(rx, y);
                    p = img.getRGB(rx, y) - rand;
                }

                //set mirror image pixel value - both left and right
                if(vert) {
                    img.setRGB(y, rx, p);
                    img.setRGB(y, lx, p);
                }
                else{
                    img.setRGB(lx, y, p);
                    img.setRGB(rx, y, p);
                }
            }
        }
    }
    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }
}
