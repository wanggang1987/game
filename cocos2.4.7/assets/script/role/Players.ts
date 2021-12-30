// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

import { CastSkill, FightDamageMsg, Role } from "../func/BasicObjects";
import { RoleAction } from "./RoleAction";


const { ccclass, property } = cc._decorator;

@ccclass
export default class Players extends cc.Component {

    private default = 0;
    @property({ type: cc.Prefab })
    private playerPrefab: cc.Prefab = null;
    @property({ type: cc.Prefab })
    private damageNumber: cc.Prefab = null;
    public players: Map<number, Role> = new Map();
    public deadPlayers: number[] = new Array();
    public clearPlayers: number[] = new Array();

    public getPlayers(): Map<number, Role> {
        return this.players;
    }

    public getDeadPlayers(): number[] {
        return this.deadPlayers;
    }

    protected update(dt: number): void {
        for (let playerId of this.deadPlayers) {
            let playerNode: cc.Node = this.node.getChildByName(playerId.toString());
            if (playerNode && playerNode.active) {
                playerNode.active = false;
            }
        }
        this.deadPlayers.length = 0;

        for (let playerId of this.clearPlayers) {
            let playerNode: cc.Node = this.node.getChildByName(playerId.toString());
            if (playerNode) {
                playerNode.destroy();
            }
        }
        this.clearPlayers.length = 0;

        this.players.forEach((player, playerId) => {
            let playerNode: cc.Node = this.playerNode(playerId);

            RoleAction.updateLocation(playerNode, player.location);
            RoleAction.updateAttribute(playerNode, player.attribute);
            RoleAction.updateFightStatus(playerNode, player.fightStatus);
        });
    }

    private playerNode(playerId: number) {
        let playerNode: cc.Node = this.node.getChildByName(playerId.toString());
        if (!playerNode) {
            playerNode = cc.instantiate(this.playerPrefab);
            playerNode.name = playerId.toString();
            this.node.addChild(playerNode);
            console.log("add playerNode " + playerNode.name);
        }
        return playerNode;
    }

    public castSkill(castskill: CastSkill, player: Role) {
        let playerNode: cc.Node = this.playerNode(player.id);
        let lableNode: cc.Node = cc.instantiate(this.damageNumber);
        RoleAction.attackSkill(castskill, playerNode, player);
        RoleAction.showSkill(castskill, playerNode, lableNode);
    }

    public showDamage(damageMsg: FightDamageMsg, player: Role) {
        let playerNode: cc.Node = this.playerNode(player.id);
        let lableNode: cc.Node = cc.instantiate(this.damageNumber);
        RoleAction.showDamage(damageMsg, playerNode, lableNode);
    }
}
