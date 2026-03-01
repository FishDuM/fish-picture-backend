package hk.fish.fishpicturebackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import hk.fish.fishpicturebackend.common.BaseResponse;
import hk.fish.fishpicturebackend.config.CosClientConfig;
import hk.fish.fishpicturebackend.domain.dto.picture.UploadPictureResult;
import hk.fish.fishpicturebackend.exception.BusinessException;
import hk.fish.fishpicturebackend.exception.StatusCode;
import hk.fish.fishpicturebackend.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 文件管理
 */
@Service
@Slf4j
public class FileManager {
    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private CosManager cosManager;

    public UploadPictureResult uploadFile(MultipartFile multipartFile, String uploadPathPrefix) {
        // 校验上传的文件
        validPicture(multipartFile);
        // 图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = multipartFile.getOriginalFilename();
        String fileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originalFilename));

        String projectName = "fish_picture";

        String uploadPath = String.format(projectName + "%s/%s", uploadPathPrefix, fileName);
        // 解析结果并返回
        File tempFile = null;
        try {
            // 创建临时文件
            tempFile = File.createTempFile(fileName, null);
            // 将上传的文件保存到临时文件
            multipartFile.transferTo(tempFile);
            // 上传文件
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, tempFile);
            // 获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            String format = imageInfo.getFormat();
            int width = imageInfo.getWidth();
            int height = imageInfo.getHeight();
            // 宽高比
            double picScale = NumberUtil.round((double) width / height, 2).doubleValue();

            // 封装返回结果
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
            uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicSize(FileUtil.size(tempFile));
            uploadPictureResult.setPicWidth(width);
            uploadPictureResult.setPicHeight(height);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(format);

            // 返回文件路径
            return uploadPictureResult;
        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new BusinessException(StatusCode.SERVER_ERROR, "上传失败");
        } finally {
            // 清理临时文件
            deleteTempFile(tempFile);
        }
    }

    /**
     * 清理临时文件
     *
     * @param tempFile 临时文件
     */
    private static void deleteTempFile(File tempFile) {
        if (tempFile != null) {
            // 删除临时文件
            boolean deleteResult = tempFile.delete();
            if (!deleteResult) {
                log.error("删除临时文件失败,文件绝对路径{}", tempFile.getAbsolutePath());
                ThrowUtils.error(StatusCode.SERVER_ERROR, "删除临时文件失败");
            }
        }
    }

    /**
     * 校验图片
     *
     * @param multipartFile 文件
     */
    private void validPicture(MultipartFile multipartFile) {
        // 校验文件不能为空
        ThrowUtils.throwIf(multipartFile.isEmpty(), StatusCode.PARAM_ERROR, "上传文件不能为空");
        // 校验文件大小
        long size = multipartFile.getSize();
        final long ONE_M = 1024 * 1024;
        ThrowUtils.throwIf(size > 2 * ONE_M, StatusCode.PARAM_ERROR, "上传文件大小不能超过2M");
        // 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // 允许上传的文件后缀集合
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpg", "jpeg", "png", "webp");
        // 判断是否包含允许的后缀
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), StatusCode.PARAM_ERROR, "上传文件格式不支持");
    }
}
