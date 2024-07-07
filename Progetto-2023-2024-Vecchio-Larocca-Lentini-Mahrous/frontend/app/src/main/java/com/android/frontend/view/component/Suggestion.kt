package com.android.frontend.view.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.navigation.Navigation
import com.android.frontend.persistence.CurrentDataUtils

@Composable
fun Suggestion(navHostController: NavHostController) {
    val query = remember { mutableStateOf(CurrentDataUtils.searchQuery) }
    val suggestions = CurrentDataUtils.searchSuggestions

    Column(modifier = Modifier.fillMaxWidth()) {
        suggestions.forEach { suggestion ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = stringResource(id = R.string.cancel),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = suggestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                query.value = suggestion
                                navHostController.navigate(Navigation.ProductsPage.route)
                            }
                    )
                }
                IconButton(
                    onClick = {
                        query.value = ""
                        navHostController.navigate(Navigation.ProductsPage.route)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.NorthWest,
                        contentDescription = stringResource(id = R.string.cancel),
                    )
                }
            }
        }
    }
}
