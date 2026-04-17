package com.rs.payments.wallet.controller;

import com.rs.payments.wallet.dto.CreateWalletRequest;
import com.rs.payments.wallet.dto.DepositRequest;
import com.rs.payments.wallet.dto.TransferRequest;
import com.rs.payments.wallet.model.Wallet;
import com.rs.payments.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallets")
@Tag(name = "Wallet Management", description = "APIs for managing user wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @Operation(
            summary = "Create a new wallet for a user",
            description = "Creates a new wallet for the specified user ID with a zero balance.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Wallet created successfully",
                            content = @Content(schema = @Schema(implementation = Wallet.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<Wallet> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        Wallet wallet = walletService.createWalletForUser(request.getUserId());
        return ResponseEntity.ok(wallet);
    }

        @Operation(
        summary = "Deposit money into wallet",
        description = "Deposits a specified amount into the user's wallet",
        responses = {
                @ApiResponse(responseCode = "200", description = "Deposit successful"),
                @ApiResponse(responseCode = "404", description = "User or wallet not found"),
                @ApiResponse(responseCode = "400", description = "Invalid amount")
        }
        )
        @PostMapping("/deposit")
        public ResponseEntity<Void> deposit(@Valid @RequestBody DepositRequest request) {

        walletService.deposit(request.getUserId(), request.getAmount());

        return ResponseEntity.ok().build();
        }

        @PostMapping("/withdraw")
        public ResponseEntity<Void> withdraw(@Valid @RequestBody DepositRequest request) {
        walletService.withdraw(request.getUserId(), request.getAmount());
        return ResponseEntity.ok().build();
        }

        @PostMapping("/transfer")
        public ResponseEntity<Void> transfer(@RequestBody TransferRequest request) {
        walletService.transfer(
                request.getFromUserId(),
                request.getToUserId(),
                request.getAmount()
        );
        return ResponseEntity.ok().build();
        }
    
}