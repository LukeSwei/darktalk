package com.luke.darktalk.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 存储策略工厂
 *
 * @author luke
 * @date 2023/05/20
 */
@Component
@Slf4j
public class StorageStrategyFactory {

    private final Map<String, StorageStrategy> storageStrategies;

    @Autowired
    public StorageStrategyFactory(List<StorageStrategy> strategies) {
        log.info("Initializing storage strategy factory",this);
        storageStrategies = strategies.stream().collect(Collectors.toMap(this::getPlatformName, Function.identity()));
    }

    public StorageStrategy getStorageStrategy(String platformName) {
        return storageStrategies.get(platformName);
    }

    private String getPlatformName(StorageStrategy storageStrategy) {
        log.info(String.format("strategy name:%s", storageStrategy.getClass().getSimpleName()));
        if (storageStrategy instanceof QiNiuStorageStrategy) {
            return "qiniu";
        } else if (storageStrategy instanceof MinIoStorageStrategy) {
            return "minio";
        }
        throw new IllegalArgumentException("未知的存储策略");
    }
}
