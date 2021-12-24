// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

const { ccclass, property } = cc._decorator;

export enum MessageType {
    LOGIN = 'LOGIN',
    PLAYER_CREATE = 'PLAYER_CREATE',
    HERO_ATTRIBUTE = 'HERO_ATTRIBUTE',
    HERO_LOCATION = 'HERO_LOCATION',
    PLAYER_ATTRIBUTE = 'PLAYER_ATTRIBUTE',
    PLAYER_LOCATION = 'PLAYER_LOCATION',
    PLAYER_DIE = 'PLAYER_DIE',
    MONSTER_ATTRIBUTE = 'MONSTER_ATTRIBUTE',
    MONSTER_LOCATION = 'MONSTER_LOCATION',
    MONSTER_DIE = 'MONSTER_DIE',
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
    roleDieMsg: RoleDie;
}

export interface RoleDie {
    id: number;
}

export interface Location {
    id: number;
    x: number;
    y: number;
}

export interface Attribute {
    id: number;
    name: string;
    level: number;
    speed: number;
    attackRange: number;
    healthPoint: number;
    healthMax: number;
}

export class Role {
    public id: number;
    public isUpdate: boolean = false;
    public attribute: Attribute;
    public location: Location;
}

@ccclass
export default class NewClass extends cc.Component {

}
