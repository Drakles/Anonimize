package Anonimize;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class App {

    private static AtomicInteger counter = new AtomicInteger();

    public static void main(String[] args) {
        App.counter.set(0);

        System.out.println("Anonimize? true/false:");
        Scanner in = new Scanner(System.in);
        final int anonimizeCut = in.nextBoolean() ? 100 : 0;

        System.out.println("To CNN format? true/false");
        final boolean isMinimize = in.nextBoolean();

        final Path path = Paths.get("data/input");
        try {
            Files.walk(path).filter(pa -> pa.toFile().isFile() && pa.toFile().getName().endsWith(".tif"))
                    .map(Path::toFile)
                    .forEach(p -> convert(p, anonimizeCut, App.counter.addAndGet(1), isMinimize));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Total files created:" + counter);
    }

    private static void convert(File image, int topCutPixel, int counterId, boolean isToMinimize) {
        try {
            BufferedImage bfInputImage = ImageIO.read(image);
            File outputFile = new File("data/output/" + counterId + image.getName());
            if (bfInputImage != null) {
                int x, y, w, h;
                if (isToMinimize) {
                    x = 400;
                    y = 405 + topCutPixel;
                    w = 245;
                    h = w;
                } else {
                    x = bfInputImage.getMinX();
                    y = topCutPixel;
                    w = bfInputImage.getWidth();
                    h = bfInputImage.getHeight() - topCutPixel;
                }

              ImageIO.write(bfInputImage.getSubimage(x, y, w, h), "tif", outputFile);
            } else {
                System.out.println("Image is null: " + image.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
