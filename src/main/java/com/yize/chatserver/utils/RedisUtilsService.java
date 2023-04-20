/*
 * Copyright (c)  $toda.year yize.link
 * editor: yize
 * date:
 * @author yize<vcsimno@163.com>
 * 本开源由yize发布和开发，部分工具引用了其他优秀团队的开源工具包。
 */

package com.yize.chatserver.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.yize.chatserver.config.RedisConfig;
import com.yize.chatserver.exception.YiException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis 工具类
 */
@DependsOn({"springUtils"})
public class RedisUtilsService {

    public static RedisUtils instance(String index) {
        return new RedisUtils(index).get();
    }


    public static class RedisUtils{
        private final RedisTemplate<String, String> redisTemplate;

        public RedisUtils(String index){
            redisTemplate = SpringUtil.getBean(RedisConfig.class).getResource(index);
        }
        public RedisUtils get(){
            return this;
        }
        /**
         * 指定缓存失效时间
         *
         * @param key  键
         * @param time 时间(秒)
         */
        public void expire(String key, long time) {
            try {
                if (time > 0) {
                    redisTemplate.expire(key, time, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                throw new YiException(e.getMessage());
            }
        }

        public Set<String> keys(String key){
            return redisTemplate.keys(key);
        }

        /**
         * 根据key 获取过期时间
         *
         * @param key 键 不能为null
         * @return 时间(秒) 返回0代表为永久有效
         */
        public long getExpire(String key) {
            Optional<Long> expire = Optional.ofNullable(redisTemplate.getExpire(key, TimeUnit.SECONDS));
            return expire.orElse(0L);
        }

        /**
         * 判断key是否存在
         *
         * @param key 键
         * @return true 存在 false不存在
         */
        public boolean hasKey(String key) {
            try {
                if(redisTemplate.hasKey(key) == null) return false;
                return Boolean.TRUE.equals(redisTemplate.hasKey(key));
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * 删除缓存
         *
         * @param key 可以传一个值 或多个
         */
        public void del(String... key) {
            try {
                Arrays.stream(key).forEach(e -> {
                    if(e != null) {
                        if (hasKey(e))
                            redisTemplate.delete(e);
                    }
                });
            } catch (Exception ignore) {

            }

        }

        //============================String=============================

        /**
         * 普通缓存获取
         *
         * @param key 键
         * @return 值
         */
        public String get(String key) {
            if(redisTemplate.hasKey(key)){
                return redisTemplate.opsForValue().get(key);
            }
            return "";
        }

        /**
         * 获取KEY对应的字符串
         *
         * @param key 健
         * @return 值
         */
        public String getString(String key) {
            if(!redisTemplate.hasKey(key)){
                return "";
            }
            Optional<Object> result = Optional.ofNullable(redisTemplate.opsForValue().get(key));
            return result.map(Object::toString).orElse("");
        }

        /**
         * 普通缓存放入
         *
         * @param key   键
         * @param value 值
         */
        public void set(String key, String value) {
            try {
                redisTemplate.opsForValue().set(key, value);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        /**
         * 普通缓存放入并设置时间
         *
         * @param key   键
         * @param value 值
         * @param time  毫秒(秒) time要大于0 如果time小于等于0 将设置无限期
         */
        public void set(String key, String value, long time) {
            try {
                if (time > 0) {
                    redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
                } else {
                    set(key, value);
                }
            } catch (Exception ignore) {

            }
        }

        /**
         * 递增
         *
         * @param key   键
         * @param delta 要增加几(大于0)
         * @return 值
         */
        public long incr(String key, long delta) {
            if (delta < 0) delta = 0;
            Optional<Long> result = Optional.ofNullable(redisTemplate.opsForValue().increment(key, delta));
            return result.orElse(0L);
        }

        /**
         * 递减
         *
         * @param key   键
         * @param delta 要减少几(小于0)
         * @return 值
         */
        public long decr(String key, long delta) {
            if (delta < 0) delta = 0;
            Optional<Long> result = Optional.ofNullable(redisTemplate.opsForValue().decrement(key, delta));
            return result.orElse(0L);
        }

        //================================Map=================================

        /**
         * HashGet
         *
         * @param key  键 不能为null
         * @param item 项 不能为null
         * @return 值
         */
        public Object hget(String key, String item) {
            return redisTemplate.opsForHash().get(key, item);
        }

        /**
         * 获取hashKey对应的所有键值
         *
         * @param key 键
         * @return 对应的多个键值
         */
        public Map<Object, Object> hmget(String key) {
            return redisTemplate.opsForHash().entries(key);
        }

        /**
         * HashSet
         *
         * @param key 键
         * @param map 对应多个键值
         */
        public void hmset(String key, Map<String, Object> map) {
            try {
                redisTemplate.opsForHash().putAll(key, map);
            } catch (Exception ignore) {

            }
        }

        /**
         * HashSet 并设置时间
         *
         * @param key  键
         * @param map  对应多个键值
         * @param time 时间(秒)
         */
        public void hmset(String key, Map<String, Object> map, long time) {
            try {
                redisTemplate.opsForHash().putAll(key, map);
                if (time > 0) {
                    expire(key, time);
                }

            } catch (Exception ignore) {

            }
        }

        /**
         * 向一张hash表中放入数据,如果不存在将创建
         *
         * @param key   键
         * @param item  项
         * @param value 值
         */
        public void hset(String key, String item, Object value) {
            try {
                redisTemplate.opsForHash().put(key, item, value);
            } catch (Exception ignore) {

            }
        }

        /**
         * 向一张hash表中放入数据,如果不存在将创建
         *
         * @param key   键
         * @param item  项
         * @param value 值
         * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
         * @return true 成功 false失败
         */
        public boolean hset(String key, String item, Object value, long time) {
            try {
                redisTemplate.opsForHash().put(key, item, value);
                if (time > 0) {
                    expire(key, time);
                }
                return true;
            } catch (Exception ignore) {

                return false;
            }
        }

        /**
         * 删除hash表中的值
         *
         * @param key  键 不能为null
         * @param item 项 可以使多个 不能为null
         */
        public void hdel(String key, Object... item) {
            try {
                redisTemplate.opsForHash().delete(key, item);
            } catch (Exception ignore) {

            }
        }

        /**
         * 判断hash表中是否有该项的值
         *
         * @param key  键 不能为null
         * @param item 项 不能为null
         * @return true 存在 false不存在
         */
        public boolean hHasKey(String key, String item) {
            return redisTemplate.opsForHash().hasKey(key, item);
        }

        /**
         * hash递增 如果不存在,就会创建一个 并把新增后的值返回
         *
         * @param key  键
         * @param item 项
         * @param by   要增加几(大于0)
         * @return 值
         */
        public double hincr(String key, String item, double by) {
            return redisTemplate.opsForHash().increment(key, item, by);
        }

        /**
         * hash递减
         *
         * @param key  键
         * @param item 项
         * @param by   要减少记(小于0)
         * @return 值
         */
        public double hdecr(String key, String item, double by) {
            return redisTemplate.opsForHash().increment(key, item, -by);
        }

        //============================set=============================

        /**
         * 根据key获取Set中的所有值
         *
         * @param key 键
         * @return 值
         */
        public Set<String> sGet(String key) {
            try {
                return redisTemplate.opsForSet().members(key);
            } catch (Exception ignore) {
                return null;
            }
        }

        /**
         * 根据value从一个set中查询,是否存在
         *
         * @param key   键
         * @param value 值
         * @return true 存在 false不存在
         */
        public boolean sHasKey(String key, String value) {
            try {
                return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
            } catch (Exception ignore) {
                return false;
            }
        }

        /**
         * 将数据放入set缓存
         *
         * @param key    键
         * @param values 值 可以是多个
         * @return 成功个数
         */
        public long sSet(String key, String... values) {
            try {
                Optional<Long> result = Optional.ofNullable(redisTemplate.opsForSet().add(key, values));
                return result.orElse(0L);
            } catch (Exception ignore) {
                return 0;
            }
        }

        /**
         * 将set数据放入缓存
         *
         * @param key    键
         * @param time   时间(秒)
         * @param values 值 可以是多个
         * @return 成功个数
         */
        public long sSetAndTime(String key, long time, String... values) {
            try {
                Optional<Long> count = Optional.ofNullable(redisTemplate.opsForSet().add(key, values));
                if (time > 0) expire(key, time);
                return count.orElse(0L);
            } catch (Exception ignore) {

                return 0;
            }
        }

        /**
         * 获取set缓存的长度
         *
         * @param key 键
         * @return 值
         */
        public long sGetSetSize(String key) {
            try {
                Optional<Long> result = Optional.ofNullable(redisTemplate.opsForSet().size(key));
                return result.orElse(0L);
            } catch (Exception ignore) {

                return 0;
            }
        }

        /**
         * 移除值为value的
         *
         * @param key    键
         * @param values 值 可以是多个
         * @return 移除的个数
         */
        public long setRemove(String key, Object... values) {
            try {
                Optional<Long> result = Optional.ofNullable(redisTemplate.opsForSet().remove(key, values));
                return result.orElse(0L);
            } catch (Exception ignore) {

                return 0;
            }
        }
        //===============================list=================================

        /**
         * 获取list缓存的内容
         *
         * @param key   键
         * @param start 开始
         * @param end   结束  0 到 -1代表所有值
         * @return 值
         */
        public List<String> lGet(String key, long start, long end) {
            try {
                return redisTemplate.opsForList().range(key, start, end);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * 获取list缓存的长度
         *
         * @param key 键
         * @return 值
         */
        public long lGetListSize(String key) {
            try {
                Optional<Long> result = Optional.ofNullable(redisTemplate.opsForList().size(key));
                return result.orElse(0L);
            } catch (Exception e) {
                return 0;
            }
        }

        /**
         * 通过索引 获取list中的值
         *
         * @param key   键
         * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
         * @return 值
         */
        public String lGetIndex(String key, long index) {
            try {
                return redisTemplate.opsForList().index(key, index);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * 将list放入缓存
         *
         * @param key   键
         * @param value 时间(秒)
         * @return 值
         */
        public boolean lSet(String key, String value) {
            try {
                redisTemplate.opsForList().rightPush(key, value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * 将list放入缓存
         *
         * @param key   键
         * @param value 值
         * @param time  时间(秒)
         * @return 值
         */
        public boolean lSet(String key, String value, long time) {
            try {
                redisTemplate.opsForList().rightPush(key, value);
                if (time > 0) expire(key, time);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * 将list放入缓存
         *
         * @param key   键
         * @param value 时间(秒)
         * @return 值
         */
        public boolean lSet(String key, List<String> value) {
            try {
                redisTemplate.opsForList().rightPushAll(key, value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * 将list放入缓存
         *
         * @param key   键
         * @param value 值
         * @param time  时间(秒)
         * @return 结果
         */
        public boolean lSet(String key, List<String> value, long time) {
            try {
                redisTemplate.opsForList().rightPushAll(key, value);
                if (time > 0) expire(key, time);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * 根据索引修改list中的某条数据
         *
         * @param key   键
         * @param index 索引
         * @param value 值
         * @return 结果
         */
        public boolean lUpdateIndex(String key, long index, String value) {
            try {
                redisTemplate.opsForList().set(key, index, value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * 移除N个值为value
         *
         * @param key   键
         * @param count 移除多少个
         * @param value 值
         * @return 移除的个数
         */
        public long lRemove(String key, long count, Object value) {
            try {
                Optional<Long> result = Optional.ofNullable(redisTemplate.opsForList().remove(key, count, value));
                return result.orElse(0L);
            } catch (Exception e) {
                return 0;
            }
        }
    }


}
