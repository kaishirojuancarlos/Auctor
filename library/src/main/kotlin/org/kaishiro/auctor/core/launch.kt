package org.kaishiro.auctor.core

import com.github.ajalt.clikt.core.main

fun launch(
    arguments: Array<String>
)
{
    CommandEngine().main(argv = arguments)
}
