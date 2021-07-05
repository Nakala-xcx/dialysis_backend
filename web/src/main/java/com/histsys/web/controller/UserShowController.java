package com.histsys.web.controller;

import com.histsys.data.model.User;
import com.histsys.data.querys.search.SearchRequest;
import com.histsys.data.repository.UserRepository;
import com.histsys.oauth.annotation.CurrentUser;
import com.histsys.web._view.UserView;
import com.histsys.web.http.ResponseBody;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/users")
public class UserShowController {
    @Resource
    private UserRepository userRepository;

    @GetMapping("/page")
    public ResponseBody page(@RequestParam(defaultValue = "1") Integer current,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestAttribute UserView params) {
        return ResponseBody.ok(userRepository.findAll(PageRequest.of(current - 1, pageSize, Sort.by("staffNo"))).map(UserView::toView));
    }

    @GetMapping("/me")
    public ResponseBody me(@CurrentUser User user) {
        return ResponseBody.ok(UserView.toView(user));
    }

    @PostMapping("/search")
    public ResponseBody search(@RequestBody SearchRequest<UserView> payload) {
        return ResponseBody.ok(userRepository.search(payload).map(UserView::toView));
//        return ResponseBody.ok(userRepository.findAll(PageRequest.of(payload.getCurrent() - 1, payload.getPageSize(), Sort.by("staffNo"))).map(UserView::toView));
    }

}
