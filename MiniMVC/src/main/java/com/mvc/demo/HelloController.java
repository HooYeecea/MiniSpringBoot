package com.mvc.demo;

import com.miniioccontainer.annotation.MyComponent;
import com.mvc.annotation.Controller;
import com.mvc.annotation.RequestMapping;
import com.mvc.annotation.RequestMethod;
import com.mvc.annotation.RequestParam;

/**
 * Sample controller for MiniMVC demos.
 */
@MyComponent
@Controller
@RequestMapping("/mvc")
public class HelloController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        return "Hello from MiniMVC!\n";
    }

    @RequestMapping(value = "/echo", method = RequestMethod.GET)
    public String echo(@RequestParam(value = "name", required = false, defaultValue = "world") String name) {
        return "echo: " + name + "\n";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(@RequestParam("a") int a, @RequestParam("b") int b) {
        return "sum=" + (a + b) + "\n";
    }
}
