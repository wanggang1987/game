/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.player;

import org.game.ms.player.template.PlayerTemplate;
import org.game.ms.player.template.WarriorTample;
import cn.hutool.json.JSONUtil;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
    private PlayerMapper playerMapper;
    @Autowired
    private PlayerTemplate playerTemplate;
    @Autowired
    private WarriorTample warriorTample;
    
    Map<Profession, PlayerTemplate> mapProfessionRole = new HashMap<>();
    
    @PostConstruct
    private void init() {
        mapProfessionRole.put(Profession.warrior, warriorTample);
    }
    
    public Player createPlayer(String name) {
        //init role
        Player player = new Player();
        player.setName(name);
        player.getProfession().add(Profession.warrior);
        player.setLevel(1);
        initPlayer(player);

        //insert to db
        PlayerPO ppo = new PlayerPO();
        ppo.setStr(JSONUtil.toJsonStr(player));
        playerMapper.insertUseGeneratedKeys(ppo);
        player.setId(ppo.getId());
        
        log.debug("init player {}", JSONUtil.toJsonStr(player));
        return player;
    }
    
    private void initPlayer(Player player) {
        player.setSpeed(playerTemplate.getSpeed());
        player.getProfession().stream().forEach(profession -> {
            PlayerTemplate template = mapProfessionRole.get(profession);
            player.setHealthPoint(player.getHealthPoint() + template.getBaseHealth() + template.getGrowthHealth() * player.getLevel());
            player.setResourcePoint(player.getResourcePoint() + template.getBaseResource() + template.getGrowthResource() * player.getLevel());
            player.setAttack(player.getAttack() + template.getBaseAttack() + template.getGrowthAttack() * player.getLevel());
            player.setDefense(player.getDefense() + template.getBaseDeffence() + template.getGrowthDefense() * player.getLevel());
        });
    }
}
