package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.CreateOnboardingChecklistRequest;
import fptu.sba301.ats.dto.request.UpdateOnboardingItemRequest;
import fptu.sba301.ats.entity.Application;
import fptu.sba301.ats.entity.OnboardingChecklist;
import fptu.sba301.ats.entity.OnboardingItem;
import fptu.sba301.ats.enums.ApplicationStage;
import fptu.sba301.ats.enums.ChecklistItemStatus;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.mapper.OnboardingMapper;
import fptu.sba301.ats.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
class OnboardingServiceImplTest {
 
    @Mock
    private OnboardingChecklistRepository checklistRepository;
    @Mock
    private OnboardingItemRepository itemRepository;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private OnboardingMapper mapper;
 
    @InjectMocks
    private OnboardingServiceImpl service;
 
    // ==========================================
    // createChecklist — Exception paths
    // ==========================================
 
    @Test
    void createChecklist_ApplicationNotFound_ThrowsNotFound() {
        java.util.UUID appId = java.util.UUID.randomUUID();
        when(applicationRepository.findById(appId)).thenReturn(Optional.empty());
 
        CreateOnboardingChecklistRequest req = new CreateOnboardingChecklistRequest(List.of());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createChecklist(appId, req));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("Application not found"));
    }
 
    @Test
    void createChecklist_NotHiredStage_ThrowsConflict() {
        java.util.UUID appId = java.util.UUID.randomUUID();
        Application app = new Application();
        app.setId(appId);
        app.setStage(ApplicationStage.OFFER); // Not HIRED
 
        when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));
 
        CreateOnboardingChecklistRequest req = new CreateOnboardingChecklistRequest(List.of());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createChecklist(appId, req));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("HIRED"));
    }
 
    @Test
    void createChecklist_AlreadyExists_ThrowsConflict() {
        java.util.UUID appId = java.util.UUID.randomUUID();
        Application app = new Application();
        app.setId(appId);
        app.setStage(ApplicationStage.HIRED);
 
        when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));
        when(checklistRepository.existsByApplicationId(appId)).thenReturn(true);
 
        CreateOnboardingChecklistRequest req = new CreateOnboardingChecklistRequest(List.of());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createChecklist(appId, req));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("already exists"));
    }
 
    // ==========================================
    // getChecklist — Exception paths
    // ==========================================
 
    @Test
    void getChecklist_NotFound_ThrowsNotFound() {
        java.util.UUID appId = java.util.UUID.randomUUID();
        when(checklistRepository.findByApplicationId(appId)).thenReturn(Optional.empty());
 
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getChecklist(appId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("not found"));
    }
 
    // ==========================================
    // updateItem — Exception & Happy paths
    // ==========================================
 
    @Test
    void updateItem_NotFound_ThrowsNotFound() {
        java.util.UUID itemId = java.util.UUID.randomUUID();
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
 
        UpdateOnboardingItemRequest req = new UpdateOnboardingItemRequest(null, null, null, null, null);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateItem(itemId, req));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("not found"));
    }
 
    @Test
    void updateItem_StatusDone_SetsCompletedAt() {
        java.util.UUID itemId = java.util.UUID.randomUUID();
        OnboardingItem item = new OnboardingItem();
        item.setId(itemId);
        item.setStatus(ChecklistItemStatus.PENDING);
 
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(OnboardingItem.class))).thenReturn(null);
 
        UpdateOnboardingItemRequest req = new UpdateOnboardingItemRequest(
                null, null, ChecklistItemStatus.DONE, null, null);
        service.updateItem(itemId, req);
 
        assertNotNull(item.getCompletedAt(), "completedAt should be set when status changes to DONE");
    }
 
    @Test
    void updateItem_StatusRevertedFromDone_ClearsCompletedAt() {
        java.util.UUID itemId = java.util.UUID.randomUUID();
        OnboardingItem item = new OnboardingItem();
        item.setId(itemId);
        item.setStatus(ChecklistItemStatus.DONE);
        item.setCompletedAt(java.time.Instant.now());
 
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(OnboardingItem.class))).thenReturn(null);
 
        UpdateOnboardingItemRequest req = new UpdateOnboardingItemRequest(
                null, null, ChecklistItemStatus.IN_PROGRESS, null, null);
        service.updateItem(itemId, req);
 
        assertNull(item.getCompletedAt(), "completedAt should be cleared when status reverts from DONE");
    }
}
