import { Attribute, CastSkill, FightDamageMsg, FightStatus, Location, Role, SkillType } from "../func/BasicObjects";

export class RoleAction {

    public static attackSkill(castskill: CastSkill, roleNode: cc.Node, target: Role) {
        if (!castskill || !roleNode || !target || !target.location || target.isAttackAction) {
            return;
        }
        if (castskill.skillType != SkillType.DAMAGE_SKILL) {
            return;
        }

        let xDistance: number = castskill.targetX - target.location.x;
        let yDistance: number = castskill.targetY - target.location.y;
        let targetDistance: number = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        let moveDistance: number = 30;
        let x: number = moveDistance / targetDistance * xDistance;
        let y: number = moveDistance / targetDistance * yDistance;
        let bodyNode: cc.Node = roleNode.getChildByName("Body");
        cc.tween(bodyNode)
            .by(0.1, { position: cc.v3(x, y) }, { easing: 'backOut' })
            .by(0.15, { position: cc.v3(-x, -y) }, { easing: 'backOut' })
            .call(() => { target.isAttackAction = false })
            .start();
        target.isAttackAction = true;
    }

    public static showDamage(damageMsg: FightDamageMsg, roleNode: cc.Node, lableNode: cc.Node) {
        let str: string = damageMsg.damage.toFixed(0).toString();
        this.showFightInfo(str, roleNode, lableNode);
    }

    public static showSkill(castskill: CastSkill, roleNode: cc.Node, lableNode: cc.Node) {
        let str: string = castskill.skillName;
        this.showFightInfo(str, roleNode, lableNode);
    }

    private static showFightInfo(str: string, roleNode: cc.Node, lableNode: cc.Node) {
        roleNode.addChild(lableNode);
        let lable: cc.Label = lableNode.getComponent(cc.Label);
        lable.string = str;
        cc.tween(lableNode)
            .by(2, { position: cc.v3(0, 30), opacity: -100 }, { easing: 'cubicOut' })
            .call(() => { lableNode.destroy() })
            .start();
    }

    public static updateLocation(roleNode: cc.Node, location: Location) {
        if (location && location.isUpdate) {
            roleNode.setPosition(location.x * 50, location.y * 50);
            location.isUpdate = false;
        }
    }

    public static updateAttribute(roleNode: cc.Node, attribute: Attribute) {
        if (attribute && attribute.isUpdate) {
            let nameNode: cc.Node = roleNode.getChildByName("Name");
            let name = nameNode.getComponent(cc.Label);
            name.string = attribute.name + ":" + attribute.id + "(lv" + attribute.level + ")";
            attribute.isUpdate = false;
        }
    }

    public static updateFightStatus(roleNode: cc.Node, fightStatus: FightStatus) {
        if (fightStatus && fightStatus.isUpdate) {
            let hpNode: cc.Node = roleNode.getChildByName("Hp");
            let hp: cc.ProgressBar = hpNode.getComponent(cc.ProgressBar);
            hp.progress = fightStatus.healthPoint / fightStatus.healthMax;
            fightStatus.isUpdate = false;
        }
    }
}