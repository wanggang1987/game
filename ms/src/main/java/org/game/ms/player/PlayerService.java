/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.player;

import org.game.ms.skill.Resource;
import org.game.ms.player.template.PlayerTemplate;
import org.game.ms.player.template.WarriorTample;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.JsonUtils;
import org.game.ms.id.IdService;
import org.game.ms.map.RootMap;
import org.game.ms.role.AttackStatus;
import org.game.ms.reward.Experience;
import org.game.ms.role.LivingStatus;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.RoleService;
import org.game.ms.role.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author gangwang
 */
@Slf4j
@Service
public class PlayerService extends RoleService {

    @Autowired
    private IdService idService;
    @Autowired
    private PlayerMapper playerMapper;
    @Autowired
    private PlayerTemplate playerTemplate;
    @Autowired
    private WarriorTample warriorTample;
    private Map<Profession, PlayerTemplate> professionMap = new HashMap<>();

    @PostConstruct
    private void init() {
        professionMap.put(Profession.WARRIOR, warriorTample);
    }

    public Player createPlayer(String name) {
        //init role
        Player player = new Player();
        player.setId(idService.newId());
        player.setName(name);
        player.getProfession().add(Profession.WARRIOR);
        player.setLevel(1);
        initPlayer(player);

        //insert to db
        PlayerPO ppo = new PlayerPO();
        ppo.setStr(JsonUtils.bean2json(player));
        playerMapper.insert(ppo);

        log.debug("init player {} ", player.getId());
        return player;
    }

    public void playerGetExp(Player player, int exp) {
        int now = player.getExperience() + exp;
        int need = Experience.UpgradeNead(player.getLevel());
        log.info("player {} exp {}/{}", player.getId(), now, need);
        if (now >= need) {
            now = now - need;
            player.setLevel(player.getLevel() + 1);
            attributeInit(player);
            log.debug("player {} level up to {}", player.getId(), player.getLevel());
        }
        player.setExperience(now);
    }

    public void playerGetCoin(Player player, int coin) {
        int now = player.getCoin() + coin;
        player.setCoin(now);
        log.info("player {} coin {}", player.getId(), now);
    }

    private void initPlayer(Player player) {
        player.setRoleType(RoleType.PLAYER);
        player.setAttackStatus(AttackStatus.NOT_ATTACK);
        player.setMoveStatus(MoveStatus.STANDING);
        player.setLivingStatus(LivingStatus.LIVING);
        player.setTargetId(null);
        attributeInit(player);
        skillInit(player);
    }

    private void attributeInit(Player player) {
        player.setSpeed(playerTemplate.getSpeed() / 1000);
        player.setAttackRange(playerTemplate.getAttackRange());
        player.setAttackCooldownMax(playerTemplate.getAttackCooldown() * 1000);
        player.setHealthMax(playerTemplate.getHealth());
        player.setHealthPoint(player.getHealthMax());
        player.setAttack(playerTemplate.getAttack());
        player.setDefense(playerTemplate.getDeffence());

        Resource resource = player.getResource();
        resource.setAngerMax(100);
        resource.setAngerPoint(resource.getAngerMax());
    }

    private void skillInit(Player player) {
        player.getProfession().forEach(profession -> {
            PlayerTemplate template = professionMap.get(profession);
            player.getSkills().addAll(template.getSkills());
        });
    }

    public Long findNearByMonster(Player player) {
        return player.getMap().findNearByMonsterIdForPlayer(player);
    }

    public void playerGotoMap(Player player, RootMap map) {
        player.setMap(map);
        map.playerComeInMap(player);
    }

    public void playerReborn(Player player) {
        initPlayer(player);
        player.getMap().playerLeaveMap(player);
        player.getMap().playerComeInMap(player);
        player.setLivingStatus(LivingStatus.LIVING);
        log.debug("playerReborn {} {}", player.getId(), player.getLocation());
    }
}
