/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.player;

import org.game.ms.skill.resource.Resource;
import org.game.ms.player.template.PlayerTemplate;
import org.game.ms.player.template.WarriorTample;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.client.MessageService;
import org.game.ms.func.FuncUtils;
import org.game.ms.func.JsonUtils;
import org.game.ms.id.IdService;
import org.game.ms.role.AttackStatus;
import org.game.ms.reward.Experience;
import org.game.ms.role.LivingStatus;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.RoleAttribute;
import org.game.ms.role.RoleType;
import org.game.ms.skill.Skill;
import org.game.ms.skill.SkillService;
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
    @Autowired
    private SkillService skillService;

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

        RoleAttribute roleAttribute = player.getRoleAttribute();
        roleAttribute.setStamina(playerTemplate.getStaminaGrow() * player.getLevel());
        roleAttribute.setStrengt(playerTemplate.getStrengtGrow() * player.getLevel());
        roleAttribute.setAgility(playerTemplate.getAgilityGrow() * player.getLevel());
        roleAttribute.setIntellect(playerTemplate.getIntellectGrow() * player.getLevel());
        roleAttribute.setSpirit(playerTemplate.getSpiritGrow() * player.getLevel());

        player.setHealthMax(roleAttribute.getStamina() * 10);
        player.setHealthPoint(player.getHealthMax());
        roleAttribute.setAttackPower(roleAttribute.getStrengt() * 1 + roleAttribute.getAgility() * 1);

        Resource resource = player.getResource();
        resource.setAttackCooldownMax(playerTemplate.getAttackCooldown() * 1000);
        resource.setSkillCooldownMax(1.5 * 1000);
        resource.setAngerMax(100);
        resource.setAngerPoint(resource.getAngerMax());
    }

    private void skillInit(Player player) {
        player.getSkills().clear();
        player.setNormalAttack(skillService.physicalAttack());
        player.getProfession().forEach(profession -> {
            List<Skill> templateSkills = skillService.professionSkill(profession);
            if (FuncUtils.notEmpty(templateSkills)) {
                templateSkills.forEach(skill -> {
                    Skill one = new Skill();
                    BeanUtils.copyProperties(skill, one);
                    player.getSkills().add(one);
                });
            }
        });
    }
}
