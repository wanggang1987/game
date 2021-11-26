package org.game.ms;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class BasicPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "create_time", updatable = false, insertable = false)
    private Timestamp createTime;
    @Column(name = "update_time", updatable = false, insertable = false)
    private Timestamp updateTime;
}
