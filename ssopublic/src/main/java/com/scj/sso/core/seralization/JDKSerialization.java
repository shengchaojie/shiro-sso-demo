package com.scj.sso.core.seralization;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 序列化默认用JDK
 */
public class JDKSerialization implements Serialization {
    @Override
    public byte[] seralize(Object t) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(t);
            return bos.toByteArray();
        }catch (Exception ex){
            throw new RuntimeException("序列化错误",ex);
        }
    }

    @Override
    public Object deseralize(byte[] bytes) {
        try(ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(bis)){
            return ois.readObject();
        }catch (Exception ex){
            throw new RuntimeException("反序列化错误",ex);
        }
    }
}
