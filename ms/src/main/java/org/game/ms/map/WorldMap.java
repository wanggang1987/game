/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.func.JsonUtils;
import org.game.ms.lifecycle.LifeCycle;
import org.game.ms.monster.Monster;
import org.game.ms.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author wanggang
 */
@Slf4j
@Component
public class WorldMap extends RootMap {

    @Autowired
    private LifeCycle lifeCycle;
    final protected int comeInLocationRandomRange = 200;
    final private int flushMonsterAroundNum = 20;
    @Autowired
    private GridService gridService;

    @Override
    public void addPlayerToMap(Player player) {
        Location location = new Location(FuncUtils.randomInRange(0, comeInLocationRandomRange), FuncUtils.randomInRange(0, comeInLocationRandomRange), 0);
        gridService.locationGrids(location);
        player.setLocation(location);
        super.addPlayerToMap(player);
    }

    @Scheduled(fixedRate = 1000 * 5)
    protected void flushAndRrmoveMonsterForPlayer() {
        log.debug("start to flushAndRrmoveMonsterForPlayer");
        int totalNum = inMapPlayerIdList.stream().map(playerId -> {
            Player player = lifeCycle.onlinePlayer(playerId);
            int addNum = flushMonsterAroundNum - findNearByMonsterNumForPlayer(player);
            createMonsterAroundPlayer(player, addNum);
            return addNum;
        }).mapToInt(Integer::intValue).sum();
        log.debug("flushAndRrmoveMonsterForPlayer add {} monsters", totalNum);
    }

    private void createMonsterAroundPlayer(Player player, int num) {
        for (int i = 0; i < num; i++) {
            Monster monster = lifeCycle.createMonsterByLevel(player.getLevel());
            Location location = new Location(
                    FuncUtils.randomInRange(player.getLocation().getX(), gridService.gridSize()),
                    FuncUtils.randomInRange(player.getLocation().getY(), gridService.gridSize()), 0);
            gridService.locationGrids(location);
            addMonsterToMap(monster, location);
            log.debug("createMonsterAroundPlayer player:{}  monster:{} ", player.getId(), JsonUtils.bean2json(monster));
        }
    }
}
