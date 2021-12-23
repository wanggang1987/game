// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:

import { Role } from "./func/BasicObjects";
const { ccclass, property } = cc._decorator;

@ccclass
export default class Hero extends cc.Component {

    private default = 0;
    @property({ type: cc.Label })
    private lable: cc.Label = null;
    private hero: Role = new Role();

    public getHero(): Role {
        return this.hero;
    }

    protected update(dt: number): void {
        if (!this.hero.isUpdate) return;

        if (this.hero.location) {
            this.node.setPosition(this.hero.location.x * 50, this.hero.location.y * 50);
        }
        if (this.hero.attribute && this.hero.location) {
            this.lable.string = this.hero.attribute.name + ":(" + this.hero.location.x.toFixed(2) + ","
                + this.hero.location.y.toFixed(2) + ")";
        }
        this.hero.isUpdate = false;
    }

}
