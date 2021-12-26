// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:

import { CastSkill, Role } from "./func/BasicObjects";
import { RoleAction } from "./func/RoleAction";
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
                this.monsters.delete(monsterId);
                console.log("destory monsterNode " + monsterNode.name);
            }
        }
        this.deadMonster.length = 0;

        this.monsters.forEach((monster, monsterId) => {
            let monsterNode: cc.Node = this.monsterNode(monsterId);

            if (monster.location && monster.location.isUpdate) {
                monsterNode.setPosition(monster.location.x * 50, monster.location.y * 50);
                monster.location.isUpdate = false;
            }

            if (monster.attribute && monster.attribute.isUpdate) {
                let nameNode: cc.Node = monsterNode.getChildByName("Name");
                let name = nameNode.getComponent(cc.Label);
                name.string = monster.attribute.name + ":" + monster.attribute.id;
                monster.attribute.isUpdate = false;
            }

            if (monster.fightStatus && monster.fightStatus.isUpdate) {
                let hpNode: cc.Node = monsterNode.getChildByName("Hp");
                let hp: cc.ProgressBar = hpNode.getComponent(cc.ProgressBar);
                hp.progress = monster.fightStatus.healthPoint / monster.fightStatus.healthMax;
                monster.fightStatus.isUpdate = false;
            }
        });
    }

    private monsterNode(monsterId: number) {
        let monsterNode: cc.Node = this.node.getChildByName(monsterId.toString());
        if (!monsterNode) {
            monsterNode = cc.instantiate(this.monsterPrefab);
            monsterNode.name = monsterId.toString();
            this.node.addChild(monsterNode);
            console.log("add monsterNode " + monsterNode.name);
        }
        return monsterNode;
    }

    public castSkill(castskill: CastSkill, player: Role) {
        RoleAction.attackSkill(castskill, this.monsterNode(player.id), player.location);
    }
}
