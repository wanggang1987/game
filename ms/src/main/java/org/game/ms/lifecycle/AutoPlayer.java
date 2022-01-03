/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.lifecycle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.fight.BufferEffectService;
import org.game.ms.fight.BattleService;
import org.game.ms.fight.FightService;
import org.game.ms.func.FuncUtils;
import org.game.ms.map.RootMap;
import org.game.ms.player.Player;
import org.game.ms.player.PlayerService;
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
    private PlayerService playerService;
    @Autowired
    private RootMap rootMap;
    @Autowired
    private BufferEffectService bufferEffectService;
    @Autowired
    private BattleService battleService;

    private final List<Player> autoPlayers = new ArrayList<>();

    public void startPlayerAutoPlay(Player player) {
        autoPlayers.add(player);
        log.debug("startPlayerAutoPlay player {} ", player.getId());
    }

    public void removePlayerAutoPlay(Player player) {
        Iterator it = autoPlayers.iterator();
        while (it.hasNext()) {
            Player next = (Player) it.next();
            if (FuncUtils.equals(next.getId(), player.getId())) {
                it.remove();
                break;
            }
        }
    }

    private void playerAuto(Player player) {
        if (FuncUtils.equals(player.getLivingStatus(), LivingStatus.DEAD)) {
            log.debug("player {} dead ", player.getId());
            playerService.playerReborn(player);
            return;
        }
        if (FuncUtils.isEmpty(player.getTarget())) {
            player.setAttackStatus(AttackStatus.NOT_ATTACK);
            player.setTarget(battleService.findMonsterFromBattle(player.getBattle()));
        }
        if (FuncUtils.isEmpty(player.getTarget())) {
            player.setTarget(rootMap.findNearByMonsterIdForPlayer(player));
            return;
        }
        if (FuncUtils.isEmpty(player.getTarget())) {
            return;
        }
        if (FuncUtils.equals(player.getTarget().getLivingStatus(), LivingStatus.DEAD)) {
            player.setTarget(null);
            return;
        }

        if (bufferEffectService.anomalyPass(player)) {
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
            log.debug("player {} start move {} to {} {}", player.getId(), player.getLocation(),
                    player.getTarget().getRoleType(), player.getTarget().getId());
            player.setMoveStatus(MoveStatus.MOVEING);
        } else if (FuncUtils.notEquals(player.getAttackStatus(), AttackStatus.OUT_RANGE)) {
            player.setMoveStatus(MoveStatus.STANDING);
        }
        if (FuncUtils.equals(player.getMoveStatus(), MoveStatus.MOVEING)) {
            rootMap.roleMoveToTargetInTick(player);
        }
    }
}
