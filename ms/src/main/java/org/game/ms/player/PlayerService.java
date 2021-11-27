/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.player;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.map.WorldMap;
import org.game.ms.role.Profession;
import org.game.ms.role.Role;
import org.game.ms.role.RoleCaculate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author gangwang
 */
@Slf4j
@Service
public class PlayerService {

    @Autowired
    private RoleCaculate roleCaculate;
    @Autowired
    private WorldMap worldMap;

    public Player createPlayer(String name) {
        Player player = new Player();

        Role role = new Role();
        role.setName(name);
        role.getProfession().add(Profession.warrior);
        role.setLevel(1);
        roleCaculate.initRole(role);
        player.setRole(role);

        player.setMap(worldMap);
        player.setLocation(worldMap.playerComeInMap(player));

        log.debug("init player {}", JSONUtil.toJsonStr(player));
        return player;
    }
}
