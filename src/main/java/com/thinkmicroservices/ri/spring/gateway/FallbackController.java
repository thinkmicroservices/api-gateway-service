/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkmicroservices.ri.spring.gateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author cwoodward
 */

@RestController
public class FallbackController {
    
    @GetMapping("/fallback/authentication")
    public ResponseEntity<?> authenticationFallbackGET(){
        return ResponseEntity.ok("api gateway service unavailable");
    }
    @PostMapping("/fallback/authentication")
    public ResponseEntity<?> authenticationFallbackPOST(){
        return ResponseEntity.ok("api gateway service unavailable");
    }
}
