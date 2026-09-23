package com.jcjiron.androidsample.presentation.kotlinbasics

private data class Hero(val name: String, val species: String)

private data class Profile(var name: String = "", var age: Int = 0)

/**
 * Scope functions: let, run, with, apply, also (+ takeIf / takeUnless).
 * Todas ejecutan un bloque sobre un objeto; cambian en cómo lo nombras (it / this) y qué regresan.
 */
object ScopeFunctionExamples {

    const val SUMMARY = """Función │ Objeto │ Regresa
─────────┼────────┼──────────────────
let      │ it     │ resultado lambda
run      │ this   │ resultado lambda
with     │ this   │ resultado lambda
apply    │ this   │ el mismo objeto
also     │ it     │ el mismo objeto"""

    val all: List<KotlinExample> by lazy {
        listOf(
            KotlinExample(
                title = "let",
                description = "Objeto como `it`, regresa el resultado del bloque. Ideal con ?. para nulls.",
                code = """
                    val nickname: String? = "Pickle Rick"
                    val length = nickname?.let { it.length } ?: 0
                """.trimIndent(),
                result = run {
                    val nickname: String? = "Pickle Rick"
                    val length = nickname?.let { it.length } ?: 0
                    "length=$length"
                },
            ),
            KotlinExample(
                title = "run",
                description = "Objeto como `this`, regresa el resultado. Para configurar y calcular algo.",
                code = """
                    val hero = Hero("Rick", "Human")
                    val summary = hero.run { "${'$'}name es ${'$'}species" }
                """.trimIndent(),
                result = run {
                    val hero = Hero("Rick", "Human")
                    hero.run { "$name es $species" }
                },
            ),
            KotlinExample(
                title = "with",
                description = "Como run, pero el objeto va como parámetro. \"Con este objeto, haz…\".",
                code = """
                    val hero = Hero("Birdperson", "Bird-Person")
                    with(hero) {
                        "${'$'}{name.uppercase()} (${'$'}species)"
                    }
                """.trimIndent(),
                result = run {
                    val hero = Hero("Birdperson", "Bird-Person")
                    with(hero) {
                        "${name.uppercase()} ($species)"
                    }
                },
            ),
            KotlinExample(
                title = "apply",
                description = "Objeto como `this`, regresa el MISMO objeto. Perfecto para inicializar.",
                code = """
                    val profile = Profile().apply {
                        name = "Morty"
                        age = 14
                    }
                """.trimIndent(),
                result = run {
                    val profile = Profile().apply {
                        name = "Morty"
                        age = 14
                    }
                    profile.toString()
                },
            ),
            KotlinExample(
                title = "also",
                description = "Objeto como `it`, regresa el MISMO objeto. Para efectos secundarios (logs).",
                code = """
                    val log = mutableListOf<String>()
                    val crew = mutableListOf("Rick")
                        .also { log.add("Tamaño antes: ${'$'}{it.size}") }
                    crew.add("Morty")
                """.trimIndent(),
                result = run {
                    val log = mutableListOf<String>()
                    val crew = mutableListOf("Rick")
                        .also { log.add("Tamaño antes: ${it.size}") }
                    crew.add("Morty")
                    "crew=$crew, log=$log"
                },
            ),
            KotlinExample(
                title = "takeIf y takeUnless",
                description = "Regresan el objeto si se cumple (o no) la condición; si no, null.",
                code = """
                    val age = 14
                    age.takeIf { it >= 18 }      // null
                    age.takeUnless { it >= 18 }  // 14
                """.trimIndent(),
                result = run {
                    val age = 14
                    "takeIf=${age.takeIf { it >= 18 }}, takeUnless=${age.takeUnless { it >= 18 }}"
                },
            ),
            KotlinExample(
                title = "Resumen",
                description = "¿Cómo se llama el objeto dentro del bloque y qué regresa?",
                code = SUMMARY,
                result = "Tip: apply/also para configurar y regresar el objeto; let/run/with para calcular algo.",
            ),
        )
    }
}
