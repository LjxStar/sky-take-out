package com.sky.controller.admin;

import com.sky.config.OssConfiguration;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);

        try {
            // 获取文件的原始文件名
            String originalFilename = file.getOriginalFilename();
            // 获取文件的扩展名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 生成新的文件名
            String newFileName = UUID.randomUUID().toString() + extension;
            // 上传文件到OSS
            String url = aliOssUtil.upload(file.getBytes(), newFileName);
            // 返回文件的URL地址
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败:", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);

    }
}
