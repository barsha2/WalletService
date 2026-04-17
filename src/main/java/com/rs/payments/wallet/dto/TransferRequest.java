package com.rs.payments.wallet.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    private UUID fromUserId;
    private UUID toUserId;
    private BigDecimal amount;
}
