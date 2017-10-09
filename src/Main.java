
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;

import static java.awt.Color.black;
import static java.awt.Color.white;


/**
 * Created by jumpr on 8/19/2016.
 */
public class Main {
    public static final int FRAMES = 340;

    private static double scale=1.0;
    private static double rotate = 0.0;
    private static double zoomFac=scale;
    private static double offsetX=0.0;
    private static double offsetY = 0.0;
    public static void main(String args[]) throws Exception {
        ArrayList<BufferedImage> sequence = new ArrayList<>();
        String path;
        path="input";
        for(File f: new File("seq/").listFiles())
            f.delete();
        File[] listOfFiles = new File(path).listFiles();
        BufferedImage output=copyImage(ImageIO.read(listOfFiles[0]));
        int rand=(int)(Math.random()*1000);
        feedbackSequence(listOfFiles,output,0,rand);
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
        mirrorImage(output,false,rand,j);
        mirrorImage(output,true,rand,j);
        File outputfile = new File("C:\\tmp\\Feedback\\"+j+".png");
        try {
            ImageIO.write(output, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return feedback(img,output,iter,(j+1),rand);
    }
    public static BufferedImage feedbackSequence(File[] imgSequence, BufferedImage output,int j, int rand){
        if(j==imgSequence.length){
            return output;
        }
        BufferedImage currentFrame= null;
        File[] outputs = new File("seq/").listFiles();
        try {
            currentFrame = ImageIO.read(imgSequence[j]);
            int w = currentFrame.getWidth();
            int h = currentFrame.getHeight();
            int iter = imgSequence.length;
            scale -= Math.sin(j/iter) / 200;
            rotate -= Math.cos(j /iter) / 100;
            zoomFac -= Math.cos(j /iter) / 200;
            offsetX -= Math.sin(j /iter) / 200;
            offsetY -= Math.cos(j /iter) / 200;
            System.out.println("j=" + j);
            System.out.println("rotate=" + rotate);
            System.out.println("zoom=" + zoomFac);
            System.out.println("offset="+offsetY+" : "+offsetY);
            BufferedImage temp = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
            if(j>0 && j-1<outputs.length) {
                try {
                    temp = ImageIO.read(outputs[j - 1]);
                }
                catch (Exception e){
                    System.out.println(e.toString() + Arrays.toString(outputs));
                    return feedbackSequence(imgSequence,currentFrame,(j),rand);
                }
            }
            AffineTransform at = new AffineTransform();
            at.translate(w / 2 + offsetX, h / 2 + offsetY);
            at.scale(scale, scale);
            at.rotate(rotate);

            at.translate(-w / 2, -h / 2);
            AffineTransformOp scaleOp =
                    new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            temp = scaleOp.filter(output, temp);
            AffineTransform zoom = new AffineTransform();
            zoom.translate(w / 2 - offsetX, h / 2 - offsetY);
            zoom.scale(zoomFac, zoomFac);
            zoom.translate(-w / 2, -h / 2);
            AffineTransformOp zoomOp = new AffineTransformOp(zoom, AffineTransformOp.TYPE_BILINEAR);
            output = zoomOp.filter(currentFrame, output);
            Graphics2D g2 = currentFrame.createGraphics();
            g2.setComposite(AlphaComposite.SrcOver.derive(0.75f));
            g2.drawImage(output, 0, 0, null);
            g2.drawImage(temp, 0, 0, null);
            g2.dispose();
            mirrorImage(currentFrame, false, rand, h / 4);
            mirrorImage(currentFrame, true, rand, w / 4);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        File outputfile = new File("seq/"+j+".png");
        try {
            ImageIO.write(currentFrame, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return feedbackSequence(imgSequence,currentFrame,(j+1),rand);
    }
    public static double getAngleCoefficient(int iter,int j,int max, boolean inverse){
        if(inverse){
            return max*(1.0*j/iter);
        }
        return max-max*(1.0*j/iter);
    }
    public static void mirrorImage(BufferedImage img, boolean vert, int rand,int offset){
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
                if (vert){
                    p = img.getRGB(y, rx);
                }
                else {
                    if(rx-offset<1){
                        p = img.getRGB(rx+offset, y);
                    }
                    else{
                        p = img.getRGB(rx-offset, y);
                    }

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
