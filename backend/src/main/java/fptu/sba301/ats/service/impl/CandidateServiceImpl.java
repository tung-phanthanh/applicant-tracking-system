package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.CandidateDetailResponse;
import fptu.sba301.ats.dto.response.CandidateDocumentResponse;
import fptu.sba301.ats.dto.response.CandidateListResponse;
import fptu.sba301.ats.entity.Application;
import fptu.sba301.ats.entity.CandidateStageHistory;
import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.enums.ApplicationStatus;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.ApplicationRepository;
import fptu.sba301.ats.repository.CandidateRepository;
import fptu.sba301.ats.repository.CandidateStageHistoryRepository;
import fptu.sba301.ats.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final ApplicationRepository applicationRepository;
    private final CandidateStageHistoryRepository candidateStageHistoryRepository;

    @Override
    public List<CandidateListResponse> getCandidateList() {
        return candidateRepository.findAllCandidatesWithApplications()
                .stream()
                .map(row -> CandidateListResponse.builder()
                        .candidateId(UUID.fromString(row.getCandidateId()))
                        .fullName(row.getFullName())
                        .email(row.getEmail())
                        .jobTitle(row.getJobTitle())
                        .stage(row.getStage())
                        .rating(row.getRating())
                        .appliedAt(row.getAppliedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CandidateDetailResponse getCandidateDetail(UUID candidateId) {
        Application application = findLatestActiveApplication(candidateId);
        if (application.getStage() == ApplicationStage.APPLIED) {
            transitionStage(application, ApplicationStage.SCREENING);
        }

        var detail = candidateRepository.findCandidateDetailById(candidateId.toString())
                .orElseThrow(() -> new BusinessException("Candidate not found", HttpStatus.NOT_FOUND));

        List<CandidateDocumentResponse> documents = candidateRepository.findDocumentsByCandidateId(candidateId.toString())
                .stream()
                .map(document -> CandidateDocumentResponse.builder()
                        .documentId(UUID.fromString(document.getDocumentId()))
                        .fileName(document.getFileName())
                        .fileUrl(document.getFileUrl())
                        .fileType(document.getFileType())
                        .fileSizeBytes(document.getFileSizeBytes())
                        .uploadedAt(document.getUploadedAt())
                        .build())
                .collect(Collectors.toList());

        return CandidateDetailResponse.builder()
                .candidateId(UUID.fromString(detail.getCandidateId()))
                .fullName(detail.getFullName())
                .email(detail.getEmail())
                .phone(detail.getPhone())
                .currentCompany(detail.getCurrentCompany())
                .jobTitle(detail.getJobTitle())
                .stage(detail.getStage())
                .status(detail.getStatus())
                .rating(detail.getRating())
                .appliedAt(detail.getAppliedAt())
                .source(detail.getSource())
                .location(detail.getLocation())
                .experienceYears(detail.getExperienceYears())
                .summary(detail.getSummary())
                .documents(documents)
                .build();
    }

    @Override
    @Transactional
    public CandidateDetailResponse updateCandidateStage(UUID candidateId, ApplicationStage targetStage) {
        Application application = findLatestActiveApplication(candidateId);

        if (application.getStage() != ApplicationStage.SCREENING) {
            throw new BusinessException("Only candidates in SCREENING can be moved to INTERVIEW or REJECTED", HttpStatus.BAD_REQUEST);
        }

        if (targetStage != ApplicationStage.INTERVIEW && targetStage != ApplicationStage.REJECTED) {
            throw new BusinessException("Target stage must be INTERVIEW or REJECTED", HttpStatus.BAD_REQUEST);
        }

        transitionStage(application, targetStage);
        return getCandidateDetail(candidateId);
    }

    private Application findLatestActiveApplication(UUID candidateId) {
        return applicationRepository.findTopByCandidate_IdAndStatusOrderByAppliedAtDesc(candidateId, ApplicationStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException("Active application not found", HttpStatus.NOT_FOUND));
    }

    private void transitionStage(Application application, ApplicationStage toStage) {
        ApplicationStage fromStage = application.getStage();
        if (fromStage == toStage) {
            return;
        }

        application.setStage(toStage);
        if (toStage == ApplicationStage.REJECTED) {
            application.setStatus(ApplicationStatus.REJECTED);
        }
        applicationRepository.save(application);

        candidateStageHistoryRepository.save(CandidateStageHistory.builder()
                .application(application)
                .fromStage(fromStage)
                .toStage(toStage)
                .build());
    }
}
