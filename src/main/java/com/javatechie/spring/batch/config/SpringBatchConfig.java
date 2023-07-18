package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.entity.Batch;
import com.javatechie.spring.batch.listener.StepSkipListener;
import com.javatechie.spring.batch.repository.BatchRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.io.File;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private BatchItemWriter batchItemWriter;

    @Bean
    @StepScope
    public FlatFileItemReader<Batch> itemReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
        FlatFileItemReader<Batch> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(new File(pathToFile)));
        flatFileItemReader.setName("CSV-Reader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;
    }

    private LineMapper<Batch> lineMapper() {
        DefaultLineMapper<Batch> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "account", "amount");

        BeanWrapperFieldSetMapper<Batch> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Batch.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public BatchProcessor processor() {
        return new BatchProcessor();
    }

    @Bean
    public ItemWriter<Batch> itemWriter() {
        return new BatchItemWriter();
    }

    @Bean
    public Step step1(FlatFileItemReader<Batch> itemReader, ItemWriter<Batch> itemWriter) {
        return stepBuilderFactory.get("slaveStep")
                .<Batch, Batch>chunk(10)
                .reader(itemReader)
                .processor(processor())
                .writer(itemWriter)
                .faultTolerant()
                .listener(skipListener())
                .skipPolicy(skipPolicy())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob(FlatFileItemReader<Batch> itemReader) {
        return jobBuilderFactory.get("importbatch")
                .flow(step1(itemReader, itemWriter()))
                .end()
                .build();
    }

    @Bean
    public SkipPolicy skipPolicy() {
        return new ExceptionSkipPolicy();
    }

    @Bean
    public SkipListener skipListener() {
        return new StepSkipListener();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;
    }
}
