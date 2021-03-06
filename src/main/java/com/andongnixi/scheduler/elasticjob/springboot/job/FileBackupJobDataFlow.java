package com.andongnixi.scheduler.elasticjob.springboot.job;

import com.andongnixi.scheduler.elasticjob.springboot.service.FileService;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.andongnixi.scheduler.elasticjob.springboot.model.FileCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件备份任务
 * @author Administrator
 * @version 1.0
 **/
@Component
public class FileBackupJobDataFlow implements DataflowJob<FileCustom> {

    //每次任务执行要备份文件的数量
    private final int FETCH_SIZE = 1;

    @Autowired
    FileService fileService;

       //抓取数据
    @Override
    public List<FileCustom> fetchData(ShardingContext shardingContext) {
        System.out.println("作业分片："+shardingContext.getShardingItem());
        //分片参数，（0=text,1=image,2=radio,3=vedio，参数就是text、image...）
        String jobParameter = shardingContext.getShardingParameter();
        //获取未备份的文件
        List<FileCustom> fileCustoms = fetchUnBackupFiles(jobParameter,FETCH_SIZE);
        return fileCustoms;
    }

    //处理数据
    @Override
    public void processData(ShardingContext shardingContext, List<FileCustom> list) {
        //进行文件备份
        backupFiles(list);
    }

    /**
     * 获取未备份的文件
     * @param count   文件数量
     * @return
     */
    public List<FileCustom> fetchUnBackupFiles(String fileType,int count){

        List<FileCustom> fileCustoms = fileService.fetchUnBackupFiles(fileType, count);
        System.out.printf("time:%s,获取文件%d个\n", LocalDateTime.now(),fileCustoms.size());
        return fileCustoms;

    }

    /**
     * 文件备份
     * @param files
     */
    public void backupFiles(List<FileCustom> files){
        fileService.backupFiles(files);
    }


}
