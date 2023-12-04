package alex.eros.pokeappdex.home

import alex.eros.pokeappdex.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun HomeScreen(homeViewModel: HomeViewModel){

    val trainerNickName:String by homeViewModel.trainerNickName.observeAsState("")

    ConstraintLayout(Modifier.fillMaxSize()) {

        val (screen,errorMessage) = createRefs()

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .constrainAs(screen) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
             Row(
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(100.dp)
                     .shadow(elevation = 8.dp)
                     .background(
                         brush = Brush.horizontalGradient(
                             colors = listOf(Color(0xFF141414), Color(0xFF383838)),
                         )
                     ),
                 horizontalArrangement = Arrangement.Start,
                 verticalAlignment = Alignment.Bottom
             ) {
                 Image( painter = painterResource(id = R.drawable.logo),
                     contentDescription = "",
                     modifier = Modifier
                         .size(64.dp, 60.dp)
                         .clickable { homeViewModel.logOut() }
                         .padding(start = 16.dp, bottom = 8.dp)
                 )
                 Text(text = "Welcome $trainerNickName", modifier = Modifier.padding(bottom = 16.dp, start = 8.dp), fontSize = 18.sp)
             }   
            }
        }
        Box(modifier = Modifier
            .constrainAs(errorMessage) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .padding(bottom = 20.dp, start = 40.dp, end = 40.dp)
            .shadow(8.dp)
        ) {

        }

    }
}