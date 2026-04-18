package com.rs.payments.wallet.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.rs.payments.wallet.model.Wallet;

public interface WalletService {
    Wallet createWalletForUser(UUID userId);
    public Wallet deposit(UUID userId, BigDecimal amount);
    public Wallet withdraw(UUID userId, BigDecimal amount);
    public Map<String, Wallet> transfer(UUID fromUserId, UUID toUserId, BigDecimal amount);
    List<Wallet> getAllWallets();
    Wallet getWalletByUserId(UUID userId);
}