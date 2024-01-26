# Izanami

## Description

Practicing Java by porting the [Hazel Engine](https://github.com/TheCherno/Hazel).

The approach will be to go through every video in the [playlist](https://www.youtube.com/playlist?list=PLlrATfBNZ98dC-V-N3m0Go4deliWHPFwT) one by one and replicate the exact behavior as in the C++ version.

MRs performed by other contributors will be handled before the next video, if any.

## Submodules

* izanami-engine is the equivalent of the "Hazel" project
* izanami-editor is the equivalent of the "Hazel Editor" project
* sandbox is the equivalent of the "Sandbox" project

## Set-up

* Java 17 required
* `git clone --recursive https://github.com/Xufuja/izanami.git`
    * If not recursively cloned, perform `git submodule update --init --recursive`
* Run `protoc --proto_path=protobuf --java_out=src/generated protobuf/*.proto` from the izanami-engine directory
  * Version [22.5](https://github.com/protocolbuffers/protobuf/releases/tag/v22.5) was used during implementation
* To update `izanami-script-core.mjs`, run `npm run build` from the izanami-script-core directory
* To update `sandbox.mjs`, run `npm run build` from the izanami-editor\sandbox-project\assets\scripts directory
* Set the working directory to `$PROJECT_DIR$\izanami-editor\` to ensure assets are properly loaded
* A project can be opened via CLI by passing the mproj path as argument, e.g. `$PROJECT_DIR$\izanami-editor\sandbox-project\Sandbox.mproj`

## Libraries

* LWJGL provides GLFW bindings, also replaces glad
* JOML replaces GLM
* Dominion replaces EnTT
* protobuf replaces yaml-cpp
* The monitor package of Apache Commons IO replaces Filewatch

## Scripting

There is no Mono available for Java so replaced it with GraalVM JS as the scripting engine. To make the workflow as similar as possible, the izanami-script-core project is set-up to create `izanami-script-core.mjs` instead of the DLL, same for `sandbox.mjs`

## Skipped

There are a few parts that I have not been able to implement for various reasons:

* Single entry point
    * Both izanami-editor and sandbox have their own main functions
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
* JavaScript debugging, the C++ version has tooling for C# debugging but there does not appear to be anything available for JavaScript
* ScopedBuffer since it is binary file reading related and using `InputStream` for that
---
