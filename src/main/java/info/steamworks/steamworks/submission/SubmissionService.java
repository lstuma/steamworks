package info.steamworks.steamworks.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubmissionService {
    public final SubmissionRepository submissionRepository;

    @Autowired
    public SubmissionService(SubmissionRepository submissionRepository)
    {
        this.submissionRepository = submissionRepository;
    }

    public List<Submission> getSubmissions()
    {
        return submissionRepository.findAll();
    }

    public void saveSubmission(Submission submission)
    {
        submissionRepository.save(submission);
    }

    public boolean addSubmission(Submission submission)
    {
        // Try saving submission to database otherwise return false
        try {
            submissionRepository.save(submission);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public Optional<Submission> getSubmission(int id)
    {
        // Return all submissions on given page
        return submissionRepository.findSubmissionById(id);
    }
}
