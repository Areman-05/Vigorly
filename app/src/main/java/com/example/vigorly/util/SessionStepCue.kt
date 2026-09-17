package com.example.vigorly.util

import java.util.Locale

/**
 * Descripción técnica corta del movimiento: qué hacer, con qué parte y qué controlar.
 */
object SessionStepCue {

    fun forStep(
        name: String,
        detailLabel: String,
        isWarmup: Boolean,
        locale: Locale = Locale.getDefault()
    ): String {
        val es = locale.language.lowercase(Locale.ROOT).let {
            it == "es" || it == "ca"
        }
        val key = name.lowercase(Locale.ROOT)
        val specific = matchCue(key, es)
        if (specific != null) return specific

        val dose = detailLabel.trim()
        return if (es) {
            buildString {
                append("Ejecuta el patrón completo del ejercicio")
                if (dose.isNotEmpty()) append(" ($dose)")
                append(". ")
                append("Mantén la zona media estable y la columna neutra. ")
                append("Mueve con control en la fase excéntrica. ")
                append(if (isWarmup) "Prioriza rango y activación, sin buscar fatiga." else "Termina cada serie con técnica limpia.")
            }
        } else {
            buildString {
                append("Run the full movement pattern")
                if (dose.isNotEmpty()) append(" ($dose)")
                append(". ")
                append("Keep the midsection braced and the spine neutral. ")
                append("Control the eccentric. ")
                append(if (isWarmup) "Chase range and activation, not fatigue." else "Finish each set with clean form.")
            }
        }
    }

    private fun matchCue(key: String, es: Boolean): String? = when {
        contains(key, "sentadilla", "squat") -> if (es)
            "Flexiona cadera y rodillas como si te sentaras, peso en el centro del pie. " +
                "Rodillas siguen la línea de los pies; pecho erguido. " +
                "Trabaja cuádriceps, glúteos e isquios. " +
                "Sube empujando el suelo sin perder la tensión del core."
        else
            "Sit hips and knees back with weight mid-foot. " +
                "Knees track over the toes; chest tall. " +
                "Loads quads, glutes and hamstrings. " +
                "Drive up through the floor while bracing the core."

        contains(key, "peso muerto", "deadlift") -> if (es)
            "Bisagra de cadera con espalda neutra; barra o peso cerca de las piernas. " +
                "Empuja el suelo con los pies y extiende cadera al final. " +
                "Trabaja cadena posterior: isquios, glúteos y erectores. " +
                "No redondees la lumbar ni tirees solo de la espalda."
        else
            "Hip hinge with a neutral spine; keep the load close to the legs. " +
                "Drive the floor away and finish by extending the hips. " +
                "Targets the posterior chain: hamstrings, glutes, erectors. " +
                "Do not round the low back or pull with the spine alone."

        contains(key, "zancada", "lunge") -> if (es)
            "Da un paso largo y baja la rodilla trasera hacia el suelo. " +
                "Torso vertical; rodilla delantera sobre el mediopié. " +
                "Trabaja cuádriceps y glúteo de la pierna delantera. " +
                "Empuja hacia atrás o arriba sin que la rodilla se desvíe hacia dentro."
        else
            "Step long and lower the back knee toward the floor. " +
                "Torso upright; front knee over mid-foot. " +
                "Loads the front-leg quad and glute. " +
                "Drive back or up without the knee collapsing inward."

        contains(key, "press banca", "bench") -> if (es)
            "Acuéstate con escápulas retraídas y pies firmes. " +
                "Baja la carga al pecho con control y empuja sin rebotar. " +
                "Trabaja pectoral, deltoides anterior y tríceps. " +
                "Mantén muñecas neutras y codos en un ángulo estable."
        else
            "Lie with scapulae set and feet planted. " +
                "Lower to the chest under control and press without bounce. " +
                "Loads chest, front delts and triceps. " +
                "Keep wrists neutral and elbows in a stable path."

        contains(key, "press militar", "overhead press", "press de hombro") -> if (es)
            "Empuja la carga en vertical por encima de la cabeza. " +
                "Core y glúteos firmes para no arquear la lumbar. " +
                "Trabaja deltoides y tríceps; estabiliza la cintura escapular. " +
                "Baja con control hasta la altura de los hombros."
        else
            "Press the load vertically overhead. " +
                "Brace core and glutes so the low back does not overarch. " +
                "Loads delts and triceps; stabilize the shoulder girdle. " +
                "Lower with control to about shoulder height."

        contains(key, "push-up", "flexión", "flexion de pecho", "flexiones") -> if (es)
            "Cuerpo en línea desde talones a cabeza; manos bajo los hombros. " +
                "Baja el pecho hacia el suelo y empuja hasta extensión de codos. " +
                "Trabaja pectoral, deltoides anterior y tríceps. " +
                "No dejes caer la cadera ni subas el culo."
        else
            "Hold a straight line from heels to head; hands under shoulders. " +
                "Lower the chest and press to elbow extension. " +
                "Loads chest, front delts and triceps. " +
                "Do not sag the hips or pike the butt up."

        contains(key, "press") -> if (es)
            "Empuja en la trayectoria del ejercicio con hombros estables. " +
                "Fase excéntrica controlada; no bloquees de golpe. " +
                "Trabaja la musculatura empujadora del tren superior. " +
                "Mantén costillas abajo y core activo."
        else
            "Press along the intended path with stable shoulders. " +
                "Control the eccentric; do not slam the lockout. " +
                "Loads the upper-body pushing muscles. " +
                "Keep ribs down and the core braced."

        contains(key, "remo", "row") -> if (es)
            "Tira de la carga hacia la cadera o las costillas bajas. " +
                "Codos cerca del cuerpo; pecho abierto y escapulas retraídas. " +
                "Trabaja dorsal, romboides y bíceps. " +
                "No uses impulso de lumbar; inicia el tirón con la espalda."
        else
            "Pull the load toward the hip or lower ribs. " +
                "Elbows close; chest open and scapulae retracting. " +
                "Loads lats, rhomboids and biceps. " +
                "Do not heave with the low back; start the pull from the upper back."

        contains(key, "dominada", "pull-up", "jalón", "jalon", "chin-up") -> if (es)
            "Cuelga activo: hombros lejos de las orejas. " +
                "Tira el pecho hacia la barra hasta pasar la barbilla. " +
                "Trabaja dorsal, bíceps y estabilizadores escapulares. " +
                "Baja con control hasta casi extensión completa de brazos."
        else
            "Hang active: shoulders away from the ears. " +
                "Pull the chest to the bar until the chin clears. " +
                "Loads lats, biceps and scapular stabilizers. " +
                "Lower with control nearly to full arm extension."

        contains(key, "plancha", "plank") -> if (es)
            "Apoya antebrazos o manos y forma una línea recta. " +
                "Aprieta abdomen, glúteos y cuádriceps. " +
                "Trabaja core profundo y estabilización lumbar. " +
                "No subas la cadera ni dejes caer el abdomen."
        else
            "Set forearms or hands and make a straight body line. " +
                "Brace abs, glutes and quads. " +
                "Loads deep core and lumbar stability. " +
                "Do not pike the hips or let the belly sag."

        contains(key, "burpee") -> if (es)
            "Baja a apoyo, lleva el pecho cerca del suelo y vuelve a pie. " +
                "Termina con un salto controlado y aterrizaje suave. " +
                "Trabaja cuerpo completo: piernas, pecho y cardio. " +
                "Mantén el ritmo limpio; no sacrifiques la posición del tronco."
        else
            "Drop to the floor, chest near the ground, then stand. " +
                "Finish with a controlled jump and soft landing. " +
                "Full-body demand: legs, chest and conditioning. " +
                "Keep clean rhythm; do not lose trunk position."

        contains(key, "mountain", "escalador") -> if (es)
            "En posición de plancha, lleva rodillas al pecho de forma alternada. " +
                "Cadera baja y estable; manos fijas bajo los hombros. " +
                "Trabaja core, flexores de cadera y hombros estabilizadores. " +
                "Evita balancear el tronco de lado a lado."
        else
            "From a plank, drive knees to the chest alternately. " +
                "Hips low and still; hands fixed under the shoulders. " +
                "Loads core, hip flexors and shoulder stability. " +
                "Avoid rocking the torso side to side."

        contains(key, "jumping jack", "salto de tijera", "tijera") -> if (es)
            "Salta abriendo piernas y elevando brazos a la vez. " +
                "Aterriza suave con rodillas semiflexionadas. " +
                "Trabaja coordinación y activación de tren inferior y hombros. " +
                "Mantén el torso erguido y la respiración continua."
        else
            "Jump the feet out while raising the arms. " +
                "Land soft with slightly bent knees. " +
                "Builds coordination and lower-body/shoulder activation. " +
                "Stay tall and keep breathing continuously."

        contains(key, "carrera", "running", "trote", "jog", "correr") -> if (es)
            "Cadencia constante; pisada bajo la cadera, no delante del cuerpo. " +
                "Tronco ligeramente inclinado; brazos en balanceo natural. " +
                "Trabaja capacidad cardiovascular y tren inferior. " +
                "No endurezcas hombros ni eleves la tensión del cuello."
        else
            "Steady cadence; land under the hips, not out in front. " +
                "Slight forward lean; natural arm swing. " +
                "Builds cardio capacity and lower-body endurance. " +
                "Do not shrug the shoulders or tense the neck."

        contains(key, "marcha") -> if (es)
            "Camina o marca el paso con brazos activos y torso erguido. " +
                "Contacto suave del pie; eleva ligeramente las rodillas. " +
                "Activa circulación y movilidad de cadera y tobillo. " +
                "Mantén respiración nasal o mixta, sin forzar."
        else
            "Walk or march with active arms and an upright torso. " +
                "Soft foot contact; lift the knees slightly. " +
                "Raises circulation and hip/ankle mobility. " +
                "Keep easy nasal or mixed breathing."

        contains(key, "rotación", "rotacion", "círculo", "circulo", "circle") -> if (es)
            "Gira la articulación implicada en todo su rango útil. " +
                "Movimiento lento y continuo, sin rebotar al final. " +
                "Prepara cápsula articular y musculatura estabilizadora. " +
                "Detente si aparece dolor agudo; busca tensión controlada."
        else
            "Rotate the involved joint through a useful range. " +
                "Slow continuous motion; no end-range bouncing. " +
                "Preps the joint capsule and stabilizing muscles. " +
                "Stop if sharp pain appears; seek controlled tension only."

        contains(key, "movilidad", "mobility") -> if (es)
            "Explora el rango articular con control y respiración estable. " +
                "Mantén la posición en el límite cómodo 1–2 segundos. " +
                "Mejora movilidad de la zona objetivo sin forzar. " +
                "No compenses con lumbar u hombros."
        else
            "Explore joint range with control and steady breathing. " +
                "Hold the comfortable end range for 1–2 seconds. " +
                "Improves mobility of the target area without forcing. " +
                "Do not compensate with the low back or shoulders."

        contains(key, "estiramiento", "stretch") -> if (es)
            "Alarga el músculo hasta una tensión clara pero tolerable. " +
                "Mantén la postura; exhala para profundizar un poco. " +
                "Reduce tono muscular y recupera longitud. " +
                "Evita rebotes y dolor agudo."
        else
            "Lengthen the muscle to a clear but tolerable tension. " +
                "Hold the posture; exhale to ease a little deeper. " +
                "Reduces tone and restores length. " +
                "No bouncing and no sharp pain."

        contains(key, "puente", "bridge", "hip thrust", "empuje de cadera") -> if (es)
            "Apoya espalda o pies y empuja la cadera hacia arriba. " +
                "Aprieta glúteos en la extensión; costillas abajo. " +
                "Trabaja glúteo mayor e isquios. " +
                "No hiperextiendas la lumbar al final."
        else
            "Brace with back or feet and drive the hips up. " +
                "Squeeze the glutes at the top; ribs down. " +
                "Loads glute max and hamstrings. " +
                "Do not hyperextend the low back at lockout."

        contains(key, "curl", "bíceps", "biceps") -> if (es)
            "Codos fijos junto al torso; flexiona el codo hasta contraer el bíceps. " +
                "Baja lento sin balancear el tronco. " +
                "Trabaja bíceps braquial y braquial. " +
                "No uses impulso de hombro o espalda."
        else
            "Pin the elbows to the torso; flex until the biceps contract. " +
                "Lower slowly without swinging the trunk. " +
                "Loads the biceps and brachialis. " +
                "Do not cheat with shoulder or back momentum."

        contains(key, "tríceps", "triceps", "fondos", "dip") -> if (es)
            "Extiende el codo manteniendo hombros bajos y estables. " +
                "Codos hacia atrás o ligeramente hacia dentro, no abiertos en exceso. " +
                "Trabaja tríceps; estabiliza pecho y hombros. " +
                "No bloquees la articulación de golpe."
        else
            "Extend the elbow with shoulders low and stable. " +
                "Elbows track back or slightly in, not flared wide. " +
                "Loads the triceps; chest and shoulders stabilize. " +
                "Do not slam into lockout."

        contains(key, "abdominal", "crunch", "sit-up", "core") -> if (es)
            "Flexiona el tronco acercando costillas al pubis. " +
                "No tires del cuello con las manos; mirada al techo/frente. " +
                "Trabaja recto abdominal y estabilizadores. " +
                "Exhala al contraer; controla la bajada."
        else
            "Curl the ribs toward the pelvis. " +
                "Do not yank the neck with the hands; eyes soft forward/up. " +
                "Loads the rectus abdominis and stabilizers. " +
                "Exhale on the crunch; control the return."

        contains(key, "respiración", "respiracion", "breath") -> if (es)
            "Inhala por la nariz expandiendo abdomen y costillas. " +
                "Exhala lento por la boca o nariz vaciando con control. " +
                "Regula el sistema nervioso y la presión intraabdominal. " +
                "Hombros relajados; no eleves el pecho de forma forzada."
        else
            "Inhale through the nose expanding belly and ribs. " +
                "Exhale slowly through mouth or nose with control. " +
                "Regulates the nervous system and intra-abdominal pressure. " +
                "Keep shoulders soft; do not force a high chest."

        contains(key, "natación", "natacion", "swim", "brazada") -> if (es)
            "Brazada larga; entra la mano alineada con el hombro. " +
                "Rota el tronco y mantén la cabeza relajada. " +
                "Trabaja dorsal, hombros y capacidad aeróbica. " +
                "No fuerces el ritmo si se rompe la técnica."
        else
            "Long strokes; hand entry in line with the shoulder. " +
                "Rotate the torso and keep the neck soft. " +
                "Loads lats, shoulders and aerobic capacity. " +
                "Do not force pace if technique breaks down."

        contains(key, "kettlebell", "swing") -> if (es)
            "Bisagra de cadera; el peso pasa entre las piernas. " +
                "Extiende cadera de forma explosiva; brazos solo guían. " +
                "Trabaja glúteos, isquios y core. " +
                "No eleves con los hombros ni arquees la lumbar."
        else
            "Hip hinge; the bell passes between the legs. " +
                "Snap the hips; arms only guide the path. " +
                "Loads glutes, hamstrings and core. " +
                "Do not lift with the shoulders or overarch the low back."

        contains(key, "paso", "step-up", "step up") -> if (es)
            "Sube al cajón empujando con la pierna de apoyo. " +
                "Torso erguido; rodilla alineada con el pie. " +
                "Trabaja cuádriceps y glúteo unilateral. " +
                "Controla la bajada; no te dejes caer."
        else
            "Step onto the box by driving through the working leg. " +
                "Torso tall; knee tracks over the foot. " +
                "Loads unilateral quad and glute. " +
                "Control the descent; do not drop."

        else -> null
    }

    private fun contains(key: String, vararg needles: String): Boolean =
        needles.any { key.contains(it) }
}
