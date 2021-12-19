// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html
import wsConnection from "./func/wsConnection";
const { ccclass, property } = cc._decorator;

@ccclass
export default class player extends cc.Component {
    
    @property({
        type: wsConnection
    })
    private websocket: wsConnection = null;

    private playerPersionX = 0;
    private playerPersionY = 0;
    private isLogin = false;
    
    protected update(dt: number): void {
        if (!this.isLogin){
            this.websocket.send("create");
            console.log("craate user");
            this.isLogin = true;
        }
    }

    start() {

    }

}
