package hk.fish.fishpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import hk.fish.fishpicturebackend.domain.dto.picture.PictureQueryRequest;
import hk.fish.fishpicturebackend.domain.dto.picture.PictureUploadRequest;
import hk.fish.fishpicturebackend.domain.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import hk.fish.fishpicturebackend.domain.entity.User;
import hk.fish.fishpicturebackend.domain.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author 30574
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2026-03-01 13:55:43
*/
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param multipartFile        文件
     * @param pictureUploadRequest 上传参数
     * @param user                 用户
     * @return 图片信息
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User user);

    /**
     * 获取查询条件
     *
     * @param pictureQueryRequest 查询参数
     * @return 查询条件
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    void validPicture(Picture picture);
}
