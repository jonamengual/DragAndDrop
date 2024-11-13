package com.example.draganddrop

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

internal val LocalDragTargetIfo = compositionLocalOf{ DragTrargetInfo() }


@Composable
fun DragableScreen(modifier: Modifier=Modifier, content:@Composable BoxScope.()-> Unit){
    val state= remember {
        DragTrargetInfo()
    }
    CompositionLocalProvider(LocalDragTargetIfo provides state){
        Box(modifier = modifier.fillMaxSize()) {
            content()
            if(state.isDagging){
                var targetSize by remember{
                    mutableStateOf(IntSize.Zero)
                }
                Box(modifier = Modifier
                    .graphicsLayer {
                        val offeset=(state.dragPosition+state.dragOffset)
                        scaleX=1.3f
                        scaleY=1.3f
                        alpha= if(targetSize==IntSize.Zero)0f else .9f
                        translationX=offeset.x.minus(targetSize.width/2)
                        translationY=offeset.y.minus(targetSize.height/2)

                    }
                    .onGloballyPositioned {
                        targetSize=it.size
                    }
                ){
                    state.draggableComposable?.invoke()
                }
            }

        }
    }
}


@Composable
fun <T> DragTrarget( modifier: Modifier=Modifier, datatoDrop:T, viewModel: MainViewModel, content: @Composable (()->Unit) ){
    var currentPosition by remember {
        mutableStateOf(Offset.Zero)}
    val currentState=LocalDragTargetIfo.current
    Box(
        modifier= modifier
            .onGloballyPositioned { currentPosition = it.localToWindow(Offset.Zero) }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {

                        viewModel.startDragging()

                        currentState.dataToDrop = datatoDrop
                        currentState.isDagging = true
                        currentState.dragPosition = currentPosition + it
                        currentState.draggableComposable = content
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)

                    },
                    onDragEnd = {

                        viewModel.stopDragging()

                        currentState.dragOffset = Offset.Zero
                        currentState.isDagging = false
                    },
                    onDragCancel = {

                        viewModel.stopDragging()

                        currentState.dragOffset = Offset.Zero
                        currentState.isDagging = false
                    }
                )
            }
    ){
        content()
    }
}

@Composable
fun <T> DropItem(
    modifier: Modifier,
    content: @Composable()(BoxScope.(isInBound:Boolean,data:T?)->Unit)
){
    val dragInfo = LocalDragTargetIfo.current
    val dragPosition = dragInfo.dragPosition
    val dragOffset =dragInfo.dragOffset
    var isCurretDropTarget by remember {
        mutableStateOf(false)
    }
    Box(modifier = modifier
        .onGloballyPositioned {
            it.boundsInWindow().let { rect ->
                isCurretDropTarget=rect.contains( dragPosition + dragOffset )
            }
        }
    ){
        val data= if(isCurretDropTarget && !dragInfo.isDagging) dragInfo.dataToDrop as T? else null
        content(isCurretDropTarget, data)
    }

}



internal class DragTrargetInfo {
    var isDagging:Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf <(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
}