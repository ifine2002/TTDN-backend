package vn.ifine.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

  @Value("${minio.url}")
  private String minioUrl;

  @Value("${minio.access-key}")
  private String minioAccessKey;

  @Value("${minio.secret-key}")
  private String minioSecretKey;

  @Value("${minio.bucket}")
  private String minioBucket;

  @Bean
  public MinioClient minioClient() {
    return MinioClient.builder().endpoint(minioUrl).credentials(minioAccessKey, minioSecretKey).build();
  }

  @Bean
  public String minioBucket() {
    return minioBucket;
  }
}
