package dev.xfj.platform.opengl;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.shader.Shader;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Pointer;
import org.lwjgl.util.shaderc.Shaderc;
import org.lwjgl.util.spvc.Spvc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL46.GL_SHADER_BINARY_FORMAT_SPIR_V;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.util.shaderc.Shaderc.*;

public class OpenGLShader implements Shader {
    private int rendererId;
    private final Path filePath;
    private final String name;
    private Map<Integer, ByteBuffer> vulkanSPIRV;
    private Map<Integer, ByteBuffer> openGLSPIRV;
    private Map<Integer, String> openGLSourceCode;

    static int shaderTypeFromString(String type) {
        return switch (type) {
            case "vertex" -> GL_VERTEX_SHADER;
            case "fragment", "pixel" -> GL_FRAGMENT_SHADER;
            default -> {
                //Some sort of exception
                Log.error("Unknown Shader Type!");
                yield 0;
            }
        };
    }

    static int gLShaderStageToShaderC(int stage) {
        return switch (stage) {
            case GL_VERTEX_SHADER -> shaderc_glsl_vertex_shader;
            case GL_FRAGMENT_SHADER -> shaderc_glsl_fragment_shader;
            //Some sort of exception
            default -> 0;
        };
    }

    static String gLShaderStageToString(int stage) {
        return switch (stage) {
            case GL_VERTEX_SHADER -> "GL_VERTEX_SHADER";
            case GL_FRAGMENT_SHADER -> "GL_FRAGMENT_SHADER";
            //Some sort of exception
            default -> null;
        };
    }

    static Path getCacheDirectory() {
        return Path.of("assets/cache/shader/opengl");
    }

    static void createCacheDirectoryIfNeeded() {
        Path cacheDirectory = getCacheDirectory();
        if (!Files.exists(cacheDirectory)) {
            try {
                Files.createDirectories(cacheDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    static String gLShaderStageCachedOpenGLFileExtension(int stage) {
        return switch (stage) {
            case GL_VERTEX_SHADER -> ".cached_opengl.vert";
            case GL_FRAGMENT_SHADER -> ".cached_opengl.frag";
            //Some sort of exception
            default -> null;
        };
    }

    static String gLShaderStageCachedVulkanFileExtension(int stage) {
        return switch (stage) {
            case GL_VERTEX_SHADER -> ".cached_vulkan.vert";
            case GL_FRAGMENT_SHADER -> ".cached_vulkan.frag";
            //Some sort of exception
            default -> null;
        };
    }

    public OpenGLShader(Path filePath) {
        this.filePath = filePath;
        this.vulkanSPIRV = new HashMap<>();
        this.openGLSPIRV = new HashMap<>();
        this.openGLSourceCode = new HashMap<>();

        OpenGLShader.createCacheDirectoryIfNeeded();

        String source = readFile(filePath);
        HashMap<Integer, String> shaderSources = preProcess(source);

        compileOrGetVulkanBinaries(shaderSources);
        compileOrGetOpenGLBinaries();
        createProgram();

        String fullName = filePath.getFileName().toString();
        int lastDot = fullName.lastIndexOf(".");
        this.name = fullName.substring(0, lastDot);
    }

    public OpenGLShader(String name, String vertexSrc, String fragmentSrc) {
        this.filePath = null;
        this.name = name;
        HashMap<Integer, String> sources = new HashMap<>();
        sources.put(GL_VERTEX_SHADER, vertexSrc);
        sources.put(GL_FRAGMENT_SHADER, fragmentSrc);

        compileOrGetVulkanBinaries(sources);
        compileOrGetOpenGLBinaries();
        createProgram();
    }

    private String readFile(Path filePath) {
        String result;
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            byte[] bytes = inputStream.readAllBytes();
            result = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Log.error("Could not open file: " + filePath);
            throw new RuntimeException(e);
        }
        return result;
    }

    private HashMap<Integer, String> preProcess(String source) {
        HashMap<Integer, String> shaderSources = new HashMap<>();

        String typeToken = "#type";
        int typeTokenLength = typeToken.length();
        int pos = source.indexOf(typeToken);
        while (pos != -1) {
            int eol = source.indexOf("\r\n", pos);
            //Some sort of exception to handle syntax errors
            int begin = pos + typeTokenLength + 1;
            String type = source.substring(begin, eol);
            //Some sort of exception to handle invalid shader types

            int nextLinePos = source.indexOf("\r\n", eol);
            //Add some sort of exception in case nextLinePos != -1, which should throw Syntax Error
            pos = source.indexOf(typeToken, nextLinePos);
            shaderSources.put(shaderTypeFromString(type), pos == -1 ? source.substring(nextLinePos) : source.substring(nextLinePos, pos));
        }
        return shaderSources;
    }


    private void compile(HashMap<Integer, String> shaderSources) {
        int program = GL45.glCreateProgram();
        //Some sort of exception if shaderSources has more than 2 entries
        List<Integer> shaderIds = new ArrayList<>(2);
        int glShaderIDIndex = 0;

        for (Map.Entry<Integer, String> entry : shaderSources.entrySet()) {
            int type = entry.getKey();
            String source = entry.getValue();

            int shader = GL45.glCreateShader(type);

            GL45.glShaderSource(shader, source);

            GL45.glCompileShader(shader);

            IntBuffer isCompiled = BufferUtils.createIntBuffer(1);
            GL45.glGetShaderiv(shader, GL_COMPILE_STATUS, isCompiled);
            if (isCompiled.get(0) == GL_FALSE) {
                int[] maxLength = new int[]{0};
                GL45.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, maxLength);

                String infoLog = glGetShaderInfoLog(shader, maxLength[0]);

                GL45.glDeleteShader(shader);
                Log.error("Shader compilation failure: " + infoLog);
                break;
            }
            GL45.glAttachShader(program, shader);
            shaderIds.add(glShaderIDIndex++, shader);
        }

        this.rendererId = program;

        GL45.glLinkProgram(program);

        IntBuffer isLinked = BufferUtils.createIntBuffer(1);
        GL45.glGetProgramiv(program, GL_LINK_STATUS, isLinked);

        if (isLinked.get(0) == GL_FALSE) {
            int[] maxLength = new int[]{0};
            GL45.glGetProgramiv(program, GL_INFO_LOG_LENGTH, maxLength);

            String infoLog = glGetProgramInfoLog(program, maxLength[0]);

            GL45.glDeleteProgram(program);

            for (int id : shaderIds) {
                GL45.glDeleteShader(id);
            }
            //Should be some sort of exception
            Log.error("Shader link failure " + infoLog);
            return;
        }
        for (int id : shaderIds) {
            GL45.glDetachShader(program, id);
            GL45.glDeleteShader(id);
        }
    }

    private void compileOrGetVulkanBinaries(Map<Integer, String> shaderSources) {
        int program = GL45.glCreateProgram();
        long compiler = shaderc_compiler_initialize();
        long options = shaderc_compile_options_initialize();
        shaderc_compile_options_set_target_env(options, shaderc_target_env_vulkan, shaderc_env_version_vulkan_1_2);

        boolean optimize = true;
        if (optimize) {
            shaderc_compile_options_set_optimization_level(options, shaderc_optimization_level_performance);
        }

        Path cacheDirectory = getCacheDirectory();

        Map<Integer, ByteBuffer> shaderData = vulkanSPIRV;
        shaderData.clear();

        for (Map.Entry<Integer, String> entry : shaderSources.entrySet()) {
            int stage = entry.getKey();
            String source = entry.getValue();

            Path shaderFilePath = filePath;
            Path cachedPath = cacheDirectory.resolve(shaderFilePath.getFileName().toString() + gLShaderStageCachedVulkanFileExtension(stage));

            if (Files.exists(cachedPath)) {
                try (InputStream inputStream = Files.newInputStream(cachedPath)) {
                    byte[] byteArray = inputStream.readAllBytes();
                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(byteArray.length).order(ByteOrder.nativeOrder());
                    byteBuffer.put(byteArray);
                    byteBuffer.flip();
                    shaderData.put(stage, byteBuffer);
                } catch (IOException e) {
                    Log.error("Could not open file: " + filePath);
                    throw new RuntimeException(e);
                }
            } else {
                long result = Shaderc.shaderc_compile_into_spv(compiler, source, gLShaderStageToShaderC(stage), filePath.toString(), "main", options);
                Shaderc.shaderc_result_get_compilation_status(result);

                if (Shaderc.shaderc_result_get_compilation_status(result) != Shaderc.shaderc_compilation_status_success) {
                    //Some sort of exception
                    Log.error(Shaderc.shaderc_result_get_error_message(result));
                }

                long length = Shaderc.shaderc_result_get_length(result);
                ByteBuffer byteBuffer = Shaderc.shaderc_result_get_bytes(result);

                byte[] byteArray = new byte[(int) length];
                byteBuffer.get(byteArray);

                byteBuffer.flip();

                shaderData.put(stage, byteBuffer);

                try (OutputStream outputStream = Files.newOutputStream(cachedPath, StandardOpenOption.CREATE)) {
                    outputStream.write(byteArray);
                } catch (IOException e) {
                    Log.error("Could not open file: " + cachedPath);
                    throw new RuntimeException(e);
                }
                Shaderc.shaderc_result_release(result);
            }
        }


        //for (Map.Entry<Integer, ByteBuffer> entry : shaderData.entrySet()) {
        //reflect(entry.getKey(), entry.getValue());
        //}
    }


    private void compileOrGetOpenGLBinaries() {
        Map<Integer, ByteBuffer> shaderData = openGLSPIRV;
        long compiler = shaderc_compiler_initialize();
        long options = shaderc_compile_options_initialize();
        shaderc_compile_options_set_target_env(options, shaderc_target_env_opengl, shaderc_env_version_opengl_4_5);

        boolean optimize = false;
        if (optimize) {
            shaderc_compile_options_set_optimization_level(options, shaderc_optimization_level_performance);
        }

        Path cacheDirectory = getCacheDirectory();

        shaderData.clear();
        openGLSourceCode.clear();

        for (Map.Entry<Integer, ByteBuffer> entry : vulkanSPIRV.entrySet()) {
            int stage = entry.getKey();
            ByteBuffer spirv = entry.getValue();

            Path shaderFilePath = filePath;
            Path cachedPath = cacheDirectory.resolve(shaderFilePath.getFileName().toString() + gLShaderStageCachedOpenGLFileExtension(stage));

            if (Files.exists(cachedPath)) {
                try (InputStream inputStream = Files.newInputStream(cachedPath)) {
                    byte[] byteArray = inputStream.readAllBytes();
                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(byteArray.length).order(ByteOrder.nativeOrder());
                    byteBuffer.put(byteArray);
                    byteBuffer.flip();
                    shaderData.put(stage, byteBuffer);
                } catch (IOException e) {
                    Log.error("Could not open file: " + filePath);
                    throw new RuntimeException(e);
                }
            } else {
                long glslCompiler;
                try (MemoryStack stack = stackPush()) {
                    int[] intArray = new int[spirv.remaining() / 4];
                    spirv.asIntBuffer().get(intArray);

                    IntBuffer bytecode = MemoryUtil.memAllocInt(intArray.length);
                    bytecode.put(intArray, 0, intArray.length);
                    bytecode.flip();

                    PointerBuffer contextPtr = stack.callocPointer(1);
                    PointerBuffer compilerPtr = stack.callocPointer(1);
                    PointerBuffer ir = stack.callocPointer(1);

                    Spvc.spvc_context_create(contextPtr);
                    long context = contextPtr.get(0);

                    Spvc.spvc_context_parse_spirv(context, bytecode, intArray.length, ir);

                    Spvc.spvc_context_create_compiler(
                            context,
                            Spvc.SPVC_BACKEND_GLSL,
                            ir.get(0),
                            Spvc.SPVC_CAPTURE_MODE_TAKE_OWNERSHIP,
                            compilerPtr
                    );

                    glslCompiler = compilerPtr.get(0);

                }
                try (MemoryStack stack = stackPush()) {
                    PointerBuffer glslResult = stack.callocPointer(1);
                    Spvc.spvc_compiler_compile(glslCompiler, glslResult);

                    String shaderCode = MemoryUtil.memUTF8(glslResult.get(0));
                    openGLSourceCode.put(stage, shaderCode);

                    String source = openGLSourceCode.get(stage);

                    long result = Shaderc.shaderc_compile_into_spv(compiler, source, gLShaderStageToShaderC(stage), filePath.toString(), "main", options);
                    Shaderc.shaderc_result_get_compilation_status(result);

                    if (Shaderc.shaderc_result_get_compilation_status(result) != Shaderc.shaderc_compilation_status_success) {
                        //Some sort of exception
                        Log.error(Shaderc.shaderc_result_get_error_message(result));
                    }

                    long length = Shaderc.shaderc_result_get_length(result);
                    ByteBuffer byteBuffer = Shaderc.shaderc_result_get_bytes(result);

                    byte[] byteArray = new byte[(int) length];
                    byteBuffer.get(byteArray);

                    byteBuffer.flip();

                    shaderData.put(stage, byteBuffer);

                    try (OutputStream outputStream = Files.newOutputStream(cachedPath, StandardOpenOption.CREATE)) {
                        outputStream.write(byteArray);
                    } catch (IOException e) {
                        Log.error("Could not open file: " + cachedPath);
                        throw new RuntimeException(e);
                    }
                    Shaderc.shaderc_result_release(result);
                }
            }

        }
    }

    private void createProgram() {
        int program = GL45.glCreateProgram();

        List<Integer> shaderIds = new ArrayList<>();
        for (Map.Entry<Integer, ByteBuffer> entry : openGLSPIRV.entrySet()) {
            int stage = entry.getKey();
            ByteBuffer spirv = entry.getValue();

            int[] shaderId = new int[1];
            shaderId[0] = GL45.glCreateShader(stage);
            shaderIds.add(shaderId[0]);

            GL45.glShaderBinary(shaderId, GL_SHADER_BINARY_FORMAT_SPIR_V, spirv);

            //Apparently this is a bug in LWGJL: https://github.com/LWJGL/lwjgl3/issues/551
            try (MemoryStack stack = stackPush()) {
                GL46.glSpecializeShader(shaderId[0], stack.UTF8("main"), stack.mallocInt(0), stack.mallocInt(0));
            }
            GL45.glAttachShader(program, shaderId[0]);
        }

        GL45.glLinkProgram(program);

        int[] isLinked = new int[1];
        GL45.glGetProgramiv(program, GL_LINK_STATUS, isLinked);

        if (isLinked[0] == GL_FALSE) {
            int[] maxLength = new int[1];
            GL45.glGetProgramiv(program, GL_INFO_LOG_LENGTH, maxLength);

            String infoLog = glGetProgramInfoLog(program, maxLength[0]);
            //Should be some sort of exception
            Log.error("Shader link failure " + infoLog);

            GL45.glDeleteProgram(program);

            for (int id : shaderIds) {
                GL45.glDeleteShader(id);
            }

        }
        for (int id : shaderIds) {
            GL45.glDetachShader(program, id);
            GL45.glDeleteShader(id);
        }

        rendererId = program;
    }

    private void reflect(int stage, ByteBuffer shaderData) {
        /*try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer bytecode = shaderData.asIntBuffer();
            bytecode.flip();

            PointerBuffer contextPtr = stack.callocPointer(1);
            PointerBuffer compilerPtr = stack.callocPointer(1);
            PointerBuffer ir = stack.callocPointer(1);

            Spvc.spvc_context_create(contextPtr);
            long context = contextPtr.get(0);

            Spvc.spvc_context_parse_spirv(context, bytecode, bytecode.array().length, ir);

            Spvc.spvc_context_create_compiler(
                    context,
                    Spvc.SPVC_BACKEND_GLSL,
                    ir.get(0),
                    Spvc.SPVC_CAPTURE_MODE_TAKE_OWNERSHIP,
                    compilerPtr
            );

            long compiler = compilerPtr.get(0);

            PointerBuffer resourcesPtr = MemoryUtil.memAllocPointer(1);
            Spvc.spvc_compiler_create_shader_resources(compiler, resourcesPtr);
            long resources = resourcesPtr.get(0);
        }*/
    }

    @Override
    public void bind() {
        GL45.glUseProgram(this.rendererId);
    }

    @Override
    public void unbind() {
        GL45.glUseProgram(0);
    }

    @Override
    public void setInt(String name, int value) {
        uploadUniformInt(name, value);
    }

    @Override
    public void setIntArray(String name, int[] values) {
        uploadUniformIntArray(name, values);
    }

    @Override
    public void setFloat(String name, float value) {
        uploadUniformFloat(name, value);
    }

    @Override
    public void setFloat2(String name, Vector2f value) {
        uploadUniformFloat2(name, value);
    }

    @Override
    public void setFloat3(String name, Vector3f value) {
        uploadUniformFloat3(name, value);
    }

    @Override
    public void setFloat4(String name, Vector4f value) {
        uploadUniformFloat4(name, value);
    }

    @Override
    public void setMat4(String name, Matrix4f value) {
        uploadUniformMat4(name, value);
    }

    public void uploadUniformInt(String name, int value) {
        int location = GL45.glGetUniformLocation(rendererId, name);
        GL45.glUniform1i(location, value);
    }

    public void uploadUniformIntArray(String name, int[] values) {
        int location = GL45.glGetUniformLocation(rendererId, name);
        GL45.glUniform1iv(location, values);
    }

    public void uploadUniformFloat(String name, float value) {
        int location = GL45.glGetUniformLocation(rendererId, name);
        GL45.glUniform1f(location, value);
    }

    public void uploadUniformFloat2(String name, Vector2f value) {
        int location = GL45.glGetUniformLocation(rendererId, name);
        GL45.glUniform2f(location, value.x, value.y);
    }

    public void uploadUniformFloat3(String name, Vector3f value) {
        int location = GL45.glGetUniformLocation(rendererId, name);
        GL45.glUniform3f(location, value.x, value.y, value.z);
    }

    public void uploadUniformFloat4(String name, Vector4f value) {
        int location = GL45.glGetUniformLocation(rendererId, name);
        GL45.glUniform4f(location, value.x, value.y, value.z, value.w);
    }

    public void uploadUniformMat3(String name, Matrix3f matrix) {
        int location = GL45.glGetUniformLocation(rendererId, name);
        GL45.glUniformMatrix3fv(location, false, matrix.get(new float[9]));
    }

    public void uploadUniformMat4(String name, Matrix4f matrix) {
        int location = GL45.glGetUniformLocation(rendererId, name);
        GL45.glUniformMatrix4fv(location, false, matrix.get(new float[16]));
    }

    @Override
    public String getName() {
        return this.name;
    }
}
