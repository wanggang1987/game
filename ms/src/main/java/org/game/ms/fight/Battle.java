/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author wanggang
 */
@Data
public class Battle {

    private long id;
    private List<Long> players = new ArrayList<>();
    private List<Long> monsters = new ArrayList<>();
}
