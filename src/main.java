public class main {
    public static void main(String[] args) {
        try {
            String originalImage = "C:\\Users\\ilyes\\Documents\\English 102\\photo_cv.jpeg";
            String serFile = "C:\\Users\\ilyes\\Documents\\advanced oop\\temp";
            String targetFolder = "C:/new_images";
            String restoredFileName = "restored_photo.jpg";


            ImageSerializer.serializeImage(originalImage, serFile);


            String newPath = ImageDeserializer.deserializeImage(serFile, targetFolder, restoredFileName);
            System.out.println("🔁 New image path: " + newPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
