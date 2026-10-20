package local.ae2lightuicompat.test;

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
        Mixins.addConfiguration("ae2lightuicompat.mixins.json");
        Class<?> type = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer");
        Constructor<?> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object transformer = constructor.newInstance();
        Method transform = type.getMethod("transformClassBytes", String.class, String.class, byte[].class);
        transform.setAccessible(true);
        int transformed = 0;
        var targets = new java.util.ArrayList<>(java.util.List.of("net.minecraft.client.gui.components.AbstractButton",
                                         "appeng.client.gui.WidgetContainer",
                                         "appeng.client.gui.widgets.VerticalButtonBar",
                                         "appeng.client.gui.widgets.IconButton",
                                         "net.minecraft.client.gui.components.AbstractWidget",
                                         "appeng.client.gui.style.Blitter",
                                         "appeng.client.gui.widgets.TabButton",
                                         "appeng.client.gui.implementations.AESubScreen",
                                         "appeng.items.tools.powered.AbstractPortableCell",
                                         "appeng.client.gui.widgets.CPUSelectionList"));
        for (String optional : new String[]{
                "net.pedroksl.advanced_ae.gui.patternencoder.DirectionInputButton",
                "net.pedroksl.advanced_ae.client.gui.AdvPatternEncoderScreen",
                "net.pedroksl.advanced_ae.client.gui.QuantumArmorConfigScreen",
                "net.pedroksl.advanced_ae.client.gui.QuantumCrafterTermScreen",
                "net.pedroksl.advanced_ae.client.gui.widgets.AAEIcon"}) {
            if (VerifyMixins.class.getClassLoader().getResource(optional.replace('.', '/') + ".class") != null)
                targets.add(optional);
        }
        for (String target : targets) {
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
                    if (instruction instanceof MethodInsnNode call && call.name.contains("ae2lightuicompat$")) hooks++;
                }
            }
            int expected = target.endsWith("AbstractButton") || target.endsWith("VerticalButtonBar")
                    || target.endsWith("WidgetContainer") ? 3 : target.endsWith("AESubScreen") ? 2 : 1;
            if (target.endsWith("AbstractPortableCell")) expected = 4;
            if (target.endsWith("AdvPatternEncoderScreen")) expected = 4; // Three injections + selection bridge.
            if (target.endsWith("QuantumArmorConfigScreen")
                    || target.endsWith("QuantumCrafterTermScreen")) expected = 3;
            if (target.endsWith("AAEIcon")) expected = 4;
            if (hooks != expected) throw new AssertionError(target + " hook count " + hooks + ", expected " + expected);
            if (target.endsWith("AbstractButton") && !node.interfaces.contains("local/ae2lightuicompat/ScopedButton"))
                throw new AssertionError("Ownership marker not attached");
            if (target.endsWith("AbstractButton") && !node.interfaces.contains("local/ae2lightuicompat/ToolbarButton"))
                throw new AssertionError("Toolbar marker not attached");
            if (target.endsWith("TabButton")) {
                if (!node.interfaces.contains("local/ae2lightuicompat/NavigationTab"))
                    throw new AssertionError("Navigation marker not attached");
                if (node.fields.stream().noneMatch(f -> f.name.equals("item"))
                        || node.fields.stream().noneMatch(f -> f.name.equals("icon")))
                    throw new AssertionError("Original tab payload lost; resource reload fallback broken");
            }
            if (target.endsWith("AbstractWidget")) {
                MethodNode invoker = node.methods.stream().filter(m -> m.name.equals("ae2lightuicompat$renderOriginal"))
                        .findFirst().orElseThrow(() -> new AssertionError("Missing virtual renderer bridge"));
                boolean virtualDispatch = false;
                for (AbstractInsnNode instruction : invoker.instructions) {
                    if (instruction instanceof MethodInsnNode call && call.getOpcode() == Opcodes.INVOKEVIRTUAL
                            && (call.name.equals("renderWidget") || call.name.equals("m_87963_"))) virtualDispatch = true;
                }
                if (!virtualDispatch) throw new AssertionError("Addon renderWidget overrides would be bypassed");
            }
            if (target.endsWith("VerticalButtonBar") && node.methods.stream().noneMatch(m ->
                    m.name.equals("drawBackgroundLayer") && m.desc.equals(
                        "(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/renderer/Rect2i;Lappeng/client/Point;)V")))
                throw new AssertionError("Toolbar background implementation missing");
            Path save = Path.of(args[0], target.substring(target.lastIndexOf('.') + 1) + ".class");
            Files.write(save, output);
            System.out.println("PASS real Mixin transformation + ASM bytecode analysis: " + target + " (" + hooks + " hooks)");
            transformed++;
        }
        if (transformed != targets.size()) throw new AssertionError();
        for (String tier : new String[]{"1k", "4k", "16k", "64k", "256k"}) {
            String id = "portable_chemical_storage_cell_" + tier;
            if (local.ae2lightuicompat.PortableCellColors.resolve(0, "appmek", id, 2, false, true) != 8440575
                    || local.ae2lightuicompat.PortableCellColors.resolve(0, "appmek", id, 2, true, true) != 0
                    || local.ae2lightuicompat.PortableCellColors.resolve(123, "appmek", id, 1, false, true) != 123
                    || local.ae2lightuicompat.PortableCellColors.resolve(0, "appmek", id, 2, false, false) != 0
                    || local.ae2lightuicompat.PortableCellColors.resolve(123, "ae2", id, 2, false, true) != 123)
                throw new AssertionError("Chemical cell palette/fallback mismatch: " + tier);
        }
        for (String id : new String[]{"back", "button_back"}) {
            if (local.ae2lightuicompat.TabRenderer.roleFor(id) != local.ae2lightuicompat.TabRenderer.BACK)
                throw new AssertionError("Missed back entry: " + id);
        }
        for (String id : new String[]{"modeTabButton0", "upgrade", "background", "goBackLater"}) {
            if (local.ae2lightuicompat.TabRenderer.roleFor(id) != local.ae2lightuicompat.TabRenderer.NONE)
                throw new AssertionError("Non-navigation item tab was reclassified: " + id);
        }
        if (local.ae2lightuicompat.ButtonTextRenderer.color(true, false) != 0xF2F2F2
                || local.ae2lightuicompat.ButtonTextRenderer.color(true, true) != 0x517497
                || local.ae2lightuicompat.ButtonTextRenderer.color(false, false) != 0x413F54)
            throw new AssertionError("AE2 1.21.1 text palette mismatch");
        System.out.println("PASS all injection targets matched Forge 47.4.0 / AE2 15.4.11; no game launched.");
    }
}
