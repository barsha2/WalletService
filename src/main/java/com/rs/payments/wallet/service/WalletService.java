package com.rs.payments.wallet.service;

import java.math.BigDecimal;
import java.util.UUID;
import com.rs.payments.wallet.model.Wallet;

public interface WalletService {
    Wallet createWalletForUser(UUID userId);
    void deposit(UUID userId, BigDecimal amount);
    void withdraw(UUID userId, BigDecimal amount);
    void transfer(UUID fromUserId, UUID toUserId, BigDecimal amount);
}