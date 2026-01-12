package com.tulip.host.service.communication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tulip.host.domain.OutboundCommunication;
import com.tulip.host.enums.CommunicationChannel;
import com.tulip.host.enums.OutboundCommunicationStatus;
import com.tulip.host.mapper.OutboundCommunicationMapper;
import com.tulip.host.repository.OutboundCommunicationRepository;

import lombok.extern.slf4j.Slf4j;
import static com.tulip.host.utils.CommonUtils.isProdProfile;

@Service
@Slf4j
public class OutboundCommunicationService {

    private final OutboundCommunicationRepository outboundCommunicationRepository;
    private final Environment env;
    private final OutboundCommunicationMapper outboundCommunicationMapper;
    private final EnumMap<CommunicationChannel, CommunicationStrategy> strategies = new EnumMap<>(
        CommunicationChannel.class);

    public OutboundCommunicationService(
        OutboundCommunicationRepository outboundCommunicationRepository,
        OutboundCommunicationMapper outboundCommunicationMapper, Environment env,
        List<CommunicationStrategy> communicationStrategies) {
        this.outboundCommunicationRepository = outboundCommunicationRepository;
        this.outboundCommunicationMapper = outboundCommunicationMapper;
        this.env = env;
        for (CommunicationStrategy s : communicationStrategies) {
            strategies.put(s.channel(), s);
        }
    }

    /**
     * Sends a communication and persists an audit record.
     * Happy path: we try to send once and mark SENT/FAILED.
     */
    @Transactional
    @Async
    public void send(CommunicationRequest request) {
        log.info("Preparing to send outbound communication: channel={}, recipient={}, entityType={}, entityId={}",
            request.getChannel(), Arrays.toString(request.getRecipient()), request.getEntityType(),
            request.getEntityId());
        log.info("IS DEV PROFILE: {}", isProdProfile(env.getActiveProfiles()));
        if (isProdProfile(env.getActiveProfiles())) {
            OutboundCommunication comm = outboundCommunicationMapper.toEntity(request);
            comm = outboundCommunicationRepository.save(comm);
            try {
                CommunicationStrategy strategy = strategies.get(request.getChannel());
                if (strategy == null) {
                    throw new IllegalStateException("No strategy registered for channel: " + request.getChannel());
                }
                comm.setSentDate(LocalDateTime.now());

                log.info("Sending {} to {}", request.getChannel()
                    .name(), Arrays.toString(request.getRecipient()));
                strategy.send(request, comm);

                comm.setStatus(OutboundCommunicationStatus.SENT);
                outboundCommunicationRepository.save(comm);
            } catch (Exception e) {
                log.error("Outbound communication failed: channel={}, recipient={}, entityType={}, entityId={}, err={}",
                    request.getChannel(), request.getRecipient(), request.getEntityType(), request.getEntityId(),
                    e.getMessage(),
                    e);
                comm.setStatus(OutboundCommunicationStatus.FAILED);
                comm.setError(e.getMessage());
                outboundCommunicationRepository.save(comm);
            }
        }
    }
}
