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

    private static Path createDirectory(String uploadDir) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        return uploadPath;
    }

    private static byte[] compressImage(byte[] imageData) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        float quality = 1.0f;

        if (imageData.length <= FileUploadUtil.MAX_SIZE) {
            return imageData;
        }

        while (outputStream.size() > FileUploadUtil.MAX_SIZE && quality > 0.1f) {
            outputStream.reset();
            ImageIO.write(resizeImage(image, (int) (image.getWidth() * quality), (int) (image.getHeight() * quality)), "jpg", outputStream);
            quality -= 0.1f;
        }

        return outputStream.toByteArray();
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    public static void saveMultipartFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {

        Path uploadPath = createDirectory(uploadDir);

        byte[] imageBytes = multipartFile.getBytes();
        if (multipartFile.isEmpty() || imageBytes.length == 0) {
            throw new IOException("The provided image data is empty.");
        }

        byte[] compressedImage = compressImage(imageBytes);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedImage);
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        int targetSize = Math.min(bufferedImage.getWidth(), bufferedImage.getHeight());
        BufferedImage finalImage = resizeImage(bufferedImage, targetSize, targetSize);

        Path filePath = uploadPath.resolve(fileName);
        ImageIO.write(finalImage, "png", filePath.toFile());
    }

    public static void saveBufferedImage(String uploadDir, String fileName, BufferedImage bufferedImage) throws IOException {
        Path uploadPath = createDirectory(uploadDir);

        // Convert the image to a byte array to compress it
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        byte[] compressedImage = compressImage(imageBytes);

        // Convert the compressed image back to a BufferedImage
        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedImage);
        bufferedImage = ImageIO.read(inputStream);

        Path filePath = uploadPath.resolve(fileName);
        ImageIO.write(bufferedImage, "png", filePath.toFile());
    }


}
