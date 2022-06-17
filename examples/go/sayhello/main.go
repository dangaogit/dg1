package main

import (
	"fmt"
	"hello"
	"log"
	"net"
	"unsafe"

	"github.com/golang/protobuf/proto"
)

func main() {
	log.Println("Hello go in bazel.")
	listen, error := net.Listen("tcp", ":8888")
	if error != nil {
		log.Println("Unable to listen: " + error.Error())
	}
	defer listen.Close()

	for {
		handleConn(listen)
	}
}

func handleConn(listen net.Listener) {
	conn, buff := listenAccept(listen)
	defer log.Println("connect closed.")
	defer conn.Close()
	sayHello := toSayHello(&buff)
	log.Printf("id '%s' say '%s'", sayHello.GetId(), sayHello.GetMessage())
	sayHelloResponse(conn, sayHello)
}

func sayHelloResponse(conn net.Conn, sayHello *hello.SayHello) {
	message := fmt.Sprintf("Hello '%v', i'm go say hello server! thx your visit.", sayHello.GetId())
	resp, err := proto.Marshal(&hello.SayHelloResponse{Code: 1, Message: message})
	if err != nil {
		log.Panicln(err.Error())
	}
	_, err = conn.Write([]byte(resp))
	if err != nil {
		log.Panicln(err.Error())
	}
}

func listenAccept(listen net.Listener) (conn net.Conn, buff []byte) {
	conn, err := listen.Accept()
	if err != nil {
		log.Println("Cannot accept a conn! error: " + err.Error())
	}

	buffer := make([]byte, 2048)
	size, err := conn.Read(buffer)
	if err != nil {
		log.Println("buff error" + err.Error())
	}
	buff = buffer[:size]
	log.Printf("receive buff size = %+v", unsafe.Sizeof(buff))
	return
}

func toSayHello(buff *[]byte) (result *hello.SayHello) {
	result = &hello.SayHello{}
	err := proto.Unmarshal(*buff, result)
	if err != nil {
		log.Printf("unmarshal SayHello fail! buff size = %+v", unsafe.Sizeof(buff))
		log.Panicln(err.Error())
	}
	return
}
