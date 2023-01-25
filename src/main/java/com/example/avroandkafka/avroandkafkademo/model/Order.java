package com.example.avroandkafka.avroandkafkademo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private int orderNum;
    private String orderItem;
    private int price;
}
