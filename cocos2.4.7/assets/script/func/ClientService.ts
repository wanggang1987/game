// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html
import WsConnection from "./WsConnection";
const { ccclass, property } = cc._decorator;

export enum MessageType {
    CONNECT,
    PLAYER_CREATE,
}

export interface CreatePlayerMsg {
    name: string;
}

export interface WsMessage {
    messageType: MessageType;
    playerId: number;
    createPlayerMsg: CreatePlayerMsg;
}

@ccclass
export default class ClientService extends cc.Component {
    @property({
        type: WsConnection
    })
    private websocket: WsConnection = null;

    public createPlayer(createPlayerMsg: CreatePlayerMsg) {
        let message = { messageType: MessageType.PLAYER_CREATE, createPlayerMsg: createPlayerMsg };
        this.websocket.send(message);
    }

    start() {

    }
}
