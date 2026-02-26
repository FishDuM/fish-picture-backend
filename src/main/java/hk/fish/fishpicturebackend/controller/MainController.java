package hk.fish.fishpicturebackend.controller;

import hk.fish.fishpicturebackend.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口
 */
@RestController
public class MainController {
    @GetMapping("/help")
    public BaseResponse help(){
        return BaseResponse.success("ok");
    }
}
