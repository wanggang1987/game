/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.game.ms.player;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Table;
import lombok.Data;
import org.game.ms.db.BasicPO;

/**
 *
 * @author gangwang
 */
@Data
@Table(name = "t_player")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerPO extends BasicPO {

    private String str;
}
