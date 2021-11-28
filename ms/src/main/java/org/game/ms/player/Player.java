/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.player;

import lombok.Data;
import org.game.ms.map.Location;
import org.game.ms.map.RootMap;
import org.game.ms.role.Role;

/**
 *
 * @author gangwang
 */
@Data
public class Player {

    private long id;
    private Role role;
    private RootMap map;
    private Location location;
}
