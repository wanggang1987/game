/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Service
public class IdService {

    @Autowired
    private GidMapper gidMapper;

    
    //TODO cache id in mem
    public Integer newId() {
        Gid id = new Gid();
        gidMapper.insertUseGeneratedKeys(id);
        return id.getId();
    }
}
