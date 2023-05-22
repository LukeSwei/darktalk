package com.luke.darktalk.strategy;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件平台策略模式
 *
 * @author luke
 * @date 2023/05/20
 */
public interface StorageStrategy {

    /**
     * 上传文件
     *
     * @param file     文件
     * @param fileName 文件名称
     * @return {@code String}
     */
    String uploadFile(MultipartFile file, String fileName);

    /**
     * 下载文件
     *
     * @param fileName 文件名称
     * @param savePath 保存路径
     */
    void downloadFile(String fileName, String savePath);

    /**
     * 删除文件
     *
     * @param fileName 文件名称
     */
    void deleteFile(String fileName);

    /**
     * 文件是否存在
     *
     * @param fileName 文件名称
     * @return boolean
     */
    boolean fileExists(String fileName);

    /**
     * 重新上传文件
     *
     * @param file     文件
     * @param fileName 文件名称
     * @return boolean
     */
    boolean resumeUploadFile(MultipartFile file, String fileName);

    /**
     * 检查文件是否存在
     *
     * @param fileName 文件名称
     * @param fileSize 文件大小
     * @return boolean
     */
    boolean checkIfFileExists(String fileName, long fileSize);

}
