package com.zhihu.wenda.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class JedisAdaptor implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdaptor.class);
    private JedisPool pool;


    public static void main(String[] args){
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功！");
        // 查看服务是否运行
        System.out.println("服务正在运行：" + jedis.ping());

        // Value为String数据类型
        jedis.set("wenda", "zhihu");
        System.out.println("String: "+jedis.get("wenda"));

        // Value为HashMap数据类型
        String userKey = "user";
        // 设置值
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "18618181818");
        // 删除值
        jedis.hdel(userKey, "phone");
        // 获取所有值
        Map<String, String> hashMap = jedis.hgetAll(userKey);
        System.out.println("hashMap:"+hashMap);
        Set<String> keys = jedis.hkeys(userKey);
        List<String> vals = jedis.hvals(userKey);
        // 如果字段不存在，则创建字段并复制，否则不进行任何操作
        jedis.hsetnx(userKey, "school", "zju");
        jedis.hsetnx(userKey, "name", "yxy");
        hashMap = jedis.hgetAll(userKey);
        System.out.println("hashMap:"+hashMap);


        // Value为List数据类型
        jedis.lpush("site-list", "zhihu", "nowcoder");
        List<String> list = jedis.lrange("site-list", 0,1);
        System.out.println("list:"+list);

        // Value为Set数据类型
        String setKey1 = "commentLike1";
        String setKey2 = "commentLike2";
        for(int i = 0; i < 10; i++){
            jedis.sadd(setKey1, String.valueOf(i));
            jedis.sadd(setKey2, String.valueOf(i*i));
        }
        Set<String> set1 = jedis.smembers(setKey1);
        Set<String> set2 = jedis.smembers(setKey2);
        System.out.println("Set: "+set1);
        System.out.println("Set:"+set2);
        // 交集
        Set<String> interSet = jedis.sinter(setKey1, setKey2);
        // 并集
        Set<String> unionSet = jedis.sunion(setKey1, setKey2);
        // 差集
        Set<String> diffSet = jedis.sdiff(setKey1, setKey2);

        // Value为SortedSet数据类型
        String zsetKey = "zset";
        // 增
        jedis.zadd(zsetKey, 1, "a");
        jedis.zadd(zsetKey, 1, "b");
        jedis.zadd(zsetKey, 1, "c");
        jedis.zadd(zsetKey, 1, "d");
        jedis.zadd(zsetKey, 1, "e");
        Set<String> zset = jedis.zrange(zsetKey, 0, 4);
        System.out.println("zSet:"+zset);

        // 对Key进行操作
        //jedis.rename(oldkey, newkey)
        //jedis.ttl(key) 以秒为单位返回 key 的剩余过期时间。
        //jedis.type(key)
        //jedis.expire(s, seconds) //设置key的过期时间
        //redis连接指定的数据库
        //jedis.select(index) 0--15
    }

    @Override
    public void afterPropertiesSet()throws Exception{
        pool = new JedisPool("redis://localhost:6379");
    }

    public long sadd(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        }catch (Exception e){
            logger.error("sadd发生异常"+e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key,value);
        }catch (Exception e){
            logger.error("srem发生异常"+e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            // 返回集合 key 的基数(集合中元素的数量)。
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("scard发生异常"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        }catch (Exception e){
            logger.error("sismember发生异常"+e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return false;
    }

    public List<String> brpop(int timeout, String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            // 它是 RPOP key 命令的阻塞版本，当给定列表内没有任何元素可供弹出的时候，
            // 连接将被 BRPOP 命令阻塞，直到等待超时或发现可弹出元素为止。
            // 返回值
            // 假如在指定时间内没有任何元素被弹出，则返回一个 nil 和等待时长。
            // 反之，返回一个含有两个元素的列表，第一个元素是被弹出元素所属的 key ，第二个元素是被弹出元素的值。
            return jedis.brpop(timeout, key);
        } catch (Exception e){
            logger.error("brpop发生异常"+e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }

    public long lpush(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e){
            logger.error("lpush发生异常"+e.getMessage());
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public Jedis getJedis(){
        return pool.getResource();
    }

    public Transaction multi(Jedis jedis){
        try {
            return jedis.multi();
        } catch (Exception e){
            logger.error("Redis创建事务发生异常！ "+ e.getMessage());
        }
        return null;
    }

    public List<Object> exec(Transaction tx, Jedis jedis){
        try {
            // 返回值
            // 事务块内所有命令的返回值，按命令执行的先后顺序排列。
            // 当操作被打断时，返回空值 nil 。
            return tx.exec();
        } catch (Exception e){
            logger.error("Redis事务执行失败！" + e.getMessage());
            // 取消事务，放弃执行事务块内的所有命令。
            // 如果正在使用 WATCH 命令监视某个(或某些) key，那么取消所有监视，
            // 等同于执行命令 UNWATCH 。
            //
            //返回值
            //总是返回 OK 。
            tx.discard();
        } finally {
            if(tx != null){
                try {
                    tx.close();
                } catch (IOException e) {
                    logger.error("Redis关闭事务异常！");
                    e.printStackTrace();
                }
            }

            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zadd(String key, double score, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.zadd(key, score, value);
        } catch (Exception e){
            logger.error("zadd发生异常！" + e.getMessage());
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public long zrem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrem(key, value);
        } catch (Exception e) {
            logger.error("zrem发生异常!" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Set<String> zrange(String key, int start, int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e){
            logger.error("zrange发生异常！" + e.getMessage());
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }

    public Set<String> zrevrange(String key, int start, int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e){
            logger.error("zrevrange发生异常！" + e.getMessage());
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }

    public long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("zcard发生异常!" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Double zscore(String key, String member){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception e){
            logger.error("zscore发生异常！"+e.getMessage());
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }
}
