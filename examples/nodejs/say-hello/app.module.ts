import { Module } from "@nestjs/common";
import { AppController } from "./app.application";

@Module({
    imports: [],
    controllers: [AppController],
    providers: []
})
export class AppModule {}