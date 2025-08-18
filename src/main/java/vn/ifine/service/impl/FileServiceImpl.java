package vn.ifine.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.ifine.service.FileService;

@Service
@Slf4j(topic = "FILE-SERVICE-IMPL")
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final MinioClient minioClient;

  @Value("${minio.bucket}")
  private String bucket;

  @Override
  public String upload(MultipartFile file){
    try {
      String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
      // Tạo bucket nếu chưa tồn tại
      boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
      if (!found) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
      }

      // Upload file
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucket)
              .object(filename)
              .stream(file.getInputStream(), file.getSize(), -1)
              .contentType(file.getContentType())
              .build()
      );
      return String.format("%s/%s/%s", "http://localhost:9000", bucket, filename);

    } catch (MinioException e){
      log.error("MinIO error: {}", e.getMessage());
      throw new RuntimeException("MinIO error: " + e.getMessage());
    }  catch (IOException e) {
      log.error("I/O error: {}", e.getMessage());
      throw new RuntimeException("I/O error: " + e.getMessage());
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException(e);
    }
  }
}
