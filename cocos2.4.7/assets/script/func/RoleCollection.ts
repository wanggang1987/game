// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

const { ccclass, property } = cc._decorator;

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
    public attribute: Attribute;
    public location: Location;
}

@ccclass
export default class RoleCollection extends cc.Component {

    private default = 0;
    private hero: Role = new Role();

    public getHero() {
        return this.hero;
    }

    public updateHeroLocation(location: Location) {
        this.hero.location = location;
        console.log(location, 'updateLocation');
    }

    public updateHeroAttribute(attribute: Attribute) {
        this.hero.attribute = attribute;
        console.log(attribute, 'updateHero');
    }

}
