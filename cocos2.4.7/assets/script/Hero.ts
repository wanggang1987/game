// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:

import { CastSkill, Role } from "./func/BasicObjects";
import { RoleAction } from "./func/RoleAction";
const { ccclass, property } = cc._decorator;

@ccclass
export default class Hero extends cc.Component {

    private default = 0;
    private hero: Role = new Role();
    @property({ type: cc.Label })
    private Name: cc.Label = null;
    @property({ type: cc.ProgressBar })
    private Hp: cc.ProgressBar = null;

    public getHero(): Role {
        return this.hero;
    }

    protected update(dt: number): void {
        if (this.hero.location && this.hero.location.isUpdate) {
            this.node.setPosition(this.hero.location.x * 50, this.hero.location.y * 50);
            this.hero.location.isUpdate = false;
        }

        if (this.hero.attribute && this.hero.attribute.isUpdate) {
            this.Name.string = "玩家:" + this.hero.attribute.id + "(lv" + this.hero.attribute.level + ")";
            this.hero.attribute.isUpdate = false;
        }

        if (this.hero.fightStatus && this.hero.fightStatus.isUpdate) {
            this.Hp.progress = this.hero.fightStatus.healthPoint / this.hero.fightStatus.healthMax;
            this.hero.fightStatus.isUpdate = false;
        }
    }

    public castSkill(castskill: CastSkill) {
        RoleAction.attackSkill(castskill, this.node, this.hero.location);
    }

}
