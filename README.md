# MMD Tools

## Description

The original goal of this project was to do something else but it turned into porting the [Hazel Engine](https://github.com/TheCherno/Hazel) to Java to practice Java.

The approach will be to go through every video in the [playlist](https://www.youtube.com/watch?v=d5wL6eZnWMU&list=PLlrATfBNZ98dC-V-N3m0Go4deliWHPFwT) one by one and replicate the exact behavior as in the C++ version.

MRs performed by other contributors will be handled before the next video, if any.

## Submodules

* mmd-engine is the equivalent of the "Hazel" project
* mmd-editor is the equivalent of the "Sandbox" project

## Set-up

* Java 17 required
* `git clone --recursive https://github.com/Xufuja/mmd-tools.git`
    * If not recursively cloned, perform `git submodule update --init --recursive`

## Libraries

* LWJGL provides GLFW bindings, also replaces glad
* JOML replaces GLM

---
