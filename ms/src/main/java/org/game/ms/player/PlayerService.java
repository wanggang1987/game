/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.player;

import org.game.ms.skill.resource.Resource;
import org.game.ms.player.template.PlayerTemplate;
import org.game.ms.player.template.WarriorTample;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.MessageService;
import org.game.ms.func.JsonUtils;
import org.game.ms.id.IdService;
import org.game.ms.role.AttackStatus;
import org.game.ms.reward.Experience;
import org.game.ms.role.LivingStatus;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.RoleType;
import org.game.ms.skill.Skill;
import org.springframework.beans.BeanUtils;
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
    @Autowired
    private MessageService messageService;
    private final Map<Profession, PlayerTemplate> professionMap = new HashMap<>();

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

    public void playerReborn(Player player) {
        initPlayer(player);
        player.getMap().playerLeaveMap(player);
        player.getMap().addPlayerToMap(player);
        player.setLivingStatus(LivingStatus.LIVING);
        messageService.heroUpdate(player);
        log.debug("playerReborn {} {}", player.getId(), player.getLocation());
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
        player.setTarget(null);
        attributeInit(player);
        skillInit(player);
        bufferInit(player);
    }

    private void bufferInit(Player player) {
        player.getBuffers().clear();
    }

    private void attributeInit(Player player) {
        player.setSpeed(playerTemplate.getSpeed() / 1000);
        player.setAttackRange(playerTemplate.getAttackRange());
        player.setHealthMax(playerTemplate.getHealth());
        player.setHealthPoint(player.getHealthMax());
        player.setAttack(playerTemplate.getAttack());
        player.setDefense(playerTemplate.getDeffence());

        Resource resource = player.getResource();
        resource.setAttackCooldownMax(playerTemplate.getAttackCooldown() * 1000);
        resource.setSkillCooldownMax(1.5 * 1000);
        resource.setAngerMax(100);
        resource.setAngerPoint(resource.getAngerMax());
    }

    private void skillInit(Player player) {
        player.getSkills().clear();
        player.getProfession().forEach(profession -> {
            PlayerTemplate template = professionMap.get(profession);
            template.getSkills().forEach(skill -> {
                Skill one = new Skill();
                BeanUtils.copyProperties(skill, one);
                player.getSkills().add(one);
            });
        });
    }
}
