/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Grid {

    private String grid;
    @JsonIgnore
    private List<Monster> monsters = new ArrayList<>();
    @JsonIgnore
    private List<Player> players = new ArrayList<>();
}
