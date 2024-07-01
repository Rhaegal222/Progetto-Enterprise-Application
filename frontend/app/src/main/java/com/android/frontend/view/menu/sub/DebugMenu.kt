package com.android.frontend.view.menu.sub

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.view_models.admin.DebugViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DebugMenu(navController: NavHostController) {

    val context = LocalContext.current

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.debug).uppercase(),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues).padding(8.dp, 0.dp)
                .verticalScroll(rememberScrollState())
        ) {
            DebugItem(context, Icons.Default.BugReport, R.string.reject_access_token)
            DebugItem(context, Icons.Default.BugReport, R.string.show_tokens)
            DebugItem(context, Icons.Default.BugReport, R.string.add_category)
            DebugItem(context, Icons.Default.BugReport, R.string.add_address)
            DebugItem(context, Icons.Default.BugReport, R.string.add_payment_method)
            DebugItem(context, Icons.Default.BugReport, R.string.add_brand)
            DebugItem(context, Icons.Default.BugReport, R.string.add_product)
        }
    }
}

@Composable
fun DebugItem(context: Context, icon: ImageVector, textResId: Int) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp)
            .clickable {
                when (textResId) {
                    R.string.reject_access_token -> DebugViewModel().rejectToken(context)
                    R.string.show_tokens -> DebugViewModel().showToken(context)
                    R.string.add_category -> DebugViewModel().generateProductCategory(context)
                    R.string.add_address -> DebugViewModel().generateAddress(context)
                    R.string.add_payment_method -> DebugViewModel().generatePayment(context)
                    R.string.add_brand -> DebugViewModel().generateBrand(context)
                    // R.string.add_product -> DebugViewModel().generateProduct(context)
                }
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = textResId),
                fontWeight = FontWeight.Bold
            )
        }
    }
}