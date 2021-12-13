/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.player;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.game.ms.role.Role;

/**
 *
 * @author gangwang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player extends Role {

    private int experience;
    private int coin;
    private List<Profession> profession = new ArrayList<>();
}
