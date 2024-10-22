package com.android.frontend.view.pages.user.browse

import android.annotation.SuppressLint
import com.android.frontend.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.brands.Instagram
import compose.icons.fontawesomeicons.brands.Twitter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(navController: NavController){
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.account).uppercase(),
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
            .fillMaxSize()
            .padding(paddingValues)) {
            Row {
                Text(
                    text = stringResource(R.string.about_description),
                    style = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.padding(10.dp),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp, top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .padding(10.dp)
                        .size(30.dp)
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Brands.Github,
                        contentDescription = stringResource(R.string.github)
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .padding(10.dp)
                        .size(30.dp)
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Brands.Instagram,
                        contentDescription = stringResource(R.string.instagram)
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .padding(10.dp)
                        .size(30.dp)
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Brands.Twitter,
                        contentDescription = stringResource(R.string.twitter)
                    )
                }
            }
        }
    }
}
