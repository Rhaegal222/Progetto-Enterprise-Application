package com.android.frontend.view.page.other

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.frontend.R
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CountryLanguagePage(navController: NavHostController) {
    val languages = listOf("English", "Italian", "French", "Spanish")
    val languageCodes = listOf("en", "it", "fr", "es")
    val flagIcons = listOf(
        R.drawable.english,
        R.drawable.italy,
        R.drawable.france,
        R.drawable.spain
    )

    val context = LocalContext.current
    val currentLanguageCode = getCurrentLanguageCode(context)

    var selectedLanguage by remember { mutableStateOf(currentLanguageCode) }

    val colors = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = colors.onBackground
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(Modifier.selectableGroup()) {
                Spacer(modifier = Modifier.height(50.dp))
                languages.forEachIndexed { index, language ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .selectable(
                                selected = (selectedLanguage == languageCodes[index]),
                                onClick = { selectedLanguage = languageCodes[index] }
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedLanguage == languageCodes[index]) colors.primary.copy(alpha = 0.2f) else colors.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedLanguage == languageCodes[index]),
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colors.primary,
                                    unselectedColor = colors.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Image(
                                painter = painterResource(id = flagIcons[index]),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = language,
                                color = colors.onBackground,
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    setLocale(context, selectedLanguage)
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text(text = "Confirm")
            }
        }
    }
}


fun setLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val resources: Resources = context.resources
    val configuration: Configuration = resources.configuration
    configuration.setLocale(locale)

    val displayMetrics = resources.displayMetrics
    resources.updateConfiguration(configuration, displayMetrics)

    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("selected_language", languageCode).apply()

    val restartIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    restartIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    context.startActivity(restartIntent)
}

fun getCurrentLanguageCode(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return sharedPreferences.getString("selected_language", Locale.getDefault().language) ?: "en"
}
