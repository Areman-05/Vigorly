package com.example.vigorly

import com.example.vigorly.data.local.WorkoutPlaylistCodec
import com.example.vigorly.data.model.WorkoutPlaylist
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutPlaylistCodecTest {

    @Test
    fun sanitizeUserLists_keepsUserCreatedLista() {
        val listaUno = WorkoutPlaylist(
            id = "custom_1",
            name = "Lista 1",
            workoutIds = listOf("a", "b", "c")
        )
        val keep = WorkoutPlaylist(
            id = "custom_2",
            name = "Piernas",
            workoutIds = listOf("a")
        )
        val cleaned = WorkoutPlaylistCodec.sanitizeUserLists(listOf(listaUno, keep))
        assertEquals(listOf(listaUno, keep), cleaned)
    }

    @Test
    fun sanitizeUserLists_dropsAutoPlaylists() {
        val auto = WorkoutPlaylist(
            id = "auto_strength",
            name = "Fuerza",
            workoutIds = listOf("a"),
            isAuto = true
        )
        assertTrue(WorkoutPlaylistCodec.sanitizeUserLists(listOf(auto)).isEmpty())
    }

    @Test
    fun uniqueName_rejectsDuplicate() {
        val existing = listOf(
            WorkoutPlaylist(id = "a", name = "Piernas", workoutIds = emptyList())
        )
        assertEquals("Piernas 2", WorkoutPlaylistCodec.uniqueName("Piernas", existing))
        assertEquals("Piernas", WorkoutPlaylistCodec.uniqueName("Piernas", existing, excludeId = "a"))
        assertTrue(WorkoutPlaylistCodec.isNameTaken("piernas", existing))
        assertTrue(!WorkoutPlaylistCodec.isNameTaken("Piernas", existing, excludeId = "a"))
    }
}
