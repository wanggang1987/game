window.__require=function t(e,o,r){function n(s,c){if(!o[s]){if(!e[s]){var a=s.split("/");if(a=a[a.length-1],!e[a]){var l="function"==typeof __require&&__require;if(!c&&l)return l(a,!0);if(i)return i(a,!0);throw new Error("Cannot find module '"+s+"'")}s=a}var p=o[s]={exports:{}};e[s][0].call(p.exports,function(t){return n(e[s][1][t]||t)},p,p.exports,t,e,o,r)}return o[s].exports}for(var i="function"==typeof __require&&__require,s=0;s<r.length;s++)n(r[s]);return n}({BasicObjects:[function(t,e,o){"use strict";cc._RF.push(e,"50a07Qu1zdMULYJiA75UzXB","BasicObjects"),Object.defineProperty(o,"__esModule",{value:!0}),o.Role=o.RoleType=o.MessageType=void 0,function(t){t.HERO_ATTRIBUTE="HERO_ATTRIBUTE",t.HERO_LOCATION="HERO_LOCATION",t.PLAYER_ATTRIBUTE="PLAYER_ATTRIBUTE",t.PLAYER_LOCATION="PLAYER_LOCATION",t.PLAYER_DIE="PLAYER_DIE",t.PLAYER_FIGHTSTATUS="PLAYER_FIGHTSTATUS",t.PLAYER_CASTSKILL="PLAYER_CASTSKILL",t.MONSTER_ATTRIBUTE="MONSTER_ATTRIBUTE",t.MONSTER_LOCATION="MONSTER_LOCATION",t.MONSTER_DIE="MONSTER_DIE",t.MONSTER_FIGHTSTATUS="MONSTER_FIGHTSTATUS",t.MONSTER_CASTSKILL="MONSTER_CASTSKILL",t.FIGHTDAMAGE="FIGHTDAMAGE",t.LOGIN="LOGIN",t.PLAYER_CREATE="PLAYER_CREATE",t.ATTRIBUTE_REQUEST="ATTRIBUTE_REQUEST"}(o.MessageType||(o.MessageType={})),function(t){t.MONSTER="MONSTER",t.PLAYER="PLAYER"}(o.RoleType||(o.RoleType={}));o.Role=function(){},cc._RF.pop()},{}],ClientService:[function(t,e,o){"use strict";cc._RF.push(e,"59866lsQUZAI6eocn1mc2ZX","ClientService");var r,n=this&&this.__extends||(r=function(t,e){return(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&(t[o]=e[o])})(t,e)},function(t,e){function o(){this.constructor=t}r(t,e),t.prototype=null===e?Object.create(e):(o.prototype=e.prototype,new o)}),i=this&&this.__decorate||function(t,e,o,r){var n,i=arguments.length,s=i<3?e:null===r?r=Object.getOwnPropertyDescriptor(e,o):r;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)s=Reflect.decorate(t,e,o,r);else for(var c=t.length-1;c>=0;c--)(n=t[c])&&(s=(i<3?n(s):i>3?n(e,o,s):n(e,o))||s);return i>3&&s&&Object.defineProperty(e,o,s),s};Object.defineProperty(o,"__esModule",{value:!0});var s=t("./WsConnection"),c=t("./BasicObjects"),a=t("../role/RoleCollection"),l=cc._decorator,p=l.ccclass,u=l.property,f=function(t){function e(){var e=null!==t&&t.apply(this,arguments)||this;return e.default=0,e.websocket=null,e.roleCollection=null,e.hero=null,e.monsters=null,e.players=null,e}return n(e,t),e.prototype.createPlayer=function(t){var e={messageType:c.MessageType.PLAYER_CREATE,createPlayerMsg:t};this.websocket.send(JSON.stringify(e)),console.log(e,e.messageType)},e.prototype.sendAttributeRequest=function(t,e){var o={messageType:c.MessageType.ATTRIBUTE_REQUEST,attributeRequest:{roleId:t,roleType:e}};this.websocket.send(JSON.stringify(o)),console.log(o,o.messageType)},e.prototype.update=function(){this.parseMessage()},e.prototype.parseMessage=function(){var t=this;this.websocket.getMessageStack().forEach(function(e){var o=e;o.messageType==c.MessageType.HERO_ATTRIBUTE?(console.log(o,o.messageType),t.roleCollection.updateHeroAttribute(o.attributeMsg),t.roleCollection.updateHeroFightstatus(o.fightStatusMsg)):o.messageType==c.MessageType.HERO_LOCATION?t.roleCollection.updateHeroLocation(o.locationMsg):o.messageType==c.MessageType.PLAYER_ATTRIBUTE?(console.log(o,o.messageType),t.roleCollection.updatePlayerAttribute(o.attributeMsg)):o.messageType==c.MessageType.PLAYER_LOCATION?t.roleCollection.updatePlayerLocation(o.locationMsg):o.messageType==c.MessageType.PLAYER_DIE?(console.log(o,o.messageType),t.roleCollection.playerDie(o.roleDieMsg)):o.messageType==c.MessageType.PLAYER_FIGHTSTATUS?t.roleCollection.updatePlayerFightStatus(o.fightStatusMsg):o.messageType==c.MessageType.PLAYER_CASTSKILL?(console.log(o,o.messageType),t.roleCollection.playerCastSkill(o.castSkillMsg)):o.messageType==c.MessageType.MONSTER_ATTRIBUTE?(console.log(o,o.messageType),t.roleCollection.updateMonsterAttribute(o.attributeMsg)):o.messageType==c.MessageType.MONSTER_LOCATION?t.roleCollection.updateMonsterLocation(o.locationMsg):o.messageType==c.MessageType.MONSTER_DIE?(console.log(o,o.messageType),t.roleCollection.monsterDie(o.roleDieMsg)):o.messageType==c.MessageType.MONSTER_FIGHTSTATUS?t.roleCollection.updateMonsterFightStatus(o.fightStatusMsg):o.messageType==c.MessageType.MONSTER_CASTSKILL?(console.log(o,o.messageType),t.roleCollection.monsterCastSkill(o.castSkillMsg)):o.messageType==c.MessageType.FIGHTDAMAGE&&console.log(o,o.messageType)}),this.websocket.clearMessageStack()},e.prototype.roleCheck=function(){this.heartBeat(),this.attributeCheck()},e.prototype.heartBeat=function(){if(this.websocket.isConnect()&&this.hero.attribute){var t={messageType:c.MessageType.LOGIN,playerId:this.hero.attribute.id};this.websocket.send(JSON.stringify(t))}},e.prototype.attributeCheck=function(){var t=this;this.monsters.forEach(function(e,o){e.attribute||t.sendAttributeRequest(o,c.RoleType.MONSTER)}),this.players.forEach(function(e,o){e.attribute||t.sendAttributeRequest(o,c.RoleType.PLAYER)})},e.prototype.start=function(){this.hero=this.roleCollection.getHero(),this.monsters=this.roleCollection.getMonsters(),this.players=this.roleCollection.getPlayers(),this.schedule(this.roleCheck,1)},i([u({type:s.default})],e.prototype,"websocket",void 0),i([u({type:a.default})],e.prototype,"roleCollection",void 0),i([p],e)}(cc.Component);o.default=f,cc._RF.pop()},{"../role/RoleCollection":"RoleCollection","./BasicObjects":"BasicObjects","./WsConnection":"WsConnection"}],Hero:[function(t,e,o){"use strict";cc._RF.push(e,"07d31HOCLVAUIwCV6eq4Xzh","Hero");var r,n=this&&this.__extends||(r=function(t,e){return(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&(t[o]=e[o])})(t,e)},function(t,e){function o(){this.constructor=t}r(t,e),t.prototype=null===e?Object.create(e):(o.prototype=e.prototype,new o)}),i=this&&this.__decorate||function(t,e,o,r){var n,i=arguments.length,s=i<3?e:null===r?r=Object.getOwnPropertyDescriptor(e,o):r;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)s=Reflect.decorate(t,e,o,r);else for(var c=t.length-1;c>=0;c--)(n=t[c])&&(s=(i<3?n(s):i>3?n(e,o,s):n(e,o))||s);return i>3&&s&&Object.defineProperty(e,o,s),s};Object.defineProperty(o,"__esModule",{value:!0});var s=t("../func/BasicObjects"),c=t("./RoleAction"),a=cc._decorator,l=a.ccclass,p=a.property,u=function(t){function e(){var e=null!==t&&t.apply(this,arguments)||this;return e.default=0,e.hero=new s.Role,e.Name=null,e.Hp=null,e}return n(e,t),e.prototype.getHero=function(){return this.hero},e.prototype.update=function(){this.hero.location&&this.hero.location.isUpdate&&(this.node.setPosition(50*this.hero.location.x,50*this.hero.location.y),this.hero.location.isUpdate=!1),this.hero.attribute&&this.hero.attribute.isUpdate&&(this.Name.string="\u73a9\u5bb6:"+this.hero.attribute.id+"(lv"+this.hero.attribute.level+")",this.hero.attribute.isUpdate=!1),this.hero.fightStatus&&this.hero.fightStatus.isUpdate&&(this.Hp.progress=this.hero.fightStatus.healthPoint/this.hero.fightStatus.healthMax,this.hero.fightStatus.isUpdate=!1)},e.prototype.castSkill=function(t){c.RoleAction.attackSkill(t,this.node,this.hero.location)},i([p({type:cc.Label})],e.prototype,"Name",void 0),i([p({type:cc.ProgressBar})],e.prototype,"Hp",void 0),i([l],e)}(cc.Component);o.default=u,cc._RF.pop()},{"../func/BasicObjects":"BasicObjects","./RoleAction":"RoleAction"}],MainPage:[function(t,e,o){"use strict";cc._RF.push(e,"a0b88NepnJAkr/molmobvVn","MainPage");var r,n=this&&this.__extends||(r=function(t,e){return(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&(t[o]=e[o])})(t,e)},function(t,e){function o(){this.constructor=t}r(t,e),t.prototype=null===e?Object.create(e):(o.prototype=e.prototype,new o)}),i=this&&this.__decorate||function(t,e,o,r){var n,i=arguments.length,s=i<3?e:null===r?r=Object.getOwnPropertyDescriptor(e,o):r;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)s=Reflect.decorate(t,e,o,r);else for(var c=t.length-1;c>=0;c--)(n=t[c])&&(s=(i<3?n(s):i>3?n(e,o,s):n(e,o))||s);return i>3&&s&&Object.defineProperty(e,o,s),s};Object.defineProperty(o,"__esModule",{value:!0});var s=t("../func/ClientService"),c=cc._decorator,a=c.ccclass,l=c.property,p=function(t){function e(){var e=null!==t&&t.apply(this,arguments)||this;return e.default=0,e.clientService=null,e}return n(e,t),e.prototype.clickStartButton=function(){this.clientService.createPlayer({name:"test"}),console.log("craate user"),this.node.active=!1},i([l({type:s.default})],e.prototype,"clientService",void 0),i([a],e)}(cc.Component);o.default=p,cc._RF.pop()},{"../func/ClientService":"ClientService"}],Monsters:[function(t,e,o){"use strict";cc._RF.push(e,"2666fdy9uxCAoQN94Lvw2qI","Monsters");var r,n=this&&this.__extends||(r=function(t,e){return(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&(t[o]=e[o])})(t,e)},function(t,e){function o(){this.constructor=t}r(t,e),t.prototype=null===e?Object.create(e):(o.prototype=e.prototype,new o)}),i=this&&this.__decorate||function(t,e,o,r){var n,i=arguments.length,s=i<3?e:null===r?r=Object.getOwnPropertyDescriptor(e,o):r;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)s=Reflect.decorate(t,e,o,r);else for(var c=t.length-1;c>=0;c--)(n=t[c])&&(s=(i<3?n(s):i>3?n(e,o,s):n(e,o))||s);return i>3&&s&&Object.defineProperty(e,o,s),s};Object.defineProperty(o,"__esModule",{value:!0});var s=t("./RoleAction"),c=cc._decorator,a=c.ccclass,l=c.property,p=function(t){function e(){var e=null!==t&&t.apply(this,arguments)||this;return e.default=0,e.monsterPrefab=null,e.monsters=new Map,e.deadMonster=new Array,e}return n(e,t),e.prototype.getDeadMonsters=function(){return this.deadMonster},e.prototype.getMonsters=function(){return this.monsters},e.prototype.update=function(){for(var t=this,e=0,o=this.deadMonster;e<o.length;e++){var r=o[e],n=this.node.getChildByName(r.toString());n&&(n.destroy(),this.monsters.delete(r),console.log("destory monsterNode "+n.name))}this.deadMonster.length=0,this.monsters.forEach(function(e,o){var r=t.monsterNode(o);e.location&&e.location.isUpdate&&(r.setPosition(50*e.location.x,50*e.location.y),e.location.isUpdate=!1),e.attribute&&e.attribute.isUpdate&&(r.getChildByName("Name").getComponent(cc.Label).string=e.attribute.name+":"+e.attribute.id,e.attribute.isUpdate=!1),e.fightStatus&&e.fightStatus.isUpdate&&(r.getChildByName("Hp").getComponent(cc.ProgressBar).progress=e.fightStatus.healthPoint/e.fightStatus.healthMax,e.fightStatus.isUpdate=!1)})},e.prototype.monsterNode=function(t){var e=this.node.getChildByName(t.toString());return e||((e=cc.instantiate(this.monsterPrefab)).name=t.toString(),this.node.addChild(e),console.log("add monsterNode "+e.name)),e},e.prototype.castSkill=function(t,e){s.RoleAction.attackSkill(t,this.monsterNode(e.id),e.location)},i([l({type:cc.Prefab})],e.prototype,"monsterPrefab",void 0),i([a],e)}(cc.Component);o.default=p,cc._RF.pop()},{"./RoleAction":"RoleAction"}],Players:[function(t,e,o){"use strict";cc._RF.push(e,"c2107YGYpFNMY2NnMn9Y9UK","Players");var r,n=this&&this.__extends||(r=function(t,e){return(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&(t[o]=e[o])})(t,e)},function(t,e){function o(){this.constructor=t}r(t,e),t.prototype=null===e?Object.create(e):(o.prototype=e.prototype,new o)}),i=this&&this.__decorate||function(t,e,o,r){var n,i=arguments.length,s=i<3?e:null===r?r=Object.getOwnPropertyDescriptor(e,o):r;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)s=Reflect.decorate(t,e,o,r);else for(var c=t.length-1;c>=0;c--)(n=t[c])&&(s=(i<3?n(s):i>3?n(e,o,s):n(e,o))||s);return i>3&&s&&Object.defineProperty(e,o,s),s};Object.defineProperty(o,"__esModule",{value:!0});var s=t("./RoleAction"),c=cc._decorator,a=c.ccclass,l=c.property,p=function(t){function e(){var e=null!==t&&t.apply(this,arguments)||this;return e.default=0,e.playerPrefab=null,e.players=new Map,e.deadPlayers=new Array,e}return n(e,t),e.prototype.getPlayers=function(){return this.players},e.prototype.getDeadPlayers=function(){return this.deadPlayers},e.prototype.update=function(){for(var t=this,e=0,o=this.deadPlayers;e<o.length;e++){var r=o[e],n=this.node.getChildByName(r.toString());n&&(n.destroy(),this.players.delete(r),console.log("destory playerNode "+n.name))}this.deadPlayers.length=0,this.players.forEach(function(e,o){var r=t.playerNode(o);e.location&&e.location.isUpdate&&(r.setPosition(50*e.location.x,50*e.location.y),e.location.isUpdate=!1),e.attribute&&e.attribute.isUpdate&&(r.getChildByName("Name").getComponent(cc.Label).string=e.attribute.name+":"+e.attribute.id+"(lv"+e.attribute.level+")",e.attribute.isUpdate=!1),e.fightStatus&&e.fightStatus.isUpdate&&(r.getChildByName("Hp").getComponent(cc.ProgressBar).progress=e.fightStatus.healthPoint/e.fightStatus.healthMax,e.fightStatus.isUpdate=!1)})},e.prototype.playerNode=function(t){var e=this.node.getChildByName(t.toString());return e||((e=cc.instantiate(this.playerPrefab)).name=t.toString(),this.node.addChild(e),console.log("add playerNode "+e.name)),e},e.prototype.castSkill=function(t,e){s.RoleAction.attackSkill(t,this.playerNode(e.id),e.location)},i([l({type:cc.Prefab})],e.prototype,"playerPrefab",void 0),i([a],e)}(cc.Component);o.default=p,cc._RF.pop()},{"./RoleAction":"RoleAction"}],RoleAction:[function(t,e,o){"use strict";cc._RF.push(e,"7f6e1gMfhFBWKjM0+DIZIRx","RoleAction"),Object.defineProperty(o,"__esModule",{value:!0}),o.RoleAction=void 0;var r=function(){function t(){}return t.attackSkill=function(t,e,o){if(t&&e&&o){var r=t.targetX-o.x,n=t.targetY-o.y,i=Math.sqrt(r*r+n*n),s=30/i*r,c=30/i*n,a=e.getChildByName("Body");cc.tween(a).by(.1,{position:cc.v3(s,c)},{easing:"backOut"}).by(.15,{position:cc.v3(-s,-c)},{easing:"backOut"}).start()}},t}();o.RoleAction=r,cc._RF.pop()},{}],RoleCollection:[function(t,e,o){"use strict";cc._RF.push(e,"aafa1Bh2MJCdZCHqcNHwg+9","RoleCollection");var r,n=this&&this.__extends||(r=function(t,e){return(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&(t[o]=e[o])})(t,e)},function(t,e){function o(){this.constructor=t}r(t,e),t.prototype=null===e?Object.create(e):(o.prototype=e.prototype,new o)}),i=this&&this.__decorate||function(t,e,o,r){var n,i=arguments.length,s=i<3?e:null===r?r=Object.getOwnPropertyDescriptor(e,o):r;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)s=Reflect.decorate(t,e,o,r);else for(var c=t.length-1;c>=0;c--)(n=t[c])&&(s=(i<3?n(s):i>3?n(e,o,s):n(e,o))||s);return i>3&&s&&Object.defineProperty(e,o,s),s};Object.defineProperty(o,"__esModule",{value:!0});var s=t("../func/BasicObjects"),c=t("./Hero"),a=t("./Monsters"),l=t("./Players"),p=cc._decorator,u=p.ccclass,f=p.property,h=function(t){function e(){var e=null!==t&&t.apply(this,arguments)||this;return e.default=0,e.heroService=null,e.hero=null,e.monstersService=null,e.monsters=null,e.deadMonsters=null,e.playersService=null,e.players=null,e.deadPlayers=null,e}return n(e,t),e.prototype.getHero=function(){return this.hero},e.prototype.getPlayers=function(){return this.players},e.prototype.getMonsters=function(){return this.monsters},e.prototype.onLoad=function(){this.hero=this.heroService.getHero(),this.monsters=this.monstersService.getMonsters(),this.deadMonsters=this.monstersService.getDeadMonsters(),this.players=this.playersService.getPlayers(),this.deadPlayers=this.playersService.getDeadPlayers()},e.prototype.updateHeroLocation=function(t){this.hero.location=t,this.hero.location.isUpdate=!0},e.prototype.updateHeroAttribute=function(t){this.hero.id=t.id,this.hero.attribute=t,this.hero.attribute.isUpdate=!0},e.prototype.updateHeroFightstatus=function(t){this.hero.fightStatus=t,this.hero.fightStatus.isUpdate=!0},e.prototype.heroCastSkill=function(t){this.heroService.castSkill(t)},e.prototype.playerRole=function(t){var e=null;return this.players.has(t)?e=this.players.get(t):((e=new s.Role).id=t,this.players.set(t,e)),e},e.prototype.updatePlayerLocation=function(t){if(t.id!==this.hero.id){var e=this.playerRole(t.id);e.location=t,e.location.isUpdate=!0}else this.updateHeroLocation(t)},e.prototype.updatePlayerAttribute=function(t){if(t.id!==this.hero.id){var e=this.playerRole(t.id);e.attribute=t,e.attribute.isUpdate=!0}else this.updateHeroAttribute(t)},e.prototype.playerDie=function(t){this.deadPlayers.push(t.id)},e.prototype.updatePlayerFightStatus=function(t){if(t.id!==this.hero.id){var e=this.playerRole(t.id);e.fightStatus=t,e.fightStatus.isUpdate=!0}else this.updateHeroFightstatus(t)},e.prototype.playerCastSkill=function(t){if(t.sourceId!==this.hero.id){var e=this.playerRole(t.sourceId);this.playersService.castSkill(t,e)}else this.heroCastSkill(t)},e.prototype.monsterRole=function(t){var e=null;return this.monsters.has(t)?e=this.monsters.get(t):((e=new s.Role).id=t,this.monsters.set(t,e)),e},e.prototype.updateMonsterAttribute=function(t){var e=this.monsterRole(t.id);e.attribute=t,e.attribute.isUpdate=!0},e.prototype.updateMonsterLocation=function(t){var e=this.monsterRole(t.id);e.location=t,e.location.isUpdate=!0},e.prototype.updateMonsterFightStatus=function(t){var e=this.monsterRole(t.id);e.fightStatus=t,e.fightStatus.isUpdate=!0},e.prototype.monsterDie=function(t){this.deadMonsters.push(t.id)},e.prototype.monsterCastSkill=function(t){var e=this.monsterRole(t.sourceId);this.monstersService.castSkill(t,e)},i([f({type:c.default})],e.prototype,"heroService",void 0),i([f({type:a.default})],e.prototype,"monstersService",void 0),i([f({type:l.default})],e.prototype,"playersService",void 0),i([u],e)}(cc.Component);o.default=h,cc._RF.pop()},{"../func/BasicObjects":"BasicObjects","./Hero":"Hero","./Monsters":"Monsters","./Players":"Players"}],TalkBox:[function(t,e,o){"use strict";cc._RF.push(e,"0f31bGJ+VRDlaP0AS7KRt/S","TalkBox");var r,n=this&&this.__extends||(r=function(t,e){return(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&(t[o]=e[o])})(t,e)},function(t,e){function o(){this.constructor=t}r(t,e),t.prototype=null===e?Object.create(e):(o.prototype=e.prototype,new o)}),i=this&&this.__decorate||function(t,e,o,r){var n,i=arguments.length,s=i<3?e:null===r?r=Object.getOwnPropertyDescriptor(e,o):r;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)s=Reflect.decorate(t,e,o,r);else for(var c=t.length-1;c>=0;c--)(n=t[c])&&(s=(i<3?n(s):i>3?n(e,o,s):n(e,o))||s);return i>3&&s&&Object.defineProperty(e,o,s),s};Object.defineProperty(o,"__esModule",{value:!0});var s=cc._decorator,c=s.ccclass,a=(s.property,function(t){function e(){return null!==t&&t.apply(this,arguments)||this}return n(e,t),i([c],e)}(cc.Component));o.default=a,cc._RF.pop()},{}],UICollection:[function(t,e,o){"use strict";cc._RF.push(e,"9908fqqfKFMqbkK6HRpuHRn","UICollection");var r,n=this&&this.__extends||(r=function(t,e){return(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&(t[o]=e[o])})(t,e)},function(t,e){function o(){this.constructor=t}r(t,e),t.prototype=null===e?Object.create(e):(o.prototype=e.prototype,new o)}),i=this&&this.__decorate||function(t,e,o,r){var n,i=arguments.length,s=i<3?e:null===r?r=Object.getOwnPropertyDescriptor(e,o):r;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)s=Reflect.decorate(t,e,o,r);else for(var c=t.length-1;c>=0;c--)(n=t[c])&&(s=(i<3?n(s):i>3?n(e,o,s):n(e,o))||s);return i>3&&s&&Object.defineProperty(e,o,s),s};Object.defineProperty(o,"__esModule",{value:!0});var s=cc._decorator,c=s.ccclass,a=(s.property,function(t){function e(){var e=null!==t&&t.apply(this,arguments)||this;return e.deadMonster=new Array,e}return n(e,t),i([c],e)}(cc.Component));o.default=a,cc._RF.pop()},{}],WsConnection:[function(t,e,o){"use strict";cc._RF.push(e,"01a98G0BedBlKVqBMrgZsAo","WsConnection");var r,n=this&&this.__extends||(r=function(t,e){return(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&(t[o]=e[o])})(t,e)},function(t,e){function o(){this.constructor=t}r(t,e),t.prototype=null===e?Object.create(e):(o.prototype=e.prototype,new o)}),i=this&&this.__decorate||function(t,e,o,r){var n,i=arguments.length,s=i<3?e:null===r?r=Object.getOwnPropertyDescriptor(e,o):r;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)s=Reflect.decorate(t,e,o,r);else for(var c=t.length-1;c>=0;c--)(n=t[c])&&(s=(i<3?n(s):i>3?n(e,o,s):n(e,o))||s);return i>3&&s&&Object.defineProperty(e,o,s),s},s=this&&this.__awaiter||function(t,e,o,r){return new(o||(o=Promise))(function(n,i){function s(t){try{a(r.next(t))}catch(e){i(e)}}function c(t){try{a(r.throw(t))}catch(e){i(e)}}function a(t){var e;t.done?n(t.value):(e=t.value,e instanceof o?e:new o(function(t){t(e)})).then(s,c)}a((r=r.apply(t,e||[])).next())})},c=this&&this.__generator||function(t,e){var o,r,n,i,s={label:0,sent:function(){if(1&n[0])throw n[1];return n[1]},trys:[],ops:[]};return i={next:c(0),throw:c(1),return:c(2)},"function"==typeof Symbol&&(i[Symbol.iterator]=function(){return this}),i;function c(t){return function(e){return a([t,e])}}function a(i){if(o)throw new TypeError("Generator is already executing.");for(;s;)try{if(o=1,r&&(n=2&i[0]?r.return:i[0]?r.throw||((n=r.return)&&n.call(r),0):r.next)&&!(n=n.call(r,i[1])).done)return n;switch(r=0,n&&(i=[2&i[0],n.value]),i[0]){case 0:case 1:n=i;break;case 4:return s.label++,{value:i[1],done:!1};case 5:s.label++,r=i[1],i=[0];continue;case 7:i=s.ops.pop(),s.trys.pop();continue;default:if(!(n=(n=s.trys).length>0&&n[n.length-1])&&(6===i[0]||2===i[0])){s=0;continue}if(3===i[0]&&(!n||i[1]>n[0]&&i[1]<n[3])){s.label=i[1];break}if(6===i[0]&&s.label<n[1]){s.label=n[1],n=i;break}if(n&&s.label<n[2]){s.label=n[2],s.ops.push(i);break}n[2]&&s.ops.pop(),s.trys.pop();continue}i=e.call(t,s)}catch(c){i=[6,c],r=0}finally{o=n=0}if(5&i[0])throw i[1];return{value:i[0]?i[1]:void 0,done:!0}}};Object.defineProperty(o,"__esModule",{value:!0});var a=cc._decorator,l=a.ccclass,p=(a.property,function(t){function e(){var e=null!==t&&t.apply(this,arguments)||this;return e.default=0,e.isConnecting=!1,e.errorStack=[],e.messageStack=[],e}return n(e,t),e.prototype.isConnect=function(){return this.isConnecting},e.prototype.getMessageStack=function(){return this.messageStack},e.prototype.clearMessageStack=function(){this.messageStack=[]},e.prototype.onLoad=function(){this.url="ws://39.99.147.120:8080/ws",this.createWs()},e.prototype.createWs=function(){"WebSocket"in window?(this.ws=new WebSocket(this.url),this.onopen(),this.onerror(),this.onclose(),this.onmessage()):console.log("\u4f60\u7684\u6d4f\u89c8\u5668\u4e0d\u652f\u6301 WebSocket")},e.prototype.onopen=function(){var t=this;this.ws.onopen=function(){console.log(t.ws,"onopen"),t.errorStack.forEach(function(e){t.send(e)}),t.errorStack=[],t.isConnecting=!0}},e.prototype.onerror=function(){var t=this;this.ws.onerror=function(e){console.log(e,"onerror"),t.isConnecting=!1}},e.prototype.onclose=function(){var t=this;this.ws.onclose=function(){console.log("onclose"),t.isConnecting=!1}},e.prototype.onmessage=function(){return s(this,void 0,void 0,function(){var t=this;return c(this,function(){return this.ws.onmessage=function(e){try{var o=JSON.parse(e.data);t.messageStack.push(o)}catch(r){console.log(r,"onmessage")}},[2]})})},e.prototype.reconnection=function(){this.isConnecting||(console.log("reconnection"),this.createWs())},e.prototype.start=function(){this.schedule(this.reconnection,3)},e.prototype.send=function(t){1===this.ws.readyState?this.ws.send(t):this.errorStack.push(t)},i([l],e)}(cc.Component));o.default=p,cc._RF.pop()},{}]},{},["BasicObjects","ClientService","WsConnection","Hero","Monsters","Players","RoleAction","RoleCollection","MainPage","TalkBox","UICollection"]);