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
    // 要连接的URL
    private url: string;
    // 一个协议字符串或一个协议字符串数组。
    // 这些字符串用来指定子协议，这样一个服务器就可以实现多个WebSocket子协议
    private protocols: string;
    // WebSocket 实例
    private ws: WebSocket;
    // 是否在重连中
    private isReconnectionLoading: boolean = false;
    // 延时重连的 id
    private timeId: number = null;
    // 是否是用户手动关闭连接
    private isCustomClose: boolean = false;
    // 错误消息队列
    private errorStack = [];
    // 消息队列
    private messageStack = [];

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
            this.isReconnectionLoading = false;
        }
    }

    // 监听错误
    private onerror() {
        this.ws.onerror = (err) => {
            console.log(err, 'onerror');
            this.reconnection();
            this.isReconnectionLoading = false;
        }
    }

    // 监听关闭
    private onclose() {
        this.ws.onclose = () => {
            console.log('onclose');
            this.reconnection();
            this.isReconnectionLoading = false;
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
        // 防止重复
        if (this.isReconnectionLoading) return;

        this.isReconnectionLoading = true;
        clearTimeout(this.timeId);
        this.timeId = setTimeout(() => {
            this.createWs();
        }, 3000)
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
