// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

const { ccclass, property } = cc._decorator;

export interface Role {
    id: number;
    name: string;
    level: number;
    speed: number;
    attackRange: number;
    healthPoint: number;
    healthMax: number;
}

@ccclass
export default class RoleCollection extends cc.Component {

    private player: Role = null;

    public updatePlayer(player: Role){
        this.player = player;
        console.log(player, 'updatePlayer');
    }
    
}
