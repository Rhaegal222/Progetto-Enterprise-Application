package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.services.interfaces.ProductService;
import it.unical.inf.ea.backend.dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;

    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductDao productDao, ModelMapper modelMapper) {
        this.productDao = productDao;
        this.modelMapper = modelMapper;
    }

    public void save(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        productDao.save(product);
    }

    @Override
    @Transactional
    public void addProduct(ProductDTO productDto) {

        Product product = new Product();
        product.setTitle(productDto.getTitle());
        product.setProductPrice(productDto.getProductPrice());
        product.setDeliveryPrice(productDto.getDeliveryPrice());
        product.setAvailability(productDto.getAvailability());
        Product newProduct = productDao.save(product);
        modelMapper.map(newProduct, ProductDTO.class);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productDao.findAll();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productDao.findById(String.valueOf(id)).orElse(null);
        return product != null ? modelMapper.map(product, ProductDTO.class) : null;
    }


    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDto) {
        ProductDTO existingProduct = productDao.findProductById(String.valueOf(id));

        if (existingProduct != null) {
            existingProduct.setTitle(productDto.getTitle());
            existingProduct.setProductPrice(productDto.getProductPrice());
            existingProduct.setDeliveryPrice(productDto.getDeliveryPrice());
            existingProduct.setAvailability(productDto.getAvailability());
            existingProduct.setProductCategory(productDto.getProductCategory());
            Product product = modelMapper.map(productDto, Product.class);
            productDao.save(product);
            return modelMapper.map(existingProduct, ProductDTO.class);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productDao.deleteById(String.valueOf(id));
    }
}
