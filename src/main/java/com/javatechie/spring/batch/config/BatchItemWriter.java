package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.entity.Batch;
import com.javatechie.spring.batch.repository.BatchRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BatchItemWriter<B> implements ItemWriter<Batch> {

    @Autowired
    private BatchRepository repository;

    @Override
    public void write(List<? extends Batch> list) throws Exception {
        System.out.println("Writer Thread "+Thread.currentThread().getName());
        repository.saveAll(list);
    }
}
