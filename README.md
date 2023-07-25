# MMD Tools

## Description

The original goal of this project was to do something else, but it turned into porting the [Hazel Engine](https://github.com/TheCherno/Hazel) to Java to practice Java.

The approach will be to go through every video in the [playlist](https://www.youtube.com/playlist?list=PLlrATfBNZ98dC-V-N3m0Go4deliWHPFwT) one by one and replicate the exact behavior as in the C++ version.

MRs performed by other contributors will be handled before the next video, if any.

## Submodules

* mmd-engine is the equivalent of the "Hazel" project
* mmd-editor is the equivalent of the "Hazel Editor" project
* sandbox is the equivalent of the "Sandbox" project

## Set-up

* Java 17 required
* `git clone --recursive https://github.com/Xufuja/mmd-tools.git`
    * If not recursively cloned, perform `git submodule update --init --recursive`
* Run `protoc --proto_path=protobuf --java_out=src/generated protobuf/*.proto` from the mmd-engine directory
* To update `mmd-script-core.mjs`, run `npm run build` from the mmd-script-core directory
To update `sandbox.mjs`, run `npm run build` from the mmd-editor\sandbox-project\assets\scripts directory

## Libraries

* LWJGL provides GLFW bindings, also replaces glad
* JOML replaces GLM
* Dominion replaces EnTT
* protobuf replaces yaml-cpp
* The monitor package of Apache Commons IO replaces Filewatch

## Scripting

There is no Mono available for Java so replaced it with GraalVM JS as the scripting engine. To make the workflow as similar as possible, the MMD-ScriptCore project is set-up to create `mmd-script-core.mjs` instead of the DLL, same for `sandbox.mjs`

## Skipped

There are a few parts that I have not been able to implement for various reasons:

* Single entry point
    * Both mmd-editor and sandbox have their own main functions
* Macros
    * They have mostly been made into methods, or just ignored
* Templates
    * Mostly the same as macros
* Destructors
* Reference counting
* Instrumentation
* Asserts
  * All of these in the C++ code base have become `RuntimeException` in Java
* Refactored Input since you cannot have abstract static methods in Java
---
