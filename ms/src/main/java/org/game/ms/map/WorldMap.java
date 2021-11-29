/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import cn.hutool.json.JSONUtil;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.monster.Monster;
import org.game.ms.monster.template.WolfTemplate;
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
    private WolfTemplate wolfTemplate;
    final protected int comeInLocationRandomRange = 200;
    final private int flushMonsterAroundNum = 20;

    @Override
    public void playerComeInMap(Player player) {
        super.playerComeInMap(player);
        Location location = new Location(FuncUtils.randomInRange(0, comeInLocationRandomRange), FuncUtils.randomInRange(0, comeInLocationRandomRange), 0);
        location.setGrid(locationInGrid(location));
        player.setLocation(location);
    }

    @Scheduled(fixedRate = 1000 * 20)
    protected void flushAndRrmoveMonsterForPlayer() {
        log.debug("start to flushAndRrmoveMonsterForPlayer");
        int totalNum = inMapPlayerList.stream().map(player -> {
            List<Monster> monsters = playerMonsterMap.get(player.getId());
            destoryFarAyayMonsters(player, monsters);
            int addNum = flushMonsterAroundNum - monsters.size();
            createMonsterAroundPlayer(player, monsters, addNum);
            return addNum;
        }).mapToInt(Integer::intValue).sum();
        log.debug("flushAndRrmoveMonsterForPlayer add {} monsters", totalNum);
    }

    protected void destoryFarAyayMonsters(Player player, List<Monster> monsters) {
        List<String> nearGrids = nearByGrids(player.getLocation());
        List<Monster> farAyayMonsters = monsters.stream()
                .filter(monster -> !nearGrids.contains(monster.getLocation().getGrid()))
                .collect(Collectors.toList());
        monsters.removeAll(farAyayMonsters);

        log.debug("destoryFarAyayMonster playerid:{} remove monsters:{} left:{}", player.getId(), farAyayMonsters.size(), monsters.size());
    }

    protected void createMonsterAroundPlayer(Player player, List<Monster> monsters, int num) {
        for (int i = 0; i < num; i++) {
            Monster monster = new Monster(wolfTemplate);
            Location location = new Location(
                    FuncUtils.randomInRange(player.getLocation().getX(), gridSize),
                    FuncUtils.randomInRange(player.getLocation().getY(), gridSize), 0);
            location.setGrid(locationInGrid(location));
            monster.setLocation(location);
            monsters.add(monster);
            insertMonsterToGrid(monster);
            log.debug("createMonsterAroundPlayer id:{}  monster:{}", player.getId(), JSONUtil.toJsonStr(monster));
        }
    }
}
