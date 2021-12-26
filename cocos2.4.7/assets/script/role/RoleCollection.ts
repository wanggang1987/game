// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

import { Attribute, Role, Location, CastSkill, FightStatus, RoleDie } from "../func/BasicObjects";
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

    public updateHeroLocation(location: Location) {
        this.hero.location = location;
        this.hero.location.isUpdate = true;
    }
    public updateHeroAttribute(attribute: Attribute) {
        this.hero.id = attribute.id;
        this.hero.attribute = attribute;
        this.hero.attribute.isUpdate = true;
    }

    public updateHeroFightstatus(fightStatus: FightStatus) {
        this.hero.fightStatus = fightStatus;
        this.hero.fightStatus.isUpdate = true;
    }

    public heroCastSkill(castskill: CastSkill) {
        this.heroService.castSkill(castskill);
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

    public updatePlayerLocation(location: Location) {
        if (location.id === this.hero.id) {
            this.updateHeroLocation(location);
            return;
        }
        let player: Role = this.playerRole(location.id);
        player.location = location;
        player.location.isUpdate = true;
    }

    public updatePlayerAttribute(attribute: Attribute) {
        if (attribute.id === this.hero.id) {
            this.updateHeroAttribute(attribute);
            return;
        }
        let player: Role = this.playerRole(attribute.id);
        player.attribute = attribute;
        player.attribute.isUpdate = true;
    }

    public playerDie(roleDie: RoleDie) {
        this.deadPlayers.push(roleDie.id);
    }

    public updatePlayerFightStatus(fightStatus: FightStatus) {
        if (fightStatus.id === this.hero.id) {
            this.updateHeroFightstatus(fightStatus);
            return;
        }
        let player: Role = this.playerRole(fightStatus.id);
        player.fightStatus = fightStatus;
        player.fightStatus.isUpdate = true;
    }

    public playerCastSkill(castskill: CastSkill) {
        if (castskill.sourceId === this.hero.id) {
            this.heroCastSkill(castskill);
            return;
        }
        let player: Role = this.playerRole(castskill.sourceId);
        this.playersService.castSkill(castskill, player);
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

    public updateMonsterAttribute(attribute: Attribute) {
        let monster: Role = this.monsterRole(attribute.id);
        monster.attribute = attribute;
        monster.attribute.isUpdate = true;
    }

    public updateMonsterLocation(location: Location) {
        let monster: Role = this.monsterRole(location.id);
        monster.location = location;
        monster.location.isUpdate = true;
    }

    public updateMonsterFightStatus(fightStatus: FightStatus) {
        let monster: Role = this.monsterRole(fightStatus.id);
        monster.fightStatus = fightStatus;
        monster.fightStatus.isUpdate = true;
    }

    public monsterDie(roleDie: RoleDie) {
        this.deadMonsters.push(roleDie.id);
    }

    public monsterCastSkill(castskill: CastSkill) {
        let monstser: Role = this.monsterRole(castskill.sourceId);
        this.monstersService.castSkill(castskill, monstser);
    }

}
