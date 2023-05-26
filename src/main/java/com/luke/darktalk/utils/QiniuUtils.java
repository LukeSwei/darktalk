package com.luke.darktalk.utils;

import com.luke.darktalk.exception.BusinessException;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.*;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Random;

/**
 * qiniu云存储工具类
 *
 * @author luke
 * @date 2023/05/21
 */
@Component
public class QiniuUtils {
    @Value("${qiniu.accessKey}")
    private String accessKey;

    @Value("${qiniu.secretKey}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    @Value("${qiniu.domain}")
    private String domain;

    public String uploadFile(MultipartFile file, String fileName) {
        Configuration cfg = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);

        try {
            Response response = uploadManager.put(file.getBytes(), fileName, upToken);
            if (response.isOK()) {
                return domain + "/" + fileName;
            }
        } catch (QiniuException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new BusinessException(null,"七牛云上传失败");
        }
        return null;
    }

    public void deleteFile(String fileName) {
        Configuration cfg = new Configuration(Region.autoRegion());
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);

        try {
            bucketManager.delete(bucket, fileName);
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    public boolean fileExists(String fileName) {
        Configuration cfg = new Configuration(Region.autoRegion());
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);

        try {
            bucketManager.stat(bucket, fileName);
            return true;
        } catch (QiniuException e) {
            if (e.response != null && e.response.statusCode == 612) {
                return false;
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

//    public boolean resumeUploadFile(File file, String fileName) {
//        Configuration cfg = new Configuration(Region.autoRegion());
////        ResumeUploader resumeUploader = new ResumeUploader(cfg);
//        UploadManager uploadManager = new UploadManager(cfg);
//        Auth auth = Auth.create(accessKey, secretKey);
//        String upToken = auth.uploadToken(bucket);
//
//        try {
//            Response response = resumeUploader.put(file, fileName, upToken, null, null);
//            if (response.isOK()) {
//                return true;
//            }
//        } catch (QiniuException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public boolean checkIfFileExists(String fileName, long fileSize) {
        Configuration cfg = new Configuration(Region.autoRegion());
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);

        try {
            String[] fileInfo = fileName.split("\\.");
            String prefix = fileInfo[0];
            String suffix = fileInfo[1];
            String key = prefix + generateRandomFileNameWithExtension(fileName) + "." + suffix;

            bucketManager.copy(bucket, fileName, bucket, key);
            bucketManager.delete(bucket, key);

            return true;
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        return false;
    }


    private String generateRandomFileNameWithExtension(String originalFileName) {
        String extension = StringUtils.isNullOrEmpty(originalFileName) ? "" : originalFileName.substring(originalFileName.lastIndexOf("."));
        String randomFileName = generateRandomFileName();
        return randomFileName + extension;
    }
    private String generateRandomFileName() {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(base.length());
            sb.append(base.charAt(index));
        }
        return sb.toString();
    }
}
