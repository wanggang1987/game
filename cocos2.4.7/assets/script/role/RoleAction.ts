import { CastSkill, Location } from "../func/BasicObjects";

export class RoleAction {

    public static attackSkill(castskill: CastSkill, roleNode: cc.Node, location: Location) {
        let xDistance: number = castskill.targetX - location.x;
        let yDistance: number = castskill.targetY - location.y;
        let targetDistance: number = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        let moveDistance: number = 30;
        let x: number = moveDistance / targetDistance * xDistance;
        let y: number = moveDistance / targetDistance * yDistance;
        let bodyNode: cc.Node = roleNode.getChildByName("Body");
        cc.tween(bodyNode)
            .by(0.1, { position: cc.v3(x, y) }, { easing: 'backOut' })
            .by(0.15, { position: cc.v3(-x, -y) }, { easing: 'backOut' })
            .start()
    }

}