// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

import Hero from "../Hero";
import Monsters from "../Monsters";
import { Attribute, Role, Location } from "./BasicObjects";

const { ccclass, property } = cc._decorator;


@ccclass
export default class RoleCollection extends cc.Component {

    private default = 0;
    private hero: Role = null;
    @property({ type: Hero })
    private heroNode: Hero = null;
    private monsters: Map<number, Role> = null;
    @property({ type: Monsters })
    private monstersNode: Monsters = null;

    protected onLoad(): void {
        this.hero = this.heroNode.getHero();
        this.monsters = this.monstersNode.getMonsters();
    }

    public updateHeroLocation(location: Location) {
        this.hero.location = location;
        this.hero.isUpdate = true;
    }
    public updateHeroAttribute(attribute: Attribute) {
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
}
