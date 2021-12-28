// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html
import WsConnection from "./WsConnection";
import { CreatePlayerMsg, MessageType, Role, RoleType, WsMessage } from "./BasicObjects";
import RoleCollection from "../role/RoleCollection";
import UICollection from "../ui/UICollection";
const { ccclass, property } = cc._decorator;

@ccclass
export default class ClientService extends cc.Component {

    private default = 0;
    @property({ type: WsConnection })
    private websocket: WsConnection = null;
    @property({ type: RoleCollection })
    private roleCollection: RoleCollection = null;
    private hero: Role = null;
    private monsters: Map<number, Role> = null;
    private players: Map<number, Role> = null;
    @property({ type: UICollection })
    private uiCollection: UICollection = null;

    public createPlayer(createPlayerMsg: CreatePlayerMsg) {
        let message = { messageType: MessageType.PLAYER_CREATE, createPlayerMsg: createPlayerMsg };
        this.websocket.send(JSON.stringify(message));
        console.log(message, message.messageType);
    }

    private sendAttributeRequest(roleId: number, roleType: RoleType) {
        let message = {
            messageType: MessageType.ATTRIBUTE_REQUEST,
            attributeRequest: { roleId: roleId, roleType: roleType }
        };
        this.websocket.send(JSON.stringify(message));
        console.log(message, message.messageType);
    }

    protected update(dt: number) {
        this.parseMessage();
    }

    private parseMessage() {
        const messageStack = this.websocket.getMessageStack();
        messageStack.forEach(wsMessage => {
            let message: WsMessage = wsMessage;
            if (message.messageType == MessageType.HERO_ATTRIBUTE) {
                console.log(message, message.messageType);
                this.roleCollection.updateHeroAttribute(message.attributeMsg);
                this.roleCollection.updateHeroFightstatus(message.fightStatusMsg);

            } else if (message.messageType == MessageType.HERO_LOCATION) {
                this.roleCollection.updateHeroLocation(message.locationMsg);

            } else if (message.messageType == MessageType.HERO_DAMAGE) {
                console.log(message, message.messageType);
                this.roleCollection.heroDamage(message.fightDamageMsg);
                this.uiCollection.addDamageMsg(message.fightDamageMsg);

            } else if (message.messageType == MessageType.HERO_BE_DAMAGED) {
                console.log(message, message.messageType);
                this.roleCollection.heroBeDamage(message.fightDamageMsg);
                this.uiCollection.addDamageMsg(message.fightDamageMsg);

            }
            else if (message.messageType == MessageType.PLAYER_ATTRIBUTE) {
                console.log(message, message.messageType);
                this.roleCollection.updatePlayerAttribute(message.attributeMsg);

            } else if (message.messageType == MessageType.PLAYER_LOCATION) {
                this.roleCollection.updatePlayerLocation(message.locationMsg);

            } else if (message.messageType == MessageType.PLAYER_DIE) {
                console.log(message, message.messageType);
                this.roleCollection.playerDie(message.roleDieMsg);

            } else if (message.messageType == MessageType.PLAYER_FIGHTSTATUS) {
                this.roleCollection.updatePlayerFightStatus(message.fightStatusMsg);

            } else if (message.messageType == MessageType.PLAYER_CASTSKILL) {
                console.log(message, message.messageType);
                this.roleCollection.playerCastSkill(message.castSkillMsg);

            } else if (message.messageType == MessageType.MONSTER_ATTRIBUTE) {
                console.log(message, message.messageType);
                this.roleCollection.updateMonsterAttribute(message.attributeMsg);

            } else if (message.messageType == MessageType.MONSTER_LOCATION) {
                this.roleCollection.updateMonsterLocation(message.locationMsg);

            } else if (message.messageType == MessageType.MONSTER_DIE) {
                console.log(message, message.messageType);
                this.roleCollection.monsterDie(message.roleDieMsg);

            } else if (message.messageType == MessageType.MONSTER_FIGHTSTATUS) {
                this.roleCollection.updateMonsterFightStatus(message.fightStatusMsg);

            } else if (message.messageType == MessageType.MONSTER_CASTSKILL) {
                console.log(message, message.messageType);
                this.roleCollection.monsterCastSkill(message.castSkillMsg);
            }
        });
        this.websocket.clearMessageStack();
    }

    private roleCheck() {
        this.heartBeat();
        this.attributeCheck();
    }

    private heartBeat() {
        if (this.websocket.isConnect() && this.hero.attribute) {
            let message = { messageType: MessageType.LOGIN, playerId: this.hero.attribute.id };
            this.websocket.send(JSON.stringify(message));
        }
    }

    private attributeCheck() {
        this.monsters.forEach((monster, monsterId) => {
            if (!monster.attribute)
                this.sendAttributeRequest(monsterId, RoleType.MONSTER);
        });
        this.players.forEach((player, playerId) => {
            if (!player.attribute)
                this.sendAttributeRequest(playerId, RoleType.PLAYER);
        });
    }

    protected start() {
        this.hero = this.roleCollection.getHero();
        this.monsters = this.roleCollection.getMonsters();
        this.players = this.roleCollection.getPlayers();
        this.schedule(this.roleCheck, 1);
    }

}
