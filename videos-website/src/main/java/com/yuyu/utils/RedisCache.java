package com.yuyu.utils;

import com.yuyu.pojo.DO.Dialogue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Component
/**
 * redis的工具类
 */
public class RedisCache
{
    @Autowired
    public RedisTemplate redisTemplate;



    /**
     * 实现了zset中score的增加操作
     * @param key
     * @param value
     */
    public void increZsetNum(final String key,final String value){
        redisTemplate.opsForZSet().incrementScore(key,value,1);

    }

    /**
     * 实现map中value值的增加操作
     * @param key
     * @param value
     */
    public void increMapNum(final String key,final String value){
        redisTemplate.opsForHash().increment(key,value,1);
    }

    /**
     * 实现map中value值的减少操作
     * @param key
     * @param value
     */
    public void decreMapNum(final String key,final String value){
        redisTemplate.opsForHash().increment(key,value,-1);
    }


    /**
     * 创建zset
     * @param key
     * @param value
     * @param score
     * @param <T>
     */
    public <T> void setCacheZset(final String key, final T value, final Double score){
        DefaultTypedTuple<String> stringDefaultTypedTuple = new DefaultTypedTuple<>(value.toString(), score);
        HashSet<DefaultTypedTuple> defaultTypedTuples = new HashSet<>();
        defaultTypedTuples.add(stringDefaultTypedTuple);
        redisTemplate.opsForZSet().add(key,defaultTypedTuples);
    }

    /**
     * 移除zset
     * @param key
     */
    public void removeZset(final String key){
        redisTemplate.delete(key);
    }

    /**
     * 获取zset
     * @param key
     * @param <T>
     */
    public <T> void getCacheZset(final String key){
        redisTemplate.opsForZSet();
    }

    /**
     * 获取zset中value的个数
     * @param key
     * @return value的个数
     */
    public Long getZsetNum(final String key){
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 获取排序后zset集合
     * @param key
     * @return 排序后的集合
     * @param <T>
     */
    public <T> Set<T> sortNum(final String key){
        Set<T> set = redisTemplate.opsForZSet().reverseRangeWithScores(key,Long.MIN_VALUE,Long.MAX_VALUE);
        return set;
    }
    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value)
    {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit)
    {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout)
    {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit)
    {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key)
    {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key)
    {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection)
    {
        return redisTemplate.delete(collection);
    }

    /**
     * 将新数据存入List中
     * @param key 存储的键
     * @param data 数据
     * @param <T> 泛型方法
     */
    public <T> void addCacheList(final String key, final T data)
    {
        redisTemplate.opsForList().rightPush(key,data);
    }

    /**
     * 缓存List数据
     * @param key 缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList)
    {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key)
    {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 用于向原来集合中添加元素
     * @param key redis中的键名
     * @param addObject 要加入的数据
     */
    public <T> void addCacheSet(final String key, final T addObject){
        redisTemplate.opsForSet().add(key,addObject);
    }

    /**
     * 用于替换原来集合中的旧元素
     * @param key redis中的键名
     * @param dialogue
     * @param primitive
     */
    public <T> void replaceCacheSet(final String key,final T dialogue,final T primitive){
        redisTemplate.opsForSet().remove(key,primitive);
        redisTemplate.opsForSet().add(key,dialogue);
    }

    /**
     * 缓存Set
     * @param key 缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet)
    {

        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext())
        {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key)
    {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap)
    {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key)
    {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value)
    {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey)
    {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 删除Hash中的数据
     * 
     * @param key
     * @param hkey
     */
    public void delCacheMapValue(final String key, final String hkey)
    {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(key, hkey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys)
    {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern)
    {
        return redisTemplate.keys(pattern);
    }
}
