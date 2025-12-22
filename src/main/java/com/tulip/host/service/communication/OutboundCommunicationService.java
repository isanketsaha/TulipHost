package com.tulip.host.service.communication;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tulip.host.domain.OutboundCommunication;
import com.tulip.host.enums.CommunicationChannel;
import com.tulip.host.enums.OutboundCommunicationStatus;
import com.tulip.host.repository.OutboundCommunicationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OutboundCommunicationService {

    private final OutboundCommunicationRepository outboundCommunicationRepository;
    private final EnumMap<CommunicationChannel, CommunicationStrategy> strategies = new EnumMap<>(
            CommunicationChannel.class);

    public OutboundCommunicationService(
            OutboundCommunicationRepository outboundCommunicationRepository,
            List<CommunicationStrategy> communicationStrategies) {
        this.outboundCommunicationRepository = outboundCommunicationRepository;
        for (CommunicationStrategy s : communicationStrategies) {
            strategies.put(s.channel(), s);
        }
    }

    /**
     * Sends a communication and persists an audit record.
     * Happy path: we try to send once and mark SENT/FAILED.
     */
    @Transactional
    public OutboundCommunication send(CommunicationRequest request) {
        OutboundCommunication comm = new OutboundCommunication();
        comm.setChannel(request.channel());
        comm.setRecipient(String.join( "," ,request.recipient())+" | "+String.join(",", request.cc()));
        comm.setEntityType(request.entityType());
        comm.setEntityId(request.entityId());
        comm.setSubject(request.subject());
        comm.setContent(request.content());
        comm.setStatus(OutboundCommunicationStatus.PENDING);
        comm = outboundCommunicationRepository.save(comm);

        try {
            CommunicationStrategy strategy = strategies.get(request.channel());
            if (strategy == null) {
                throw new IllegalStateException("No strategy registered for channel: " + request.channel());
            }
            strategy.send(request);
            comm.setStatus(OutboundCommunicationStatus.SENT);
            comm.setSentDate(LocalDateTime.now());
            return outboundCommunicationRepository.save(comm);
        } catch (Exception e) {
            log.warn("Outbound communication failed: channel={}, recipient={}, entityType={}, entityId={}, err={}",
                    request.channel(), request.recipient(), request.entityType(), request.entityId(), e.getMessage(),
                    e);
            comm.setStatus(OutboundCommunicationStatus.FAILED);
            comm.setError(e.getMessage());
            return outboundCommunicationRepository.save(comm);
        }
    }
}
