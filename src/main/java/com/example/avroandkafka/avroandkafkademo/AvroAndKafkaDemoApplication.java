package com.example.avroandkafka.avroandkafkademo;

import com.example.avroandkafka.avroandkafkademo.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class AvroAndKafkaDemoApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(AvroAndKafkaDemoApplication.class, args);


        MessageProducer producer = context.getBean(MessageProducer.class);
        MessageListener listener = context.getBean(MessageListener.class);



        /*
         * Sending message to 'greeting' topic. This will send
         * and received a java object with the help of
         * greetingKafkaListenerContainerFactory.
         */
        producer.sendOrderMessage(new Order(22, "How to be happy?", 2000));
        listener.orderLatch.await(10, TimeUnit.SECONDS);

        context.close();
    }

    @Bean
    public MessageProducer messageProducer() {
        return new MessageProducer();
    }

    @Bean
    public MessageListener messageListener() {
        return new MessageListener();
    }

    public static class MessageProducer {


        @Autowired
        private KafkaTemplate<String, Order> orderKafkaTemplate;

        @Value(value = "${order.topic.name}")
        private String orderTopicName;


        public void sendOrderMessage(Order order) {
            orderKafkaTemplate.send(orderTopicName, order);
        }
    }

    public static class MessageListener {

        private CountDownLatch latch = new CountDownLatch(3);

        private CountDownLatch orderLatch = new CountDownLatch(1);

        @KafkaListener(topics = "${order.topic.name}", groupId = "foo", containerFactory = "fooKafkaListenerContainerFactory")
        public void listenGroupFoo(Order message) {
            System.out.println("Received Message in group 'foo': " + message);
            latch.countDown();
        }

        @KafkaListener(topics = "${order.topic.name}", groupId = "bar", containerFactory = "barKafkaListenerContainerFactory")
        public void listenGroupBar(Order message) {
            System.out.println("Received Message in group 'bar': " + message);
            latch.countDown();
        }


        @KafkaListener(topics = "${order.topic.name}", containerFactory = "greetingKafkaListenerContainerFactory")
        public void orderListener(Order order) {
            System.out.println("Received greeting message: " + order);
            this.orderLatch.countDown();
        }

    }

}
