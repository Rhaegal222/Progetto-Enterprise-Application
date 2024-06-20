package it.unical.inf.ea.backend.data.services.interfaces;

import it.unical.inf.ea.backend.data.entities.*;
import org.springframework.stereotype.Service;

@Service
public interface SalesExecutionService {

    Order cancelOrder(Order order, User loggedUser);

    Transaction payProduct(Order order, User loggedUser, PaymentMethod paymentMethod);

    Delivery sendProduct(Order order, User loggedUser, String shipper, Address senderAddress);

    Order completeOrder(Order order,User loggedUser);//invocato dal compratore per settare l'ordine su completed e sbloccare la recensione

}
