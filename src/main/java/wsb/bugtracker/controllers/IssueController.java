package wsb.bugtracker.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import wsb.bugtracker.models.Issue;
import wsb.bugtracker.models.Person;
import wsb.bugtracker.models.Project;
import wsb.bugtracker.services.IssueService;
import wsb.bugtracker.services.PersonService;
import wsb.bugtracker.services.ProjectService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/issues")
public class IssueController {

    private final IssueService issueService;
    private final ProjectService projectService;
    private final PersonService personService;

    @GetMapping
    ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("issues/index");

        List<Issue> issues = issueService.findAll();
        modelAndView.addObject("issues", issues);

        return modelAndView;
    }

    @GetMapping("/create")
    ModelAndView create() {
        ModelAndView modelAndView = new ModelAndView("/issues/create");

        Issue newIssue = new Issue();
        modelAndView.addObject("issue", newIssue);

        List<Project> projects = projectService.findAll();
        modelAndView.addObject("project", projects);

        List<Person> people = personService.findAll();
        modelAndView.addObject("people", people);

        return modelAndView;
    }

    @PostMapping("/save")
    ModelAndView save(@ModelAttribute @Valid Issue issue, BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();

        if (result.hasErrors()) {
            modelAndView.setViewName("issues/create");
            modelAndView.addObject("issue", issue);
            modelAndView.addObject("projects", projectService.findAll());
            modelAndView.addObject("people", personService.findAll());
            return modelAndView;
        }

        issueService.save(issue);
        modelAndView.setViewName("redirect:/issues");
        return modelAndView;
    }
}
