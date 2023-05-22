package com.luke.darktalk.strategy;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 存储服务
 *
 * @author luke
 * @date 2023/05/21
 */
@Service
public class StorageService {
    private final StorageStrategyFactory storageStrategyFactory;

    @Autowired
    public StorageService(StorageStrategyFactory storageStrategyFactory) {
        this.storageStrategyFactory = storageStrategyFactory;
    }

    public String uploadFile(MultipartFile file, String fileName, String platformName) {
        StorageStrategy storageStrategy = storageStrategyFactory.getStorageStrategy(platformName);
        if (storageStrategy != null) {
            return storageStrategy.uploadFile(file, fileName);
        }
        return null;
    }

    public void downloadFile(String fileName, String savePath, String platformName) {
        StorageStrategy storageStrategy = storageStrategyFactory.getStorageStrategy(platformName);
        if (storageStrategy != null) {
            storageStrategy.downloadFile(fileName, savePath);
        }
    }

    public void deleteFile(String fileName, String platformName) {
        StorageStrategy storageStrategy = storageStrategyFactory.getStorageStrategy(platformName);
        if (storageStrategy != null) {
            storageStrategy.deleteFile(fileName);
        }
    }

    public boolean fileExists(String fileName, String platformName) {
        StorageStrategy storageStrategy = storageStrategyFactory.getStorageStrategy(platformName);
        if (storageStrategy != null) {
            return storageStrategy.fileExists(fileName);
        }
        return false;
    }

    public boolean resumeUploadFile(MultipartFile file, String fileName, String platformName) {
        StorageStrategy storageStrategy = storageStrategyFactory.getStorageStrategy(platformName);
        if (storageStrategy != null) {
            return storageStrategy.resumeUploadFile(file, fileName);
        }
        return false;
    }

    public boolean checkIfFileExists(String fileName, long fileSize, String platformName) {
        StorageStrategy storageStrategy = storageStrategyFactory.getStorageStrategy(platformName);
        if (storageStrategy != null) {
            return storageStrategy.checkIfFileExists(fileName, fileSize);
        }
        return false;
    }
}

