package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.dto.TransactionDTO;
import it.unical.inf.ea.backend.dto.creation.TransactionCreateDTO;

public interface TransactionService {
    TransactionDTO createTransaction(TransactionCreateDTO transactionDTO) throws IllegalAccessException;

    TransactionDTO transactionById(String id);

    Iterable<TransactionDTO> findAll() throws IllegalAccessException;
}
