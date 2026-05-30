package com.example.vigorly.ui.setup

/**
 * Flags temporales de desarrollo del wizard.
 * Revertir todo a false cuando el usuario pida restaurar el flujo normal.
 */
object SetupDevFlags {
    /** Tras login, ir siempre al wizard (ignora onboarding completado). */
    const val FORCE_SETUP_AFTER_LOGIN = true

    /** Tras la splash, ir siempre al wizard (ignora login y onboarding). */
    const val FORCE_SPLASH_TO_SETUP = true
}
