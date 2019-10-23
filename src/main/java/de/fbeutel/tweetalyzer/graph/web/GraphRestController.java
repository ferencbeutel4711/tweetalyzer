package de.fbeutel.tweetalyzer.graph.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.fbeutel.tweetalyzer.graph.domain.PublicNetwork;
import de.fbeutel.tweetalyzer.graph.service.GraphService;

@RestController
@RequestMapping("/graph/user")
public class GraphRestController {

  private final GraphService graphService;

  public GraphRestController(GraphService userService) {
    this.graphService = userService;
  }

  @GetMapping
  public ResponseEntity<PublicNetwork> findForGraph(@RequestParam(value = "limit", defaultValue = "100") final int limit,
                                                    @RequestParam(value = "hashtag", required = false) final String hashtag,
                                                    @RequestParam(value = "username", required = false) final String username) {
    return ResponseEntity.ok(graphService.calculateNetwork(limit, hashtag, username));
  }
}
