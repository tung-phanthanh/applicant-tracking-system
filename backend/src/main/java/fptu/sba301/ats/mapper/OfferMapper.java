package fptu.sba301.ats.mapper;

import fptu.sba301.ats.dto.response.OfferApprovalResponse;
import fptu.sba301.ats.dto.response.OfferResponse;
import fptu.sba301.ats.entity.Offer;
import fptu.sba301.ats.entity.OfferApproval;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    @Mapping(target = "candidateName", ignore = true)
    @Mapping(target = "jobTitle", ignore = true)
    OfferResponse toResponse(Offer offer);

    @Mapping(target = "approverName", ignore = true)
    @Mapping(target = "approvedBy", source = "approvedBy.id")
    OfferApprovalResponse toResponse(OfferApproval approval);
}
