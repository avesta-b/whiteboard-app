/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.helpers

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

// https://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other
fun overlap(firstRectangleCoordinate: Offset,
            firstRectangleSize: Size,
            secondRectangleCoordinate: Offset,
            secondRectangleSize: Size): Boolean {
    val firstX1 = firstRectangleCoordinate.x
    val firstX2 = firstRectangleCoordinate.x + firstRectangleSize.width
    val firstY1 = firstRectangleCoordinate.y
    val firstY2 = firstRectangleCoordinate.y + firstRectangleSize.height
    val secondX1 = secondRectangleCoordinate.x
    val secondX2 = secondRectangleCoordinate.x + secondRectangleSize.width
    val secondY1 = secondRectangleCoordinate.y
    val secondY2 = secondRectangleCoordinate.y + secondRectangleSize.height
    return firstX1 < secondX2 && firstX2 > secondX1 && firstY1 < secondY2 && firstY2 > secondY1
}