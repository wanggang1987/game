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
    MONSTER_ATTRIBUTE,
    MONSTER_LOCATION,
    MONSTER_DIE,
    MONSTER_FIGHTSTATUS,
    PLAYER_ATTRIBUTE,
    PLAYER_LOCATION,
    PLAYER_DIE,
    PLAYER_FIGHTSTATUS,
    //reveive
    PLAYER_CREATE,
    LOGIN,
    ATTRIBUTE_REQUEST,
}
