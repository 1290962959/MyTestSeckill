package com.hb.MySeckill.config;

import com.hb.MySeckill.bean.MQConfigBean;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Configuration
public class MQConfig {
    @Value("${rabbitmq.address-list}")
    private String addressList;

    @Value("${rabbitmq.username}")
    private String username;

    @Value("${rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.virtual-host}")
    private String virtualHost;

    @Value("${rabbitmq.queue}")
    private String queue;

    @Value("${rabbitmq.publisher-confirm}")
    private boolean publisherConfirm;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public MQConfigBean mqConfigBean() {
        MQConfigBean mqConfigBean = new MQConfigBean();

        String[] addresStrList = addressList.split(",");
        List<Address> addresses = new LinkedList<>();
        for(String address : addresStrList) {
            String[] addressStr = address.split(":");

            addresses.add(new Address(addressStr[0], Integer.valueOf(addressStr[1])));
        }

        mqConfigBean.setAddressList(addresses);
        mqConfigBean.setUsername(username);
        mqConfigBean.setPassword(password);
        mqConfigBean.setVirtualHost(virtualHost);
        mqConfigBean.setQueue(queue);
        mqConfigBean.setPublisherConfirm(publisherConfirm);
        mqConfigBean.setExchange(exchange);
        mqConfigBean.setRoutingKey(routingKey);

        return mqConfigBean;
    }

    @Bean(name = "mqConnection")
    public Connection mqConnection(@Autowired MQConfigBean mqConfigBean) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);

        // 也可以使用uri的方式
        //factory.setUri("amqp://userName:password@hostName:portNumber/virtualHost");

        return connectionFactory.newConnection(mqConfigBean.getAddressList()); // 考虑集群
    }

    @Bean(name = "mqRecConnection")
    public Connection mqRecConnection(@Autowired MQConfigBean mqConfigBean) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);

        // 也可以使用uri的方式
        //factory.setUri("amqp://userName:password@hostName:portNumber/virtualHost");

        return connectionFactory.newConnection(mqConfigBean.getAddressList()); // 考虑集群
    }
}
