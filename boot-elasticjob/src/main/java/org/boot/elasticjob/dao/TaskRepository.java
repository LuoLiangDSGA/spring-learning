package org.boot.elasticjob.dao;

import org.boot.elasticjob.entity.JobTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/10
 * 继承JpaRepository,该接口本身已经实现了创建（save）、更新（save）、删除（delete）、查询（findAll、findOne）等基本操作的函数
 **/
public interface TaskRepository extends JpaRepository<JobTask, Long>, JpaSpecificationExecutor<JobTask> {

}
