package com.scj.sso.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by shengchaojie on 2018/3/15.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebResult<T> {

    T data;

    private String message;

    private Long code;

    private boolean flag;


}
