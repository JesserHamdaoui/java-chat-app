import java.io.*;
import java.nio.file.Files;

public class ImageSerializer {


    public static void serializeImage(String imagePath, String outputSerPath) throws IOException {
        File imageFile = new File(imagePath);
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputSerPath))) {
            oos.writeObject(imageBytes);
            System.out.println("✅ Image serialized to: " + outputSerPath);
        }
    }
}
