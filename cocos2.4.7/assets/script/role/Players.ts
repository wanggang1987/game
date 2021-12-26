// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

import { CastSkill, Role } from "../func/BasicObjects";
import { RoleAction } from "./RoleAction";


const { ccclass, property } = cc._decorator;

@ccclass
export default class Players extends cc.Component {

    private default = 0;
    @property({ type: cc.Prefab })
    private playerPrefab: cc.Prefab = null;
    private players: Map<number, Role> = new Map();
    private deadPlayers: number[] = new Array();

    public getPlayers(): Map<number, Role> {
        return this.players;
    }

    public getDeadPlayers(): number[] {
        return this.deadPlayers;
    }

    protected update(dt: number): void {
        for (let playerId of this.deadPlayers) {
            let playerNode: cc.Node = this.node.getChildByName(playerId.toString());
            if (playerNode) {
                playerNode.destroy();
                this.players.delete(playerId);
                console.log("destory playerNode " + playerNode.name);
            }
        }
        this.deadPlayers.length = 0;

        this.players.forEach((player, playerId) => {
            let playerNode: cc.Node = this.playerNode(playerId);

            if (player.location && player.location.isUpdate) {
                playerNode.setPosition(player.location.x * 50, player.location.y * 50);
                player.location.isUpdate = false;
            }

            if (player.attribute && player.attribute.isUpdate) {
                let nameNode: cc.Node = playerNode.getChildByName("Name");
                let name = nameNode.getComponent(cc.Label);
                name.string = player.attribute.name + ":" + player.attribute.id + "(lv" + player.attribute.level + ")";
                player.attribute.isUpdate = false;
            }

            if (player.fightStatus && player.fightStatus.isUpdate) {
                let hpNode: cc.Node = playerNode.getChildByName("Hp");
                let hp: cc.ProgressBar = hpNode.getComponent(cc.ProgressBar);
                hp.progress = player.fightStatus.healthPoint / player.fightStatus.healthMax;
                player.fightStatus.isUpdate = false;
            }
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
        RoleAction.attackSkill(castskill, this.playerNode(player.id), player.location);
    }
}
