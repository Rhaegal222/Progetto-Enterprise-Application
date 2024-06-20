package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.OrderDao;
import it.unical.inf.ea.backend.data.dao.PaymentMethodDao;
import it.unical.inf.ea.backend.data.dao.TransactionDao;
import it.unical.inf.ea.backend.data.entities.*;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.interfaces.TransactionService;
import it.unical.inf.ea.backend.dto.TransactionDTO;
import it.unical.inf.ea.backend.dto.creation.TransactionCreateDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
import it.unical.inf.ea.backend.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import lombok.experimental.StandardException

import java.time.Clock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImp implements TransactionService {
    private final TransactionDao transactionDao;
    private final OrderDao orderDao;
    private final PaymentMethodDao paymentMethodDao;
    private final SalesExecutionServiceImp salesExecutionServiceImp;
    private final ModelMapper modelMapper;
    //private final JwtContextUtils jwtContextUtils;
    private final Clock clock;

    public TransactionDTO createTransaction(TransactionCreateDTO transactionDTO) throws IllegalAccessException {
        /*
        Order order = orderDao.findById(Long.valueOf(transactionDTO.getOrder().getId())).orElseThrow(EntityNotFoundException::new); //passaggio a Long per User
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        PaymentMethod paymentMethod = paymentMethodDao.findById(transactionDTO.getPaymentMethod().getId()).orElseThrow(EntityNotFoundException::new);


        if(!order.getUser().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("Cannot create transaction");

        checkCardOwnership(loggedUser,paymentMethod);

        //transaction = transactionRepository.save(transaction);
        return mapToDTO(salesExecutionServiceImp.payProduct(order,loggedUser,paymentMethod));
         */
        return null;
    }

    public TransactionDTO transactionById(String id) {
        /*
        Transaction transaction = transactionDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (!transaction.getOrder().getUser().getId().equals(loggedUser.getId())
                || !transaction.getOrder().getProduct().getSeller().getId().equals(loggedUser.getId()))
            if (loggedUser.getRole().equals(UserRole.USER))
                throw new EntityNotFoundException();

        return mapToDTO(transaction);
         */
        return null;
    }

    public Iterable<TransactionDTO> findAll() throws IllegalAccessException {
        /*
        if (jwtContextUtils.getUserLoggedFromContext().getRole().equals(UserRole.USER))
            throw new IllegalAccessException("Cannot access this resource");

        return transactionDao.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
         */
        return null;
    }


    private void throwOnIdMismatch(String id, TransactionDTO transactionDTO){
        if(!transactionDTO.getId().equals(id))
            throw new IdMismatchException();
    }

    public Transaction mapToEntity(TransactionDTO transactionDTO){return modelMapper.map(transactionDTO,Transaction.class);}
    public TransactionDTO mapToDTO(Transaction transaction){return modelMapper.map(transaction,TransactionDTO.class);}

    private void checkCardOwnership(User user, PaymentMethod paymentMethod) throws IllegalAccessException {
        /*
        if(!user.getPaymentMethods().contains(paymentMethod))
            throw new IllegalAccessException("Cannot use this credit card");
         */

    }
}
