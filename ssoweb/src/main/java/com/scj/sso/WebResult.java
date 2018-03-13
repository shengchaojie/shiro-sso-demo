package com.scj.sso;

public class WebResult<T> {

    private T object;

    //1 success 0 failed
    private boolean flag;

    private String message;

    public WebResult(T object, boolean flag) {
        this.object = object;
        this.flag = flag;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
