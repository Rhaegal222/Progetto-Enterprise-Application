import com.android.frontend.dto.ProductImageDTO
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductBasicDTO(
    var id: String,
    var title: String? = null,
    var description: String? = null,
    var descriptionBrand: String? = null,
    var ingredients: String? = null,
    var nutritionalValues: String? = null,
    var uploadDate: LocalDateTime? = null,
    var productCost: BigDecimal,
    var deliveryCost: BigDecimal,
    var quantity: Int,
    var brand: String? = null,
    var productImages: List<ProductImageDTO>? = null,
    var category: CategoryDTO? = null,
    var onSale: Boolean,
    var discountedPrice: BigDecimal? = null
)
