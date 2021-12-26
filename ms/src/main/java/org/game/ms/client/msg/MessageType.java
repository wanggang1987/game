/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.client.msg;

/**
 *
 * @author wanggang
 */
public enum MessageType {
    //send
    HERO_ATTRIBUTE,
    HERO_LOCATION,
    PLAYER_ATTRIBUTE,
    PLAYER_LOCATION,
    PLAYER_DIE,
    PLAYER_FIGHTSTATUS,
    PLAYER_CASTSKILL,
    MONSTER_ATTRIBUTE,
    MONSTER_LOCATION,
    MONSTER_DIE,
    MONSTER_FIGHTSTATUS,
    MONSTER_CASTSKILL,
    FIGHTDAMAGE,
    //reveive
    PLAYER_CREATE,
    LOGIN,
    ATTRIBUTE_REQUEST,
}
