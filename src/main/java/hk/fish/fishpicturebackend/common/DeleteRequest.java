package hk.fish.fishpicturebackend.common;

import lombok.Data;

// 通用的删除请求
@Data
public class DeleteRequest {
    // 删除的id
    private Long id;
}
