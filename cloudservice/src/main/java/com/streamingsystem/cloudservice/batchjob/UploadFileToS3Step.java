//package com.streamingsystem.cloudservice.batchjob;
//
//import com.streamingsystem.cloudservice.dto.FileObjectDTO;
//import com.streamingsystem.cloudservice.service.StorageService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.scope.context.ChunkContext;
//import org.springframework.batch.core.step.StepContribution;
//import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.batch.infrastructure.repeat.RepeatStatus;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//@Component
//@RequiredArgsConstructor
//@StepScope
//@Slf4j
//public class UploadFileToS3Step implements Tasklet {
//    private final StorageService storageService;
//
//    @Value("#{jobParameters['output.file.name']}")
//    private String outputFile;
//
//    @Override
//    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//        log.info("-----------> Now storing files to bucket on s3");
//
//        var objectDTO = mapToObjectDTO();
//        String fileUploaded = storageService.uploadFile(objectDTO);
//
//        log.info("----------> File uploaded successfully on S3 : {}", fileUploaded);
//        return RepeatStatus.FINISHED;
//    }
//
//    private FileObjectDTO mapToObjectDTO() throws IOException {
//        String filename = new File(outputFile).getName();
//        Path filePath = Path.of(outputFile);
//
//        String contentType = Files.probeContentType(filePath);
//        long size = Files.size(filePath);
//        byte[] data = Files.readAllBytes(filePath);
//        return new FileObjectDTO(filename, contentType, size, data);
//    }
//}
