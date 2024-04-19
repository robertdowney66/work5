package com.yuyu.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 封装了user的部分属性，用于传输
 */
public class UserDTO implements Serializable {
    private static final long serialVersionUID = -4035678542386831255L;


    private Long id;
    private String userName;
    private String avatarUrl;
}
