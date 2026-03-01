package hk.fish.fishpicturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hk.fish.fishpicturebackend.domain.dto.picture.PictureQueryRequest;
import hk.fish.fishpicturebackend.domain.dto.picture.PictureUploadRequest;
import hk.fish.fishpicturebackend.domain.dto.picture.UploadPictureResult;
import hk.fish.fishpicturebackend.domain.entity.Picture;
import hk.fish.fishpicturebackend.domain.entity.User;
import hk.fish.fishpicturebackend.domain.vo.PictureVO;
import hk.fish.fishpicturebackend.domain.vo.UserVO;
import hk.fish.fishpicturebackend.exception.StatusCode;
import hk.fish.fishpicturebackend.exception.ThrowUtils;
import hk.fish.fishpicturebackend.manager.FileManager;
import hk.fish.fishpicturebackend.service.PictureService;
import hk.fish.fishpicturebackend.mapper.PictureMapper;
import hk.fish.fishpicturebackend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 30574
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2026-03-01 13:55:43
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    @Resource
    private FileManager fileManager;

    @Resource
    private UserService userService;

    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User user) {
        // 校验参数
        ThrowUtils.throwIf(multipartFile.isEmpty(), StatusCode.PARAM_ERROR, "上传文件不能为空");
        ThrowUtils.throwIf(pictureUploadRequest == null, StatusCode.PARAM_ERROR, "上传参数不能为空");
        ThrowUtils.throwIf(user == null, StatusCode.UN_LOGIN_ERROR, "请先登录");
        // 判断是新增还是删除
        Long pictureId = null;
        if (pictureUploadRequest.getId() != null) {
            pictureId = pictureUploadRequest.getId();
        }
        // 如果是更新，判断数据库里是否存在
        if (pictureId != null) {
            Picture picture = this.getById(pictureId);
            ThrowUtils.throwIf(picture == null, StatusCode.NOT_FOUND_ERROR, "图片不存在");
        }
        // 按照用户id创建上传路径
        String path = String.format("/public/%s", user.getId());
        // 上传图片，得到图片信息
        UploadPictureResult uploadPictureResult = fileManager.uploadFile(multipartFile, path);
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(user.getId());
        // 操作数据库
        // 如果pictureId不为空，则更新
        if (pictureId != null) {
            // 如果是更新，则设置id和创建时间
            picture.setId(pictureId);
            picture.setCreateTime(new Date());
        }
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, StatusCode.SERVER_ERROR, "图片上传失败");
        return PictureVO.objToVo(picture);
    }

    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {

        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null){
            return queryWrapper;
        }

        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();

        if (StrUtil.isNotBlank(searchText)){
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("introduction", searchText));
        }

        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);

        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }

        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;

    }

    /**
     * 获取图片封装类
     */
    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    /**
     * 分页获取图片封装
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    /**
     * 验证数据
     */
    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, StatusCode.PARAM_ERROR);
        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id 不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), StatusCode.PARAM_ERROR, "id 不能为空");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, StatusCode.PARAM_ERROR, "url 过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, StatusCode.PARAM_ERROR, "简介过长");
        }
    }

}




