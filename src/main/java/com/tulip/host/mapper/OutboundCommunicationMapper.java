package com.tulip.host.mapper;

import com.tulip.host.domain.OutboundCommunication;
import com.tulip.host.enums.OutboundCommunicationStatus;
import com.tulip.host.service.communication.CommunicationRequest;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Arrays;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OutboundCommunicationMapper {


    @Mapping(target = "recipient", ignore = true)
    OutboundCommunication toEntity(CommunicationRequest request);


    @AfterMapping
    default void map(CommunicationRequest request, @MappingTarget OutboundCommunication modal) {

        String to = joinIfPresent(request.getRecipient());
        String cc = joinIfPresent(request.getCc());

        if (StringUtils.isNotBlank(cc)) {
            to = StringUtils.isNotBlank(to) ? to + " | " + cc : cc;
        }

        modal.setRecipient(to);
    }

    private String joinIfPresent(String[] values) {
        return values == null || values.length == 0
            ? null
            : Arrays.stream(values)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(", "));
    }


}
