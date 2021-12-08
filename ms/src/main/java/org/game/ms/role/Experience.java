/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.role;

/**
 *
 * @author wanggang
 */
public class Experience {

    static public Integer MonsterExp(Integer level) {
        return 2 * level;
    }

    static public Integer UpgradeNead(Integer level) {
        return 10 * level * level;
    }
}
