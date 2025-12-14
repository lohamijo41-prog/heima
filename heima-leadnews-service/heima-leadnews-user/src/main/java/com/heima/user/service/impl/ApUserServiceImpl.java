package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.utils.JWTUtil;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import java.util.HashMap;
import java.util.Map;
@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {
    /**
     * app端登录功能
     */
    @Override
    public ResponseResult login(LoginDto dto) {
        // 1.正常登录 用户名和密码
        if(StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getPassword())){

            // 打印日志：看看前端到底传了啥
            log.info("【登录调试】前端传入手机号: {}, 密码: {}", dto.getPhone(), dto.getPassword());

            // 1.1 根据手机号查询用户信息
            ApUser dbUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));

            if(dbUser == null){
                log.error("【登录调试】数据库里没找到这个手机号: {}", dto.getPhone());
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户信息不存在");
            }

            // 1.2 比对密码
            String salt = dbUser.getSalt();
            String password = dto.getPassword();
            String pswd = DigestUtils.md5DigestAsHex((password + salt).getBytes());

            // 打印日志：看看密码比对情况
            log.info("【登录调试】数据库Salt: {}", salt);
            log.info("【登录调试】数据库存的密文: {}", dbUser.getPassword());
            log.info("【登录调试】计算出来的密文: {}", pswd);

            if(!pswd.equals(dbUser.getPassword())){
                log.error("【登录调试】密码对不上！");
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }

            // 1.3 返回数据
            String token = JWTUtil.getToken(dbUser.getId().longValue());
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            dbUser.setPassword("");
            dbUser.setSalt("");
            map.put("user",dbUser);
            return ResponseResult.okResult(map);
        }else {
            // 2.游客登录
            Map<String,Object> map = new HashMap<>();
            map.put("token", JWTUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }
    }
}
