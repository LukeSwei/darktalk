package com.luke.darktalk.strategy;

import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.config.MinIoConfig;
import com.luke.darktalk.contant.CommonConstant;
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


    @Autowired
    private MinIoConfig minIoConfig;
    @Override
    public String uploadFile(MultipartFile file, String fileName,String path) {
        try {
            MinIoUtils.uploadFile(minIoConfig.getBucketName(),
                    fileName,
                    file.getInputStream(),path);
            String imgUrl = minIoConfig.getFileHost()
                    + CommonConstant.FILE_SEPARATOR
                    + minIoConfig.getBucketName()
                    + CommonConstant.FILE_SEPARATOR
                    + path
                    + CommonConstant.FILE_SEPARATOR
                    + fileName;
            return imgUrl;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void downloadFile(String fileName, String savePath) {

    }

    @Override
    public void deleteFile(String fileName) {
        try {
            MinIoUtils.removeFile(minIoConfig.getBucketName(),fileName);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"Minio存储策略中无法找到文件");
        }
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
