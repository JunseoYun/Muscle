package Muscle.common.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:/secret/application-s3.properties")
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 1. 파일 업로드 메서드
    public List<String> uploadFiles(MultipartFile[] files, String dirName) throws IOException {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                File uploadFile = convert(file)
                        .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File convert failed"));

                String fileUrl = upload(uploadFile, dirName);
                imageUrls.add(fileUrl);  // 이미지의 S3 URL 저장
            } catch (AmazonS3Exception e) {
                throw new RuntimeException("S3에 파일 업로드 중 오류 발생: " + e.getMessage());
            }
        }

        return imageUrls;
    }

    private String upload(File uploadFile, String dirName){
        String fileName = dirName + "/"+ UUID.randomUUID().toString()+ uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        System.out.println(uploadImageUrl);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {
        // ACL 설정 없이 S3에 파일 업로드
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile));
        // 업로드 된 파일의 S3 URL 주소 반환
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }


    private void removeNewFile(File targetFile){
        // Multipartfile -> file로 전환되면서 로컬에 파일 생성된 것을 삭제
        if(targetFile.delete()){
            System.out.println("파일이 삭제되었습니다.");
        }else
        {
            System.out.println("파일이 삭제되지 않았습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        // 파일을 저장할 임시 디렉토리 또는 위치 설정
        File convertFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());

        // 파일 생성 및 변환
        try (FileOutputStream fileOutputStream = new FileOutputStream(convertFile)) {
            fileOutputStream.write(file.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("MultipartFile -> File convert failed", e);
        }

        return Optional.of(convertFile);
    }


    public void deleteFile(String url){
        String fileName = extractFileNameFromUrl(url);

        // S3 객체 삭제
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
        System.out.println("파일이 삭제되었습니다: " + fileName);
    }

    // URL에서 파일 경로 추출
    private String extractFileNameFromUrl(String fileUrl) {
        // URL 형식: https://bucket-name.s3.region.amazonaws.com/folder/filename
        // URL에서 버킷 이름과 경로를 제외한 파일 경로를 추출
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }


}
