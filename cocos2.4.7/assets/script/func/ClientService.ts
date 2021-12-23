// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html
import WsConnection from "./WsConnection";
import RoleCollection from "./RoleCollection"
import { CreatePlayerMsg, MessageType, Role } from "./BasicObjects";
const { ccclass, property } = cc._decorator;

@ccclass
export default class ClientService extends cc.Component {

    private default = 0;
    @property({ type: WsConnection })
    private websocket: WsConnection = null;
    @property({ type: RoleCollection })
    private roleCollection: RoleCollection = null;

    public createPlayer(createPlayerMsg: CreatePlayerMsg) {
        let message = { messageType: MessageType.PLAYER_CREATE, createPlayerMsg: createPlayerMsg };
        this.websocket.send(JSON.stringify(message));
    }

    protected update(dt: number) {
        this.parseMessage();
    }

    private parseMessage() {
        const messageStack = this.websocket.getMessageStack();
        messageStack.forEach(message => {
            if (message.messageType == MessageType.HERO_ATTRIBUTE) {
                this.roleCollection.updateHeroAttribute(message.attributeMsg);
            } else if (message.messageType == MessageType.HERO_LOCATION) {
                this.roleCollection.updateHeroLocation(message.locationMsg);
            } else if (message.messageType == MessageType.MONSTER_LOCATION) {
                this.roleCollection.updateMonsterLocation(message.locationMsg);
            }
        });
        this.websocket.clearMessageStack();
    }

    private heartBeat() {
        let hero: Role = this.roleCollection.getHero();
        if (this.websocket.isConnect() && hero.attribute) {
            let message = { messageType: MessageType.LOGIN, playerId: hero.attribute.id };
            this.websocket.send(JSON.stringify(message));
        }
    }

    protected start() {
        this.schedule(this.heartBeat, 1);
    }

}
