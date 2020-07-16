/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkmicroservices.ri.spring.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
 

@Component
public class GatewayAuthenticationFilter extends AbstractGatewayFilterFactory<GatewayAuthenticationFilter.Config> {

    private Logger logger = LoggerFactory.getLogger(GatewayAuthenticationFilter.class);
    private static final String REQUIRED_SCOPES = "scopes";
    
    
   
    
    @Value("${jwt.secret}")
	private String jwtSecret;

//    @Autowired
//	private JwtTokenUtil jwtTokenUtil;
    
    public GatewayAuthenticationFilter() {
        super(Config.class);
        logger.info("filter created");
    }

    private boolean isAuthorizationValid(String authorizationHeader) {
        boolean isValid = true;

        // Logic for checking the value
        return isValid;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            logger.info("api gateway filter->");
            logger.info("required roles: " + config.getScopes());
            
            ServerHttpRequest request = exchange.getRequest();
            
             // if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                 String authorizationHeader = request.getHeaders().get("Authorization").get(0);
                 //String authorizationHeader= request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                 logger.info("authorization header:"+authorizationHeader);
                 if( !authorizationHeader.isEmpty() && authorizationHeader.startsWith("Bearer ")){
                     try {
                         String jwt=authorizationHeader.substring(7);
                         logger.info("jwt=>"+jwt);
                         logger.info("jwtSecret:"+jwtSecret);
                         //https://github.com/jwtk/jjwt#jws-read
                         Jws<Claims> jws = Jwts.parser()
                                 //.setSigningKey(jwtSecret.getBytes("UTF-8"))
                                 .setSigningKey(jwtSecret)
                                 .parseClaimsJws(jwt);
                         
                         String subject=jws.getBody().getSubject();
                         
                         logger.info("jwt.subject=>"+subject);
                         // issued at
                         
                         Date issuedAt=jws.getBody().getIssuedAt();
                         logger.info("jwt issued at:"+issuedAt);
                         
                         /// check token 
                         Date tokenExpiration=jws.getBody().getExpiration();
                         logger.info("jwt expiration:"+tokenExpiration);
                         logger.info("check scopes:");
                         // get scopes
                         List<String> scopes = (List<String>) jws.getBody().get("scopes");
                        
                             
                         if(!canAllowAccess(config,scopes)){
                           logger.info("insufficient token scope");
                           return this.onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
  
                         }
                         
                         
                     } catch (Exception ex) {
                         logger.error("jwt parse error", ex);
                     }
                     
                     
                     
                     
                     
                 }else{
                     logger.info("No Authorization header");
                     return this.onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);

                 }
                 
              //}
  
              /**
              
  
              if (!this.isAuthorizationValid(authorizationHeader)) {
                  return this.onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
              }
  
              ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().
                      header("secret", RandomStringUtils.random(10)).
                      build();
 
              return chain.filter(exchange.mutate().request(modifiedRequest).build());
             */
            return chain.filter(exchange.mutate().request(request).build());
        };
    }
    
    
     
    
    private boolean canAllowAccess(Config config, List<String> scopes){
        // grab the required scopes configuration arg, split on the ',' and trim each ellement
        String[] requiredScopes=Arrays.stream(config.getScopes().split(","))
        .map(String::trim)
        .toArray(String[]::new);
        logger.info("required scopes:"+String.join(",",requiredScopes));
        logger.info("token scopes:"+String.join(",",scopes));
        // check that all token scopes are included 
        for(String scope: requiredScopes){
            
             
            if(!scopes.contains(scope)){
                return false;
            }else{
                
            }
        }
        return true;
    }
/*
    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList(REQUIRED_ROLES);
    }
*/
    public static class Config {

        private String scopes;
       

         
       public void setScopes(String scopes){
           this.scopes=scopes;
       }

        /**
         * @return the scopes
         */
        public String getScopes() {
            return scopes;
        }

       

    }

}
