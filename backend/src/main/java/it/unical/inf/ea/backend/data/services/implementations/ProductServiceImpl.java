package it.unical.inf.ea.backend.data.services.implementations;

import it.unical.inf.ea.backend.data.dao.ProductDao;
import it.unical.inf.ea.backend.data.entities.Product;
import it.unical.inf.ea.backend.data.services.interfaces.ProductService;
import it.unical.inf.ea.backend.dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDto) {
        Product product = modelMapper.map(productDto, Product.class);
        product = productDao.save(product);
        return modelMapper.map(product, ProductDTO.class);
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
        if (product != null) return modelMapper.map(product, ProductDTO.class);
        return null;
    }




    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDto) {
        ProductDTO existingProduct = productDao.findProductById(String.valueOf(id));

        if (existingProduct != null) {
            // Update relevant fields (excluding id)
            existingProduct.setTitle(productDto.getTitle());
            existingProduct.setDescription(productDto.getDescription());
            existingProduct.setProductCost(productDto.getProductCost());
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
