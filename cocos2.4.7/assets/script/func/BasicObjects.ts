// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

export enum MessageType {
    //receive
    HERO_ATTRIBUTE = 'HERO_ATTRIBUTE',
    HERO_LOCATION = 'HERO_LOCATION',
    PLAYER_ATTRIBUTE = 'PLAYER_ATTRIBUTE',
    PLAYER_LOCATION = 'PLAYER_LOCATION',
    PLAYER_DIE = 'PLAYER_DIE',
    PLAYER_FIGHTSTATUS = 'PLAYER_FIGHTSTATUS',
    PLAYER_CASTSKILL = 'PLAYER_CASTSKILL',
    MONSTER_ATTRIBUTE = 'MONSTER_ATTRIBUTE',
    MONSTER_LOCATION = 'MONSTER_LOCATION',
    MONSTER_DIE = 'MONSTER_DIE',
    MONSTER_FIGHTSTATUS = 'MONSTER_FIGHTSTATUS',
    MONSTER_CASTSKILL = 'MONSTER_CASTSKILL',
    FIGHTDAMAGE = 'FIGHTDAMAGE',
    //send
    LOGIN = 'LOGIN',
    PLAYER_CREATE = 'PLAYER_CREATE',
    ATTRIBUTE_REQUEST = 'ATTRIBUTE_REQUEST',
}

export enum RoleType {
    MONSTER = 'MONSTER',
    PLAYER = 'PLAYER',
}

export interface CreatePlayerMsg {
    name: string;
}

export interface WsMessage {
    time: number;
    //receive
    messageType: MessageType;
    attributeMsg: Attribute;
    fightStatusMsg: FightStatus;
    locationMsg: Location;
    roleDieMsg: RoleDie;
    castSkillMsg: CastSkill;
    //send
    playerId: number;
    createPlayerMsg: CreatePlayerMsg;
    attributeRequest: AttributeRequest;
}

export interface CastSkill {
    sourceId: number;
    sourceType: RoleType;
    targetId: number;
    targetType: RoleType;
    skillName: string;
    targetX: number;
    targetY: number;
}

export interface FightDamage {
    sourceId: number;
    sourceType: RoleType;
    targetId: number;
    targetType: RoleType;
    skillName: string;
}

export interface RoleDie {
    id: number;
}

export interface Location {
    isUpdate: boolean;
    id: number;
    x: number;
    y: number;
}

export interface AttributeRequest {
    roleId: number;
    roleType: RoleType;
}

export interface Attribute {
    isUpdate: boolean;
    id: number;
    name: string;
    level: number;
    speed: number;
}

export interface FightStatus {
    isUpdate: boolean;
    id: number;
    healthPoint: number;
    healthMax: number;
}

export class Role {
    public id: number;
    public attribute: Attribute;
    public location: Location;
    public fightStatus: FightStatus;
}
