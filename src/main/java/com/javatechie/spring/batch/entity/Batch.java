package com.javatechie.spring.batch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BATCH_INFO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Batch {
    @Id
    @Column(name = "BATCH_ID")
    private Long id;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "ACCOUNT")
    private String account;
    @Column(name = "AMOUNT")
    private int amount;
}

