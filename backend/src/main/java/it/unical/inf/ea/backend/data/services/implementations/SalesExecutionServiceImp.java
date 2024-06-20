package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.OrderDao;
import it.unical.inf.ea.backend.data.entities.*;
import it.unical.inf.ea.backend.data.entities.embedded.CustomMoney;
import it.unical.inf.ea.backend.data.services.interfaces.SalesExecutionService;
import it.unical.inf.ea.backend.dto.enums.Availability;
import it.unical.inf.ea.backend.dto.enums.DeliveryStatus;
import it.unical.inf.ea.backend.dto.enums.OrderState;
import it.unical.inf.ea.backend.dto.enums.TransactionState;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SalesExecutionServiceImp implements SalesExecutionService {
    private final Clock clock;
    private final ModelMapper modelMapper;
    private final OrderDao orderDao;

    @Override
    public Order cancelOrder(Order order, User loggedUser) {
        LocalDateTime now = timeNow();
        order.getProduct().setAvailability(Availability.AVAILABLE);
        order.setState(OrderState.CANCELED);
        order.setOrderUpdateDate(now);

        return order;
    }

    @Override
    public Transaction payProduct(Order order, User loggedUser, PaymentMethod paymentMethod) {
        Transaction transaction = new Transaction();

        LocalDateTime now = timeNow();

        Double productPrice = order.getProduct().getProductCost().getPrice();
        Double amount = productPrice+order.getProduct().getDeliveryCost().getPrice();
        transaction.setAmount(new CustomMoney(amount,order.getProduct().getProductCost().getCurrency()));
        //transaction.setOrder(order);
        transaction.setCreationTime(now);
        order.setOrderUpdateDate(now);
        transaction.setPaymentMethod(paymentMethod.getCreditCard().substring(0,4) + " **** **** " + paymentMethod.getCreditCard().substring(15,19));
        transaction.setPaymentMethodOwner(paymentMethod.getOwner());
        Random random = new Random();
        if(random.nextInt(101)>=90)
            transaction.setTransactionState(TransactionState.REJECTED);
        else{
            transaction.setTransactionState(TransactionState.COMPLETED);
            order.setState(OrderState.PURCHASED);
            order.setOrderUpdateDate(now);
            order.getProduct().setAvailability(Availability.UNAVAILABLE);
        }
        order.setTransaction(transaction);
        orderDao.save(order);
        return transaction;
    }

    @Override
    public Delivery sendProduct(Order order, User loggedUser, String shipper, Address senderAddress) {
        Delivery delivery = new Delivery();
        LocalDateTime now = timeNow();

        delivery.setSendTime(now);
        order.setState(OrderState.SHIPPED);
        order.setOrderUpdateDate(now);

        order.setDelivery(delivery);

        delivery.setDeliveryStatus(DeliveryStatus.SHIPPED);
        delivery.setDeliveryCost(order.getProduct().getDeliveryCost());
        delivery.setShipper(shipper);
        delivery.setSenderAddress(senderAddress);
        delivery.setReceiverAddress(order.getDeliveryAddress());

        orderDao.save(order);

        return delivery;
    }


    @Override
    public Order completeOrder(Order order, User loggedUser) {
        order.setState(OrderState.COMPLETED);
        order.setOrderUpdateDate(timeNow());
        return order;
    }

    private LocalDateTime timeNow(){
        return LocalDateTime.now(clock);
    }

}
