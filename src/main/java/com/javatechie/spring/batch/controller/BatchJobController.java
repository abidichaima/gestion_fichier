package com.javatechie.spring.batch.controller;

import com.javatechie.spring.batch.entity.Batch;
import com.javatechie.spring.batch.repository.BatchRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class BatchJobController {

    public static final String TEMP_STORAGE_PATH = "/user/temp/";

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private BatchRepository repository;

    private final String TEMP_STORAGE = "/user/batch-files/";

    @PostMapping(path = "/importData")
    public void startBatch(@RequestParam("file") MultipartFile multipartFile) {

        try {
            String originalFileName = multipartFile.getOriginalFilename();
            File fileToImport = new File(TEMP_STORAGE + originalFileName);
            multipartFile.transferTo(fileToImport);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fullPathFileName", TEMP_STORAGE + originalFileName)
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(job, jobParameters);

            if (execution.getStatus() == BatchStatus.COMPLETED) {
                // Supprime le fichier du répertoire TEMP_STORAGE
                Files.deleteIfExists(Paths.get(TEMP_STORAGE + originalFileName));
            }

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException | IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/batchs")
    public List<Batch> getAll() {
        return repository.findAll();
    }
}
