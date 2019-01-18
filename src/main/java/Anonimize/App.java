package Anonimize;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class App {

  public static void main(String[] args) {

    Scanner in = new Scanner(System.in);
    System.out.println("Path:");

    String dataPath = in.next();
    System.out.println("Top cut:");
    int cut = in.nextInt();

    Path path = Paths.get(dataPath);

    System.out.println("Absolute path to data folder: " + path.toAbsolutePath().toString());

    try {
      Files.walk(path).filter(pa -> pa.toFile().isFile() && pa.toFile().getName().endsWith(".tif"))
          .map(Path::toFile)
          .forEach(p -> convert(p, cut));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void convert(File image, int topCutPixel) {
    try {
      BufferedImage bfImage = ImageIO.read(image);
      if(bfImage != null) {
        ImageIO.write(cropImage(bfImage, bfImage.getMinX(), topCutPixel, bfImage.getWidth(),
            bfImage.getHeight() - topCutPixel), "tif", image);
      }else {
        System.out.println("Image is null: " + image.getPath());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static BufferedImage cropImage(BufferedImage bufferedImage, int x, int y, int width,
      int height) {
    return bufferedImage.getSubimage(x, y, width, height);
  }

}
