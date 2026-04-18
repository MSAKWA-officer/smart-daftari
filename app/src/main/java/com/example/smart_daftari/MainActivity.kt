package com.example.smart_daftari

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll



import androidx.compose.ui.draw.clip
// Hakikisha pia hizi zipo
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainAppNavigation()
                }
            }
        }
    }
}

// --- NAVIGATION WRAPPER ---
@Composable
fun MainAppNavigation() {
    var currentScreen by remember { mutableStateOf("Wateja") }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                val items = listOf(
                    "Wateja" to Icons.Default.People,
                    "Bidhaa" to Icons.Default.ShoppingCart,
                    "Mikopo" to Icons.Default.ReceiptLong,
                    "Malipo" to Icons.Default.History,
                    "Score" to Icons.Default.Star
                )
                items.forEach { (name, icon) ->
                    NavigationBarItem(
                        selected = currentScreen == name,
                        onClick = { currentScreen = name },
                        icon = { Icon(icon, contentDescription = name) },
                        label = { Text(name, fontSize = 10.sp) }
                    )
                }
            }
        }
    ) { paddingValues ->
        // The paddingValues from Scaffold prevent the content from being hidden behind the NavigationBar
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentScreen) {
                "Wateja" -> CustomerScreen()
                "Bidhaa" -> ProductScreen()
                "Mikopo" -> DebtScreen()
                "Malipo" -> PaymentHistoryScreen()
                "Score"  -> CreditScoreDashboard()
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
    }
}
//PRODUCT PAGE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    var products by remember { mutableStateOf(listOf<Product>()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // State za fomu
    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productStock by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<String?>(null) }
    var showSheet by remember { mutableStateOf(false) }

    fun loadProducts() {
        isLoading = true
        scope.launch {
            try {
                val res = withContext(Dispatchers.IO) { ApiClient.api.getProducts() }
                if (res.isSuccessful) {
                    products = res.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Imeshindwa kupakia data", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadProducts() }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingId = null
                    productName = ""; productPrice = ""; productStock = ""; productDescription = ""
                    showSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Bidhaa Mpya") }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text(
                "Smart Daftari Stok",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Tafuta bidhaa...") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Close, null) }
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )

            if (isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)))
                Spacer(Modifier.height(16.dp))
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val filteredProducts = products.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            (it.description?.contains(searchQuery, ignoreCase = true) ?: false)
                }

                items(filteredProducts) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(15.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                                modifier = Modifier.size(55.dp)
                            ) {
                                Icon(Icons.Default.Inventory2, null, Modifier.padding(14.dp), tint = MaterialTheme.colorScheme.primary)
                            }

                            Spacer(Modifier.width(16.dp))

                            Column(Modifier.weight(1f)) {
                                Text(product.name.uppercase(), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Layers, null, Modifier.size(14.dp), tint = Color.Gray)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Stok: ${product.stock_quantity}", fontSize = 13.sp, color = Color.DarkGray)
                                }
                                Text("Tsh ${numberFormat.format(product.price)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 17.sp)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                FilledTonalIconButton(onClick = {
                                    editingId = product.id.toString()
                                    productName = product.name
                                    productPrice = product.price.toString()
                                    productStock = product.stock_quantity.toString()
                                    productDescription = product.description ?: ""
                                    showSheet = true
                                }) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp)) }

                                IconButton(onClick = {
                                    scope.launch {
                                        try {
                                            val res = withContext(Dispatchers.IO) { ApiClient.api.deleteProduct("eq.${product.id}") }
                                            if (res.isSuccessful) loadProducts()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Shida ya kufuta", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }) { Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(18.dp)) }
                            }
                        }
                    }
                }
            }
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(Modifier.padding(24.dp).fillMaxWidth().navigationBarsPadding()) {
                    Text(
                        if (editingId == null) "Ongeza Bidhaa" else "Hariri Bidhaa",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text("Jina la Bidhaa") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = productPrice,
                            onValueChange = { productPrice = it },
                            label = { Text("Bei") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = productStock,
                            onValueChange = { productStock = it },
                            label = { Text("Stok") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = productDescription,
                        onValueChange = { productDescription = it },
                        label = { Text("Maelezo (Sio lazima)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (productName.isBlank()) {
                                Toast.makeText(context, "Jaza jina la bidhaa", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val p = Product(
                                name = productName,
                                price = productPrice.toDoubleOrNull() ?: 0.0,
                                stock_quantity = productStock.toIntOrNull() ?: 0,
                                description = productDescription
                            )

                            scope.launch {
                                try {
                                    val res = if (editingId == null) {
                                        withContext(Dispatchers.IO) { ApiClient.api.addProduct(p) }
                                    } else {
                                        withContext(Dispatchers.IO) { ApiClient.api.updateProduct("eq.$editingId", p) }
                                    }

                                    if (res.isSuccessful) {
                                        showSheet = false
                                        loadProducts()
                                        Toast.makeText(context, "Imefanikiwa!", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Hitilafu imetokea!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (editingId == null) "Hifadhi" else "Sasisha")
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}



// --- CUSTOMER PAGES ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // State za data
    var customers by remember { mutableStateOf(listOf<Customer>()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // State za fomu
    var showSheet by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var guarantorName by remember { mutableStateOf("") }
    var guarantorPhone by remember { mutableStateOf("") }

    // 1. READ (Kupata Wateja)
    fun loadCustomers() {
        isLoading = true
        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) { ApiClient.api.getCustomers() }
                if (response.isSuccessful) {
                    customers = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Mtandao una shida", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadCustomers() }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingId = null
                    name = ""; phone = ""; guarantorName = ""; guarantorPhone = ""
                    showSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.PersonAdd, null) },
                text = { Text("Mteja Mpya") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text(
                "Orodha ya Wateja",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            // SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Tafuta kwa jina...") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = Color.Transparent
                )
            )

            if (isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)))
                Spacer(Modifier.height(16.dp))
            }

            // CUSTOMER LIST
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val filtered = customers.filter { it.name.contains(searchQuery, true) }
                items(filtered) { customer ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                modifier = Modifier.size(50.dp)
                            ) {
                                Icon(Icons.Default.Person, null, modifier = Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.primary)
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(Modifier.weight(1f)) {
                                Text(customer.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(customer.phone, color = Color.Gray, fontSize = 13.sp)
                                // Score Badge
                                Text(
                                    "Score: ${customer.creditScore}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = getScoreColor(customer.creditScore)
                                )
                            }

                            // UPDATE & DELETE BUTTONS
                            Row {
                                IconButton(onClick = {
                                    editingId = customer.id.toString()
                                    name = customer.name
                                    phone = customer.phone
                                    guarantorName = customer.guarantorName ?: ""
                                    guarantorPhone = customer.guarantorPhone ?: ""
                                    showSheet = true
                                }) {
                                    Icon(Icons.Default.Edit, "Edit", tint = Color.Gray, modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        try {
                                            val res = withContext(Dispatchers.IO) {
                                                ApiClient.api.deleteCustomer("eq.${customer.id}")
                                            }
                                            if (res.isSuccessful) loadCustomers()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Imeshindwa kufuta", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, "Futa", tint = Color.Red, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- BOTTOM SHEET FOMU (CREATE & UPDATE) ---
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        if (editingId == null) "Sajili Mteja Mpya" else "Hariri Taarifa",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Jina kamili") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Namba ya Simu") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))

                    Text("Taarifa za Mdhamini", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = guarantorName, onValueChange = { guarantorName = it }, label = { Text("Jina la Mdhamini") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = guarantorPhone, onValueChange = { guarantorPhone = it }, label = { Text("Simu ya Mdhamini") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (name.isBlank() || phone.isBlank()) {
                                Toast.makeText(context, "Jaza Jina na Simu!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val customerData = Customer(
                                name = name,
                                phone = phone,
                                guarantorName = guarantorName,
                                guarantorPhone = guarantorPhone,
                                creditScore = 0 // Score ya kuanzia
                            )

                            scope.launch {
                                try {
                                    val res = if (editingId == null) {
                                        withContext(Dispatchers.IO) { ApiClient.api.addCustomer(customerData) }
                                    } else {
                                        withContext(Dispatchers.IO) { ApiClient.api.updateCustomer("eq.$editingId", customerData) }
                                    }

                                    if (res.isSuccessful) {
                                        showSheet = false
                                        loadCustomers()
                                        Toast.makeText(context, "Imefanikiwa!", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Hitilafu imetokea!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(if (editingId == null) "Hifadhi Mteja" else "Sasisha Taarifa")
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}



//DEBT PAGE
// --- DEBT SCREEN (MIKOPO) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    // Data lists
    var customers by remember { mutableStateOf(listOf<Customer>()) }
    var products by remember { mutableStateOf(listOf<Product>()) }
    var debts by remember { mutableStateOf(listOf<Debt>()) }

    // UI States
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var dueDate by remember { mutableStateOf("") }
    var isCustExpanded by remember { mutableStateOf(false) }
    var isProdExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showDebtForm by remember { mutableStateOf(false) }

    // Payment States
    var showPaymentDialog by remember { mutableStateOf(false) }
    var debtTarget by remember { mutableStateOf<Debt?>(null) }
    var paymentAmount by remember { mutableStateOf("") }

    fun loadData() {
        isLoading = true
        scope.launch {
            try {
                val custRes = withContext(Dispatchers.IO) { ApiClient.api.getCustomers() }
                val prodRes = withContext(Dispatchers.IO) { ApiClient.api.getProducts() }
                val debtRes = withContext(Dispatchers.IO) { ApiClient.api.getDebts() }

                if (custRes.isSuccessful) customers = custRes.body() ?: emptyList()
                if (prodRes.isSuccessful) products = prodRes.body() ?: emptyList()
                if (debtRes.isSuccessful) debts = debtRes.body() ?: emptyList()
            } catch (e: Exception) {
                Toast.makeText(context, "Hitilafu ya kupakia data", Toast.LENGTH_SHORT).show()
            } finally { isLoading = false }
        }
    }

    LaunchedEffect(Unit) { loadData() }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    selectedCustomer = null; selectedProduct = null; dueDate = ""
                    showDebtForm = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.AddCard, null) },
                text = { Text("Toa Mkopo") }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text(
                "Msimamizi wa Mikopo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            // --- SUMMARY CARD ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("JUMLA YA MADENI", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
                        val totalRemaining = debts.sumOf { it.remainingAmount }
                        Text(
                            text = "TSh ${numberFormat.format(totalRemaining)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Icon(Icons.Default.TrendingUp, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(40.dp))
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("Mikopo Inayoendelea", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            if (isLoading) LinearProgressIndicator(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val activeDebts = debts.filter { it.remainingAmount > 0 }
                items(activeDebts) { debt ->
                    val customer = customers.find { it.id == debt.customerId }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                modifier = Modifier.size(50.dp)
                            ) {
                                Icon(Icons.Default.HistoryEdu, null, modifier = Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.error)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(customer?.name ?: "Mteja", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Deni: ${numberFormat.format(debt.remainingAmount)} /=", color = Color.Red, fontWeight = FontWeight.SemiBold)
                                Text("Mwisho: ${debt.dueDate ?: "Hajapangiwa"}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Button(
                                onClick = {
                                    debtTarget = debt
                                    paymentAmount = ""
                                    showPaymentDialog = true
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("LIPA", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- DIALOG YA KUTOA MKOPO MPYA ---
        if (showDebtForm) {
            AlertDialog(
                onDismissRequest = { showDebtForm = false },
                title = { Text("Toa Mkopo", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Dropdown Mteja
                        ExposedDropdownMenuBox(expanded = isCustExpanded, onExpandedChange = { isCustExpanded = !isCustExpanded }) {
                            OutlinedTextField(
                                value = selectedCustomer?.name ?: "Chagua Mteja",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isCustExpanded) }
                            )
                            ExposedDropdownMenu(expanded = isCustExpanded, onDismissRequest = { isCustExpanded = false }) {
                                customers.forEach { cust ->
                                    DropdownMenuItem(text = { Text(cust.name) }, onClick = { selectedCustomer = cust; isCustExpanded = false })
                                }
                            }
                        }

                        // Dropdown Bidhaa
                        ExposedDropdownMenuBox(expanded = isProdExpanded, onExpandedChange = { isProdExpanded = !isProdExpanded }) {
                            OutlinedTextField(
                                value = selectedProduct?.name ?: "Chagua Bidhaa",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isProdExpanded) }
                            )
                            ExposedDropdownMenu(expanded = isProdExpanded, onDismissRequest = { isProdExpanded = false }) {
                                products.forEach { prod ->
                                    DropdownMenuItem(text = { Text(prod.name) }, onClick = { selectedProduct = prod; isProdExpanded = false })
                                }
                            }
                        }

                        OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Tarehe ya Kurudisha (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (selectedCustomer == null || selectedProduct == null) return@Button
                        val newDebt = Debt(
                            customerId = selectedCustomer!!.id!!,
                            productId = selectedProduct!!.id!!,
                            amountBorrowed = selectedProduct!!.price,
                            remainingAmount = selectedProduct!!.price,
                            dueDate = dueDate
                        )
                        scope.launch {
                            val res = withContext(Dispatchers.IO) { ApiClient.api.createDebt(newDebt) }
                            if (res.isSuccessful) { showDebtForm = false; loadData() }
                        }
                    }) { Text("Toa Mkopo") }
                }
            )
        }

        // --- DIALOG YA KULIPIA ---
        if (showPaymentDialog && debtTarget != null) {
            AlertDialog(
                onDismissRequest = { showPaymentDialog = false },
                title = { Text("Lipa Deni") },
                text = {
                    OutlinedTextField(value = paymentAmount, onValueChange = { paymentAmount = it }, label = { Text("Kiasi cha Kulipa") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                },
                confirmButton = {
                    Button(onClick = {
                        val amount = paymentAmount.toDoubleOrNull() ?: 0.0
                        if (amount <= 0) return@Button

                        val newRemaining = (debtTarget!!.remainingAmount - amount).coerceAtLeast(0.0)

                        scope.launch {
                            // 1. Rekodi Malipo
                            ApiClient.api.recordPayment(Payment(debtId = debtTarget!!.id!!, amountPaid = amount))
                            // 2. Sasisha Deni
                            ApiClient.api.updateDebtRemaining("eq.${debtTarget!!.id}", mapOf("remaining_amount" to newRemaining))

                            showPaymentDialog = false
                            loadData()
                            Toast.makeText(context, "Malipo yamepokelewa", Toast.LENGTH_SHORT).show()
                        }
                    }) { Text("Hifadhi Malipo") }
                }
            )
        }
    }
}


//Payment page
@Composable
fun PaymentHistoryScreen() {
    val scope = rememberCoroutineScope()
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    val context = LocalContext.current

    var payments by remember { mutableStateOf(listOf<Payment>()) }
    var debts by remember { mutableStateOf(listOf<Debt>()) }
    var customers by remember { mutableStateOf(listOf<Customer>()) }
    var isLoading by remember { mutableStateOf(false) }

    // Kazi ya kuvuta data zote
    fun loadAllData() {
        isLoading = true
        scope.launch {
            try {
                val payRes = withContext(Dispatchers.IO) { ApiClient.api.getPayments() }
                val debtRes = withContext(Dispatchers.IO) { ApiClient.api.getDebts() }
                val custRes = withContext(Dispatchers.IO) { ApiClient.api.getCustomers() }

                if (payRes.isSuccessful) payments = payRes.body() ?: emptyList()
                if (debtRes.isSuccessful) debts = debtRes.body() ?: emptyList()
                if (custRes.isSuccessful) customers = custRes.body() ?: emptyList()
            } catch (e: Exception) {
                Toast.makeText(context, "Hitilafu: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadAllData() }

    Column(Modifier.padding(16.dp).fillMaxSize()) {
        Text(
            "Historia ya Malipo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Kumbukumbu ya fedha zilizopokelewa",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            LinearProgressIndicator(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
        }

        if (payments.isEmpty() && !isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Hakuna malipo yaliyorekodiwa", color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Kupanga malipo ya karibuni yawe juu (Kama createdAt si null)
            val sortedPayments = payments.sortedByDescending { it.createdAt }

            items(sortedPayments) { payment ->
                val relatedDebt = debts.find { it.id == payment.debtId }
                val customerName = customers.find { it.id == relatedDebt?.customerId }?.name ?: "Mteja Hajulikani"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp),
                                tint = Color(0xFF2E7D32)
                            )
                        }

                        Spacer(Modifier.width(16.dp))

                        Column(Modifier.weight(1f)) {
                            Text(customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                "Tarehe: ${formatSupabaseDate(payment.createdAt)}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = payment.paymentMethod?.uppercase() ?: "CASH",
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "+${numberFormat.format(payment.amountPaid)}/=",
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )

                            IconButton(
                                onClick = {
                                    scope.launch {
                                        try {
                                            // REKEBISHO: Supabase inahitaji eq. filter
                                            val res = withContext(Dispatchers.IO) {
                                                ApiClient.api.deletePayment("eq.${payment.id}")
                                            }

                                            if (res.isSuccessful) {
                                                loadAllData()
                                                Toast.makeText(context, "Malipo yamefutwa", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Futa",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun CreditScoreDashboard() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    var customers by remember { mutableStateOf(listOf<Customer>()) }
    var debts by remember { mutableStateOf(listOf<Debt>()) }
    var isLoading by remember { mutableStateOf(false) }

    fun loadStats() {
        isLoading = true
        scope.launch {
            try {
                val custRes = withContext(Dispatchers.IO) { ApiClient.api.getCustomers() }
                val debtRes = withContext(Dispatchers.IO) { ApiClient.api.getDebts() }
                if (custRes.isSuccessful) customers = custRes.body() ?: emptyList()
                if (debtRes.isSuccessful) debts = debtRes.body() ?: emptyList()
            } catch (e: Exception) {
                Toast.makeText(context, "Hitilafu kupakia data", Toast.LENGTH_SHORT).show()
            } finally { isLoading = false }
        }
    }

    LaunchedEffect(Unit) { loadStats() }

    Column(Modifier.padding(16.dp).fillMaxSize()) {
        Text(
            "Uaminifu & Takwimu",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text("Uchambuzi wa uaminifu wa wateja wako", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(20.dp))

        if (isLoading) LinearProgressIndicator(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)))

        // --- 1. TOP ANALYTICS CARDS ---
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Kadi ya Wateja Bora
            Card(
                Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF2E7D32))
                    Text("Waminifu", fontSize = 12.sp, color = Color.DarkGray)
                    val highScores = customers.count { it.creditScore >= 80 }
                    Text("$highScores", fontWeight = FontWeight.Black, fontSize = 20.sp)
                }
            }
            // Kadi ya Madeni Sugu
            Card(
                Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F))
                    Text("Wasumbufu", fontSize = 12.sp, color = Color.DarkGray)
                    val lowScores = customers.count { it.creditScore < 50 }
                    Text("$lowScores", fontWeight = FontWeight.Black, fontSize = 20.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("Ranking ya Wateja (Score)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(12.dp))

        // --- 2. CUSTOMER SCORE LIST ---
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Kupanga wateja kuanzia mwenye score kubwa
            val sortedCustomers = customers.sortedByDescending { it.creditScore }

            items(sortedCustomers) { customer ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rank Circle
                        Surface(
                            shape = CircleShape,
                            color = getScoreColor(customer.creditScore).copy(alpha = 0.1f),
                            modifier = Modifier.size(45.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "${customer.creditScore}",
                                    fontWeight = FontWeight.Bold,
                                    color = getScoreColor(customer.creditScore)
                                )
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        Column(Modifier.weight(1f)) {
                            Text(customer.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            val totalDebt = debts.filter { it.customerId == customer.id }.sumOf { it.remainingAmount }
                            Text("Deni la sasa: ${numberFormat.format(totalDebt)}/=", fontSize = 12.sp, color = Color.Gray)
                        }

                        // Status Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = getScoreColor(customer.creditScore)
                        ) {
                            Text(
                                text = when {
                                    customer.creditScore >= 80 -> "BORA"
                                    customer.creditScore >= 50 -> "WASTANI"
                                    else -> "MBAYA"
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}


// Hii iwekwe nje ya MainActivity class (chini kabisa ya faili)
fun formatSupabaseDate(rawDate: String?): String {
    if (rawDate == null) return ""
    return try {
        // Supabase hutoa tarehe kama "2024-03-21T10:00:00Z"
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        val date = inputFormat.parse(rawDate)
        outputFormat.format(date ?: java.util.Date())
    } catch (e: Exception) {
        rawDate.take(10) // Kama ikishindikana, chukua YYYY-MM-DD pekee
    }
}


// Iweke hii chini kabisa ya file lako, nje ya Class au Screens zingine
fun getScoreColor(score: Int): Color {
    return when {
        score >= 80 -> Color(0xFF2E7D32) // Kijani (Salama)
        score >= 50 -> Color(0xFFF9A825) // Njano/Machungwa (Onyo)
        else -> Color(0xFFC62828)        // Nyekundu (Hatari)
    }
}


