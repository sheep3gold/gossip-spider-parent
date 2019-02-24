package com.ittest.redistest;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TestJedisPool {
    public void testJedisPool() {
        //1 获得连接池配置对象，设置配置项
        JedisPoolConfig config = new JedisPoolConfig();

        // 1.1 最大连接数
        config.setMaxTotal(30);
        // 1.2  最大空闲连接数
        config.setMaxIdle(10);

        //2 获得连接池
        JedisPool jedisPool = new JedisPool(config, "localhost", 6379);

        //3 获得核心对象
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            //4 设置数据
            String name = jedis.get("name");
            System.out.println(name);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (jedis != null) {
                jedis.close();
            }
            if (jedisPool != null) {
                jedisPool.close();
            }
        }

    }
}
