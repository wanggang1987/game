/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.id;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author wanggang
 */
@Service
public class IdService {

    @Autowired
    private GidMapper gidMapper;

    private long id = 1;
    private long idMax = 0;
    private final int range = 1000;

//    @PostConstruct
//    private void idInit() {
//        Gid gid = new Gid();
//        gidMapper.insertUseGeneratedKeys(gid);
//        id = gid.getId();
//        idMax = id;
//    }
//
//    @Scheduled(fixedRate = 1000 * 1)
//    private void idGrow() {
//        if (id >= idMax - range) {
//            Gid gid = new Gid();
//            idMax += range * 2;
//            gid.setId(idMax);
//            gidMapper.insert(gid);
//        }
//    }

    public long newId() {
        return id++;
    }
}
