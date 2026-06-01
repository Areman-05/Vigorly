package com.example.vigorly.data.catalog

import com.example.vigorly.data.model.WorkoutType
import com.example.vigorly.data.catalog.WorkoutCatalogBuilder.block
import com.example.vigorly.data.catalog.WorkoutCatalogBuilder.detail
import com.example.vigorly.data.catalog.WorkoutCatalogBuilder.exercise

internal fun allCatalogWorkouts() = listOf(
    detail(
        id = "titan_protocol",
        name = "Protocolo Titán",
        description = "Sesión de hipertrofia con movimientos compuestos para ganar fuerza y densidad muscular.",
        type = WorkoutType.STRENGTH,
        durationMinutes = 45,
        targetMuscles = "Espalda y bíceps",
        targetDescription = "Tirón pesado",
        intensity = "Alta",
        estimatedCalories = 450,
        blocks = listOf(
            block("a", "A", "Tirón pesado", listOf(
                exercise("e1", "Remo con barra", "4 series · 8-10 reps"),
                exercise("e2", "Dominadas lastradas", "3 series · 6-8 reps")
            )),
            block("b", "B", "Hipertrofia", listOf(
                exercise("e3", "Curl martillo con mancuernas", "3 series · 12-15 reps")
            ))
        )
    ),
    detail(
        id = "hiit_sprint",
        name = "Intervalos HIIT",
        description = "Entrenamiento por intervalos de alta intensidad para mejorar resistencia y quemar calorías.",
        type = WorkoutType.HIIT,
        durationMinutes = 35,
        targetMuscles = "Cuerpo completo",
        targetDescription = "Cardio y piernas",
        intensity = "Alta",
        estimatedCalories = 420,
        blocks = listOf(
            block("a", "A", "Calentamiento", listOf(
                exercise("h1", "Estiramientos dinámicos", "5 min", "self_improvement"),
                exercise("h2", "Trote suave", "5 min", "directions_run")
            )),
            block("b", "B", "Sprints", listOf(
                exercise("h3", "30 s sprint / 30 s descanso", "8 rondas", "directions_run"),
                exercise("h4", "Caminata de vuelta a la calma", "5 min", "directions_walk")
            ))
        )
    ),
    detail(
        id = "upper_body_power",
        name = "Potencia tren superior",
        description = "Press y tirón pesados para desarrollar fuerza y masa en el tren superior.",
        type = WorkoutType.STRENGTH,
        durationMinutes = 60,
        targetMuscles = "Pecho y tríceps",
        targetDescription = "Empuje",
        intensity = "Alta",
        estimatedCalories = 540,
        blocks = listOf(
            block("a", "A", "Fuerza de empuje", listOf(
                exercise("u1", "Press banca con barra", "4 series · 6-8 reps"),
                exercise("u2", "Press inclinado con mancuernas", "3 series · 10 reps")
            )),
            block("b", "B", "Accesorios", listOf(
                exercise("u3", "Extensiones en polea", "3 series · 12-15 reps")
            ))
        )
    ),
    detail(
        id = "morning_swim",
        name = "Natación matutina",
        description = "Sesión continua en piscina para resistencia cardiovascular y recuperación activa.",
        type = WorkoutType.SWIM,
        durationMinutes = 45,
        targetMuscles = "Cuerpo completo",
        targetDescription = "Resistencia",
        intensity = "Moderada",
        estimatedCalories = 380,
        blocks = listOf(
            block("a", "A", "Serie principal", listOf(
                exercise("s1", "Crol de calentamiento", "400 m suaves", "pool"),
                exercise("s2", "Intervalos de 50 m", "8 × 50 m moderado", "pool"),
                exercise("s3", "Vuelta a la calma", "200 m suaves", "pool")
            ))
        )
    ),
    detail(
        id = "core_blast",
        name = "Core intenso",
        description = "Circuito de core para estabilidad y fuerza del tronco en levantamientos compuestos.",
        type = WorkoutType.STRENGTH,
        durationMinutes = 30,
        targetMuscles = "Core y abdomen",
        targetDescription = "Zona media",
        intensity = "Moderada",
        estimatedCalories = 280,
        blocks = listOf(
            block("a", "A", "Circuito", listOf(
                exercise("c1", "Elevaciones de piernas", "3 series · 12 reps"),
                exercise("c2", "Leñador en polea", "3 series · 15 por lado"),
                exercise("c3", "Plancha", "3 series · 60 s", "self_improvement")
            ))
        )
    ),
    detail(
        id = "leg_day",
        name = "Piernas completas",
        description = "Sesión de piernas con sentadilla y bisagra para cuádriceps, isquios y glúteos.",
        type = WorkoutType.STRENGTH,
        durationMinutes = 55,
        targetMuscles = "Piernas y glúteos",
        targetDescription = "Tren inferior",
        intensity = "Alta",
        estimatedCalories = 520,
        blocks = listOf(
            block("a", "A", "Sentadilla", listOf(
                exercise("l1", "Sentadilla trasera", "5 series · 5 reps"),
                exercise("l2", "Peso muerto rumano", "4 series · 8 reps")
            )),
            block("b", "B", "Accesorios", listOf(
                exercise("l3", "Zancadas caminando", "3 series · 20 pasos", "directions_walk")
            ))
        )
    ),
    detail(
        id = "recovery_yoga",
        name = "Yoga de recuperación",
        description = "Flujo suave de movilidad para recuperarte entre sesiones exigentes.",
        type = WorkoutType.RECOVERY,
        durationMinutes = 45,
        targetMuscles = "Cuerpo completo",
        targetDescription = "Movilidad",
        intensity = "Baja",
        estimatedCalories = 150,
        blocks = listOf(
            block("a", "A", "Secuencia", listOf(
                exercise("y1", "Saludo al sol A", "3 rondas", "self_improvement"),
                exercise("y2", "Postura de la paloma", "2 min por lado", "self_improvement"),
                exercise("y3", "Postura del niño", "3 min", "self_improvement")
            ))
        )
    ),
    detail(
        id = "full_body_functional",
        name = "Funcional cuerpo completo",
        description = "Rutina equilibrada con patrones básicos para fuerza general y coordinación.",
        type = WorkoutType.STRENGTH,
        durationMinutes = 40,
        targetMuscles = "Cuerpo completo",
        targetDescription = "Patrones funcionales",
        intensity = "Moderada",
        estimatedCalories = 360,
        blocks = listOf(
            block("a", "A", "Bloque principal", listOf(
                exercise("f1", "Sentadilla goblet", "4 series · 10 reps"),
                exercise("f2", "Press de hombros", "3 series · 10 reps"),
                exercise("f3", "Remo con mancuerna", "3 series · 12 reps")
            )),
            block("b", "B", "Finisher", listOf(
                exercise("f4", "Farmer carry", "3 × 40 m", "directions_walk")
            ))
        )
    ),
    detail(
        id = "emom_20",
        name = "EMOM 20 minutos",
        description = "Cada minuto al minuto: trabajo breve y descanso corto para acondicionamiento metabólico.",
        type = WorkoutType.HIIT,
        durationMinutes = 20,
        targetMuscles = "Cuerpo completo",
        targetDescription = "Metabólico",
        intensity = "Alta",
        estimatedCalories = 240,
        blocks = listOf(
            block("a", "A", "EMOM", listOf(
                exercise("e1", "Burpees", "Min 1-5", "fitness_center"),
                exercise("e2", "Kettlebell swing", "Min 6-10", "fitness_center"),
                exercise("e3", "Mountain climbers", "Min 11-15", "fitness_center"),
                exercise("e4", "Air squat", "Min 16-20", "fitness_center")
            ))
        )
    ),
    detail(
        id = "power_walk",
        name = "Caminata rápida",
        description = "Cardio de bajo impacto ideal para días activos o recuperación ligera.",
        type = WorkoutType.CARDIO,
        durationMinutes = 35,
        targetMuscles = "Piernas",
        targetDescription = "Zona 2",
        intensity = "Moderada",
        estimatedCalories = 220,
        blocks = listOf(
            block("a", "A", "Sesión", listOf(
                exercise("p1", "Calentamiento", "5 min", "directions_walk"),
                exercise("p2", "Caminata rápida continua", "25 min", "directions_walk"),
                exercise("p3", "Vuelta a la calma", "5 min", "directions_walk")
            ))
        )
    ),
    detail(
        id = "indoor_cycling",
        name = "Ciclismo indoor",
        description = "Intervalos en bicicleta estática para mejorar resistencia cardiovascular.",
        type = WorkoutType.CARDIO,
        durationMinutes = 40,
        targetMuscles = "Piernas",
        targetDescription = "Cardio",
        intensity = "Moderada",
        estimatedCalories = 340,
        blocks = listOf(
            block("a", "A", "Intervalos", listOf(
                exercise("b1", "Calentamiento", "8 min suave", "directions_bike"),
                exercise("b2", "2 min fuerte / 2 min suave", "6 rondas", "directions_bike"),
                exercise("b3", "Enfriamiento", "6 min", "directions_bike")
            ))
        )
    ),
    detail(
        id = "mobility_flow",
        name = "Movilidad activa",
        description = "Secuencia de movilidad articular para mejorar rango y reducir rigidez.",
        type = WorkoutType.RECOVERY,
        durationMinutes = 25,
        targetMuscles = "Caderas y espalda",
        targetDescription = "Movilidad",
        intensity = "Baja",
        estimatedCalories = 90,
        blocks = listOf(
            block("a", "A", "Flujo", listOf(
                exercise("m1", "Círculos de cadera", "2 min", "self_improvement"),
                exercise("m2", "Gato-vaca", "2 min", "self_improvement"),
                exercise("m3", "Estiramiento 90/90", "3 min por lado", "self_improvement")
            ))
        )
    ),
    detail(
        id = "kettlebell_circuit",
        name = "Circuito kettlebell",
        description = "Trabajo con pesa rusa para potencia, core y resistencia en cadena cinética.",
        type = WorkoutType.STRENGTH,
        durationMinutes = 35,
        targetMuscles = "Cuerpo completo",
        targetDescription = "Potencia",
        intensity = "Alta",
        estimatedCalories = 380,
        blocks = listOf(
            block("a", "A", "Circuito × 4", listOf(
                exercise("k1", "Swing", "15 reps"),
                exercise("k2", "Goblet squat", "12 reps"),
                exercise("k3", "Press unilateral", "10 por brazo")
            ))
        )
    ),
    detail(
        id = "calisthenics_base",
        name = "Calistenia base",
        description = "Progresiones con peso corporal para fuerza relativa y control.",
        type = WorkoutType.STRENGTH,
        durationMinutes = 35,
        targetMuscles = "Tren superior y core",
        targetDescription = "Peso corporal",
        intensity = "Moderada",
        estimatedCalories = 300,
        blocks = listOf(
            block("a", "A", "Principal", listOf(
                exercise("ca1", "Flexiones", "4 series · 8-12 reps"),
                exercise("ca2", "Fondos en paralelas", "3 series · 6-10 reps"),
                exercise("ca3", "Plancha lateral", "3 × 45 s", "self_improvement")
            ))
        )
    ),
    detail(
        id = "tabata_16",
        name = "Tabata 16 min",
        description = "20 segundos on / 10 off durante 8 rondas por ejercicio. Máxima intensidad en poco tiempo.",
        type = WorkoutType.HIIT,
        durationMinutes = 16,
        targetMuscles = "Cuerpo completo",
        targetDescription = "Tabata",
        intensity = "Alta",
        estimatedCalories = 200,
        blocks = listOf(
            block("a", "A", "Bloque 1", listOf(
                exercise("t1", "Sentadilla con salto", "8 rondas tabata", "fitness_center")
            )),
            block("b", "B", "Bloque 2", listOf(
                exercise("t2", "Burpees", "8 rondas tabata", "fitness_center")
            ))
        )
    ),
    detail(
        id = "zone2_cardio",
        name = "Cardio zona 2",
        description = "Ritmo conversacional sostenido para base aeróbica y recuperación entre cargas fuertes.",
        type = WorkoutType.CARDIO,
        durationMinutes = 45,
        targetMuscles = "Sistema cardiovascular",
        targetDescription = "Resistencia base",
        intensity = "Moderada",
        estimatedCalories = 320,
        blocks = listOf(
            block("a", "A", "Continuo", listOf(
                exercise("z1", "Trote o bici suave", "40 min", "directions_run"),
                exercise("z2", "Estiramientos", "5 min", "self_improvement")
            ))
        )
    ),
    detail(
        id = "shoulder_focus",
        name = "Hombros y trapecio",
        description = "Volumen controlado para deltoides y estabilidad de hombro.",
        type = WorkoutType.STRENGTH,
        durationMinutes = 40,
        targetMuscles = "Hombros",
        targetDescription = "Deltoides",
        intensity = "Moderada",
        estimatedCalories = 290,
        blocks = listOf(
            block("a", "A", "Hombros", listOf(
                exercise("sh1", "Press militar", "4 series · 8 reps"),
                exercise("sh2", "Elevaciones laterales", "3 series · 15 reps"),
                exercise("sh3", "Pájaros", "3 series · 15 reps")
            ))
        )
    ),
    detail(
        id = "glute_ham",
        name = "Glúteos e isquios",
        description = "Enfoque en cadena posterior para potencia y estética del tren inferior.",
        type = WorkoutType.STRENGTH,
        durationMinutes = 45,
        targetMuscles = "Glúteos e isquios",
        targetDescription = "Posterior",
        intensity = "Alta",
        estimatedCalories = 400,
        blocks = listOf(
            block("a", "A", "Principal", listOf(
                exercise("g1", "Hip thrust", "4 series · 10 reps"),
                exercise("g2", "Peso muerto rumano", "4 series · 8 reps"),
                exercise("g3", "Curl femoral", "3 series · 12 reps")
            ))
        )
    ),
    detail(
        id = "vinyasa_yoga",
        name = "Vinyasa fluido",
        description = "Yoga dinámico que enlaza respiración y movimiento para flexibilidad y calma.",
        type = WorkoutType.RECOVERY,
        durationMinutes = 50,
        targetMuscles = "Cuerpo completo",
        targetDescription = "Flexibilidad",
        intensity = "Baja",
        estimatedCalories = 180,
        blocks = listOf(
            block("a", "A", "Flujo", listOf(
                exercise("v1", "Secuencia saludo al sol", "10 min", "self_improvement"),
                exercise("v2", "Guerrero I y II", "8 min", "self_improvement"),
                exercise("v3", "Torsión y savasana", "8 min", "self_improvement")
            ))
        )
    ),
    detail(
        id = "swim_technique",
        name = "Natación técnica",
        description = "Drills de crol para mejorar eficiencia y resistencia en el agua.",
        type = WorkoutType.SWIM,
        durationMinutes = 40,
        targetMuscles = "Cuerpo completo",
        targetDescription = "Técnica",
        intensity = "Moderada",
        estimatedCalories = 350,
        blocks = listOf(
            block("a", "A", "Drills", listOf(
                exercise("st1", "Crol con tabla", "8 × 50 m", "pool"),
                exercise("st2", "Un brazo", "6 × 25 m", "pool"),
                exercise("st3", "Nado continuo", "400 m", "pool")
            ))
        )
    ),
    detail(
        id = "push_pull",
        name = "Empuje y tirón",
        description = "Combinación clásica de pecho/espalda en la misma sesión para equilibrio.",
        type = WorkoutType.STRENGTH,
        durationMinutes = 50,
        targetMuscles = "Pecho y espalda",
        targetDescription = "Balance",
        intensity = "Alta",
        estimatedCalories = 430,
        blocks = listOf(
            block("a", "A", "Empuje", listOf(
                exercise("pp1", "Press banca", "4 × 8"),
                exercise("pp2", "Fondos", "3 × 10")
            )),
            block("b", "B", "Tirón", listOf(
                exercise("pp3", "Remo con barra", "4 × 8"),
                exercise("pp4", "Jalón al pecho", "3 × 12")
            ))
        )
    ),
    detail(
        id = "stair_climber",
        name = "Escaleras cardio",
        description = "Subidas intensas para piernas y capacidad aeróbica sin impacto de carrera.",
        type = WorkoutType.CARDIO,
        durationMinutes = 25,
        targetMuscles = "Piernas y glúteos",
        targetDescription = "Cardio",
        intensity = "Alta",
        estimatedCalories = 280,
        blocks = listOf(
            block("a", "A", "Intervalos", listOf(
                exercise("sc1", "2 min subida / 1 min descanso", "6 rondas", "stairs"),
                exercise("sc2", "Caminata recuperación", "5 min", "directions_walk")
            ))
        )
    ),
    detail(
        id = "stretch_cooldown",
        name = "Estiramientos post-entreno",
        description = "Rutina de estiramientos estáticos para bajar pulsaciones y mejorar recuperación.",
        type = WorkoutType.RECOVERY,
        durationMinutes = 20,
        targetMuscles = "Cuerpo completo",
        targetDescription = "Flexibilidad",
        intensity = "Baja",
        estimatedCalories = 60,
        blocks = listOf(
            block("a", "A", "Estiramientos", listOf(
                exercise("cd1", "Isquios y cuádriceps", "5 min", "self_improvement"),
                exercise("cd2", "Pecho y hombros", "5 min", "self_improvement"),
                exercise("cd3", "Respiración diafragmática", "5 min", "self_improvement")
            ))
        )
    ),
    detail(
        id = "battle_rope_hiit",
        name = "Cuerdas HIIT",
        description = "Ondas y slams con cuerda para potencia anaeróbica y core.",
        type = WorkoutType.HIIT,
        durationMinutes = 22,
        targetMuscles = "Hombros y core",
        targetDescription = "Potencia",
        intensity = "Alta",
        estimatedCalories = 260,
        blocks = listOf(
            block("a", "A", "Rondas", listOf(
                exercise("br1", "Ondas alternas", "30 s on / 30 s off × 8", "fitness_center"),
                exercise("br2", "Slams", "30 s on / 30 s off × 6", "fitness_center")
            ))
        )
    )
)
