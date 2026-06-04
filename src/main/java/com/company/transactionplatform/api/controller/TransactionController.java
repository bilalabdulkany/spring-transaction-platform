package com.company.transactionplatform.api.controller;

import com.company.transactionplatform.api.request.CreateTransactionRequest;
import com.company.transactionplatform.api.response.TransactionResponse;
import com.company.transactionplatform.application.command.CreateTransactionCommand;
import com.company.transactionplatform.application.service.TransactionApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionApplicationService service;

    public TransactionController(TransactionApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody CreateTransactionRequest request) {
        TransactionResponse response = service.createTransaction(new CreateTransactionCommand(
                request.idempotencyKey(),
                request.fromAccountNumber(),
                request.toAccountNumber(),
                request.amount(),
                request.currency()
        ));

        return ResponseEntity.accepted().body(response);
    }
}
