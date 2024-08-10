package com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.FolderData
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.preview.LightDarkPrev
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.theme.VideoPlayerTheme


@LightDarkPrev
@Composable
fun DirItemPreview() {
    VideoPlayerTheme {
        DirItem(folder = FolderData.sample)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DirItem(
    folder: FolderData,
    modifier: Modifier = Modifier,
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor =  Color.Transparent
        ),
        leadingContent = {
            val imagePainter: Painter = painterResource(id = com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.R.drawable.ic_folder)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent), // Ensure transparency
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = imagePainter,
                    contentDescription = "Custom Image",
                    modifier = Modifier
                        .sizeIn(maxWidth = 250.dp)
                        .fillMaxWidth(0.45f)
                        .aspectRatio(1f)
                        .background(color = Color.Transparent) // Ensure transparency
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .height(intrinsicSize = IntrinsicSize.Max)
                        .background(color = Color.Transparent) // Ensure transparency
                ) {
                    Text(
                        text = folder.name,
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis,
                        modifier = modifier
                            .fillMaxWidth(1f)
                            .background(Color.Transparent), // Ensure transparency
                        maxLines = 1
                    )
                    Text(
                        text = folder.path,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .fillMaxWidth(1f)
                            .background(Color.Transparent), // Ensure transparency
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .background(Color.Transparent), // Ensure transparency
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        DetailsChipUi(
                            text = "${folder.mediaCount} " + stringResource(id = R.string.video.takeIf { folder.mediaCount == 1 }
                                ?: R.string.videos)
                        )
                        DetailsChipUi(text = folder.formattedMediaSize)
                    }
                }
            }
        },
        headlineContent = {},
        supportingContent = {},
        modifier = modifier
            .background(Color.Transparent) // Ensure transparency
    )

    Divider(thickness = 1.dp, modifier = Modifier.padding(start = 15.dp, end = 15.dp))

}
