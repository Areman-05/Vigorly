package com.example.vigorly.ui.setup

/**
 * Flags temporales de flujo. Revertir cuando el usuario confirme el orden final.
 */
object SetupDevFlags {
    /** Tras login, ir siempre al wizard (ignora onboarding completado). */
    const val FORCE_SETUP_AFTER_LOGIN = false

    /** Tras la splash, ir siempre al wizard (ignora login y onboarding). */
    const val FORCE_SPLASH_TO_SETUP = false

    /** Tras la splash, ir siempre a login (p. ej. mientras se retoca la home). */
    const val FORCE_SPLASH_TO_LOGIN = true
}
