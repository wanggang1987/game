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
    // send
    HERO_UPDATE,
    ATTRIBUTE,
    LOCATION,
    ROLE_DIE,
    FIGHTSTATUS,
    CASTSKILL,
    FIGHTDAMAGE,

    // reveive
    PLAYER_CREATE,
    LOGIN,
    ATTRIBUTE_REQUEST,
}
