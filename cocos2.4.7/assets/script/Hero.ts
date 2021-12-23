// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html
import RoleCollection, { Role } from "./func/RoleCollection";
const { ccclass, property } = cc._decorator;

@ccclass
export default class Hero extends cc.Component {

    private default = 0;
    @property({
        type: RoleCollection
    })
    private roleCollection: RoleCollection = null;
    @property({
        type: cc.Label
    })
    private lable: cc.Label = null;

    protected onLoad() {
    }

    protected update(dt: number): void {
        let hero: Role = this.roleCollection.getHero();
        if (hero.attribute) {
            this.lable.string = hero.attribute.name + ":(" + hero.location.x.toFixed(2) + ","
                + hero.location.y.toFixed(2) + ")";
        }
        if (hero.location) {
            this.node.setPosition(hero.location.x * 50, hero.location.y * 50);
        }
    }

}
