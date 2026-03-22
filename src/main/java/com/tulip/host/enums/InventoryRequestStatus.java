package com.tulip.host.enums;

public enum InventoryRequestStatus {
    PENDING, // waiting for principal
    PRINCIPAL_APPROVED, // principal approved, waiting for admin
    APPROVED, // admin approved, ready to fulfill
    REJECTED,
    FULFILLED,
}
