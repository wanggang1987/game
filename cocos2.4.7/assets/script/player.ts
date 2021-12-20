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

    protected onLoad() {
    }

    protected update(dt: number): void {

    }

}
