package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.ProductCategoryDao;
import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.dao.UserDao;
import it.unical.inf.ea.backend.data.entities.*;
import it.unical.inf.ea.backend.data.entities.embedded.CustomMoney;
import it.unical.inf.ea.backend.data.services.interfaces.ProductService;
import it.unical.inf.ea.backend.dto.*;
import it.unical.inf.ea.backend.dto.basics.ProductBasicDTO;
import it.unical.inf.ea.backend.dto.creation.AlcoholicCreateDTO;
import it.unical.inf.ea.backend.dto.creation.ProductCreateDTO;
import it.unical.inf.ea.backend.dto.creation.SavoryCreateDTO;
import it.unical.inf.ea.backend.dto.creation.SweetCreateDTO;
import it.unical.inf.ea.backend.dto.enums.*;
import it.unical.inf.ea.backend.exception.IdMismatchException;
import it.unical.inf.ea.backend.config.security.Constants;
import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.config.security.TokenStore;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductDao productDao;
    private final ModelMapper modelMapper;
    private final UserDao userDao;
    private final ProductCategoryDao productCategoryDao;
    private final TokenStore tokenStore;

    private final Clock clock;


    private final JwtContextUtils jwtContextUtils;

    @Override
    public ProductDTO createProduct(ProductCreateDTO productCreateDTO) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        try {
            LocalDateTime now = getTimeNow();
            Product product = mapToEntityCreation(productCreateDTO);
            product.setUploadDate(now);
            product.setLastUpdateDate(now);
            product.setSeller(loggedUser);
            product.setAvailability(Availability.AVAILABLE);
            ProductCategory category;
            if(productCreateDTO.getProductCategory().getId() == null) {
                category = productCategoryDao.findProductCategoryByTertiaryCat(productCreateDTO.getProductCategory().getTertiaryCat());
                if(category != null)
                    product.setProductCategory(category);
                else throw new IllegalArgumentException("Category not found");
            }

            if(productCreateDTO instanceof AlcoholicCreateDTO){
                Alcoholic alcoholic= (Alcoholic) product;
            }
            else if (productCreateDTO instanceof SavoryCreateDTO){
                Savory savory= (Savory) product;

            } else if (productCreateDTO instanceof SweetCreateDTO) {
                Sweet sweet= (Sweet) product;
            }

            productDao.save(product);

            return mapToProductDetailsDTO(product);
        } catch (Exception e) {

            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO patch) throws IllegalAccessException {
        throwOnIdMismatch(id, patch);
        Product product = productDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !product.getSeller().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("Cannot update product of others");

        if (patch.getDescription() != null && !product.getDescription().equals(patch.getDescription()))
            product.setDescription(patch.getDescription());

        product.setTitle(patch.getTitle());

        product.setProductCost(checkAndChangeCustomMoney(product.getProductCost(), patch.getProductCost()));
        product.setDeliveryCost(checkAndChangeCustomMoney(product.getDeliveryCost(), patch.getDeliveryCost()));

        if (patch.getBrand() != null && !product.getBrand().equals(patch.getBrand()))
            product.setBrand(patch.getBrand());
        if (!product.getProductSize().equals(patch.getProductSize()))
            product.setProductSize(patch.getProductSize());

        if (patch instanceof AlcoholicDTO) {
            AlcoholicDTO categorized = (AlcoholicDTO) patch;
        } else if (patch instanceof SavoryDTO) {
            SavoryDTO categorized = (SavoryDTO) patch;
        } else if (patch instanceof SweetDTO) {
            SweetDTO categorized = (SweetDTO) patch;
        }

        productDao.save(product);
        return mapToProductDetailsDTO(product);
    }

    @Override
    public void deleteProduct(String id) throws IllegalAccessException {
        Product product = productDao.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if ( !loggedUser.getRole().equals(UserRole.USER) )
            throw new IllegalAccessException("Only admin can delete product");

        try {
            productDao.delete(product);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ProductDTO getProductById(String id, boolean ignoreVisibility) {
        Product product = productDao.findById(id)
                .orElseThrow((() ->
                        new EntityNotFoundException("Product not found")
                ));

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        String loggedUseridString = "";
        if (loggedUser != null){
            loggedUseridString = loggedUser.getId();
        }


        if (loggedUser != null && product.getSeller().getId().equals(loggedUser.getId()) && loggedUser.getRole().equals(UserRole.USER))
            return mapToProductDetailsDTO(product);

        productDao.save(product);
        return mapToProductDetailsDTO(product);
    }

    @Override
    public ProductBasicDTO getProductBasicById(String id, boolean ignoreVisibility) {
        return mapToProductBasicDTO(mapToEntity(getProductById(id, ignoreVisibility)));
    }


    @Override
    public Page<ProductBasicDTO> getProductFilteredPage(Specification<Product> withFilters, int page, int size, String sortBy, String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        String loggedUseridString;
        if (loggedUser != null){
            loggedUseridString = loggedUser.getId();
        } else {
            loggedUseridString = "";
        }

        Page<Product> products = productDao.findAll(withFilters, pageable);

        List<ProductBasicDTO> collect = products.stream().filter(product -> product.getSeller().getId().equals(loggedUseridString) || product.getSeller().getStatus().equals(UserStatus.ACTIVE)).map(s -> modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect, pageable, products.getTotalElements());
    }

    @Override
    public Page<ProductBasicDTO> getMyProducts(int page, int size) {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser.getSellingProducts() != null)
            return new PageImpl<>(
                    loggedUser.getSellingProducts().stream().map(s -> modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList()),
                    PageRequest.of(page, size),
                    loggedUser.getSellingProducts().size());
        else
            return new PageImpl<>(null, PageRequest.of(page, size), 0);
    }

    @Override
    public Iterable<ProductCategoryDTO> getCategoriesList() {
        List<ProductCategory> productCategories = productCategoryDao.findAll();

        return productCategories.stream().map(s -> modelMapper.map(s, ProductCategoryDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Iterable<String> getPrimaryCategoriesList() {
        Map<String, ProductCategory> categoriesMap = new HashMap<>();
        List<ProductCategory> productCategories = productCategoryDao.findAll();

        productCategories.forEach(c -> {
            if (!categoriesMap.containsKey(c.getPrimaryCat())) {
                categoriesMap.put(c.getPrimaryCat(), c);
            }
        });
        return new ArrayList<String>(categoriesMap.keySet());
    }

    @Override
    public Iterable<String> getSecondaryCategoriesListByPrimaryCat(String primaryCategory) {
        List<ProductCategory> productCategories = productCategoryDao.findAllByPrimaryCat(primaryCategory);
        Map<String, ProductCategory> categoriesMap = new HashMap<>();

        productCategories.forEach(c -> {
            if (!categoriesMap.containsKey(c.getSecondaryCat())) {
                categoriesMap.put(c.getSecondaryCat(), c);
            }
        });
        return new ArrayList<String>(categoriesMap.keySet());
    }

    @Override
    public String getCategoryId(String category) {
        String id =  productCategoryDao.findProductCategoryByTertiaryCat(category).getId();
        return id;
    }

    @Override
    public Iterable<String> getTertiaryCategoriesListBySecondaryCat(String secondaryCategory) {
        List<ProductCategory> productCategories = productCategoryDao.findAllBySecondaryCat(secondaryCategory);
        Map<String, ProductCategory> categoriesMap = new HashMap<>();

        productCategories.forEach(c -> {
            if (!categoriesMap.containsKey(c.getTertiaryCat())) {
                categoriesMap.put(c.getTertiaryCat(), c);
            }
        });
        return new ArrayList<String>(categoriesMap.keySet());
    }


    private LocalDateTime getTimeNow() {
        return LocalDateTime.now(clock);
    }

    private CustomMoney checkAndChangeCustomMoney(CustomMoney customMoney, CustomMoneyDTO customMoneyDTO) {
        if (!customMoney.getPrice().equals(customMoneyDTO.getPrice()))
            customMoney.setPrice(customMoneyDTO.getPrice());
        if (!customMoney.getCurrency().equals(customMoneyDTO.getCurrency()))
            customMoney.setCurrency(customMoneyDTO.getCurrency());
        return customMoney;
    }

    private Product mapToEntityCreation(ProductCreateDTO productCreateDTO) {
        if (productCreateDTO.getProductCategory().getPrimaryCat().equals("Alcoholic")) {
            return modelMapper.map(productCreateDTO, Alcoholic.class);
        } else if (productCreateDTO.getProductCategory().getPrimaryCat().equals("Sweet")) {

            return modelMapper.map(productCreateDTO, Sweet.class);
        } else if (productCreateDTO.getProductCategory().getPrimaryCat().equals("Savory")) {
            return modelMapper.map(productCreateDTO, Savory.class);
        } else {
            return modelMapper.map(productCreateDTO, Product.class);
        }
    }


    private Product mapToEntity(ProductDTO productDTO) {
        return switch (productDTO.getProductCategory().getPrimaryCat()) {
            case "Alcoholic" -> modelMapper.map(productDTO, Alcoholic.class);
            case "Sweet" -> modelMapper.map(productDTO, Sweet.class);
            case "Savory" -> modelMapper.map(productDTO, Savory.class);
            default -> modelMapper.map(productDTO, Product.class);
        };
    }

    private ProductDTO mapToProductDetailsDTO(Product product) {
        return switch (product.getProductCategory().getPrimaryCat()) {
            case "Alcoholic" -> modelMapper.map(product, AlcoholicDTO.class);
            case "Sweet" -> modelMapper.map(product, SweetDTO.class);
            case "Savory" -> modelMapper.map(product, SavoryDTO.class);
            default -> modelMapper.map(product, ProductDTO.class);
        };
    }

    private List<ProductBasicDTO> mapToProductBasicDTOList(Page<Product> products) {
        return products.stream().map(s -> modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
    }

    private ProductBasicDTO mapToProductBasicDTO(Product product) {
        return modelMapper.map(product, ProductBasicDTO.class);
    }


    private void throwOnIdMismatch(String productId, ProductDTO productDTO) {
        if (!productDTO.getId().equals(productId)) {
            throw new IdMismatchException();
        }
    }

    public String getCapabilityUrl(String productId) {
        String token = getCapabilityToken(productId);
        return getCapabilityUrlFromToken(token);
    }
    private String getCapabilityUrlFromToken(String token) {
        return Constants.BASE_PATH + "products/capability/" + token;
    }

    @Override
    public CapabilityDTO getCapability(String productId) {
        String token = getCapabilityToken(productId);
        return new CapabilityDTO(token, getCapabilityUrlFromToken(token));
    }


    @Override
    public String getCapabilityToken(String productId) {
        Optional<String> username = jwtContextUtils.getUsernameFromContext();
        if (username.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        Product product = productDao.findById(productId).orElseThrow(EntityNotFoundException::new);
        if (!product.getSeller().getUsername().equals(username.get())) {
            throw new RuntimeException("Unauthorized operation");
        }
        return tokenStore.createCapabilityToken(productId);
    }

}
