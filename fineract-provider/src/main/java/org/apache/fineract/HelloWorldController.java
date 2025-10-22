package org.apache.fineract;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spring-api/v2")
public class HelloWorldController {

  @GetMapping(path = "helloworld")
  public ResponseEntity<String> helloworld() {
    return ResponseEntity
              .status(HttpStatus.OK)
              .body("Helloworld Response Entity");
  }
}
