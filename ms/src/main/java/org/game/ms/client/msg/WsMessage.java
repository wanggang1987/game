/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.client.msg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 *
 * @author wanggang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WsMessage {

    @JsonIgnore
    private String seesionId;
    //send
    private MessageType messageType;
    private AttributeMsg attributeMsg;
    private FightStatusMsg fightStatusMsg;
    private LocationMsg locationMsg;
    private RoleDieMsg roleDieMsg;
    private CastSkillMsg castSkillMsg;
    private FightDamageMsg fightDamageMsg;
    //receive
    private Long playerId;
    private AttributeRequest attributeRequest;
    private CreatePlayerMsg createPlayerMsg;
}
