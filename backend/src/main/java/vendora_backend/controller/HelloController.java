package vendora_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vendora_backend.dto.HelloResponse;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public HelloResponse hello() {
        return new HelloResponse(
                "Welcome to Vendora API",
                "running"
        );
    }
}