"""Compile directly against official Forge 47.4.0 runtime jars (SRG names).

Run prepare_dependencies.py first, or point AE2_FORGE_DIR at a prepared client.
The resulting JAR is already in the production namespace; never reobfuscate it.
"""
from pathlib import Path
import hashlib
import json
import os
import shutil
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parent
JAR_NAME = 'AE2-Light-UI-Compat-Forge-1.20.1-v1.0.0.jar'
FORGE_DIR = Path(os.environ.get('AE2_FORGE_DIR', ROOT / '.deps/forge-client')).resolve()
LIB = FORGE_DIR / 'libraries'
FORGE = LIB / 'net/minecraftforge/forge/1.20.1-47.4.0/forge-1.20.1-47.4.0-client.jar'
EXTRA_LIB = Path(os.environ.get('AE2_EXTRA_LIB_DIR', ROOT / '.deps/minecraft-libraries')).resolve()
java_home = os.environ.get('AE2_BUILD_JAVA_HOME') or os.environ.get('JAVA_HOME')
javac = shutil.which('javac')
JAVA_HOME = Path(java_home) if java_home else (Path(javac).resolve().parent.parent if javac else Path('/missing-jdk'))
AE2_JAR = Path(os.environ.get('AE2_JAR', ROOT / '.deps/appliedenergistics2-forge-15.4.11.jar')).resolve()


def classpath():
    # Patched Minecraft classes take precedence over the unpatched SRG jar.
    build_tools = {'installertools', 'binarypatcher', 'ForgeAutoRenamingTool',
                   'jarsplitter', 'srgutils', 'mergetool'}
    jars = [FORGE] + sorted(p for p in LIB.rglob('*.jar')
                           if p != FORGE and not p.name.endswith(('-slim.jar', '-extra.jar'))
                           and not build_tools.intersection(p.parts))
    # Runtime dependencies prepared from Mojang's pinned 1.20.1 metadata.
    jars += sorted(p for p in EXTRA_LIB.rglob('*.jar')
                   if not p.name.endswith(('-sources.jar', '-javadoc.jar')))
    return jars


def main():
    jars = classpath()
    assert FORGE.is_file(), 'Run prepare_dependencies.py or set AE2_FORGE_DIR.'
    assert (JAVA_HOME / 'bin/javac.exe').is_file(), 'Set JAVA_HOME or AE2_BUILD_JAVA_HOME to a Windows JDK 17+.'
    classes = ROOT / 'build/classes'
    classes.mkdir(parents=True, exist_ok=True)
    sources = sorted((ROOT / 'src/main/java').rglob('*.java'))
    args = ['--release', '17', '-encoding', 'UTF-8', '-proc:none', '-classpath',
            os.pathsep.join(map(str, jars)), '-d', str(classes)] + list(map(str, sources))
    # javac argument files avoid Windows command-line length limits.
    argfile = ROOT / 'build/javac.args'
    argfile.write_text('\n'.join('"' + a.replace('\\', '/') + '"' for a in args), encoding='utf-8')
    result = subprocess.run([str(JAVA_HOME / 'bin/javac.exe'), '@' + str(argfile)],
                            capture_output=True, text=True, encoding='utf-8', errors='replace')
    (ROOT / 'build/compile.log').write_text(result.stdout + result.stderr, encoding='utf-8')
    if result.returncode:
        raise SystemExit(result.stdout + result.stderr)
    output = ROOT / 'dist' / JAR_NAME
    output.parent.mkdir(exist_ok=True)
    entries = {p.relative_to(classes).as_posix(): p.read_bytes()
               for p in classes.rglob('*.class')}
    resources = ROOT / 'src/main/resources'
    entries.update({p.relative_to(resources).as_posix(): p.read_bytes()
                    for p in resources.rglob('*') if p.is_file()})
    entries['META-INF/MANIFEST.MF'] = (b'Manifest-Version: 1.0\r\n'
        b'MixinConfigs: ae2isolatedui.mixins.json\r\n\r\n')
    entries['LICENSE.txt'] = (ROOT / 'LICENSE').read_bytes()
    with zipfile.ZipFile(output, 'w', zipfile.ZIP_DEFLATED) as z:
        for name, data in sorted(entries.items()):
            z.writestr(name, data)
    with zipfile.ZipFile(output) as z:
        assert z.testzip() is None
        assert all(not n.startswith(('net/minecraft/', 'org/spongepowered/')) for n in z.namelist())
    report = {'output': output.name, 'java_release': 17,
              'compiled_against': 'official Forge 1.20.1-47.4.0 runtime jars',
              'namespace': 'SRG (no remapping needed)',
              'classes': sorted(n for n in entries if n.endswith('.class')),
              'sha256': hashlib.sha256(output.read_bytes()).hexdigest(),
              'in_game_tested': False}
    (ROOT / 'validation-mod.json').write_text(json.dumps(report, indent=2), encoding='utf-8')
    print(json.dumps(report, indent=2))


if __name__ == '__main__':
    main()
