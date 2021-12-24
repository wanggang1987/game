/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.client.msg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 *
 * @author wanggang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WsMessage {

    //send
    private String seesionId;
    private MessageType messageType;
    private AttributeMsg attributeMsg;
    private LocationMsg locationMsg;
    private RoleDieMsg roleDieMsg;
    //receive
    private Long playerId;
    private AttributeRequest attributeRequest;
    private CreatePlayerMsg createPlayerMsg;
}
