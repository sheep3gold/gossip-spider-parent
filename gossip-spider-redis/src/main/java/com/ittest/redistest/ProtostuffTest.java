package com.ittest.redistest;

import redis.clients.jedis.Jedis;

import java.util.Date;

public class ProtostuffTest {
    public static void main(String[] args) {
        ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String key = "club:1";
        Club club = new Club(1, "AC", "米兰", new Date(), 1);
        //序列化
        byte[] clubBytes = protostuffSerializer.serialize(club);
        jedis.set(key.getBytes(), clubBytes);
        //反序列化
        byte[] resultBytes = jedis.get(key.getBytes());
        Club resultClub = protostuffSerializer.deserialize(resultBytes);
        System.out.println(resultClub.getId());
    }
}
