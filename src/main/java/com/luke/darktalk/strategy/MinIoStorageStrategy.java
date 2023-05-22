package com.luke.darktalk.strategy;

import com.luke.darktalk.config.MinIoConfig;
import com.luke.darktalk.exception.BusinessException;
import com.luke.darktalk.utils.MinIoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * MinIO存储策略
 *
 * @author luke
 * @date 2023/05/20
 */
@Component
public class MinIoStorageStrategy implements StorageStrategy {
    @Override
    public String uploadFile(MultipartFile file, String fileName) {
        return null;
    }

    @Override
    public void downloadFile(String fileName, String savePath) {

    }

    @Override
    public void deleteFile(String fileName) {

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
