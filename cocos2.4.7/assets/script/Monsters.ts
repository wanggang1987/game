// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:

import { Role } from "./func/BasicObjects";
import Roles from "./Roles";
const { ccclass, property } = cc._decorator;

@ccclass
export default class Monsters extends cc.Component {

    private default = 0;
    @property({ type: cc.Prefab })
    private monsterPrefab: cc.Prefab = null;
    private monsters: Map<number, Role> = new Map();
    private deadMonster: number[] = new Array();

    public getDeadMonsters(): number[] {
        return this.deadMonster;
    }

    public getMonsters(): Map<number, Role> {
        return this.monsters;
    }

    protected update(dt: number): void {
        for (let monsterId of this.deadMonster) {
            let monsterNode: cc.Node = this.node.getChildByName(monsterId.toString());
            if (monsterNode) {
                monsterNode.destroy();
                console.log("destory monsterNode " + monsterNode.name);
            }
        }
        this.deadMonster.length = 0;

        this.monsters.forEach((monster, monsterId) => {
            let monsterNode: cc.Node = this.node.getChildByName(monsterId.toString());
            if (!monsterNode) {
                monsterNode = cc.instantiate(this.monsterPrefab);
                monsterNode.name = monsterId.toString();
                this.node.addChild(monsterNode);
                console.log("add monsterNode " + monsterNode.name);
            }

            if (monster.location && monster.location.isUpdate) {
                monsterNode.setPosition(monster.location.x * 50, monster.location.y * 50);
                monster.location.isUpdate = false;
            }

            if (monster.attribute && monster.attribute.isUpdate) {
                let name: cc.Node = monsterNode.getChildByName("Name");
                if (name) {
                    let nameLabel = name.getComponent(cc.Label);
                    nameLabel.string = monster.attribute.name + ":" + monster.attribute.id;
                    monster.attribute.isUpdate = false;
                }

            }
        });

    }
}
