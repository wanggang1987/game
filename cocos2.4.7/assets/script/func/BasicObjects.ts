// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

export enum MessageType {
    //receive
    HERO_UPDATE = 'HERO_UPDATE',
    ATTRIBUTE = 'ATTRIBUTE',
    LOCATION = 'LOCATION',
    FIGHTSTATUS = 'FIGHTSTATUS',
    CASTSKILL = 'CASTSKILL',
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

export enum SkillType {
    DAMAGE_SKILL = 'DAMAGE_SKILL',
    SOURCE_MOVE_SKILL = 'SOURCE_MOVE_SKILL',
    BUFFER_SKILL = 'BUFFER_SKILL',
    CONTROL_SKILL = 'CONTROL_SKILL',
    NORMAL_ATTACK = 'NORMAL_ATTACK',
}

export enum LivingStatus {
    LIVING = 'LIVING',
    DEAD = 'DEAD',
}

export interface CreatePlayerMsg {
    name: string;
}

export interface WsMessage {
    //receive
    messageType: MessageType;
    attributeMsg: Attribute;
    fightStatusMsg: FightStatus;
    locationMsg: Location;
    roleDieMsg: RoleDie;
    castSkillMsg: CastSkill;
    fightDamageMsg: FightDamageMsg;
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
    skillId: number;
    skillType: SkillType;
    skillName: string;
    targetX: number;
    targetY: number;
}

export interface FightDamageMsg {
    sourceId: number;
    sourceType: RoleType;
    targetId: number;
    targetType: RoleType;
    skillName: string;
    damage: number;
}

export interface RoleDie {
    id: number;
    roleType: RoleType;
}

export interface Location {
    isUpdate: boolean;
    updateTime: number;
    id: number;
    roleType: RoleType;
    x: number;
    y: number;
    grid: string;
}

export interface AttributeRequest {
    roleId: number;
    roleType: RoleType;
}

export interface Attribute {
    isUpdate: boolean;
    updateTime: number;
    id: number;
    roleType: RoleType;
    name: string;
    level: number;
}

export interface FightStatus {
    isUpdate: boolean;
    updateTime: number;
    id: number;
    roleType: RoleType;
    healthPoint: number;
    healthMax: number;
    livingStatus: LivingStatus;
}

export class Role {
    public updateTime: number;
    public isOutGrids: boolean;
    public id: number;
    public isAttackAction = false;
    public attribute: Attribute;
    public location: Location;
    public fightStatus: FightStatus;
}
