/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.lifecycle;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.fight.FightService;
import org.game.ms.func.FuncUtils;
import org.game.ms.player.Player;
import org.game.ms.player.PlayerService;
import org.game.ms.role.AttackStatus;
import org.game.ms.role.LivingStatus;
import org.game.ms.role.MoveStatus;
import org.game.ms.role.Role;
import org.game.ms.role.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class AutoPlayer {

    @Autowired
    private LifeCycle lifeCycle;
    @Autowired
    private FightService fightService;
    @Autowired
    private PlayerService playerService;

    private final List<Player> autoPlayers = new ArrayList<>();

    public void startPlayerAutoPlay(Player player) {
        autoPlayers.add(player);
        log.debug("startPlayerAutoPlay player {} ", player.getId());
    }

    public void removePlayerAutoPlay(Player player) {
        autoPlayers.remove(player);
    }

    private void playerAuto(Player player) {
        if (FuncUtils.equals(player.getLivingStatus(), LivingStatus.DEAD)) {
            log.debug("player {} dead ", player.getId());
            playerService.playerReborn(player);
            return;
        }
        if (player.getTargetId() == null) {
            player.setAttackStatus(AttackStatus.NOT_ATTACK);
            player.setTargetId(playerService.findNearByMonster(player));
            player.setTargetType(RoleType.MONSTER);
            return;
        }
        Role target = lifeCycle.getRole(player.getTargetType(), player.getTargetId());
        if (target == null) {
            player.setTargetType(null);
            player.setTargetId(null);
            return;
        }
        if (FuncUtils.equals(target.getLivingStatus(), LivingStatus.DEAD)) {
            player.setTargetId(null);
            return;
        }

        autoAttack(player);
        autoMove(player);
    }

    public void autoPlayForTick() {
        autoPlayers.forEach(player -> playerAuto(player));
    }

    private void autoAttack(Player player) {
        fightService.autoFight(player);
    }

    private void autoMove(Player player) {
        if (FuncUtils.equals(player.getAttackStatus(), AttackStatus.OUT_RANGE)
                && FuncUtils.equals(player.getMoveStatus(), MoveStatus.STANDING)) {
            log.debug("player {} start move {} to {} {}", player.getId(), player.getLocation(), player.getTargetType(), player.getTargetId());
            player.setMoveStatus(MoveStatus.MOVEING);
        } else if (FuncUtils.notEquals(player.getAttackStatus(), AttackStatus.OUT_RANGE)) {
            player.setMoveStatus(MoveStatus.STANDING);
        }
        if (FuncUtils.equals(player.getMoveStatus(), MoveStatus.MOVEING)) {
            playerService.moveToTargetInTick(player);
        }
    }
}
