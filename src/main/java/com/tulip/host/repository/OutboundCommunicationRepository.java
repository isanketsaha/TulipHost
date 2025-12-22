package com.tulip.host.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tulip.host.domain.OutboundCommunication;

public interface OutboundCommunicationRepository extends JpaRepository<OutboundCommunication, Long> {
}
