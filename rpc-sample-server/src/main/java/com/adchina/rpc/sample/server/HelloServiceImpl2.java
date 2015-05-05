package com.adchina.rpc.sample.server;

import com.adchina.rpc.sample.api.HelloService;
import com.adchina.rpc.sample.api.Person;
import com.adchina.rpc.server.RpcService;

@RpcService(name = "sample.hello")
public class HelloServiceImpl2 implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
