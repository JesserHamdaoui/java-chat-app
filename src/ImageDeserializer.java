import java.io.*;

public class ImageDeserializer {

    public static String deserializeImage(String serPath, String targetFolder, String newImageName) throws IOException, ClassNotFoundException {
        byte[] imageBytes;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serPath))) {
            imageBytes = (byte[]) ois.readObject();
        }

        File outFolder = new File(targetFolder);
        if (!outFolder.exists()) outFolder.mkdirs();

        File newImageFile = new File(outFolder, newImageName);
        try (FileOutputStream fos = new FileOutputStream(newImageFile)) {
            fos.write(imageBytes);
        }

        System.out.println("✅ Image restored to: " + newImageFile.getAbsolutePath());
        return newImageFile.getAbsolutePath();
    }
}
