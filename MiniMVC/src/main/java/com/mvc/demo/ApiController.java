package com.mvc.demo;

import com.miniioccontainer.annotation.MyComponent;
import com.mvc.annotation.PathVariable;
import com.mvc.annotation.RequestBody;
import com.mvc.annotation.RequestMapping;
import com.mvc.annotation.RequestMethod;
import com.mvc.annotation.RequestParam;
import com.mvc.annotation.RestController;
import com.mvc.annotation.Valid;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JSON demo using {@link RestController}.
 */
@MyComponent
@RestController
@RequestMapping("/api")
public class ApiController {

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public Map<String, Object> ping() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", true);
        body.put("message", "pong");
        return body;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Map<String, Object> user(@RequestParam(value = "id", defaultValue = "1") int id) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", id);
        body.put("name", "user-" + id);
        return body;
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public Map<String, Object> userByPath(@PathVariable("id") int id) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", id);
        body.put("name", "user-" + id);
        body.put("source", "path");
        return body;
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public Map<String, Object> createUser(@Valid @RequestBody CreateUserRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("created", true);
        body.put("name", request.getName());
        body.put("age", request.getAge());
        return body;
    }
}
