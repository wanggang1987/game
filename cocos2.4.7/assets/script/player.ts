// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html
import ClientService from "./func/ClientService";
const { ccclass, property } = cc._decorator;

@ccclass
export default class Player extends cc.Component {

    @property({
        type: ClientService
    })
    private clientService: ClientService = null;

    private playerPersionX = 0;
    private playerPersionY = 0;
    private isLogin = false;
    private playerId = 0;

    protected onLoad() {
        if (!this.isLogin) {
            let message = { name: 'test' };
            this.clientService.createPlayer(message);
            console.log("craate user");
            this.isLogin = true;
        }
    }

    protected update(dt: number): void {

    }

    start() {

    }

}
