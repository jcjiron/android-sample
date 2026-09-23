package com.jcjiron.androidsample.presentation.kotlinbasics

private const val MAX_EPISODES = 51

private class Portal {
    lateinit var destination: String

    fun isReady() = ::destination.isInitialized
}

private fun fail(message: String): Nothing = throw IllegalStateException(message)

/** Todos los tipos de variables y declaraciones básicas de Kotlin, con su resultado real. */
object VariableExamples {

    val all: List<KotlinExample> by lazy {
        listOf(
            KotlinExample(
                title = "val — solo lectura",
                description = "Se asigna una vez y ya no cambia. Úsala por default.",
                code = """
                    val name = "Rick"
                    // name = "Morty"  ← no compila
                """.trimIndent(),
                result = run {
                    val name = "Rick"
                    name
                },
            ),
            KotlinExample(
                title = "var — mutable",
                description = "Se puede reasignar. Úsala solo cuando de verdad cambie.",
                code = """
                    var episodes = 10
                    episodes += 1
                """.trimIndent(),
                result = run {
                    var episodes = 10
                    episodes += 1
                    episodes.toString()
                },
            ),
            KotlinExample(
                title = "const val — constante de compilación",
                description = "Solo top-level, en object o companion object, y solo primitivos o String.",
                code = """
                    private const val MAX_EPISODES = 51
                """.trimIndent(),
                result = MAX_EPISODES.toString(),
            ),
            KotlinExample(
                title = "Números: Byte, Short, Int, Long, Float, Double",
                description = "Enteros de 8, 16, 32 y 64 bits; decimales de 32 y 64 bits.",
                code = """
                    val byte: Byte = 127
                    val short: Short = 32_767
                    val int: Int = 2_147_483_647
                    val long: Long = 9_000_000_000L
                    val float: Float = 3.14f
                    val double: Double = 3.141592653589793
                """.trimIndent(),
                result = run {
                    val byte: Byte = 127
                    val short: Short = 32_767
                    val int: Int = 2_147_483_647
                    val long: Long = 9_000_000_000L
                    val float: Float = 3.14f
                    val double: Double = 3.141592653589793
                    "byte=$byte\nshort=$short\nint=$int\nlong=$long\nfloat=$float\ndouble=$double"
                },
            ),
            KotlinExample(
                title = "Boolean y Char",
                description = "Boolean es true/false. Char es un solo carácter con comillas simples.",
                code = """
                    val isAlive: Boolean = true
                    val initial: Char = 'R'
                    initial.code  // valor Unicode
                """.trimIndent(),
                result = run {
                    val isAlive: Boolean = true
                    val initial: Char = 'R'
                    "isAlive=$isAlive, initial=$initial, code=${initial.code}"
                },
            ),
            KotlinExample(
                title = "String y string templates",
                description = "Con \$ metes variables y con \${} expresiones dentro del texto.",
                code = """
                    val name = "Morty"
                    "Hola, ${'$'}name. Tienes ${'$'}{name.length} letras"
                """.trimIndent(),
                result = run {
                    val name = "Morty"
                    "Hola, $name. Tienes ${name.length} letras"
                },
            ),
            KotlinExample(
                title = "Raw strings (triple comilla)",
                description = "Texto multilínea sin escapar nada; trimIndent() quita la sangría.",
                code = "val poem = \"\"\"\n    Wubba\n    lubba\n    dub dub\n\"\"\".trimIndent()",
                result = """
                    Wubba
                    lubba
                    dub dub
                """.trimIndent(),
            ),
            KotlinExample(
                title = "Nullables: ?, ?. y ?: (Elvis)",
                description = "Un tipo con ? acepta null. ?. solo llama si no es null; ?: da un valor por default.",
                code = """
                    var nickname: String? = null
                    nickname?.length          // null
                    nickname ?: "Sin apodo"   // "Sin apodo"
                """.trimIndent(),
                result = run {
                    val nickname: String? = null
                    "length=${nickname?.length}, elvis=${nickname ?: "Sin apodo"}"
                },
            ),
            KotlinExample(
                title = "!! — \"confía en mí, no es null\"",
                description = "Si resulta que sí es null, truena con NullPointerException. Evítalo.",
                code = """
                    val nickname: String? = null
                    nickname!!.length
                """.trimIndent(),
                result = run {
                    val nickname: String? = null
                    runCatching { nickname!!.length }
                        .fold({ it.toString() }, { it::class.simpleName.orEmpty() })
                },
            ),
            KotlinExample(
                title = "lateinit var",
                description = "Una var no-null que se inicializa después (ej. en onCreate o en tests).",
                code = """
                    class Portal {
                        lateinit var destination: String
                        fun isReady() = ::destination.isInitialized
                    }
                    val portal = Portal()
                    portal.isReady()              // false
                    portal.destination = "C-137"
                    portal.isReady()              // true
                """.trimIndent(),
                result = run {
                    val portal = Portal()
                    val before = portal.isReady()
                    portal.destination = "C-137"
                    "antes=$before, después=${portal.isReady()}"
                },
            ),
            KotlinExample(
                title = "by lazy",
                description = "Se calcula la primera vez que se usa y se guarda el resultado.",
                code = """
                    var calls = 0
                    val universe by lazy { calls++; "C-137" }
                    universe
                    universe
                    // calls == 1
                """.trimIndent(),
                result = run {
                    var calls = 0
                    val universe by lazy {
                        calls++
                        "C-137"
                    }
                    universe
                    "universe=$universe, calls=$calls"
                },
            ),
            KotlinExample(
                title = "Inferencia de tipos",
                description = "No hace falta escribir el tipo si Kotlin lo puede deducir.",
                code = """
                    val count = 42        // Int
                    val price = 9.99      // Double
                    val big = 42L         // Long
                """.trimIndent(),
                result = run {
                    val count = 42
                    val price = 9.99
                    val big = 42L
                    listOf<Any>(count, price, big).joinToString { it::class.simpleName.orEmpty() }
                },
            ),
            KotlinExample(
                title = "Any, Unit y Nothing",
                description = "Any es el padre de todo. Unit = \"no regresa nada útil\". Nothing = nunca regresa.",
                code = """
                    val anything: Any = 3
                    fun log(): Unit = println("hola")
                    fun fail(msg: String): Nothing = throw IllegalStateException(msg)
                """.trimIndent(),
                result = run {
                    val anything: Any = 3
                    val error = runCatching { fail("boom") }.exceptionOrNull()
                    "anything=$anything, fail -> ${error?.let { it::class.simpleName }}"
                },
            ),
            KotlinExample(
                title = "is, as y as? (smart cast)",
                description = "is revisa el tipo y hace cast automático. as? regresa null si no se puede.",
                code = """
                    val value: Any = "Portal gun"
                    if (value is String) value.length   // smart cast
                    value as? Int                       // null
                """.trimIndent(),
                result = run {
                    val value: Any = "Portal gun"
                    val length = if (value is String) value.length else 0
                    "length=$length, asInt=${value as? Int}"
                },
            ),
            KotlinExample(
                title = "Array",
                description = "Tamaño fijo. Existen también IntArray, DoubleArray, etc.",
                code = """
                    val seasons = arrayOf(1, 2, 3)
                    seasons[0] = 10
                """.trimIndent(),
                result = run {
                    val seasons = arrayOf(1, 2, 3)
                    seasons[0] = 10
                    seasons.contentToString()
                },
            ),
            KotlinExample(
                title = "List y MutableList",
                description = "List es de solo lectura; MutableList permite agregar y quitar.",
                code = """
                    val family = listOf("Rick", "Morty")
                    val crew = mutableListOf("Rick")
                    crew.add("Summer")
                """.trimIndent(),
                result = run {
                    val family = listOf("Rick", "Morty")
                    val crew = mutableListOf("Rick")
                    crew.add("Summer")
                    "family=$family, crew=$crew"
                },
            ),
            KotlinExample(
                title = "Set",
                description = "Colección sin repetidos.",
                code = """
                    val species = setOf("Human", "Alien", "Human")
                """.trimIndent(),
                result = setOf("Human", "Alien", "Human").toString(),
            ),
            KotlinExample(
                title = "Map",
                description = "Pares llave → valor. Con mutableMapOf se puede modificar.",
                code = """
                    val ages = mutableMapOf("Rick" to 70, "Morty" to 14)
                    ages["Summer"] = 17
                    ages["Rick"]
                """.trimIndent(),
                result = run {
                    val ages = mutableMapOf("Rick" to 70, "Morty" to 14)
                    ages["Summer"] = 17
                    "ages=$ages, Rick=${ages["Rick"]}"
                },
            ),
            KotlinExample(
                title = "Pair, Triple y destructuring",
                description = "Agrupan 2 o 3 valores sin crear una clase; se pueden desarmar.",
                code = """
                    val (name, age) = Pair("Rick", 70)
                    val trio = Triple("Rick", "Morty", "Summer")
                """.trimIndent(),
                result = run {
                    val (name, age) = Pair("Rick", 70)
                    val trio = Triple("Rick", "Morty", "Summer")
                    "name=$name, age=$age, trio=$trio"
                },
            ),
        )
    }
}
