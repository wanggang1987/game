/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.game.ms.func.FuncUtils;
import org.game.ms.monster.Monster;
import org.game.ms.monster.WolfTemplate;
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

    private String name = "艾泽拉斯";
    final protected int comeInLocationRandomRange = 200;
    final private int flushMonsterAroundNum = 20;

    private Map<Long, List<Monster>> playerMonsters = new HashMap<>();
    @Autowired
    private WolfTemplate wolfTemplate;

    @Override
    public Location playerComeInMap(Player player) {
        players.add(player);
        Location location = new Location(FuncUtils.randomInRange(0, comeInLocationRandomRange), FuncUtils.randomInRange(0, comeInLocationRandomRange), 0);
        location.setGrid(locationInGrid(location));
        return location;
    }

    @Override
    @Scheduled(fixedRate = 1000 * 20)
    protected void flushAndRrmoveMonsterForPlayer() {
        int totalNum = players.stream().map(player -> {
            List<Monster> monsters = playerMonsters.get(player.getId());
            if (monsters == null) {
                monsters = new ArrayList<>();
                playerMonsters.put(player.getId(), monsters);
            }
            destoryFarAyayMonster(player, monsters);
            int addNum = flushMonsterAroundNum - monsters.size();
            createMonsterAroundPlayer(player, monsters, addNum);
            return addNum;
        }).mapToInt(Integer::intValue).sum();

        log.debug("flushMonsterForPlayer add {} monsters", totalNum);
    }

    @Override
    protected void destoryFarAyayMonster(Player player, List<Monster> monsters) {
        List<Monster> farAyayMonsters = monsters.stream().filter(monster -> isFarAway(player.getLocation(), monster.getLocation()))
                .collect(Collectors.toList());
        monsters.removeAll(farAyayMonsters);
        log.debug("destoryFarAyayMonster playerid:{} remove monsters:{} left:{}", player.getId(), farAyayMonsters.size(), monsters.size());
    }

    @Override
    protected void createMonsterAroundPlayer(Player player, List<Monster> monsters, int num) {
        for (int i = 0; i < num; i++) {
            Monster monster = new Monster(wolfTemplate);
            Location location = new Location(
                    FuncUtils.randomInRange(player.getLocation().getX(), gridSize),
                    FuncUtils.randomInRange(player.getLocation().getY(), gridSize), 0);
            location.setGrid(locationInGrid(location));
            monster.setLocation(location);
            monsters.add(monster);
            log.debug("createMonsterAroundPlayer id:{}  monster:{} ", player.getId(), monster);
        }
    }
}
