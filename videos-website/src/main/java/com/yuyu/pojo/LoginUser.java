package com.yuyu.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.yuyu.pojo.DO.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
/**
 * 用于用户登录时，实现登录验证的必须类
 */
public class LoginUser implements UserDetails {

    private User userDO;

    /**
     * 存储权限信息
     */
    private List<String> permissions;

    @JSONField(serialize = false)
    private List<GrantedAuthority> authorities;

    public LoginUser(User userDO, List<String> permissions) {
        this.userDO = userDO;
        this.permissions = permissions;
    }

    /**
     * 将list中权限封装到authorities构成collection中
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (authorities!=null){
            return authorities;
        }

        authorities = new ArrayList<>();
        for (String permission : permissions) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(permission);
            authorities.add(simpleGrantedAuthority);
        }

        return authorities;

    }

    @Override
    public String getPassword() {
        return userDO.getPassword();
    }

    @Override
    public String getUsername() {
        return userDO.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
