/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.monster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.game.ms.role.Role;

/**
 *
 * @author gangwang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Monster extends Role {

}
