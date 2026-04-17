package com.rs.payments.wallet.service.impl;

import com.rs.payments.wallet.exception.ResourceNotFoundException;
import com.rs.payments.wallet.model.User;
import com.rs.payments.wallet.model.Wallet;
import com.rs.payments.wallet.repository.UserRepository;
import com.rs.payments.wallet.repository.WalletRepository;
import com.rs.payments.wallet.service.WalletService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public WalletServiceImpl(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public Wallet createWalletForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUser(user);
        user.setWallet(wallet);

        user = userRepository.save(user); // Cascade saves wallet
        return user.getWallet();
    }

    @Override
    @Transactional
    public void deposit(UUID userId, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(amount));

        walletRepository.save(wallet);
    }


    @Override
    @Transactional
    public void withdraw(UUID userId, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));

        walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void transfer(UUID fromUserId, UUID toUserId, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Cannot transfer to same user");
        }

        User fromUser = userRepository.findById(fromUserId)
            .orElseThrow(() -> new RuntimeException("Sender not found"));

        User toUser = userRepository.findById(toUserId)
            .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Wallet fromWallet = walletRepository.findByUser(fromUser)
            .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet toWallet = walletRepository.findByUser(toUser)
            .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
        toWallet.setBalance(toWallet.getBalance().add(amount));

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);
    }
}