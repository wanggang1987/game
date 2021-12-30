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
    public hero: Role = null;
    @property({ type: Monsters })
    private monstersService: Monsters = null;
    public monsters: Map<number, Role> = null;
    private deadMonsters: number[] = null;
    public clearMonster: number[] = null;
    @property({ type: Players })
    private playersService: Players = null;
    public players: Map<number, Role> = null;
    private deadPlayers: number[] = null;
    public clearPlayers: number[] = null;

    protected onLoad(): void {
        this.hero = this.heroService.hero;
        this.monsters = this.monstersService.monsters;
        this.deadMonsters = this.monstersService.deadMonsters;
        this.clearMonster = this.monstersService.clearMonster;
        this.players = this.playersService.players;
        this.deadPlayers = this.playersService.deadPlayers;
        this.clearPlayers = this.playersService.clearPlayers;
    }

    private gridStr(x: number, y: number): string {
        let nx = Number((x / 20).toFixed(0));
        x = nx > 0 ? x + 1 : x - 1;
        let ny = Number((y / 20).toFixed(0));
        return "x:" + nx + "y:" + ny + "z:0";
    }
    private nearGrids(location: Location): string[] {
        let x = location.x; let y = location.y;
        let grids: string[] = new Array();
        grids.push(this.gridStr(x, y));
        grids.push(this.gridStr(x + 20, y));
        grids.push(this.gridStr(x, y - 20));
        grids.push(this.gridStr(x - 20, y));
        grids.push(this.gridStr(x, y + 20));
        grids.push(this.gridStr(x + 20, y - 20));
        grids.push(this.gridStr(x - 20, y - 20));
        grids.push(this.gridStr(x - 20, y + 20));
        grids.push(this.gridStr(x + 20, y + 20));
        return grids;
    }

    public resourceClear() {
        if (this.hero.location) {
            let nearGrids: string[] = this.nearGrids(this.hero.location);

            this.players.forEach((player, playerId) => {
                if (player.location && !nearGrids.includes(player.location.grid)) {

                console.log(player.location, '1111111111111')
                console.log(this.hero.location, '22222222222')


                    this.clearPlayers.push(playerId);
                }
            });
            this.monsters.forEach((monster, monsterId) => {
                if (monster.location && !nearGrids.includes(monster.location.grid)) {


                console.log(monster.location, '1111111111111')
                console.log(this.hero.location, '22222222222')


                    this.clearPlayers.push(monsterId);
                }
            });

        }

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
        role.updateTime = location.updateTime;
    }

    public updateAttribute(attribute: Attribute) {
        let role: Role = this.selectRole(attribute.id, attribute.roleType);
        if (role.attribute && role.attribute.updateTime > attribute.updateTime) {
            return;
        }
        role.attribute = attribute;
        role.attribute.isUpdate = true;
        role.updateTime = attribute.updateTime;
    }

    public updateFightStatus(fightStatus: FightStatus) {
        let role: Role = this.selectRole(fightStatus.id, fightStatus.roleType);
        if (role.fightStatus && role.fightStatus.updateTime > fightStatus.updateTime) {
            return;
        }
        role.fightStatus = fightStatus;
        role.fightStatus.isUpdate = true;
        role.updateTime = fightStatus.updateTime;
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
