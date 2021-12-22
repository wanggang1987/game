// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html
import WsConnection from "./WsConnection";
import RoleCollection, { Attribute, Location, Role } from "./RoleCollection"
const { ccclass, property } = cc._decorator;

export enum MessageType {
    LOGIN = 'LOGIN',
    HERO_CREATE = 'HERO_CREATE',
    HERO_ATTRIBUTE = 'HERO_ATTRIBUTE',
    HERO_LOCATION = 'HERO_LOCATION',
}

export interface CreatePlayerMsg {
    name: string;
}

export interface WsMessage {
    messageType: MessageType;
    playerId: number;
    createPlayerMsg: CreatePlayerMsg;
    attributeMsg: Attribute;
    locationMsg: Location;
}


@ccclass
export default class ClientService extends cc.Component {

    private default = 0;
    @property({
        type: WsConnection
    })
    private websocket: WsConnection = null;
    @property({
        type: RoleCollection
    })
    private roleCollection: RoleCollection = null;

    public createPlayer(createPlayerMsg: CreatePlayerMsg) {
        let message = { messageType: MessageType.HERO_CREATE, createPlayerMsg: createPlayerMsg };
        this.websocket.send(JSON.stringify(message));
    }

    protected update(dt: number) {
        this.parseMessage();
    }

    private parseMessage() {
        const messageStack = this.websocket.getMessageStack();
        messageStack.forEach(message => {
            if (message.messageType == MessageType.HERO_ATTRIBUTE) {
                const attribute: Attribute = message.attributeMsg;
                this.roleCollection.updateHeroAttribute(attribute);
            }
            if (message.messageType == MessageType.HERO_LOCATION) {
                const location: Location = message.locationMsg;
                this.roleCollection.updateHeroLocation(location);
            }
        });
        this.websocket.clearMessageStack();
    }

    private heartBeat() {
        let hero :Role = this.roleCollection.getHero();
        if (this.websocket.isConnect() && hero.attribute) {
            let message = { messageType: MessageType.LOGIN, playerId: hero.attribute.id };
            this.websocket.send(JSON.stringify(message));
        }
    }

    protected start() {
        this.schedule(this.heartBeat, 1);
    }

}
