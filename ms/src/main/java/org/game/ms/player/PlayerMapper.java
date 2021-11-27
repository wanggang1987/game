/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.player;

import org.apache.ibatis.annotations.Mapper;
import org.game.ms.db.TKMapper;

/**
 *
 * @author wanggang
 */
@Mapper
public interface PlayerMapper extends TKMapper<PlayerPO> {

}
