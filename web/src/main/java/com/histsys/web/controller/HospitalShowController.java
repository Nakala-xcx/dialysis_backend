package com.histsys.web.controller;

import com.histsys.data.repository.HospitalRepository;
import com.histsys.web.http.ResponseBody;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalShowController {
    @Resource
    private HospitalRepository hospitalRepository;

    @GetMapping("/search")
    public ResponseBody search(@RequestParam(defaultValue = "") String word) {
        return ResponseBody.ok(hospitalRepository.findAll(Sort.by("createdAt").descending()));
    }

    @GetMapping("/page")
    public ResponseBody page(@RequestParam(defaultValue = "1") Integer current,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseBody.ok(hospitalRepository.findAll(PageRequest.of(current - 1, pageSize, Sort.unsorted())));
    }

    @GetMapping("/item/{id}")
    public ResponseBody page(@PathVariable String id) {
        return ResponseBody.ok(hospitalRepository.findById(id));
    }

}
