package com.scj.sso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebResult<T> {

    private T object;

    //1 success 0 failed
    private boolean flag;

    private Long code;

    private String message;

    public WebResult(T object, boolean flag) {
        this.object = object;
        this.flag = flag;
    }
}
