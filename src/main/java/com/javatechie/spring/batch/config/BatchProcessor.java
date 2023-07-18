package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.entity.Batch;
import org.springframework.batch.item.ItemProcessor;

public class BatchProcessor implements ItemProcessor<Batch, Batch> {
    @Override
    public Batch process(Batch batch) {
        int age = Integer.parseInt(batch.getAccount());
        if (age >= 18) {
            return batch;
        }
        return null;
    }
}
