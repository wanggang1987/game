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
            Long targetId = player.getMap().findNearByMonsterIdForPlayer(player);
            player.setTarget(lifeCycle.onlineMonster(targetId));
        }
        if (player.getTarget() == null) {
            return;
        }
        if (LivingStatus.DEAD.equals(player.getTarget().getLivingStatus())) {
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
        if (AttackStatus.OUT_RANGE.equals(player.getAttackStatus())
                && MoveStatus.STANDING.equals(player.getMoveStatus())) {
            log.debug("player {} start move to location {}", player.getId(), player.getTarget().getLocation());
            player.setMoveStatus(MoveStatus.MOVEING);
        }
        if (!AttackStatus.OUT_RANGE.equals(player.getAttackStatus())) {
            player.setMoveStatus(MoveStatus.STANDING);
        }
        if (MoveStatus.MOVEING.equals(player.getMoveStatus())) {
            player.getMap().playerMoveToTargetInTick(player);
        }
    }
}
