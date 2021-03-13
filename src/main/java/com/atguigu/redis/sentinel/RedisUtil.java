package com.atguigu.redis.sentinel;

import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedisUtil {
    public static void main(String[] args) {

        //Jedis jedis = RedisUtil.getJedis();
        Jedis jedis = RedisUtil.getJedisFormSentinel();
//        Jedis jedis = new Jedis("hadoop102",6379);
//        System.out.println(jedis.ping());

        jedis.set("k1000","v1000");

        Map<String, String> userMap = jedis.hgetAll("user:0101");
        for (Map.Entry<String, String> entry : userMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        Set<Tuple> topTuple = jedis.zrevrangeWithScores("article:topn", 0, 2);
        for (Tuple tuple : topTuple) {
            System.out.println(tuple.getElement() + ":" + tuple.getScore());
        }

        Set<String> keyset = jedis.keys("*");
        for (String key : keyset) {
            System.out.println(key);
        }
        jedis.close();
    }

    private static JedisPool jedisPool=null;
    public static Jedis getJedis(){

        if(jedisPool==null){
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            //最大连接
            jedisPoolConfig.setMaxTotal(100);
            //最大、最小空闲
            jedisPoolConfig.setMinIdle(20);
            jedisPoolConfig.setMinIdle(30);
            //资源耗尽时等待
            jedisPoolConfig.setBlockWhenExhausted(true);
            jedisPoolConfig.setMaxWaitMillis(5000);
            //从池中去连接后要进行测试
            //导致连接池中的连接坏掉：1.服务器端重启过  2.网断过  3.服务器端维持空闲时间连接超时
            jedisPoolConfig.setTestOnBorrow(true);
            //jedisPoolConfig.setTestWhileIdle(true);
            jedisPool=new JedisPool("hadoop102",6379);

        }
        Jedis jedis = jedisPool.getResource();
        return jedis;

    }
    //  could not  get resource from pool
    //1 检查地址端口
    //2  检查bind 是否注掉了
    //3  检查连接池资源是否耗尽 ，jedis使用后 没有通过close 还给池子

    private static JedisSentinelPool jedisSentinelPool=null;
    public static Jedis getJedisFormSentinel(){
        if(jedisSentinelPool==null){
            //创建哨兵池
            Set<String> sentinels=new HashSet<String>();
            sentinels.add("192.168.133.102:26379");

            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            //最大连接
            jedisPoolConfig.setMaxTotal(100);
            //最大、最小空闲
            jedisPoolConfig.setMinIdle(20);
            jedisPoolConfig.setMinIdle(30);
            //资源耗尽时等待
            jedisPoolConfig.setBlockWhenExhausted(true);
            jedisPoolConfig.setMaxWaitMillis(5000);
            //从池中去连接后要进行测试
            //导致连接池中的连接坏掉：1.服务器端重启过  2.网断过  3.服务器端维持空闲时间连接超时
            jedisPoolConfig.setTestOnBorrow(true);

            jedisSentinelPool = new JedisSentinelPool("mymaster",sentinels,jedisPoolConfig);

        }
        Jedis jedis = jedisSentinelPool.getResource();
        return jedis;

    }


}
