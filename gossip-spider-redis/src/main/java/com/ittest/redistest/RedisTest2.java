package com.ittest.redistest;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisTest2 {
    public static void main(String[] args) {
        Jedis jedis = null;

        try {
            jedis = new Jedis("127.0.0.1", 6379);
            jedis.set("hello", "world");
            //1.String
            System.out.println(jedis.get("hello"));
            System.out.println(jedis.incr("counter"));
            //2.hash
            jedis.hset("myhash", "f1", "v1");
            jedis.hset("myhash", "f2", "v2");
            Map<String, String> map = jedis.hgetAll("myhash");
            System.out.println(map);
//            3.list
            jedis.rpush("mylist", "1");
            jedis.rpush("mylist", "2");
            jedis.rpush("mylist", "3");
            List<String> mylist = jedis.lrange("mylist", 0, -1);
            System.out.println(mylist);
//            4.set
            jedis.sadd("myset", "a");
            jedis.sadd("myset", "b");
            jedis.sadd("myset", "a");
            Set<String> myset = jedis.smembers("myset");
            System.out.println(myset);
//            5.zset
            jedis.zadd("myzset", 99, "tom");
            jedis.zadd("myzset", 66, "peter");
            jedis.zadd("myzset", 33, "james");
            Set<Tuple> myzset = jedis.zrangeWithScores("myzset", 0, -1);
            for (Tuple tuple : myzset) {
//                String string = tuple.toString();
                String string = tuple.getElement() + " " + tuple.getScore();
                System.out.println(string);
            }
//            System.out.println(myzset);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
