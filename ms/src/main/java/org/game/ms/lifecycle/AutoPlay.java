/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.lifecycle;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;
import org.game.ms.role.Role;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Slf4j
@Service
public class AutoPlay {

    private List<Player> autoPlayers = new ArrayList<>();

    public void startPlayerAutoPlay(Player player) {
        autoPlayers.add(player);
    }

    public void removePlayerAutoPlay(Player player) {
        autoPlayers.remove(player);
    }

    private void playerAuto(Player player) {
        Role target = player.getTarget();
        if (target == null) {
            target = player.getMap().findNearByMonsterForPlayer(player);
            player.setTarget(target);
        }
        if (target == null) {
            return;
        }
        player.getMap().playerMoveToTargetInTick(player);
    }

    public void autoPlayForTick() {
        autoPlayers.forEach(player -> playerAuto(player));
    }
}
