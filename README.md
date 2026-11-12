# Auctor

Auctor is an advanced authentication wrapper for Linux systems, designed to simplify privilege elevation through automatic environment detection. It intelligently determines whether to use a graphical or terminal-based authentication backend, providing a unified authentication interface across different desktop environments and system configurations.

The project is developed entirely in **Kotlin/JVM**, using **Gradle** as its build system. It leverages the **Micronaut framework** to facilitate native compilation, although native-image support is still under development and has not yet been configured for production stability. Contributions towards improving and stabilising this functionality are welcome.

Auctor is built using **GraalVM JDK 25**. It is recommended that contributors use the same JDK version when cloning, building, or developing forks of the project to ensure consistency across development environments.

## Architectural Dependencies

The following table highlights some of the project's primary dependencies and their respective roles. It is not intended to be an exhaustive inventory of all dependencies.

| Dependency | Purpose |
| --- | --- |
| Clikt | Provides command-line argument parsing and command-line interface functionality. |
| Compose Desktop | Provides the graphical backend. |

## Usage and Contributions

Auctor is open-source software distributed under the terms of the **Mozilla Public License 2.0 (MPL-2.0)**. You are free to use, modify, and distribute the project in accordance with the terms of the licence.

Contributions are welcome, whether through bug fixes, feature enhancements, architectural improvements, or documentation.

When contributing, please keep the following principles in mind:

- **Maintain architectural consistency:** Preserve the project's overall structure and design conventions.
- **Prioritise upstream compatibility:** Implement changes in a manner that allows them to be integrated into the upstream project without unnecessary modifications.
- **Maintain code quality:** Follow established Kotlin and Gradle conventions and favour maintainable, modular implementations.

For licensing details, refer to the [LICENSE](LICENSE) file.