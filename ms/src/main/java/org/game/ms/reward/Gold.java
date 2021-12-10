/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.reward;

import org.game.ms.func.FuncUtils;

/**
 *
 * @author wanggang
 */
public class Gold {

    static public int MonsterCoin(int level) {
        int base = 20 * level;
        return FuncUtils.randomInPersentRange(base, 30);
    }

}
