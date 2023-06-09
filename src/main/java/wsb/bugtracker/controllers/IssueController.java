package wsb.bugtracker.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import wsb.bugtracker.filters.IssueFilter;
import wsb.bugtracker.mail.MailService;
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
    private final MailService mailService;

    @GetMapping
    @Secured("ROLE_VIEW_ISSUE")
    ModelAndView index(@ModelAttribute IssueFilter filter, Pageable pageable) {
        ModelAndView modelAndView = new ModelAndView("issues/index");

        Page<Issue> issues = issueService.findAll(filter.buildSpecification(), pageable);
        modelAndView.addObject("issues", issues);

        List<Person> people = personService.findAll();
        modelAndView.addObject("people", people);

        List<Project> projects = projectService.findAll();
        modelAndView.addObject("projects", projects);

        modelAndView.addObject("filter", filter);

        return modelAndView;
    }

    @GetMapping("/create")
    @Secured("ROLE_CREATE_ISSUE")
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
    @Secured("ROLE_CREATE_ISSUE")
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
        mailService.sendNewIssueMail(issue);

        modelAndView.setViewName("redirect:/issues");
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    @Secured("ROLE_DELETE_ISSUE")
    ModelAndView delete(@PathVariable Long id) {
        Issue deletedIssue = issueService.findById(id);
        mailService.sendDeleteIssueMail(deletedIssue);

        System.out.println("deleting request" + id);
        issueService.delete(id);

        return new ModelAndView("redirect:/issues");
    }

    @GetMapping("/edit/{id}")
    @Secured("ROLE_EDIT_ISSUE")
    ModelAndView edit(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("/issues/edit");
        Issue issue = issueService.findById(id);
        modelAndView.addObject("issue", issue);

        List<Person> people = personService.findAll();
        modelAndView.addObject("people", people);

        List<Project> projects = projectService.findAll();
        modelAndView.addObject("project", projects);
        return modelAndView;
    }

    @PostMapping("/update/{id}")
    @Secured("ROLE_CREATE_ISSUE")
    ModelAndView update(@ModelAttribute @Valid Issue issue, BindingResult result, @PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView();

        if (result.hasErrors()) {
            modelAndView.setViewName("issues/edit");
            modelAndView.addObject("issue", issue);
            return modelAndView;
        }

        Issue editIssue = issueService.findById(id);
        if (editIssue == null) {
            return new ModelAndView("redirect:/issues");
        }

        editIssue.setProject(issue.getProject());
        editIssue.setName(issue.getName());
        editIssue.setDescription(issue.getDescription());
        editIssue.setCreator(issue.getCreator());
        editIssue.setAssignee((issue.getAssignee()));
        editIssue.setStatus(issue.getStatus());
        editIssue.setPriority(issue.getPriority());
        editIssue.setType(issue.getType());


        issueService.save(editIssue);
        mailService.sendEditIssueMail(editIssue);

        modelAndView.setViewName("redirect:/issues");
        return modelAndView;
    }
}