package com.histsys.data.file.impl;

import com.histsys.data.file.FileService;
import com.histsys.data.pojo.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@Service
@Slf4j
@PropertySource(ignoreResourceNotFound = false, value = "classpath:application-store[${spring.profiles.active}].properties")
public class FileServiceImpl implements FileService {

    private String originHost;
    private Path savedDir;

    public FileServiceImpl(@Value("${file.origin-host}/files") String originHost,
                           @Value("${file.upload-dir}") String savedDir) {
        this.originHost = originHost;
        this.savedDir = Path.of(savedDir);
        this.init();
    }

    @Override
    public void init() {
        // 如果文件夹不存在，则创建
        assert savedDir != null;
        File dir = savedDir.toFile();
        if (!dir.exists()) {
            log.info("Creating file store dir...");
            if (dir.mkdir()) {
                log.info("File store dir created.");
            }
        }
    }

    @Override
    public FileInfo store(File file) throws FileStorageException, IOException {
        if (!file.exists()) throw new FileStorageException("File not exist");
        InputStream inputStream = new FileInputStream(file);
        // create a tmp file
        String suffix = suffix(file.getName());
        String distFilename = genFilename(suffix);
        Files.copy(inputStream, savedDir.resolve(distFilename), StandardCopyOption.REPLACE_EXISTING);
        String url = originHost + "/" + distFilename;
        // return
        FileInfo info = new FileInfo();
        info.setFileName(file.getName());
        info.setFileType(suffix);
        info.setCreatedAt(new Date());
        info.setUrl(url);
        return info;
    }


    @Override
    public Path load(String filename) {
        return savedDir.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) throws FileStorageException {
        Path path = load(filename);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new FileStorageException(e.getMessage());
        }
        if (resource.exists() || resource.isReadable()) {
            return resource;
        }
        throw new FileStorageException("File load error: not readable.");
    }

    // 根据当前时间戳和原文件后缀
    private String genFilename(String suffix) {
        int randomNum = (int) (100000 + Math.random() * 899999);
        return System.currentTimeMillis() + "-" + randomNum + "." + suffix;
    }

    private String suffix(String originFilename) {
        String suffix = null;
        if (originFilename == null || originFilename.isBlank()) {
            suffix = "unknown";
        } else if (originFilename.contains(".")) {
            String[] strs = originFilename.split(".");
            suffix = strs[strs.length - 1];
        } else {
            suffix = "unknown";
        }
        return suffix;
    }
}
