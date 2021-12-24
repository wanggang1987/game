// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

import { Role } from "./func/BasicObjects";

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

    public getDeadPlayers() :number[] {
        return this.deadPlayers;
    }

    protected update(dt: number): void {
        this.players.forEach((player, playerId) => {
            let playerNode: cc.Node = this.node.getChildByName(playerId.toString());
            if (!playerNode) {
                playerNode = cc.instantiate(this.playerPrefab);
                playerNode.name = playerId.toString();
                this.node.addChild(playerNode);
                console.log("add playerNode " + playerNode.name);
            }

            if (player.isUpdate) {
                playerNode.setPosition(player.location.x * 50, player.location.y * 50);
                player.isUpdate = false;
            }
        });

        for (let playerId of this.deadPlayers ){
            let playerNode: cc.Node = this.node.getChildByName(playerId.toString());
            if (playerNode) {
                playerNode.destroy();
                console.log("destory playerNode " + playerNode.name);
            }
        }
        this.deadPlayers.length = 0;
    }
}
