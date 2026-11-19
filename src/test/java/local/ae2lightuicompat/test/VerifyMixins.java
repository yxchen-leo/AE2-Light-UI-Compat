package local.ae2lightuicompat.test;

import local.ae2lightuicompat.ButtonTextRenderer;
import local.ae2lightuicompat.PortableCellColors;
import local.ae2lightuicompat.TabRenderer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.BasicVerifier;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Real transformations and bytecode checks; this does not launch the game. */
public final class VerifyMixins {
    private static final String HOOK_PREFIX = "ae2lightuicompat$";
    private static final String MARKER_PACKAGE = "local/ae2lightuicompat/";

    private static final List<Target> REQUIRED_TARGETS =
            List.of(
                    new Target("net.minecraft.client.gui.components.AbstractButton", 3),
                    new Target("appeng.client.gui.WidgetContainer", 3),
                    new Target("appeng.client.gui.widgets.VerticalButtonBar", 3),
                    new Target("appeng.client.gui.widgets.IconButton", 1),
                    new Target("net.minecraft.client.gui.components.AbstractWidget", 1),
                    new Target("appeng.client.gui.style.Blitter", 1),
                    new Target("appeng.client.gui.widgets.TabButton", 1),
                    new Target("appeng.client.gui.implementations.AESubScreen", 2),
                    new Target("appeng.items.tools.powered.AbstractPortableCell", 4),
                    new Target("appeng.client.gui.widgets.CPUSelectionList", 1),
                    new Target("appeng.client.gui.widgets.AECheckbox", 3),
                    new Target("appeng.client.gui.widgets.Scrollbar", 2),
                    new Target("appeng.client.gui.AEBaseScreen", 2),
                    new Target("net.minecraft.server.packs.repository.Pack", 1),
                    new Target("net.minecraft.world.level.storage.loot.LootDataType", 1));

    private static final List<Target> OPTIONAL_TARGETS =
            List.of(
                    new Target(
                            "net.pedroksl.advanced_ae.gui.patternencoder.DirectionInputButton", 1),
                    // Three injections, the selection bridge and two shared scrollbar updates.
                    new Target("net.pedroksl.advanced_ae.client.gui.AdvPatternEncoderScreen", 6),
                    new Target("net.pedroksl.advanced_ae.client.gui.QuantumArmorConfigScreen", 3),
                    new Target("net.pedroksl.advanced_ae.client.gui.QuantumCrafterTermScreen", 3),
                    new Target("net.pedroksl.advanced_ae.client.gui.widgets.AAEIcon", 4));

    private record Target(String name, int expectedHooks) {
        String resource() {
            return name.replace('.', '/') + ".class";
        }
    }

    private VerifyMixins() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: VerifyMixins <output-directory>");
        }
        Path outputDirectory = Path.of(args[0]);
        Files.createDirectories(outputDirectory);

        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
        Mixins.addConfiguration("ae2lightuicompat.mixins.json");
        Class<?> type = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer");
        Constructor<?> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object transformer = constructor.newInstance();
        Method transform =
                type.getMethod("transformClassBytes", String.class, String.class, byte[].class);
        transform.setAccessible(true);

        List<Target> targets = availableTargets();
        for (Target target : targets) {
            verifyTransformation(target, transformer, transform, outputDirectory);
        }
        verifyPortableCellColors();
        verifyNavigationRoles();
        verifyButtonColors();
        System.out.println(
                "PASS "
                        + targets.size()
                        + " configured targets transformed with ASM verification; no game"
                        + " launched.");
    }

    private static List<Target> availableTargets() {
        List<Target> targets = new ArrayList<>(REQUIRED_TARGETS);
        for (Target optional : OPTIONAL_TARGETS) {
            if (VerifyMixins.class.getClassLoader().getResource(optional.resource()) != null) {
                targets.add(optional);
            } else {
                System.out.println("SKIP optional target (fixture absent): " + optional.name());
            }
        }
        return targets;
    }

    private static byte[] readClass(Target target) throws IOException {
        try (InputStream input =
                VerifyMixins.class.getClassLoader().getResourceAsStream(target.resource())) {
            if (input == null) {
                throw new AssertionError("Missing class " + target.name());
            }
            return input.readAllBytes();
        }
    }

    private static void verifyTransformation(
            Target target, Object transformer, Method transform, Path outputDirectory)
            throws Exception {
        byte[] input = readClass(target);
        byte[] output = (byte[]) transform.invoke(transformer, target.name(), target.name(), input);
        if (Arrays.equals(input, output)) {
            throw new AssertionError("No transformation " + target.name());
        }
        ClassNode node = new ClassNode();
        new ClassReader(output).accept(node, 0);
        int hooks = 0;
        for (MethodNode method : node.methods) {
            if ((method.access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) == 0) {
                new Analyzer<>(new BasicVerifier()).analyze(node.name, method);
            }
            for (AbstractInsnNode instruction : method.instructions) {
                if (instruction instanceof MethodInsnNode call && call.name.contains(HOOK_PREFIX)) {
                    hooks++;
                }
            }
        }
        if (hooks != target.expectedHooks()) {
            throw new AssertionError(
                    target.name()
                            + " hook count "
                            + hooks
                            + ", expected "
                            + target.expectedHooks());
        }
        verifyStructure(node);
        Path save = outputDirectory.resolve(target.resource());
        Files.createDirectories(save.getParent());
        Files.write(save, output);
        System.out.println(
                "PASS real Mixin transformation + ASM bytecode analysis: "
                        + target.name()
                        + " ("
                        + hooks
                        + " hooks)");
    }

    private static void verifyStructure(ClassNode node) {
        switch (node.name) {
            case "net/minecraft/client/gui/components/AbstractButton" -> {
                requireInterface(node, "ScopedButton");
                requireInterface(node, "ToolbarButton");
            }
            case "appeng/client/gui/widgets/TabButton" -> {
                requireInterface(node, "NavigationTab");
                if (node.fields.stream().noneMatch(field -> field.name.equals("item"))
                        || node.fields.stream().noneMatch(field -> field.name.equals("icon"))) {
                    throw new AssertionError(
                            "Original tab payload lost; resource reload fallback broken");
                }
            }
            case "net/minecraft/client/gui/components/AbstractWidget" ->
                    verifyVirtualRenderer(node);
            case "appeng/client/gui/widgets/VerticalButtonBar" -> {
                if (node.methods.stream()
                        .noneMatch(
                                method ->
                                        method.name.equals("drawBackgroundLayer")
                                                && method.desc.equals(
                                                        "(Lnet/minecraft/client/gui/GuiGraphics;"
                                                            + "Lnet/minecraft/client/renderer/Rect2i;Lappeng/client/Point;)V"))) {
                    throw new AssertionError("Toolbar background implementation missing");
                }
            }
            case "net/minecraft/server/packs/repository/Pack" -> {
                if (node.fields.stream()
                        .noneMatch(
                                field ->
                                        field.name.equals("title")
                                                && (field.access & Opcodes.ACC_FINAL) == 0)) {
                    throw new AssertionError(
                            "Pack display title must remain mutable after transformation");
                }
            }
            default -> {
                // Other targets are checked by exact hook counts and ASM above.
            }
        }
    }

    private static void requireInterface(ClassNode node, String marker) {
        if (!node.interfaces.contains(MARKER_PACKAGE + marker)) {
            throw new AssertionError(marker + " marker not attached to " + node.name);
        }
    }

    private static void verifyVirtualRenderer(ClassNode node) {
        MethodNode invoker =
                node.methods.stream()
                        .filter(method -> method.name.equals(HOOK_PREFIX + "renderOriginal"))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Missing virtual renderer bridge"));
        for (AbstractInsnNode instruction : invoker.instructions) {
            if (instruction instanceof MethodInsnNode call
                    && call.getOpcode() == Opcodes.INVOKEVIRTUAL
                    && (call.name.equals("renderWidget") || call.name.equals("m_87963_"))) {
                return;
            }
        }
        throw new AssertionError("Addon renderWidget overrides would be bypassed");
    }

    private static void verifyPortableCellColors() {
        for (String tier : List.of("1k", "4k", "16k", "64k", "256k")) {
            String id = "portable_chemical_storage_cell_" + tier;
            if (PortableCellColors.resolve(0, "appmek", id, 2, false, true) != 0x80CAFF
                    || PortableCellColors.resolve(0, "appmek", id, 2, true, true) != 0
                    || PortableCellColors.resolve(123, "appmek", id, 1, false, true) != 123
                    || PortableCellColors.resolve(0, "appmek", id, 2, false, false) != 0
                    || PortableCellColors.resolve(123, "ae2", id, 2, false, true) != 123) {
                throw new AssertionError("Chemical cell palette/fallback mismatch: " + tier);
            }
        }
    }

    private static void verifyNavigationRoles() {
        for (String id : List.of("back", "button_back")) {
            if (TabRenderer.roleFor(id) != TabRenderer.BACK) {
                throw new AssertionError("Missed back entry: " + id);
            }
        }
        for (String id : List.of("modeTabButton0", "upgrade", "background", "goBackLater")) {
            if (TabRenderer.roleFor(id) != TabRenderer.NONE) {
                throw new AssertionError("Non-navigation item tab was reclassified: " + id);
            }
        }
    }

    private static void verifyButtonColors() {
        if (ButtonTextRenderer.color(true, false) != 0xF2F2F2
                || ButtonTextRenderer.color(true, true) != 0x517497
                || ButtonTextRenderer.color(false, false) != 0x413F54) {
            throw new AssertionError("AE2 1.21.1 text palette mismatch");
        }
    }
}
