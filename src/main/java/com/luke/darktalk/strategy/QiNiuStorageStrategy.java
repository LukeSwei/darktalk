package com.luke.darktalk.strategy;

import com.luke.darktalk.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 七牛云存储策略
 *
 * @author luke
 * @date 2023/05/20
 */
@Component
public class QiNiuStorageStrategy implements StorageStrategy {

    @Autowired
    private QiniuUtils qiniuUtils;


    @Override
    public String uploadFile(MultipartFile file, String fileName,String path) {
        String url = qiniuUtils.uploadFile(file, fileName);

        return url;
    }

    @Override
    public void downloadFile(String fileName, String savePath) {

    }

    @Override
    public void deleteFile(String fileName) {
        qiniuUtils.deleteFile(fileName);
    }

    @Override
    public boolean fileExists(String fileName) {
        return false;
    }

    @Override
    public boolean resumeUploadFile(MultipartFile file, String fileName) {
        return false;
    }

    @Override
    public boolean checkIfFileExists(String fileName, long fileSize) {
        return false;
    }
}
