package it.unical.inf.ea.backend.config;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUploadUtil {

    private static final long MAX_SIZE = 3 * 1024 * 1024; // 3 MB

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        byte[] imageBytes = multipartFile.getBytes();
        if (multipartFile.isEmpty() || imageBytes.length == 0) {
            throw new IOException("The provided image data is empty.");
        }

        byte[] compressedImage;
        if (multipartFile.getSize() > MAX_SIZE) {
            compressedImage = compressImage(imageBytes);
        } else {
            compressedImage = imageBytes;
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedImage);
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        // Ridimensiona l'immagine mantenendo le proporzioni
        int targetSize = Math.min(bufferedImage.getWidth(), bufferedImage.getHeight());
        BufferedImage finalImage = resizeImage(bufferedImage, targetSize, targetSize);

        Path filePath = uploadPath.resolve(fileName);
        ImageIO.write(finalImage, "jpg", filePath.toFile());
    }

    private static byte[] compressImage(byte[] imageData) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        float quality = 1.0f;

        while (outputStream.size() > FileUploadUtil.MAX_SIZE && quality > 0.1f) {
            outputStream.reset();
            ImageIO.write(resizeImage(image, (int) (image.getWidth() * quality), (int) (image.getHeight() * quality)), "jpg", outputStream);
            quality -= 0.1f;
        }

        return outputStream.toByteArray();
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = outputImage.createGraphics();
        graphics.drawImage(resultingImage, 0, 0, null);
        graphics.dispose();
        return outputImage;
    }
}
