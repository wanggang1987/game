// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html
import RoleCollection, { Role } from "./func/RoleCollection";
const { ccclass, property } = cc._decorator;

@ccclass
export default class Player extends cc.Component {

    private default = 0;
    @property({
        type: RoleCollection
    })
    private roleCollection: RoleCollection = null;
    @property({
        type: cc.Label
    })
    private lable: cc.Label = null;
    private role: Role = null;

    protected onLoad() {
    }

    protected update(dt: number): void {
        this.role = this.roleCollection.getPlayer();
        if (this.role){
            this.node.setPosition(this.role.location.x,this.role.location.y );
            this.lable.string = this.role.name + ":(" + this.node.position.x.toFixed(2) + ","
                + this.node.position.y.toFixed(2) + ")";
        }
    }

}
