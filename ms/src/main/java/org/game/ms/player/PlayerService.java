/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.player;

import cn.hutool.json.JSONUtil;
import java.util.HashMap;
import java.util.Map;
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
    @Autowired
    private PlayerMapper playerMapper;

    private Map<Long, Player> onlinePlayers = new HashMap<>();

    public Player createPlayer(String name) {
        //init player and role
        Player player = new Player();
        Role role = new Role();
        player.setRole(role);
        role.setName(name);
        role.getProfession().add(Profession.warrior);
        role.setLevel(1);
        roleCaculate.initRole(role);

        //insert to db
        PlayerPO ppo = new PlayerPO();
        ppo.setStr(JSONUtil.toJsonStr(player));
        playerMapper.insertUseGeneratedKeys(ppo);
        player.setId(ppo.getId());

        //go to the world map
        player.setMap(worldMap);
        player.setLocation(worldMap.playerComeInMap(player));

        //player online
        onlinePlayers.put(player.getId(), player);

        log.debug("init player {}", JSONUtil.toJsonStr(player));
        return player;
    }
}
