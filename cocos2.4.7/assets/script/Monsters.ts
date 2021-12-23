// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:

import { Role } from "./func/BasicObjects";
const { ccclass, property } = cc._decorator;

@ccclass
export default class Monsters extends cc.Component {

    private default = 0;
    @property({ type: cc.Prefab })
    private monsterPrefab: cc.Prefab = null;
    private monsters: Map<number, Role> = new Map();

    public getMonsters(): Map<number, Role> {
        return this.monsters;
    }

    protected update(dt: number): void {
        this.monsters.forEach((monster, monsterId) => {
            let monsterNode: cc.Node = this.node.getChildByName(monsterId.toString());
            if (!monsterNode) {
                monsterNode = cc.instantiate(this.monsterPrefab);
                monsterNode.name = monsterId.toString();
                this.node.addChild(monsterNode);
                console.log("add monsterNode " + monsterNode.name);
            }

            if (monster.isUpdate) {
                monsterNode.setPosition(monster.location.x * 50, monster.location.y * 50);
                monster.isUpdate = false;
            }
        });
    }
}
