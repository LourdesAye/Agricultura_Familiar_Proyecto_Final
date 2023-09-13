import com.example.agroagil.core.models.Product

class Sells (
    val sells:List<Sell> = emptyList()
)

data class Sell(
    val nameUser:String = "",
    val items: List<Product> = emptyList(),
    var price: Int = 0,
    val date: String = "01/01/2023 00:00",
    val paid: Boolean = true
)