package com.histsys.data.file;

import com.histsys.data.pojo.FileInfo;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public interface FileService {
    void init();

    FileInfo store(File file) throws FileStorageException, IOException;

    Path load(String filename);

    Resource loadAsResource(String filename) throws MalformedURLException, FileStorageException;

    static class FileStorageException extends Exception {
        public FileStorageException(String message) {
            super(message);
        }
    }
}
