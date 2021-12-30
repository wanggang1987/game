// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:

import { CastSkill, FightDamageMsg, Role } from "../func/BasicObjects";
import { RoleAction } from "./RoleAction";

const { ccclass, property } = cc._decorator;

@ccclass
export default class Monsters extends cc.Component {

    private default = 0;
    @property({ type: cc.Prefab })
    private monsterPrefab: cc.Prefab = null;
    @property({ type: cc.Prefab })
    private damageNumber: cc.Prefab = null;
    public monsters: Map<number, Role> = new Map();
    public deadMonsters: number[] = new Array();
    public clearMonster: number[] = new Array();

    protected update(dt: number): void {
        for (let monsterId of this.deadMonsters) {
            let monsterNode: cc.Node = this.node.getChildByName(monsterId.toString());
            if (monsterNode && monsterNode.active) {
                monsterNode.active = false;
            }
        }
        this.deadMonsters.length = 0;

        for (let monsterId of this.clearMonster) {
            let monsterNode: cc.Node = this.node.getChildByName(monsterId.toString());
            if (monsterNode) {
                monsterNode.destroy();
                console.log("destory monsterNode " + monsterNode.name);
            }
        }
        this.clearMonster.length = 0;

        this.monsters.forEach((monster, monsterId) => {
            let monsterNode: cc.Node = this.monsterNode(monsterId);

            RoleAction.updateLocation(monsterNode, monster.location);
            RoleAction.updateAttribute(monsterNode, monster.attribute);
            RoleAction.updateFightStatus(monsterNode, monster.fightStatus);
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

    public castSkill(castskill: CastSkill, monster: Role) {
        let monsterNode: cc.Node = this.monsterNode(monster.id);
        let lableNode: cc.Node = cc.instantiate(this.damageNumber);
        RoleAction.attackSkill(castskill, monsterNode, monster);
        RoleAction.showSkill(castskill, monsterNode, lableNode);
    }

    public showDamage(damageMsg: FightDamageMsg, monster: Role) {
        let monsterNode: cc.Node = this.monsterNode(monster.id);
        let lableNode: cc.Node = cc.instantiate(this.damageNumber);
        RoleAction.showDamage(damageMsg, monsterNode, lableNode);
    }
}
