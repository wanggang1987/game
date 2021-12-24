// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

import Hero from "../Hero";
import Monsters from "../Monsters";
import Players from "../Players";
import { Attribute, Role, Location, RoleDie } from "./BasicObjects";
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

    protected onLoad(): void {
        this.hero = this.heroService.getHero();
        this.monsters = this.monstersService.getMonsters();
        this.deadMonsters = this.monstersService.getDeadMonsters();
        this.players = this.playersService.getPlayers();
        this.deadPlayers = this.playersService.getDeadPlayers();
    }

    public updateHeroLocation(location: Location) {
        this.hero.location = location;
        this.hero.isUpdate = true;
    }
    public updateHeroAttribute(attribute: Attribute) {
        this.hero.id = attribute.id;
        this.hero.attribute = attribute;
        this.hero.isUpdate = true;
    }

    public updateMonsterLocation(location: Location) {
        let monsterId: number = location.id;
        let monster: Role = null;
        if (this.monsters.has(monsterId)) {
            monster = this.monsters.get(monsterId);
        } else {
            monster = new Role();
            monster.id = monsterId;
            this.monsters.set(monsterId, monster);
        }
        monster.location = location;
        monster.isUpdate = true;
    }

    public monsterDie(roleDie: RoleDie) {
        this.deadMonsters.push(roleDie.id);
    }

    public playerDie(roleDie: RoleDie) {
        this.deadPlayers.push(roleDie.id);
    }

    public updatePlayerLocation(location: Location) {
        if (location.id === this.hero.id) {
            this.updateHeroLocation(location);
            return;
        }
        let playerId: number = location.id;
        let player: Role = null;
        if (this.players.has(playerId)) {
            player = this.players.get(playerId);
        } else {
            player = new Role();
            player.id = playerId;
            this.players.set(playerId, player);
        }
        player.location = location;
        player.isUpdate = true;
    }

    public updatePlayerAttribute(attribute: Attribute) {
        if (attribute.id === this.hero.id) {
            this.updateHeroAttribute(attribute);
            return;
        }
    }
}
