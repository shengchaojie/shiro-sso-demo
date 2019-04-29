package com.scj.sso.core;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息载体
 * 这个其实就相当于session
 */
@Data
public class Principal implements Serializable{

    private static final long serialVersionUID = 5231134212346077681L;

    private Long userId;

    private String username;

    private String telephone;


}
