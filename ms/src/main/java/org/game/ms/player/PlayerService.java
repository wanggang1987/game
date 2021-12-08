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
import org.game.ms.id.IdService;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.Experience;
import org.game.ms.role.LivingStatus;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.RoleType;
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
    private IdService idService;
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
        player.setId(idService.newId());
        player.setName(name);
        player.getProfession().add(Profession.warrior);
        player.setLevel(1);
        initPlayer(player);

        //insert to db
        PlayerPO ppo = new PlayerPO();
        ppo.setStr(JSONUtil.toJsonStr(player));
        playerMapper.insert(ppo);

        log.debug("init player {}", JSONUtil.toJsonStr(player));
        return player;
    }

    public void playerGetExp(Player player, int exp) {
        long now = player.getExperience() + exp;
        long need = Experience.UpgradeNead(player.getLevel());
        log.info("player {} exp {}/{}", player.getId(), now, need);
        if (now >= need) {
            now = now - need;
            player.setLevel(player.getLevel() + 1);
            attributeInit(player);
            log.debug("player {} level up to {}", player.getId(), player.getLevel());
        }
        player.setExperience(now);
    }

    private void initPlayer(Player player) {
        player.setRoleType(RoleType.PLAYER);
        player.setSpeed(playerTemplate.getSpeed() / 1000);
        player.setAttackRange(playerTemplate.getAttackRange());
        player.setAttackCooldownMax(playerTemplate.getAttackCooldown() * 1000);
        player.setAttackStatus(AttackStatus.NOT_ATTACK);
        player.setMoveStatus(MoveStatus.STANDING);
        player.setLivingStatus(LivingStatus.LIVING);
        attributeInit(player);
    }

    private void attributeInit(Player player) {
        player.setHealthMax(0);
        player.setHealthPoint(0);
        player.setResourceMax(0);
        player.setResourcePoint(0);
        player.setAttack(0);
        player.setDefense(0);
        player.getProfession().stream().forEach(profession -> {
            PlayerTemplate template = mapProfessionRole.get(profession);
            player.setHealthMax(player.getHealthMax() + template.getBaseHealth() + template.getGrowthHealth() * player.getLevel());
            player.setHealthPoint(player.getHealthMax());
            player.setResourceMax(player.getResourceMax() + template.getBaseResource() + template.getGrowthResource() * player.getLevel());
            player.setResourcePoint(player.getResourceMax());
            player.setAttack(player.getAttack() + template.getBaseAttack() + template.getGrowthAttack() * player.getLevel());
            player.setDefense(player.getDefense() + template.getBaseDeffence() + template.getGrowthDefense() * player.getLevel());
        });
    }
}
