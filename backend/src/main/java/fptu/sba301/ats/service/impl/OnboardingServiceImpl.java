package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.CreateOnboardingChecklistRequest;
import fptu.sba301.ats.dto.request.UpdateOnboardingItemRequest;
import fptu.sba301.ats.dto.response.OnboardingChecklistResponse;
import fptu.sba301.ats.dto.response.OnboardingItemResponse;
import fptu.sba301.ats.entity.*;
import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.enums.ChecklistItemStatus;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.mapper.OnboardingMapper;
import fptu.sba301.ats.repository.*;
import fptu.sba301.ats.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class OnboardingServiceImpl implements OnboardingService {

    private final OnboardingChecklistRepository checklistRepository;
    private final OnboardingItemRepository itemRepository;
    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final OnboardingMapper mapper;

    // ==============================
    // CREATE CHECKLIST
    // ==============================

    @Override
    @Transactional
    public OnboardingChecklistResponse createChecklist(java.util.UUID applicationId,
            CreateOnboardingChecklistRequest request) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(
                        "Application not found with id: " + applicationId, HttpStatus.NOT_FOUND));

        // Guard: candidate must be HIRED
        if (application.getStage() != ApplicationStage.HIRED) {
            throw new BusinessException(
                    "Onboarding checklist can only be created for HIRED candidates (current stage: "
                            + application.getStage() + ")", HttpStatus.CONFLICT);
        }

        // Guard: checklist already exists
        if (checklistRepository.existsByApplicationId(applicationId)) {
            throw new BusinessException(
                    "Onboarding checklist already exists for application " + applicationId, HttpStatus.CONFLICT);
        }

        OnboardingChecklist checklist = OnboardingChecklist.builder()
                .applicationId(applicationId)
                .build();
        checklist = checklistRepository.save(checklist);

        // Build items with auto-incremented order if not supplied
        final OnboardingChecklist savedChecklist = checklist;
        AtomicInteger order = new AtomicInteger(1);
        List<OnboardingItem> items = request.items().stream().map(req -> {
            OnboardingItem item = mapper.toEntity(req);
            item.setChecklist(savedChecklist);
            item.setStatus(ChecklistItemStatus.PENDING);
            if (item.getDisplayOrder() == null)
                item.setDisplayOrder(order.getAndIncrement());
            return item;
        }).collect(Collectors.toList());

        itemRepository.saveAll(items);
        checklist.setItems(items);

        log.info("Created onboarding checklist for applicationId={}", applicationId);
        return buildChecklistResponse(checklist, application);
    }

    // ==============================
    // GET CHECKLIST
    // ==============================

    @Override
    @Transactional(readOnly = true)
    public OnboardingChecklistResponse getChecklist(java.util.UUID applicationId) {
        OnboardingChecklist checklist = checklistRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new BusinessException(
                        "Onboarding checklist not found for application " + applicationId, HttpStatus.NOT_FOUND));
        Application application = applicationRepository.findById(applicationId).orElseThrow();
        return buildChecklistResponse(checklist, application);
    }

    // ==============================
    // UPDATE ITEM
    // ==============================

    @Override
    @Transactional
    public OnboardingItemResponse updateItem(java.util.UUID itemId, UpdateOnboardingItemRequest request) {
        OnboardingItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(
                        "Onboarding item not found with id: " + itemId, HttpStatus.NOT_FOUND));

        if (request.title() != null)
            item.setTitle(request.title());
        if (request.description() != null)
            item.setDescription(request.description());
        if (request.assignedTo() != null)
            item.setAssignedTo(request.assignedTo());
        if (request.dueDate() != null)
            item.setDueDate(request.dueDate());

        if (request.status() != null) {
            ChecklistItemStatus oldStatus = item.getStatus();
            item.setStatus(request.status());
            // Auto-manage completedAt
            if (request.status() == ChecklistItemStatus.DONE && oldStatus != ChecklistItemStatus.DONE) {
                item.setCompletedAt(Instant.now());
            } else if (request.status() != ChecklistItemStatus.DONE) {
                item.setCompletedAt(null);
            }
        }

        return mapper.toResponse(itemRepository.save(item));
    }

    // ==============================
    // Helpers
    // ==============================

    private OnboardingChecklistResponse buildChecklistResponse(OnboardingChecklist checklist,
            Application application) {
        String candidateName = candidateRepository.findById(application.getCandidate().getId())
                .map(Candidate::getFullName).orElse("Unknown");

        List<OnboardingItemResponse> itemResponses = checklist.getItems().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        int total = itemResponses.size();
        long completed = itemResponses.stream()
                .filter(i -> i.status() == ChecklistItemStatus.DONE).count();

        return new OnboardingChecklistResponse(
                checklist.getId(), checklist.getApplicationId(), candidateName,
                checklist.getCreatedAt(), total, (int) completed, itemResponses);
    }
}
