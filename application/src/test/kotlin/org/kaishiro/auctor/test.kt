package org.kaishiro.auctor

import jakarta.inject.Singleton

@Singleton
class GreetingService
{
    fun greet(
        name: String
    ): String = "Hello, $name!"
}
