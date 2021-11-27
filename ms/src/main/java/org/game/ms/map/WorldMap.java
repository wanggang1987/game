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
public class WorldMap extends GameMap {

    private String name = "艾泽拉斯";
    final protected int locationRandomRange = 200;
    final private int monsterAroundNum = 20;
    final private int monsterFlushDistance = 20;

    private Map<Long, List<Monster>> playerMonsters = new HashMap<>();
    @Autowired
    private WolfTemplate wolfTemplate;

    @Override
    public Location playerComeInMap(Player player) {
        players.add(player);
        return new Location(FuncUtils.randomInRange(0, locationRandomRange), FuncUtils.randomInRange(0, locationRandomRange), 0);
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
            int addNum = monsterAroundNum - monsters.size();
            createMonsterAroundPlayer(player, monsters, addNum);
            return addNum;
        }).mapToInt(Integer::intValue).sum();

        log.debug("flushMonsterForPlayer add {} monsters", totalNum);
    }

    private boolean isFarAway(Location playerLocation, Location monsterLocation) {
        double x = playerLocation.getX() - monsterLocation.getX();
        double y = playerLocation.getY() - monsterLocation.getY();
        return x * x + y * y >= monsterFlushDistance * monsterFlushDistance;
    }

    @Override
    protected void destoryFarAyayMonster(Player player, List<Monster> monsters) {
        List<Monster> farAyayMonsters = monsters.stream().filter(monster -> isFarAway(player.getLocation(), monster.getLocation()))
                .collect(Collectors.toList());
        monsters.removeAll(farAyayMonsters);
        log.debug("destoryFarAyayMonster id:{} remove monsters:{} left:{}", player.getId(), farAyayMonsters.size(), monsters.size());
    }

    @Override
    protected void createMonsterAroundPlayer(Player player, List<Monster> monsters, int num) {
        for (int i = 0; i < num; i++) {
            Monster monster = new Monster(wolfTemplate);
            monster.setLocation(new Location(
                    FuncUtils.randomInRange(player.getLocation().getX(), monsterFlushDistance),
                    FuncUtils.randomInRange(player.getLocation().getY(), monsterFlushDistance), 0));
            monsters.add(monster);
            log.debug("createMonsterAroundPlayer id:{}  monster:{} ", player.getId(), monster);
        }
    }
}
