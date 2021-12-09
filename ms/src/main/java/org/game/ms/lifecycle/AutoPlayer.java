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
import org.game.ms.role.AttackStatus;
import org.game.ms.role.LivingStatus;
import org.game.ms.role.MoveStatus;
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
    private FightService fightService;
    @Autowired
    private LifeCycle lifeCycle;

    private final List<Player> autoPlayers = new ArrayList<>();

    public void startPlayerAutoPlay(Player player) {
        autoPlayers.add(player);
        log.debug("startPlayerAutoPlay player {} ", player.getId());
    }

    public void removePlayerAutoPlay(Player player) {
        autoPlayers.remove(player);
    }

    private void playerAuto(Player player) {
        if (player.getTarget() == null) {
            player.setAttackStatus(AttackStatus.NOT_ATTACK);
            Long targetId = player.getMap().findNearByMonsterIdForPlayer(player);
            player.setTarget(lifeCycle.onlineMonster(targetId));
        }
        if (player.getTarget() == null) {
            return;
        }
        if (FuncUtils.equals(player.getTarget().getLivingStatus(), LivingStatus.DEAD)) {
            player.setTarget(null);
            return;
        }
        autoAttack(player);
        autoMove(player);
    }

    public void autoPlayForTick() {
        autoPlayers.forEach(player -> playerAuto(player));
    }

    private void autoAttack(Player player) {
        fightService.fight(player);
    }

    private void autoMove(Player player) {
        if (FuncUtils.equals(player.getAttackStatus(), AttackStatus.OUT_RANGE)
                && FuncUtils.equals(player.getMoveStatus(), MoveStatus.STANDING)) {
            log.debug("player {} start move to location {}", player.getId(), player.getTarget().getLocation());
            player.setMoveStatus(MoveStatus.MOVEING);
        } else if (FuncUtils.notEquals(player.getAttackStatus(), AttackStatus.OUT_RANGE)) {
            player.setMoveStatus(MoveStatus.STANDING);
        }
        if (FuncUtils.equals(player.getMoveStatus(), MoveStatus.MOVEING)) {
            player.getMap().roleMoveToTargetInTick(player);
        }
    }
}
