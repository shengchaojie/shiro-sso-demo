package com.scj.sso.core;

import com.scj.sso.core.seralization.Serialization;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class RedisSessionDAO extends AbstractSessionDAO{

    private static final String REDIS_SESSION_KEY ="SSO:REDIS_SESSION_KEY";

    private StringRedisTemplate stringRedisTemplate;

    private Serialization serialization;

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        stringRedisTemplate.execute(new RedisCallback<Object>() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.hSet(REDIS_SESSION_KEY.getBytes(),sessionId.toString().getBytes(),serialization.seralize(session));
                return null;
            }
        });
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable serializable) {
        return (Session) stringRedisTemplate.execute(new RedisCallback<Object>() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] bytes = connection.hGet(REDIS_SESSION_KEY.getBytes(),serializable.toString().getBytes());
                return serialization.deseralize(bytes);
            }
        });
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        stringRedisTemplate.execute(new RedisCallback<Object>() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.hSet(REDIS_SESSION_KEY.getBytes(),session.getId().toString().getBytes(),serialization.seralize(session));
                return null;
            }
        });
    }

    @Override
    public void delete(Session session) {
        stringRedisTemplate.opsForHash().delete(REDIS_SESSION_KEY,session.getId().toString());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        List<Session> sessionList = new ArrayList<>();
        Set<Object> keys = stringRedisTemplate.opsForHash().keys(REDIS_SESSION_KEY);
        for (Object key:keys){
            sessionList.add((Session) stringRedisTemplate.execute(new RedisCallback<Object>() {
                @Nullable
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    byte[] bytes = connection.hGet(REDIS_SESSION_KEY.getBytes(),key.toString().getBytes());
                    return serialization.deseralize(bytes);
                }
            }));
        }
        return sessionList;
    }

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void setSerialization(Serialization serialization) {
        this.serialization = serialization;
    }
}
