package fptu.sba301.ats.service;

import fptu.sba301.ats.entity.Application;
import fptu.sba301.ats.entity.CandidateStageHistory;
import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.repository.ApplicationRepository;
import fptu.sba301.ats.repository.CandidateStageHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationStageTransitionService {

    private final ApplicationRepository applicationRepository;
    private final CandidateStageHistoryRepository candidateStageHistoryRepository;

    @Transactional
    public void transition(Application application, ApplicationStage toStage) {
        ApplicationStage fromStage = application.getStage();
        if (fromStage == toStage) {
            return;
        }
        application.setStage(toStage);
        applicationRepository.save(application);
        candidateStageHistoryRepository.save(CandidateStageHistory.builder()
                .application(application)
                .fromStage(fromStage)
                .toStage(toStage)
                .build());
    }
}
