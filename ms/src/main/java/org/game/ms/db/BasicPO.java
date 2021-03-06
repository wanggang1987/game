package org.game.ms.db;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

@Data
public class BasicPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "create_time", updatable = false, insertable = false)
    private Timestamp createTime;
    @Column(name = "update_time", updatable = false, insertable = false)
    private Timestamp updateTime;
}
