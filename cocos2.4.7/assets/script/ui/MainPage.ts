// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html
import ClientService from "../func/ClientService";
const {ccclass, property} = cc._decorator;

@ccclass
export default class MainPage extends cc.Component {

    @property({
        type: ClientService
    })
    private clientService: ClientService = null;

    private createUser(){
        let message = { name: 'test' };
        this.clientService.createPlayer(message);
        console.log("craate user");
    }

}
