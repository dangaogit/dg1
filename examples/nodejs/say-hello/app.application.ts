import { Controller, Get, Logger } from "@nestjs/common";
import { Socket } from "net";
import * as pb from 'protobufjs';

@Controller()
export class AppController {
  @Get("say-hello")
  public async sayHello(): Promise<string> {
    const socket = new Socket({});
    const conn = socket.connect(8888);
    const root = new pb.Root().define('hello').add(new pb.Type('SayHello').add(new pb.Field('id', 1, 'string')).add(new pb.Field('message', 2, 'string')));
    root.add(new pb.Type('SayHelloResponse').add(new pb.Field('code', 1, 'int32')).add(new pb.Field('message', 2, 'string')));
    const message = root.lookupType('hello.SayHello');
    const response = root.lookupType('hello.SayHelloResponse');
    conn.write(message.encode(message.create({id: 'nodejs', message: `Hello there's nodejs.`})).finish(), 'utf-8');
    conn.on("connect", () => Logger.log(`connected to 8888.`));
    conn.on("data", (buff) => {
      const responseBody = response.decode(buff).toJSON();
      Logger.log(`received code '${responseBody.code}' msg '${responseBody.message}'`);
    });
    return "send say hello compile!";
  }
}
