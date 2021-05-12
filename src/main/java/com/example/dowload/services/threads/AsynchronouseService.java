package com.example.dowload.services.threads;

import com.example.dowload.services.CreateFilename;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
@Data
public class AsynchronouseService {

    private final Executor taskExecutor;
    @Autowired
    private MyThreadList myThreadList;

    private final ApplicationContext context;

    private final CreateFilename createFilename;

    public AsynchronouseService(ApplicationContext context, @Qualifier("primaryTaskExecutor") Executor taskExecutor, CreateFilename createFilename) {
        this.taskExecutor = taskExecutor;
        this.context = context;
        this.createFilename = createFilename;
    }

    public void setTaskExecutor(String url) {
        String filename=createFilename.getFilename(url);
        MyThread myThread = context.getBean(MyThread.class);
        myThread.setUrl(url);
        myThread.setFilename(filename);
        myThreadList.addTh(filename,myThread);
        taskExecutor.execute(myThread);
    }

}
