import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import hello.Hello.SayHello;
import hello.Hello.SayHelloResponse;

public class JavaServer {
    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(9999), 0);
            server.createContext("/say-hello", new HttpHandler() {
                public void handle(HttpExchange t) throws IOException {
                    InputStream in = t.getRequestBody();
                    System.out.println(t.getRemoteAddress() + ": request");
                    String resp = "Hello this is java example.";
                    t.sendResponseHeaders(200, resp.length());
                    OutputStream out = t.getResponseBody();
                    out.write(resp.getBytes());
                    System.out.println(t.getLocalAddress() + ": response");
                    out.close();
                    sayHello("I'm is java.");
                }
            });
            server.setExecutor(null);
            server.start();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        System.out.println("hello java");
    }

    private static void sayHello(String message) {
        try (Socket socketToServer = new Socket("localhost", 8888)) {
            SayHello sayHelloReq = SayHello.newBuilder()
                .setId("java")                    
                .setMessage(message)
                .build();
            sayHelloReq.writeTo(socketToServer.getOutputStream());
            System.out.println(String.format("SayHello send: id = [%s] message = [%s]", sayHelloReq.getId(), sayHelloReq.getMessage()));
            SayHelloResponse sayHelloResp = SayHelloResponse.parseFrom(socketToServer.getInputStream());
            System.out.println(String.format("SayHello receive: code = [%s] message = [%s]", sayHelloResp.getCode(), sayHelloResp.getMessage()));
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}