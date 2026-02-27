package hk.fish.fishpicturebackend.aop;

import hk.fish.fishpicturebackend.annotation.AuthCheck;
import hk.fish.fishpicturebackend.domain.entity.User;
import hk.fish.fishpicturebackend.domain.enums.UserRole;
import hk.fish.fishpicturebackend.exception.StatusCode;
import hk.fish.fishpicturebackend.exception.ThrowUtils;
import hk.fish.fishpicturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 鉴权拦截器
 */
@Aspect
@Component
public class AuthInterceptor {
    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        // 获取当前登录用户
        User loginUser = userService.getCurrentUserInSystem(request);
        UserRole mustUserRole = UserRole.getUserRoleByValue(mustRole);
        // 如果不需要权限就放行
        if (mustUserRole == null){
            return joinPoint.proceed();
        }
        // 必须有权限才会通过以下代码
        String userRole = loginUser.getUserRole();
        UserRole userRoleEnum = UserRole.getUserRoleByValue(userRole);
        if (userRoleEnum == null){
            ThrowUtils.error(StatusCode.FORBIDDEN_ERROR);
        }
        // 需要管理员权限
        if (mustRole.equals(UserRole.ADMIN.getValue()) && userRoleEnum != UserRole.ADMIN){
            ThrowUtils.error(StatusCode.FORBIDDEN_ERROR);
        }
        // 放行
        return joinPoint.proceed();
    }

}
