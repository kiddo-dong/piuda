package project.piuda.global.infrastructure;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.piuda.global.exception.BusinessException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3UploadService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    private static final List<String> ALLOWED_AUDIO_TYPES = List.of(
            "audio/wav", "audio/wave", "audio/x-wav",
            "audio/mpeg", "audio/mp3",
            "audio/mp4", "audio/aac",
            "audio/ogg", "audio/webm"
    );

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        validateImage(multipartFile);
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new BusinessException("파일 변환에 실패했습니다."));
        return upload(uploadFile, dirName);
    }

    public String uploadAudio(MultipartFile multipartFile, String dirName) throws IOException {
        validateAudio(multipartFile);
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new BusinessException("파일 변환에 실패했습니다."));
        return upload(uploadFile, dirName);
    }

    public void delete(String fileUrl) {
        try {
            String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);
            amazonS3Client.deleteObject(bucket, key);
        } catch (Exception e) {
            log.warn("S3 파일 삭제 실패: {}", fileUrl, e);
        }
    }

    public String uploadFile(MultipartFile multipartFile, String dirName) throws IOException {
        validateNotEmpty(multipartFile);
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new BusinessException("파일 변환에 실패했습니다."));
        return upload(uploadFile, dirName);
    }

    public String uploadAudioBytes(byte[] bytes, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + ".wav";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("audio/wav");
        metadata.setContentLength(bytes.length);
        amazonS3Client.putObject(bucket, fileName, new ByteArrayInputStream(bytes), metadata);
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void validateImage(MultipartFile file) {
        validateNotEmpty(file);
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException("지원하지 않는 파일 형식입니다. (jpg, png, gif, webp만 허용)");
        }
    }

    private void validateAudio(MultipartFile file) {
        validateNotEmpty(file);
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_AUDIO_TYPES.contains(contentType)) {
            throw new BusinessException("지원하지 않는 파일 형식입니다. (wav, mp3, mp4, aac, ogg, webm만 허용)");
        }
    }

    private void validateNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("파일이 비어있습니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("파일 크기는 10MB를 초과할 수 없습니다.");
        }
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        targetFile.delete();
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
}
