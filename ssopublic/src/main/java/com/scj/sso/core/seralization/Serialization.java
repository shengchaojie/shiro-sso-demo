package com.scj.sso.core.seralization;

public interface Serialization {

    byte[] seralize(Object t);

    Object deseralize(byte[] bytes);

}
