package it.unical.inf.ea.backend;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.CartDao;
import it.unical.inf.ea.backend.data.entities.Cart;
import it.unical.inf.ea.backend.data.entities.CartItem;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.implementations.CartServiceImp;
import it.unical.inf.ea.backend.dto.CartDTO;
import it.unical.inf.ea.backend.dto.CartItemDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CartServiceImpTest {

    @Mock
    private CartDao cartDao;

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private JwtContextUtils jwtContextUtils;

    @InjectMocks
    private CartServiceImp cartService;

    private Cart defaultCartEntity;
    private CartDTO defaultCartDTO;
    private CartItem defaultCartItemEntity;
    private CartItemDTO defaultCartItemDTO;
    private User loggedUser;
    private UUID cartId;
    private UUID cartItemId;

    @BeforeEach
    public void setUp() {
        cartId = UUID.randomUUID();
        cartItemId = UUID.randomUUID();

        loggedUser = new User();
        loggedUser.setId(UUID.randomUUID());

        defaultCartItemEntity = new CartItem();
        defaultCartItemEntity.setId(cartItemId);
        defaultCartItemEntity.setProductId(1L);
        defaultCartItemEntity.setQuantity(2);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(defaultCartItemEntity);

        defaultCartEntity = new Cart();
        defaultCartEntity.setId(cartId);
        defaultCartEntity.setUser(loggedUser);
        defaultCartEntity.setItems(cartItems);

        defaultCartItemDTO = CartItemDTO.builder()
                .id(cartItemId)
                .productId(1L)
                .quantity(2)
                .build();

        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        cartItemDTOs.add(defaultCartItemDTO);

        defaultCartDTO = CartDTO.builder()
                .id(cartId)
                .items(cartItemDTOs)
                .build();
    }

    @Test
    void getCartByUserId_existingId() {
        when(cartDao.findByUserId(loggedUser.getId())).thenReturn(Optional.of(defaultCartEntity));
        when(modelMapper.map(defaultCartEntity, CartDTO.class)).thenReturn(defaultCartDTO);

        CartDTO cartDTO = cartService.getCartByUserId(loggedUser.getId());

        assertThat(cartDTO).usingRecursiveComparison().isEqualTo(defaultCartDTO);
    }

    @Test
    void getCartByUserId_nonExistentId() {
        when(cartDao.findByUserId(loggedUser.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartService.getCartByUserId(loggedUser.getId()));
    }

    @Test
    void getCartForLoggedUser_existingUser() {
        when(jwtContextUtils.getUserLoggedFromContext()).thenReturn(loggedUser);
        when(cartDao.findByUser(loggedUser)).thenReturn(Optional.of(defaultCartEntity));
        when(modelMapper.map(defaultCartEntity, CartDTO.class)).thenReturn(defaultCartDTO);

        CartDTO cartDTO = cartService.getCartForLoggedUser();

        assertThat(cartDTO).usingRecursiveComparison().isEqualTo(defaultCartDTO);
    }

    @Test
    void getCartForLoggedUser_noUserInContext() {
        when(jwtContextUtils.getUserLoggedFromContext()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> cartService.getCartForLoggedUser());
    }

}
