package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.entity.Interview;
import fptu.sba301.ats.repository.InterviewRepository;
import fptu.sba301.ats.service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewServiceImpl implements InterviewService {
    @Autowired
    private InterviewRepository interviewRepository;
    
    @Override
    public List<Interview> getAllInterviews() {
        return interviewRepository.findAll();
    }
}
