// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

import { FightDamageMsg } from "../func/BasicObjects";

const { ccclass, property } = cc._decorator;

@ccclass
export default class UICollection extends cc.Component {

    private damageMsgs: string[] = new Array();

    public addDamageMsg(fightDamageMsg: FightDamageMsg) {
        let info = fightDamageMsg.sourceType + ":" + fightDamageMsg.sourceId
            + " " + fightDamageMsg.skillName + " "
            + fightDamageMsg.targetType + ":" + fightDamageMsg.targetId;
        this.damageMsgs.push(info);
        if (this.damageMsgs.length > 20) {
            this.damageMsgs.shift();
        }
    }
}
