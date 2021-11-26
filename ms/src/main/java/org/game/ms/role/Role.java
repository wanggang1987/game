/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.role;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.game.ms.BasicPO;

/**
 *
 * @author wanggang
 */
@Data
public class Role extends BasicPO {

    private List<Profession> profession = new ArrayList<>();
    private Integer level;
    private Double healthPoint;
    private Double resourcePoint;
    private Double attack;
    private Double defense;

}
