package com.tulip.host.mapper;

import com.tulip.host.domain.OutboundCommunication;
import com.tulip.host.enums.CommunicationChannel;
import com.tulip.host.service.communication.CommunicationRequest;
import com.tulip.host.utils.CommonUtils;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OutboundCommunicationMapper {
    @Mapping(target = "recipient", ignore = true)
    OutboundCommunication toEntity(CommunicationRequest request);

    @AfterMapping
    default void map(CommunicationRequest request, @MappingTarget OutboundCommunication modal) {
        String to = CommonUtils.formatRecipient(request);
        String cc = joinIfPresent(request.getCc());

        if (StringUtils.isNotBlank(cc)) {
            to = StringUtils.isNotBlank(to) ? to + " | " + cc : cc;
        }

        modal.setRecipient(to);
    }

    private String joinIfPresent(String[] values) {
        return values == null || values.length == 0
            ? null
            : Arrays.stream(values).filter(StringUtils::isNotBlank).collect(Collectors.joining(", "));
    }
}
