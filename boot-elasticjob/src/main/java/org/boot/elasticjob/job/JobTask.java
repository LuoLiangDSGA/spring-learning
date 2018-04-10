package org.boot.elasticjob.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/10
 **/
@Entity(name = "JOB_TASK")
@Data
@Builder
public class JobTask {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String content;
    @Column
    private Integer status;
    @Column(table = "send_time")
    private Long sendTime;
}
