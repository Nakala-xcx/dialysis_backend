package com.histsys.file;

import com.histsys.data.file.FileService;
import com.histsys.web.http.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseBody.status(201).body(fileService.store(file.getResource().getFile())).toResponseEntity();
        } catch (FileService.FileStorageException e) {
            e.printStackTrace();
            return ResponseBody.status(400).message("文件上传失败，请稍后重试").toResponseEntity();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseBody.status(400).message("文件上传失败，IOException").toResponseEntity();
        }
    }
//    文件下载走 nginx 自动代理
//    @GetMapping("/")

}
