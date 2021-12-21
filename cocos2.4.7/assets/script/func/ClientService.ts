// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html
import WsConnection from "./WsConnection";
import RoleCollection, { Role } from "./RoleCollection"
const { ccclass, property } = cc._decorator;

export enum MessageType {
    PLAYER_CREATE = 'PLAYER_CREATE',
    PLAYER_ATTRIBUTE = 'PLAYER_ATTRIBUTE',
    PLAYER_LOGIN = 'PLAYER_LOGIN',
}

export interface CreatePlayerMsg {
    name: string;
}

export interface WsMessage {
    messageType: MessageType;
    playerId: number;
    createPlayerMsg: CreatePlayerMsg;
    playerMsg: Role;
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
        let message = { messageType: MessageType.PLAYER_CREATE, createPlayerMsg: createPlayerMsg };
        this.websocket.send(JSON.stringify(message));
    }

    protected update(dt: number) {
        this.parseMessage();
    }

    private parseMessage() {
        const messageStack = this.websocket.getMessageStack();
        messageStack.forEach(message => {
            if (message.messageType == MessageType.PLAYER_ATTRIBUTE) {
                const player: Role = message.playerMsg;
                this.roleCollection.updatePlayer(player);
            }
        });
        this.websocket.clearMessageStack();
    }

    private heartBeat() {
        if (this.websocket.isConnect() && this.roleCollection.player) {
            let message = { messageType: MessageType.PLAYER_LOGIN, playerId: this.roleCollection.player.id };
            this.websocket.send(JSON.stringify(message));
        }
    }

    protected start() {
        this.schedule(this.heartBeat, 1);
    }

}
