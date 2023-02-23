package com.yugesh.compressedimageshare.ui.utils

import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Contains the default values used by [CIScaffold]. */
object CIScaffoldDefaults {

    /** The default SnackBar Host. */
    @Composable
    fun GetSnackBarHost(
        snackbarHostState: SnackbarHostState,
        modifier: Modifier = Modifier
    ) {
        SnackbarHost(snackbarHostState) { data ->
            CISnackBar(
                modifier = modifier,
                title = data.message,
                actionLabel = data.actionLabel,
                onActionClick = { data.performAction() }
            )
        }
    }
}
