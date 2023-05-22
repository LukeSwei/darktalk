package com.luke.darktalk.strategy;

import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.exception.BusinessException;
import com.luke.darktalk.utils.QiniuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 七牛云存储策略
 *
 * @author luke
 * @date 2023/05/20
 */
@Component
public class QiNiuStorageStrategy implements StorageStrategy {

    @Autowired
    private QiniuUtil qiniuUtil;


    @Override
    public String uploadFile(MultipartFile file, String fileName) {
        String url = qiniuUtil.uploadFile(file, fileName);

        return url;
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
