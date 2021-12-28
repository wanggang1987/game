import { CastSkill, FightDamageMsg, Location } from "../func/BasicObjects";

export class RoleAction {

    public static attackSkill(castskill: CastSkill, roleNode: cc.Node, location: Location) {
        if (!castskill || !roleNode || !location) {
            return;
        }

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
            .start();
    }

    public static showDamage(damageMsg: FightDamageMsg, roleNode: cc.Node, lableNode: cc.Node) {
        roleNode.addChild(lableNode);
        let lable: cc.Label = lableNode.getComponent(cc.Label);
        lable.string = damageMsg.damage.toFixed(0).toString();
        cc.tween(lableNode)
            .by(2, { position: cc.v3(0, 30), opacity: -100 }, { easing: 'CubicOut' })
            .call(() => { lableNode.destroy() })
            .start();
    }
}