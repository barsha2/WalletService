package com.rs.payments.wallet.service.impl;

import com.rs.payments.wallet.exception.ResourceNotFoundException;
import com.rs.payments.wallet.model.User;
import com.rs.payments.wallet.model.Wallet;
import com.rs.payments.wallet.repository.UserRepository;
import com.rs.payments.wallet.repository.WalletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WalletServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    @DisplayName("Should create wallet for existing user")
    void shouldCreateWalletForExistingUser() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // The service saves the user, which cascades to wallet. 
        // We mock save to return the user.
        when(userRepository.save(user)).thenReturn(user);

        // When
        Wallet result = walletService.createWalletForUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getBalance());
        assertEquals(walletService.createWalletForUser(userId).getBalance(), BigDecimal.ZERO);
        
        // Verify interactions
        verify(userRepository, times(2)).findById(userId); // Called twice due to second assert
        verify(userRepository, times(2)).save(user);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> walletService.createWalletForUser(userId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldDepositMoneySuccessfully() {
        // given
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        User user = new User();
        user.setId(userId);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

        // when
        walletService.deposit(userId, amount);

        // then
        assertEquals(new BigDecimal("100.00"), wallet.getBalance());
        verify(walletRepository).save(wallet);
    }

    @Test
    void shouldWithdrawMoneySuccessfully() {
        // given
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");

        User user = new User();
        user.setId(userId);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal("100.00"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

        // when
        walletService.withdraw(userId, amount);

        // then
        assertEquals(new BigDecimal("50.00"), wallet.getBalance());
        verify(walletRepository).save(wallet);
    }


    @Test
    void shouldThrowExceptionWhenInsufficientBalance() {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("150.00");

        User user = new User();
        user.setId(userId);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal("100.00"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

        assertThrows(RuntimeException.class, () -> {
            walletService.withdraw(userId, amount);
        });
    }

    @Test
    void shouldTransferMoneySuccessfully() {
        UUID fromUserId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");

        User fromUser = new User();
        fromUser.setId(fromUserId);

        User toUser = new User();
        toUser.setId(toUserId);

        Wallet fromWallet = new Wallet();
        fromWallet.setUser(fromUser);
        fromWallet.setBalance(new BigDecimal("100.00"));

        Wallet toWallet = new Wallet();
        toWallet.setUser(toUser);
        toWallet.setBalance(new BigDecimal("20.00"));

        when(userRepository.findById(fromUserId)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(toUserId)).thenReturn(Optional.of(toUser));
        when(walletRepository.findByUser(fromUser)).thenReturn(Optional.of(fromWallet));
        when(walletRepository.findByUser(toUser)).thenReturn(Optional.of(toWallet));

        walletService.transfer(fromUserId, toUserId, amount);

        assertEquals(new BigDecimal("50.00"), fromWallet.getBalance());
        assertEquals(new BigDecimal("70.00"), toWallet.getBalance());
    }

    @Test
void shouldFailWhenInsufficientBalance() {
    UUID fromUserId = UUID.randomUUID();
    UUID toUserId = UUID.randomUUID();
    BigDecimal amount = new BigDecimal("150.00");

    User fromUser = new User();
    fromUser.setId(fromUserId);

    Wallet fromWallet = new Wallet();
    fromWallet.setUser(fromUser);
    fromWallet.setBalance(new BigDecimal("100.00"));

    when(userRepository.findById(fromUserId)).thenReturn(Optional.of(fromUser));
    when(walletRepository.findByUser(fromUser)).thenReturn(Optional.of(fromWallet));

    assertThrows(RuntimeException.class, () -> {
        walletService.transfer(fromUserId, toUserId, amount);
    });
}
}
