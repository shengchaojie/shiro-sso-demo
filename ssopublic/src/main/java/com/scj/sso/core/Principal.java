package com.scj.sso.core;

import lombok.Data;

import java.io.Serializable;

@Data
public class Principal implements Serializable{

    private Long userId;

    private String username;

    private String telephone;

}
