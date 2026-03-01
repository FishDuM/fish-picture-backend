package hk.fish.fishpicturebackend.controller;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import hk.fish.fishpicturebackend.annotation.AuthCheck;
import hk.fish.fishpicturebackend.common.BaseResponse;
import hk.fish.fishpicturebackend.exception.BusinessException;
import hk.fish.fishpicturebackend.exception.StatusCode;
import hk.fish.fishpicturebackend.exception.ThrowUtils;
import hk.fish.fishpicturebackend.manager.CosManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import static hk.fish.fishpicturebackend.common.BaseCode.ADMIN;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    @Resource
    private CosManage cosManage;

    @AuthCheck(mustRole = ADMIN)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUpload(@RequestPart("file")MultipartFile multipartFile){
        // 获取文件名
        String fileName = multipartFile.getOriginalFilename();
        // 创建文件路径
        String filePath = String.format("/test/%s", fileName);

        File tempFile = null;

        try {
            // 创建临时文件
            tempFile = File.createTempFile(fileName, null);
            // 将上传的文件保存到临时文件
            multipartFile.transferTo(tempFile);
            // 上传文件
            cosManage.putObject(filePath, tempFile);
            // 返回文件路径
            return BaseResponse.success(filePath);
        } catch (Exception e) {
            log.error("上传文件失败,路径名{}", filePath, e);
            throw new BusinessException(StatusCode.SERVER_ERROR,"上传文件失败");
        } finally {
            if (tempFile != null) {
                // 删除临时文件
                boolean delete = tempFile.delete();
                if (!delete){
                    log.error("删除临时文件失败,文件名{}", tempFile.getName());
                    ThrowUtils.error(StatusCode.SERVER_ERROR, "删除临时文件失败");
                }
            }
        }
    }

    /**
     * 测试文件下载
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @AuthCheck(mustRole = ADMIN)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManage.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(StatusCode.PARAM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }

}
