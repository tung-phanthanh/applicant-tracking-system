package fptu.sba301.ats.mapper;

import fptu.sba301.ats.dto.request.CreateOfferRequest;
import fptu.sba301.ats.dto.response.OfferApprovalResponse;
import fptu.sba301.ats.dto.response.OfferResponse;
import fptu.sba301.ats.entity.Offer;
import fptu.sba301.ats.entity.OfferApproval;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface OfferMapper {

    OfferResponse toResponse(Offer offer);

    OfferApprovalResponse toApprovalResponse(OfferApproval approval);

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
