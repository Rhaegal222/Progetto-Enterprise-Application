package com.android.frontend.view.page.other

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.frontend.R
import java.util.*

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Language",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(Modifier.selectableGroup()) {
            languages.forEachIndexed { index, language ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (selectedLanguage == languageCodes[index]),
                            onClick = { selectedLanguage = languageCodes[index] }
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedLanguage == languageCodes[index]),
                        onClick = null // null recommended for accessibility with screenreaders
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        painter = painterResource(id = flagIcons[index]),
                        contentDescription = null, // Provide a description for accessibility
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    BasicText(text = language)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                setLocale(context, selectedLanguage)
                navController.popBackStack()
            }
        ) {
            Text(text = "Confirm")
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

    // Save the selected language in shared preferences
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("selected_language", languageCode).apply()

    // This code restarts the app to apply the new language settings
    val restartIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    restartIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    context.startActivity(restartIntent)
}

fun getCurrentLanguageCode(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return sharedPreferences.getString("selected_language", Locale.getDefault().language) ?: "en"
}
