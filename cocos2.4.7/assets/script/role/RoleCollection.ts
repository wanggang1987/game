// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

import { Attribute, Role, Location, CastSkill, FightStatus, RoleDie, FightDamageMsg, RoleType } from "../func/BasicObjects";
import Hero from "./Hero";
import Monsters from "./Monsters";
import Players from "./Players";

const { ccclass, property } = cc._decorator;

@ccclass
export default class RoleCollection extends cc.Component {

    private default = 0;
    @property({ type: Hero })
    private heroService: Hero = null;
    private hero: Role = null;
    @property({ type: Monsters })
    private monstersService: Monsters = null;
    private monsters: Map<number, Role> = null;
    private deadMonsters: number[] = null;
    @property({ type: Players })
    private playersService: Players = null;
    private players: Map<number, Role> = null;
    private deadPlayers: number[] = null;

    public getHero(): Role {
        return this.hero;
    }

    public getPlayers() {
        return this.players;
    }
    public getMonsters() {
        return this.monsters;
    }

    protected onLoad(): void {
        this.hero = this.heroService.getHero();
        this.monsters = this.monstersService.getMonsters();
        this.deadMonsters = this.monstersService.getDeadMonsters();
        this.players = this.playersService.getPlayers();
        this.deadPlayers = this.playersService.getDeadPlayers();
    }

    private playerRole(id: number): Role {
        let player: Role = null;
        if (this.players.has(id)) {
            player = this.players.get(id);
        } else {
            player = new Role();
            player.id = id;
            this.players.set(id, player);
        }
        return player;
    }

    private monsterRole(id: number): Role {
        let monster: Role = null;
        if (this.monsters.has(id)) {
            monster = this.monsters.get(id);
        } else {
            monster = new Role();
            monster.id = id;
            this.monsters.set(id, monster);
        }
        return monster;
    }

    private selectRole(id: number, roleType: RoleType): Role {
        if (!this.hero) return null;

        if (roleType == RoleType.PLAYER && id == this.hero.id) {
            return this.hero;
        } else if (roleType == RoleType.PLAYER) {
            return this.playerRole(id);
        } else if (roleType == RoleType.MONSTER) {
            return this.monsterRole(id);
        }
    }

    public updateHeroAttribute(attribute: Attribute) {
        this.hero.id = attribute.id;
        this.updateAttribute(attribute);
    }

    public updateLocation(location: Location) {
        let role: Role = this.selectRole(location.id, location.roleType);
        if (role.location && role.location.updateTime > location.updateTime) {
            return;
        }
        role.location = location;
        role.location.isUpdate = true;
    }

    public updateAttribute(attribute: Attribute) {
        let role: Role = this.selectRole(attribute.id, attribute.roleType);
        if (role.attribute && role.attribute.updateTime > attribute.updateTime) {
            return;
        }
        role.attribute = attribute;
        role.attribute.isUpdate = true;
    }

    public updateFightStatus(fightStatus: FightStatus) {
        let role: Role = this.selectRole(fightStatus.id, fightStatus.roleType);
        if (role.fightStatus && role.fightStatus.updateTime > fightStatus.updateTime) {
            return;
        }
        role.fightStatus = fightStatus;
        role.fightStatus.isUpdate = true;
    }

    public roleDie(roleDie: RoleDie) {
        if (roleDie.roleType == RoleType.PLAYER) {
            this.deadPlayers.push(roleDie.id);
        } else if (roleDie.roleType == RoleType.MONSTER) {
            this.deadMonsters.push(roleDie.id);
        }
    }

    public roleCastSkill(castskill: CastSkill) {
        if (castskill.sourceType == RoleType.PLAYER && castskill.sourceId == this.hero.id) {
            this.heroService.castSkill(castskill);
        } else if (castskill.sourceType == RoleType.PLAYER) {
            let player: Role = this.playerRole(castskill.sourceId);
            this.playersService.castSkill(castskill, player);
        } else if (castskill.sourceType == RoleType.MONSTER) {
            let monstser: Role = this.monsterRole(castskill.sourceId);
            this.monstersService.castSkill(castskill, monstser);
        }
    }

    public fightDamage(fightDamageMsg: FightDamageMsg) {
        if (fightDamageMsg.targetType == RoleType.PLAYER && fightDamageMsg.targetId == this.hero.id) {
            this.heroService.showDamage(fightDamageMsg);
        } else if (fightDamageMsg.targetType == RoleType.PLAYER) {
            let player: Role = this.playerRole(fightDamageMsg.targetId);
            this.playersService.showDamage(fightDamageMsg, player);
        } else if (fightDamageMsg.targetType == RoleType.MONSTER) {
            let monstser: Role = this.monsterRole(fightDamageMsg.targetId);
            this.monstersService.showDamage(fightDamageMsg, monstser);
        }
    }
}
