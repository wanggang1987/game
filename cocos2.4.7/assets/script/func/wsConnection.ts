// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html
const { ccclass, property } = cc._decorator;

@ccclass
export default class WsConnection extends cc.Component {

    private default = 0;
    private url: string;
    private ws: WebSocket;
    private isConnecting: boolean = false;
    private errorStack = [];
    private messageStack = [];

    public isConnect() {
        return this.isConnecting;
    }

    public getMessageStack() {
        return this.messageStack;
    }

    public clearMessageStack() {
        this.messageStack = []
    }

    protected onLoad() {
        this.url = "ws://localhost:8080/ws";
        this.createWs();
    }

    private createWs() {
        if ('WebSocket' in window) {
            // 实例化
            this.ws = new WebSocket(this.url);
            // 监听事件
            this.onopen();
            this.onerror();
            this.onclose();
            this.onmessage();
        } else {
            console.log('你的浏览器不支持 WebSocket');
        }
    }

    // 监听成功
    private onopen() {
        this.ws.onopen = () => {
            console.log(this.ws, 'onopen');
            // 发送成功连接之前所发送失败的消息
            this.errorStack.forEach(message => {
                this.send(message);
            })
            this.errorStack = [];
            this.isConnecting = true;
        }
    }

    // 监听错误
    private onerror() {
        this.ws.onerror = (err) => {
            console.log(err, 'onerror');
            this.isConnecting = false;
        }
    }

    // 监听关闭
    private onclose() {
        this.ws.onclose = () => {
            console.log('onclose');
            this.isConnecting = false;
        }
    }

    // 接收 WebSocket 消息
    private async onmessage() {
        this.ws.onmessage = (event) => {
            try {
                const message = JSON.parse(event.data);
                this.messageStack.push(message);
                console.log(message, 'onmessage')
            } catch (error) {
                console.log(error, 'onmessage');
            }
        }
    }

    // 重连
    private reconnection() {
        if (this.isConnecting) {
            return;
        }
        console.log('reconnection');
        this.createWs();
    }

    protected start() {
        this.schedule(this.reconnection, 3);
    }

    // 发送消息
    public send(message) {
        // 连接失败时的处理
        if (this.ws.readyState !== 1) {
            this.errorStack.push(message);
            return;
        }
        this.ws.send(message);
    }
}
