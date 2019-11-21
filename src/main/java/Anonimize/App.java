package Anonimize;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;

public class App {

  private static AtomicInteger counter = new AtomicInteger(0);

  public static void main(String[] args) {
    App.counter.set(0);

    try {
      final Scanner in = new Scanner(System.in);
      System.out.println("Use custom folder for input data? default is 'data/input' true/false");

      Path path;
      if (in.nextBoolean()) {
        System.out.println("Specify path for custom folder");
        path = Paths.get(in.next());
      } else {
        path = Paths.get("data/input");
      }

      System.out.println("Anonimize images? true/false:");
      final int anonimizeCut = in.nextBoolean() ? 100 : 0;

      System.out.println("Convert to format accepted by model? true/false");
      final boolean isToModelFormat = in.nextBoolean();

      System.out.println("Use custom image extension? default is 'tif' true/false");
      String imageExtension;
      if (in.nextBoolean()) {
        System.out.println("Specify image extension");
        imageExtension = in.next();
      } else {
        imageExtension = "tif";
      }

      Files.walk(path)
          .filter(pa -> pa.toFile().isFile() && pa.toFile().getName().endsWith(imageExtension))
          .map(Path::toFile)
          .forEach(
              image ->
                  convert(
                      image,
                      anonimizeCut,
                      App.counter.addAndGet(1),
                      isToModelFormat,
                      imageExtension));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InputMismatchException e) {
      System.out.println(
          "Please answer only 'true' or 'false' if question specify 'true/false' at the end of the message");
    } finally {
      System.out.println("Total files created:" + counter);
    }
  }

  private static void convert(
      File image, int topCutPixel, int counterId, boolean isToModelFormat, String imageExtension) {
    try {
      final BufferedImage bfInputImage = ImageIO.read(image);
      final File outputFile = new File("data/output/" + counterId + image.getName());

      if (bfInputImage != null) {
        int x, y, w, h;
        if (isToModelFormat) {
          x = 200;
          y = 330 + topCutPixel;
          w = 630;
          h = 330;
        } else {
          x = bfInputImage.getMinX();
          y = topCutPixel;
          w = bfInputImage.getWidth();
          h = bfInputImage.getHeight() - topCutPixel;
        }
        ImageIO.write(bfInputImage.getSubimage(x, y, w, h), imageExtension, outputFile);
      } else {
        System.out.println("Image in directory" + image.getPath() + "is null");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
