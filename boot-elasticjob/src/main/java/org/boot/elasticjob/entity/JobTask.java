package org.boot.elasticjob.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/10
 **/
@Entity
@Table(name = "JOB_TASK")
@Data
@NoArgsConstructor
public class JobTask {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String content;
    /**
     * 0-未执行
     * 1-已执行
     */
    @Column
    private Integer status;
    @Column(name = "send_time")
    private Long sendTime;

    public JobTask(String content, Integer status, Long sendTime) {
        this.content = content;
        this.status = status;
        this.sendTime = sendTime;
    }
}
