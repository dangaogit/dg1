import { Logger } from '@nestjs/common';
import { NestFactory } from '@nestjs/core';
import { ExpressAdapter } from '@nestjs/platform-express';
import { AppModule } from './app.module';

export async function bootstrap(): Promise<void> {
    const app = await NestFactory.create(AppModule, new ExpressAdapter());
    await app.listen(7777);
    Logger.log('Application run in http://localhost:7777 .');
}

bootstrap();
