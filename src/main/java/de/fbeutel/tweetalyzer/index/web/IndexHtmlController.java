package de.fbeutel.tweetalyzer.index.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/")
public class IndexHtmlController {

  @GetMapping
  public ModelAndView getIndexPage() {
    return new ModelAndView("index", new ConcurrentHashMap<>());
  }
}
