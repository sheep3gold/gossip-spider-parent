package cn.itheima.spider.version2.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaSpiderProducer {
    private static KafkaProducer<String, String> producer;

    static {
        //1.创建kafka的生产者对象
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.78.141:9092,192.168.78.142:9092,192.168.78.143:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<String, String>(props);
    }

    public void messageToKafka(String newsJson) {
        //2.发送数据
        ProducerRecord<String, String> record = new ProducerRecord<>("gossip-newsJson", newsJson);
        producer.send(record);
        producer.flush();
    }
}
