package it.unical.inf.ea.backend;

import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.dao.PaymentMethodDao;
import it.unical.inf.ea.backend.data.entities.PaymentMethod;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.data.services.implementations.PaymentMethodServiceImp;
import it.unical.inf.ea.backend.dto.PaymentMethodDTO;
import it.unical.inf.ea.backend.dto.creation.PaymentMethodCreateDTO;
import it.unical.inf.ea.backend.dto.enums.UserRole;
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
public class PaymentMethodServiceImpTest {

    @Mock
    private PaymentMethodDao paymentMethodDao;
    @Mock
    private JwtContextUtils jwtContextUtils;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PaymentMethodServiceImp paymentMethodServiceImp;
    private PaymentMethodCreateDTO defaultPaymentMethodCreateDTO;
    private PaymentMethodDTO defaultPaymentMethodDTO;
    private PaymentMethod defaultPaymentMethodEntity;
    private User defaultUserEntity;

    @BeforeEach
    public void setUp() {
        defaultUserEntity = User.builder()
                .id(UUID.fromString("00000001-0001-0001-0001-000000000001"))
                .username("username")
                .password("password")
                .email("email@gmail.com")
                .role(UserRole.USER)
                .build();

        defaultPaymentMethodCreateDTO = PaymentMethodCreateDTO.builder()
                .cardNumber("1234567890123456")
                .expireMonth("12")
                .expireYear("2026")
                .isDefault(true)
                .owner("owner")
                .build();

        defaultPaymentMethodEntity = PaymentMethod.builder()
                .id(UUID.fromString("00000001-0001-0001-0001-000000000001"))
                .cardNumber("1234567890123456")
                .expireMonth("12")
                .expireYear("2026")
                .owner("owner")
                .isDefault(true)
                .user(defaultUserEntity)
                .build();

        defaultPaymentMethodDTO = PaymentMethodDTO.builder()
                .id("00000001-0001-0001-0001-000000000001")
                .cardNumber("1234567890123456")
                .expireMonth("12")
                .expireYear("2026")
                .isDefault(true)
                .owner("owner")
                .build();
    }

    @Test
    void mapEntityToDTO() {
        PaymentMethodDTO paymentMethodDTO = PaymentMethodDTO.builder()
                .id("1-1-1-1-1")
                .cardNumber("1234567890123456")
                .expireMonth("12")
                .expireYear("2026")
                .owner("owner")
                .isDefault(true)
                .build();

        when(modelMapper.map(paymentMethodDTO, PaymentMethod.class)).thenAnswer(invocation -> {
            PaymentMethodDTO dto = invocation.getArgument(0);
            return PaymentMethod.builder()
                    .id(UUID.fromString(dto.getId()))
                    .cardNumber(dto.getCardNumber())
                    .expireMonth(dto.getExpireMonth())
                    .expireYear(dto.getExpireYear())
                    .owner(dto.getOwner())
                    .isDefault(dto.getIsDefault())
                    .user(defaultUserEntity)
                    .build();
        });

        PaymentMethod p = mapToEntity(paymentMethodDTO);

        PaymentMethod expectedPayment = PaymentMethod.builder()
                .id(UUID.fromString("1-1-1-1-1"))
                .cardNumber("1234567890123456")
                .expireMonth("12")
                .expireYear("2026")
                .owner("owner")
                .isDefault(true)
                .user(defaultUserEntity)
                .build();

        assertThat(p).usingRecursiveComparison().isEqualTo(expectedPayment);
    }

    @Test
    void mapDTOToEntity() {
        PaymentMethod paymentMethod = PaymentMethod.builder()
                .id(UUID.fromString("00000001-0001-0001-0001-000000000001"))
                .cardNumber("1234567890123456")
                .expireMonth("12")
                .expireYear("2026")
                .owner("owner")
                .isDefault(true)
                .user(defaultUserEntity)
                .build();

        when(modelMapper.map(paymentMethod, PaymentMethodDTO.class)).thenAnswer(invocation -> {
            PaymentMethod entity = invocation.getArgument(0);
            return PaymentMethodDTO.builder()
                    .id(entity.getId().toString())
                    .cardNumber(entity.getCardNumber())
                    .expireMonth(entity.getExpireMonth())
                    .expireYear(entity.getExpireYear())
                    .owner(entity.getOwner())
                    .isDefault(entity.isDefault())
                    .build();
        });

        PaymentMethodDTO p = mapToDTO(paymentMethod);

        PaymentMethodDTO expectedPayment = PaymentMethodDTO.builder()
                .id("00000001-0001-0001-0001-000000000001")
                .cardNumber("1234567890123456")
                .expireMonth("12")
                .expireYear("2026")
                .owner("owner")
                .isDefault(true)
                .build();

        assertThat(p).usingRecursiveComparison().isEqualTo(expectedPayment);
    }

    @Test
    void savePaymentMethod() throws IllegalAccessException {
        defaultUserEntity.setPaymentMethods(new ArrayList<>());

        when(jwtContextUtils.getUserLoggedFromContext()).thenReturn(defaultUserEntity);
        when(modelMapper.map(defaultPaymentMethodCreateDTO, PaymentMethod.class)).thenReturn(defaultPaymentMethodEntity);
        when(paymentMethodDao.save(any(PaymentMethod.class))).thenReturn(defaultPaymentMethodEntity);

        paymentMethodServiceImp.createPaymentMethod(defaultPaymentMethodCreateDTO);

        verify(paymentMethodDao, times(1)).save(eq(defaultPaymentMethodEntity));
    }

    @Test
    void updatePaymentMethod_nonExistentId() {
        PaymentMethodDTO paymentMethodDTO = PaymentMethodDTO.builder()
                .id("00000001-0001-0001-0001-000000000001")
                .cardNumber("1234567890123456")
                .expireMonth("12")
                .expireYear("2026")
                .isDefault(true)
                .owner("owner")
                .build();

        UUID nonExistentId = UUID.fromString("00000002-0002-0002-0002-000000000002");

        assertThrows(EntityNotFoundException.class, () -> {
            paymentMethodServiceImp.updatePaymentMethod(nonExistentId, paymentMethodDTO);
        });
    }

    @Test
    void replacePaymentMethod() throws IllegalAccessException {
        when(jwtContextUtils.getUserLoggedFromContext()).thenReturn(defaultUserEntity);
        when(paymentMethodDao.findById(UUID.fromString("1-1-1-1-1"))).thenReturn(Optional.ofNullable(defaultPaymentMethodEntity));
        when(modelMapper.map(defaultPaymentMethodEntity, PaymentMethodDTO.class)).thenReturn(defaultPaymentMethodDTO);

        paymentMethodServiceImp.updatePaymentMethod(UUID.fromString("1-1-1-1-1"), defaultPaymentMethodDTO);

        verify(paymentMethodDao, times(1)).save(defaultPaymentMethodEntity);
    }

    @Test
    void deletePaymentMethod() throws IllegalAccessException {
        when(jwtContextUtils.getUserLoggedFromContext()).thenReturn(defaultUserEntity);
        when(paymentMethodDao.findById(UUID.fromString("1-1-1-1-1"))).thenReturn(Optional.ofNullable(defaultPaymentMethodEntity));

        paymentMethodServiceImp.deletePaymentMethod(UUID.fromString("1-1-1-1-1"));

        verify(paymentMethodDao, times(1)).deleteById(UUID.fromString("1-1-1-1-1"));
    }

    @Test
    void getPaymentMethodById() throws IllegalAccessException {
        when(jwtContextUtils.getUserLoggedFromContext()).thenReturn(defaultUserEntity);
        when(paymentMethodDao.findById(UUID.fromString("1-1-1-1-1"))).thenReturn(Optional.ofNullable(defaultPaymentMethodEntity));
        when(modelMapper.map(defaultPaymentMethodEntity, PaymentMethodDTO.class)).thenReturn(defaultPaymentMethodDTO);

        PaymentMethodDTO paymentMethodDTO = paymentMethodServiceImp.getPaymentMethodById(UUID.fromString("1-1-1-1-1"));

        assertThat(paymentMethodDTO).usingRecursiveComparison().isEqualTo(defaultPaymentMethodDTO);
    }

    @Test
    void getAllPaymentMethods() {
        List<PaymentMethod> paymentMethods = List.of(defaultPaymentMethodEntity);
        when(paymentMethodDao.findAll()).thenReturn(paymentMethods);
        when(modelMapper.map(defaultPaymentMethodEntity, PaymentMethodDTO.class)).thenReturn(defaultPaymentMethodDTO);

        List<PaymentMethodDTO> paymentMethodDTOs = paymentMethodServiceImp.getAllPaymentMethods();

        assertThat(paymentMethodDTOs).usingRecursiveComparison().isEqualTo(List.of(defaultPaymentMethodDTO));
    }

    private PaymentMethod mapToEntity(PaymentMethodDTO paymentMethodDTO) {
        return modelMapper.map(paymentMethodDTO, PaymentMethod.class);
    }

    private PaymentMethodDTO mapToDTO(PaymentMethod paymentMethod) {
        return modelMapper.map(paymentMethod, PaymentMethodDTO.class);
    }
}
