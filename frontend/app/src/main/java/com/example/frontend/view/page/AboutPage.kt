package com.example.frontend.view.page

import android.annotation.SuppressLint
import com.example.frontend.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import compose.icons.FontAwesomeIcons
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.brands.Instagram
import compose.icons.fontawesomeicons.brands.Twitter


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(navController: NavController){
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.Black
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(50.dp))

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
                        .background(Color(0xFF9DCEFF))
                        .padding(10.dp)
                        .size(30.dp)
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Brands.Github,
                        contentDescription = stringResource(R.string.social_github),
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF9DCEFF))
                        .padding(10.dp)
                        .size(30.dp)
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Brands.Instagram,
                        contentDescription = stringResource(R.string.social_instagram),
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF9DCEFF))
                        .padding(10.dp)
                        .size(30.dp)
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Brands.Twitter,
                        contentDescription = stringResource(R.string.social_twitter),
                        tint = Color.White
                    )
                }
            }
        }
    }
}