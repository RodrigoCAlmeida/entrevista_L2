package br.com.teste.controller;

import br.com.teste.dto.PackingRequestDTO;
import br.com.teste.service.PackingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin
@RestController
public class PublicController {

    private final PackingService packingService;
    public PublicController(PackingService packingService) {
        this.packingService = packingService;
    }

    @GetMapping("/public/greetings")
    public ResponseEntity<String> getPublicGreetings() {
        System.out.println(LocalDateTime.now()+ " - /public/greetings");
        return ResponseEntity.ok("Public endpoint!");
    }

    @SecurityRequirement(name = "ApiKeyAuth")
    @PostMapping("/protected/packOrders")
    public ResponseEntity<Map<String, Object>> packOrders(@RequestBody PackingRequestDTO request) {
        Map<String, Object> result = packingService.processAndPackOrders(request);
        System.out.println(LocalDateTime.now()+ " - /protected/packOrders");
        return ResponseEntity.ok(result);
    }

}
