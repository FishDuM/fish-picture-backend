package hk.fish.fishpicturebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hk.fish.fishpicturebackend.domain.entity.Picture;
import hk.fish.fishpicturebackend.service.PictureService;
import hk.fish.fishpicturebackend.mapper.PictureMapper;
import org.springframework.stereotype.Service;

/**
* @author 30574
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2026-03-01 13:55:43
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

}




