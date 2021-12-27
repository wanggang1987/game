/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.fight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;

/**
 *
 * @author wanggang
 */
@Data
public class Battle {

    private long id;
    @JsonIgnore
    private List<Player> players = new ArrayList<>();
    @JsonIgnore
    private List<Monster> monsters = new ArrayList<>();
}
