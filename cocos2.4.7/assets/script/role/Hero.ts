// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:

import { CastSkill, FightDamageMsg, Role } from "../func/BasicObjects";
import { RoleAction } from "./RoleAction";

const { ccclass, property } = cc._decorator;

@ccclass
export default class Hero extends cc.Component {

    private default = 0;
    private hero: Role = new Role();
    @property({ type: cc.Prefab })
    private damageNumber: cc.Prefab = null;

    public getHero(): Role {
        return this.hero;
    }

    protected update(dt: number): void {
        RoleAction.updateLocation(this.node, this.hero.location);
        RoleAction.updateAttribute(this.node, this.hero.attribute);
        RoleAction.updateFightStatus(this.node, this.hero.fightStatus);
    }

    public castSkill(castskill: CastSkill) {
        RoleAction.attackSkill(castskill, this.node, this.hero);
        let lableNode: cc.Node = cc.instantiate(this.damageNumber);
        RoleAction.showSkill(castskill, this.node, lableNode);
    }

    public showDamage(damageMsg: FightDamageMsg) {
        let lableNode: cc.Node = cc.instantiate(this.damageNumber);
        RoleAction.showDamage(damageMsg, this.node, lableNode);
    }
}
