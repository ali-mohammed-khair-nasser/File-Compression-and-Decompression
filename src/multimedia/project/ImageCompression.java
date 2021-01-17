package multimedia.project;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;

class ImageCompression {
    public void CompressImage(String path) throws IOException {
        File input = new File(path);
        BufferedImage image = ImageIO.read(input);
        String name=input.getName();
        int n=name.lastIndexOf(".");
        String n1=name.substring(0,n);          
        File compressedImageFile = new File(input.getParent()+"\\"+n1+"compress.jpg");
        OutputStream os =new FileOutputStream(compressedImageFile);
        Iterator<ImageWriter>writers =  ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter) writers.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.05f);
        writer.write(null, new IIOImage(image, null, null), param);
        os.close();
        ios.close();
        writer.dispose();
    }
          
    public double getCompressionRatio(String in, String compressed) {
        List<String> list = new ArrayList<>();
        File f=new File(in);
        list.add(in);
        long size = 0;
        for (int i = 0; i < list.size(); i++) {
            String path = list.get(i);
            File tmp = new File(path);
            if (tmp.isDirectory()) {
                File[] files = tmp.listFiles();
                if (files != null) { for (File file : files) list.add(file.getAbsolutePath()); }
            } else { size += tmp.length(); }
        }
        String name=f.getName();
        int n=name.lastIndexOf(".");
        String n1=name.substring(0,n);          
        File com=new File(compressed+"\\"+n1+"compress.jpg");
        return (size - com.length()) * 1.0 / size * 100.0;
    }
}