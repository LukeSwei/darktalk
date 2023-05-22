package com.luke.darktalk.controller;


import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.common.ResultUtils;
import com.luke.darktalk.component.mail.Mail;
import com.luke.darktalk.component.service.ProduceService;
import com.luke.darktalk.config.MinIoConfig;
import com.luke.darktalk.model.domain.User;
import com.luke.darktalk.service.MomentService;
import com.luke.darktalk.strategy.StorageService;
import com.luke.darktalk.utils.JsonUtil;
import com.luke.darktalk.utils.JwtUtils;
import com.luke.darktalk.utils.MinIoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private ProduceService testService;

    @Autowired
    private StorageService storageService;

    @PostMapping("send")
    public boolean sendMail(Mail mail) {
        return testService.send(mail);
    }

    @GetMapping("/token")
    public String token(HttpServletResponse response){
        User user = new User();
        user.setUserAccount("1111111111");
        String toStr = JsonUtil.objToStr(user);
        String token = JwtUtils.generateJwtToken(toStr);
        response.setHeader("token", token);
        return token;
    }

    @GetMapping("/check")
    public User check(@RequestHeader(value = "token", required = false) String token) {
        boolean checkToken = JwtUtils.checkToken(token);
        User user = JwtUtils.getUserByJwtToken(token);
        System.out.println("该 token 的 user 为： " + user);
        return user;
    }

    @Autowired
    private MinIoConfig minIoConfig;

    @PostMapping("upload")
    public BaseResponse upload(@RequestParam("file") MultipartFile file) throws Exception {

        String fileName = file.getOriginalFilename();
        String imgUrl = storageService.uploadFile(file, fileName, "qiniu");
//        MinIoUtils.uploadFile(minIoConfig.getBucketName(),
//                              fileName,
//                              file.getInputStream());
//
//        String imgUrl = minIoConfig.getFileHost()
//                        + "/"
//                        + minIoConfig.getBucketName()
//                        + "/"
//                        + fileName;

        return ResultUtils.success(imgUrl);
    }
}