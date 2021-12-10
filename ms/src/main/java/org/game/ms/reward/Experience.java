/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.reward;

/**
 *
 * @author wanggang
 */
public class Experience {

    static public int MonsterExp(int level) {
        return 2 * level;
    }

    static public int UpgradeNead(int level) {
        return 10 * level * level;
    }
}
