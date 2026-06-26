package com.bankrolldiscipline.web.dui;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Local mock API for validated JSON-driven UI intents. */
@RestController
@RequestMapping(path = "/api/dui/intent", produces = MediaType.APPLICATION_JSON_VALUE)
public class DuiIntentController {

  private final DuiIntentService intentService;

  public DuiIntentController(DuiIntentService intentService) {
    this.intentService = intentService;
  }

  @GetMapping
  public DuiIntentResponse screen(@RequestParam(defaultValue = "landing") String screen) {
    return intentService.initialScreen(screen);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public DuiIntentResponse submit(@RequestBody DuiIntentRequest request) {
    return intentService.evaluate(request);
  }
}
