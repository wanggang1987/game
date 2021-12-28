// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html
import ClientService from "../func/ClientService";
import UICollection from "./UICollection";
const { ccclass, property } = cc._decorator;

@ccclass
export default class MainPage extends cc.Component {

    private default = 0;
    @property({ type: ClientService })
    private clientService: ClientService = null;
    @property({ type: UICollection })
    private uICollection: UICollection = null;

    private clickStartButton() {
        let message = { name: 'test' };
        this.clientService.createPlayer(message);
        console.log("craate user");
        let startButtonNode: cc.Node = this.node.getChildByName("StartButton");
        startButtonNode.active = false;
    }

    protected update(dt: number): void {
        if (this.uICollection.damageMsgUpdate) {
            let damageInfo: string = "";
            this.uICollection.damageMsgs.forEach(damageMsg => {
                let info = damageMsg.sourceType + ":" + damageMsg.sourceId
                    + " " + damageMsg.skillName + " "
                    + damageMsg.targetType + ":" + damageMsg.targetId;
                damageInfo = damageInfo.concat("\n").concat(info);
            });
            let damageInfoBoxNode: cc.Node = this.node.getChildByName("DamageInfoBox");
            let damageInfoBoxLable: cc.Label = damageInfoBoxNode.getComponent(cc.Label);
            damageInfoBoxLable.string = damageInfo;
            this.uICollection.damageMsgUpdate = false;
        }
    }
}
