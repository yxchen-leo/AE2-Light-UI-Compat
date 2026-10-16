package local.ae2isolatedui.test;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.*;

public final class VerifyMixins {
    public static void main(String[] args) throws Exception {
        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
        Mixins.addConfiguration("ae2isolatedui.mixins.json");
        Class<?> type = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer");
        Constructor<?> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object transformer = constructor.newInstance();
        Method transform = type.getMethod("transformClassBytes", String.class, String.class, byte[].class);
        transform.setAccessible(true);
        int transformed = 0;
        for (String target : new String[]{"net.minecraft.client.gui.components.AbstractButton",
                                         "appeng.client.gui.WidgetContainer"}) {
            String resource = target.replace('.', '/') + ".class";
            byte[] input;
            try (InputStream in = VerifyMixins.class.getClassLoader().getResourceAsStream(resource)) {
                if (in == null) throw new AssertionError("Missing class " + target);
                input = in.readAllBytes();
            }
            byte[] output = (byte[]) transform.invoke(transformer, target, target, input);
            if (java.util.Arrays.equals(input, output)) throw new AssertionError("No transformation " + target);
            ClassNode node = new ClassNode();
            new ClassReader(output).accept(node, 0);
            int hooks = 0;
            for (MethodNode method : node.methods) {
                if ((method.access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) == 0)
                    new Analyzer<BasicValue>(new BasicVerifier()).analyze(node.name, method);
                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction instanceof MethodInsnNode call && call.name.contains("ae2isolatedui$")) hooks++;
                }
            }
            int expected = target.endsWith("AbstractButton") ? 2 : 1;
            if (hooks != expected) throw new AssertionError(target + " hook count " + hooks + ", expected " + expected);
            if (target.endsWith("AbstractButton") && !node.interfaces.contains("local/ae2isolatedui/ScopedButton"))
                throw new AssertionError("Ownership marker not attached");
            Path save = Path.of(args[0], target.substring(target.lastIndexOf('.') + 1) + ".class");
            Files.write(save, output);
            System.out.println("PASS real Mixin transformation + ASM bytecode analysis: " + target + " (" + hooks + " hooks)");
            transformed++;
        }
        if (transformed != 2) throw new AssertionError();
        System.out.println("PASS all injection targets matched Forge 47.4.0 / AE2 15.4.11; no game launched.");
    }
}
