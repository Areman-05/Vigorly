package com.example.vigorly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.vigorly.data.catalog.WorkoutCoverUrls
import com.example.vigorly.ui.theme.SurfaceContainerHigh

@Composable
fun RemoteCoverImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    colorFilter: ColorFilter? = null
) {
    val resolved = url?.takeIf { it.isNotBlank() } ?: WorkoutCoverUrls.fallback
    val errorFallback = WorkoutCoverUrls.fallbackFor(resolved)
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(resolved)
            .crossfade(true)
            .error(null)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier.fillMaxSize(),
        contentScale = contentScale,
        colorFilter = colorFilter
    ) {
        when (val state = painter.state) {
            is AsyncImagePainter.State.Error -> {
                // Reintento con otra portada (no siempre la misma)
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(errorFallback)
                        .crossfade(true)
                        .build(),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                ) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Error,
                        is AsyncImagePainter.State.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(SurfaceContainerHigh)
                            )
                        }
                        else -> SubcomposeAsyncImageContent()
                    }
                }
            }
            is AsyncImagePainter.State.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.04f))
                )
            }
            else -> {
                // keep compiler happy with unused state
                @Suppress("UNUSED_VARIABLE")
                val ignored = state
                SubcomposeAsyncImageContent()
            }
        }
    }
}
