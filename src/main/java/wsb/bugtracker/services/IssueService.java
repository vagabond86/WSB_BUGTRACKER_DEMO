package wsb.bugtracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import wsb.bugtracker.models.Issue;
import wsb.bugtracker.models.Person;
import wsb.bugtracker.repositories.IssueRepository;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;

    public Page<Issue> findAll(Specification<Issue> specification, Pageable pageable) {
        return issueRepository.findAll(specification, pageable);
    }

    public void save(Issue issue) {
        if (issue.getDateCreated() == null) {
            issue.setDateCreated(new Date());
        }
        issue.setLastUpdated(new Date());
        issueRepository.save(issue);
    }

    public void delete(Long id) {
        issueRepository.deleteById(id);
    }

    public Issue findById(Long id) {
        return issueRepository.findById(id).orElse(null);
    }

    public List<Issue> findAll() {
        return issueRepository.findAll();
    }

    public List<Issue> findByAssignee(Person assignee) {
        return issueRepository.findByAssignee(assignee);
    }
}