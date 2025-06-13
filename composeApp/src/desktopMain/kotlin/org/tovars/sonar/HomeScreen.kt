package org.tovars.sonar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowsMaximize
import compose.icons.tablericons.ArrowsMinimize
import compose.icons.tablericons.ArrowsSort
import compose.icons.tablericons.PlayerPlay
import compose.icons.tablericons.PlayerSkipBack
import compose.icons.tablericons.PlayerSkipForward
import compose.icons.tablericons.Playlist
import compose.icons.tablericons.Volume
import compose.icons.tablericons.X
import org.jetbrains.compose.resources.painterResource
import sonar_musicplayer.composeapp.generated.resources.Res
import sonar_musicplayer.composeapp.generated.resources.compose_multiplatform

@Composable
fun HomeScreen(
    exitApplication: () -> Unit = {},
    maximizeApplication: () -> Unit = {},
    minimizeApplication: () -> Unit = {}
){

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.Transparent
    ){

        ContentHomeScreen(
            exitApplication = exitApplication,
            maximizeApplication = maximizeApplication,
            minimizeApplication = minimizeApplication
        )

    }

}

@Composable
fun ContentHomeScreen(
    exitApplication: () -> Unit = {},
    maximizeApplication: () -> Unit = {},
    minimizeApplication: () -> Unit = {}
){

    Column (horizontalAlignment = Alignment.CenterHorizontally){

        Box {

            Column (
                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .background(Color(0xfe6565251))
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(16.dp),
            ){

                ButtonsWindows(
                    exitApplication = exitApplication,
                    maximizeApplication = maximizeApplication,
                    minimizeApplication = minimizeApplication

                )

                Spacer( modifier = Modifier.height(50.dp))

                ItemsMenu()

            }

            Column (
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black)
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.9f)
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
            ){



            }

        }


        Spacer( modifier = Modifier.size(16.dp))

        Row (
            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                .background(Color(0xfe6565251))
                .fillMaxWidth(0.8f)
                .height(100.dp)
                .padding(16.dp),
        ){

            MusicPlayer()

        }

    }


}

@Composable
fun ButtonsWindows(
    exitApplication: () -> Unit = {},
    maximizeApplication: () -> Unit = {},
    minimizeApplication: () -> Unit = {}
){
    Row (
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ){

        IconButton(
            onClick = exitApplication,
            modifier = Modifier.clip(CircleShape)
                .size(16.dp)
                .background(Color(0xffe77467))
                .padding(2.dp)
        ){
            Icon(
                TablerIcons.X,
                "",
                tint = Color.Black

            )
        }

        IconButton(
            onClick = minimizeApplication,
            modifier = Modifier.clip(CircleShape)
                .size(16.dp)
                .background(Color(0xfff2c040))
                .padding(2.dp)
        ){
            Icon(
                TablerIcons.ArrowsMinimize,
                "",
                tint = Color.Black

            )
        }

        IconButton(
            onClick = maximizeApplication,
            modifier = Modifier.clip(CircleShape)
                .size(16.dp)
                .background(Color(0xff27d140))
                .padding(2.dp)
        ){
            Icon(
                TablerIcons.ArrowsMaximize,
                "",
                tint = Color.Black

            )
        }

    }
}

enum class ItemsMenu(name: String) {
    Browse("Browse"),
    Songs("Songs"),
    Albums("Albums"),
    Artists("Artists"),
    Radio("Radio"),
    RecentlyPlayed("Recently Played"),
    FavoriteSongs("Favorite Songs"),
    LocalFile("Local File")
}

@Composable
fun ItemsMenu(){

    var items by remember { mutableStateOf(0) }

    Column {

        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = "Library",
            color = Color.White,
            fontSize = 26.sp
        )


        ItemsMenu.entries.forEachIndexed { index, itemsMenu ->

            if (itemsMenu == ItemsMenu.RecentlyPlayed){

                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = "My Music",
                    color = Color.White,
                    fontSize = 26.sp
                )

            }

            Box (
                modifier = Modifier.clip(RoundedCornerShape(40.dp))
                    .clickable {
                        items = index
                    }
                    .background(if (items == index) Color(0xff3d3b39) else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ){
                Text(
                    itemsMenu.name,
                    fontSize = 16.sp,
                    color = if (items == index) Color(0xffdcdeb6) else Color.LightGray
                )
            }

            Spacer( modifier = Modifier.height(8.dp))

        }


    }

}

@Composable
fun MusicPlayer(){

    Row (
        verticalAlignment = Alignment.CenterVertically,
    ){

        Image(
            modifier = Modifier.clip(RoundedCornerShape(10.dp))
                .background(Color.Gray),
            painter = painterResource(Res.drawable.compose_multiplatform),
            contentDescription = ""
        )

        Column (
            modifier = Modifier.padding(horizontal = 16.dp)
                .weight(1f)
        ){

            Text(
                "Artist Name",
                color = Color.LightGray,
                fontSize = 12.sp
            )
            Text(
                "Music Name",
                color = Color.White,
                fontSize = 16.sp
            )

        }

        Row (
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){

            IconButton(
                onClick = {}
            ) {
                Icon(
                    modifier = Modifier.clip(CircleShape)
                        .size(40.dp)
                        .padding(10.dp),
                    imageVector = TablerIcons.PlayerSkipBack,
                    contentDescription = "",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {}
            ) {
                Icon(
                    modifier = Modifier.clip(CircleShape)
                        .size(40.dp)
                        .background(Color(0xfff6fb8d))
                        .padding(10.dp),
                    imageVector = TablerIcons.PlayerPlay,
                    contentDescription = "",
                    tint = Color.Black
                )
            }

            IconButton(
                onClick = {}
            ) {
                Icon(
                    modifier = Modifier.clip(CircleShape)
                        .size(40.dp)
                        .padding(10.dp),
                    imageVector = TablerIcons.PlayerSkipForward,
                    contentDescription = "",
                    tint = Color.White
                )
            }

        }

        Box (
            modifier = Modifier.padding(horizontal = 16.dp)
                .fillMaxHeight()
                .width(500.dp)
                .background(Color.DarkGray)

        )

        Text(
            "0:00",
            color = Color.White,
            fontSize = 12.sp
        )

        Spacer( modifier = Modifier.weight(0.1f))

        Row {

            IconButton(
                onClick = {}
            ) {
                Icon(
                    TablerIcons.ArrowsSort,
                    "",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = TablerIcons.Playlist,
                    contentDescription = "",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {}
            ) {
                Icon(
                    TablerIcons.Volume,
                    "",
                    tint = Color.White
                )
            }

        }


    }

}