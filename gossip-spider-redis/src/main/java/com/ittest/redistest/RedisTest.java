package com.ittest.redistest;

import redis.clients.jedis.Jedis;

public class RedisTest {
    public static void main(String[] args) {
        /*Jedis jedis = new Jedis("127.0.0.1", 6379);
        String setResult = jedis.set("hello", "world");
        String value = jedis.get("hello");
        System.out.println(setResult);
        System.out.println(value);*/
        Jedis jedis = null;
        try {
            jedis = new Jedis("127.0.0.1", 6379);
            jedis.get("hello");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }
}
