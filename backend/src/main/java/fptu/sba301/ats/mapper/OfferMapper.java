package fptu.sba301.ats.mapper;

import fptu.sba301.ats.dto.request.CreateOfferRequest;
import fptu.sba301.ats.dto.response.OfferApprovalResponse;
import fptu.sba301.ats.dto.response.OfferResponse;
import fptu.sba301.ats.entity.Offer;
import fptu.sba301.ats.entity.OfferApproval;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OfferMapper {

    // Offer → OfferResponse
    @Mapping(target = "applicationId", source = "application.id")
    @Mapping(target = "createdByName", expression = "java(offer.getCreatedBy() != null ? offer.getCreatedBy().toString() : null)")
    @Mapping(target = "candidateName", source = "application.candidate.fullName")
    @Mapping(target = "approvals",     source = "approvals")
    OfferResponse toResponse(Offer offer);

    List<OfferResponse> toResponseList(List<Offer> offers);

    // OfferApproval → OfferApprovalResponse
    @Mapping(target = "offerId",        source = "offer.id")
    @Mapping(target = "approvedByName", source = "approvedBy.fullName")
    OfferApprovalResponse toApprovalResponse(OfferApproval approval);

    // CreateOfferRequest → Offer (manual mapping due to BaseEntity fields)
    default Offer toEntity(CreateOfferRequest request) {
        Offer offer = Offer.builder()
                .salary(request.getSalary())
                .equity(request.getEquity())
                .signOnBonus(request.getSignOnBonus())
                .positionTitle(request.getPositionTitle())
                .startDate(request.getStartDate())
                .expiryDate(request.getExpiryDate())
                .build();
        return offer;
    }

    // Update existing Offer from request
    default void updateFromRequest(CreateOfferRequest request, Offer offer) {
        if (request.getSalary() != null) {
            offer.setSalary(request.getSalary());
        }
        if (request.getEquity() != null) {
            offer.setEquity(request.getEquity());
        }
        if (request.getSignOnBonus() != null) {
            offer.setSignOnBonus(request.getSignOnBonus());
        }
        if (request.getPositionTitle() != null) {
            offer.setPositionTitle(request.getPositionTitle());
        }
        if (request.getStartDate() != null) {
            offer.setStartDate(request.getStartDate());
        }
        if (request.getExpiryDate() != null) {
            offer.setExpiryDate(request.getExpiryDate());
        }
    }
}
