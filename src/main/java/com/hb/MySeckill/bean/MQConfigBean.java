package com.hb.MySeckill.bean;


import com.rabbitmq.client.Address;

import java.util.List;

/**
 * 读取rabbitmq配置并存为bean
 */
public class MQConfigBean {
    private List<Address> addressList;
    private String username;
    private String password;
    private String virtualHost;
    private String queue;
    private boolean publisherConfirm;
    private String exchange;
    private String routingKey;

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public boolean isPublisherConfirm() {
        return publisherConfirm;
    }

    public void setPublisherConfirm(boolean publisherConfirm) {
        this.publisherConfirm = publisherConfirm;
    }
}
