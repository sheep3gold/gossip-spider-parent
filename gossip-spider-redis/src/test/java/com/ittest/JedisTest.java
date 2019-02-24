package com.ittest;

import com.ittest.redisUtils.JedisUtil;
import org.junit.Test;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class JedisTest {
    @Test
    public void jedisOne() {
        //创建jedis对象
        Jedis jedis = new Jedis("192.168.78.141", 6379);

        //2.测试是否连通
        String pong = jedis.ping();

        System.out.println(pong);

        jedis.close();
    }

    /**
     * 操作redis-->String
     * @throws InterruptedException
     */
    @Test
    public void jedisOfString() throws InterruptedException {
        Jedis jedis = new Jedis("192.168.78.141", 6379);
        //存值
        jedis.set("age", "19");
        String age = jedis.get("age");
        System.out.println(age);

        //删除值
//        jedis.del("age");

        //对age进行+1操作
        Long incr = jedis.incr("age");
        System.out.println(incr);

        //进行-1操作
        Long decr = jedis.decr("age");
        System.out.println(decr);

        //拼接字符串：如果key存在就是拼接，不存在创建
        jedis.append("hobby", "篮球");
        String hobby = jedis.get("hobby");
        System.out.println(hobby);

        //为key设置有效时长
        Long expire = jedis.expire("hobby", 5);
        System.out.println(expire);
        while (jedis.exists("hobby")) {
            System.out.println(jedis.ttl("hobby"));
            Thread.sleep(1000);
        }

        //为新建的key设置有效时间
        jedis.setex("date", 10, "2018.9.15");

        while (jedis.exists("date")) {
            System.out.println(jedis.ttl("date"));
            Thread.sleep(1000);
        }

        jedis.close();
    }

    /**
     *  使用redis操作list
     */
    @Test
    public void jedisOfList() {
        Jedis jedis = new Jedis("192.168.78.141", 6379);
        jedis.del("list1");
        jedis.del("list2");

        jedis.lpush("list1", "a", "b", "c", "d");
        String rElement = jedis.rpop("list1");
        System.out.println(rElement);//a

        jedis.rpush("list2", "a", "b", "c", "d");
        String lElement = jedis.lpop("list2");
        System.out.println(lElement);//a

        List<String> list = jedis.lrange("list1", 0, -1);
        System.out.println(list);

        Long llen = jedis.llen("list2");
        System.out.println(llen);//3

        jedis.linsert("list1", BinaryClient.LIST_POSITION.BEFORE, "b", "0");
        jedis.linsert("list1", BinaryClient.LIST_POSITION.AFTER, "c", "1");
        //d,c,1,0,b
        jedis.rpoplpush("list1", "list1");
        //b,d,c,1,0
        list = jedis.lrange("list1", 0, -1);
        System.out.println(list);

        jedis.close();
    }

    /**
     * 使用redis操作hash
     */
    @Test
    public void jedisOfHash() {
        Jedis jedis = new Jedis("192.168.78.141", 6379);

        jedis.hset("person", "name", "隔壁老王");
        jedis.hset("person", "age", "30");
        jedis.hset("person", "birthday", "1988年9.15");

        String name = jedis.hget("person", "name");
        String age = jedis.hget("person", "age");
        String birthday = jedis.hget("person", "birthday");
        System.out.println(name);
        System.out.println(age);
        System.out.println(birthday);

        //一次获取多个hash中的key的值
        List<String> values = jedis.hmget("person", "name", "age");
        System.out.println(values);

        //获取hash中的所有数据
        Map<String, String> map = jedis.hgetAll("person");
        for (String key : map.keySet()) {
            System.out.println(key + "  " + map.get(key));
        }

        //获取map中的所有的key和value
        Set<String> hkeys = jedis.hkeys("person");
        List<String> hvals = jedis.hvals("person");
        System.out.println(hkeys);
        System.out.println(hvals);

        //删除map中的某个key
        jedis.hdel("person", "name", "age");

        //删除整个map
        jedis.del("person");

        //释放资源
        jedis.close();
    }

    /**
     * 使用redis操作set
     */
    @Test
    public void jedisOfSet() {
        Jedis jedis = new Jedis("192.168.78.141", 6379);
        jedis.sadd("set1", "a", "b", "c", "d");
        jedis.sadd("set2", "a", "e", "f", "d");

        //获取数据
        Set<String> setList = jedis.smembers("set1");
        System.out.println(setList);

        //删除set中的指定的值
        jedis.srem("set1", "b", "d");

        //判断某个元素是否在set集合中
        Boolean is = jedis.sismember("set1", "b");
        System.out.println(is);//false

        //查看set集合中一共有多少个数据
        Long size = jedis.scard("set1");
        System.out.println(size);//2

        jedis.close();
    }

    /**
     * 使用redis操作sortedSet集合
     */
    @Test
    public void jedisOfsortedSet() {
        Jedis jedis = new Jedis("192.168.78.141", 6379);

        jedis.zadd("math", 98.2, "老王");
        jedis.zadd("math", 59.9, "小明");
        jedis.zadd("math", 79, "老张");
        jedis.zadd("math", 59.2, "小李");

        //查看sortedSet中某元素的排名:从小到大
        Long zrank = jedis.zrank("math", "小李");
        System.out.println(zrank);//0

        //查看sortedSet中的元素：从大到小
        Set<Tuple> zrevrange = jedis.zrevrangeWithScores("math", 0, -1);
        for (Tuple tuple : zrevrange) {
            String element = tuple.getElement();
            double score = tuple.getScore();
            System.out.println(element + " " + score);//王，张，明，李
        }

        //查看sortedSet中的元素：从小到大
        Set<Tuple> zrange = jedis.zrangeWithScores("math", 0, -1);
        for (Tuple tuple : zrange) {
            String element = tuple.getElement();
            double score = tuple.getScore();
            System.out.println(element + " " + score);
        }

        //删除某个元素
        jedis.zrem("math", "老张");

        jedis.close();

    }

    /**
     * 连接池测试
     */
    @Test
    public void JedisUtilTest() {
        Jedis jedis = JedisUtil.getJedis();
        String ping = jedis.ping();
        System.out.println(ping);
    }

    @Test
    public void Test() {
        Jedis jedis = new Jedis();
        jedis.lpush("list1", "1");
        List<String> list = jedis.brpop(20, "list1");
        System.out.println(list.get(0) + "  " + list.get(1));
        jedis.close();
    }


}
