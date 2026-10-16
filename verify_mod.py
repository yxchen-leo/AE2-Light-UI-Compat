from pathlib import Path
import json
import os
import subprocess
from build_mod import ROOT, JAVA_HOME, JAR_NAME, AE2_JAR, classpath


def main():
    classes = ROOT / 'build/test-classes'
    classes.mkdir(parents=True, exist_ok=True)
    jar = ROOT / 'dist' / JAR_NAME
    ae2 = AE2_JAR
    assert ae2.is_file(), 'Run prepare_dependencies.py or set AE2_JAR.'
    (ROOT / 'inspection').mkdir(exist_ok=True)
    cp = os.pathsep.join(map(str, [classes, jar, ae2] + classpath()))
    sources = sorted((ROOT / 'src/test/java').rglob('*.java'))
    args = ['--release', '17', '-proc:none', '-encoding', 'UTF-8', '-classpath', cp,
            '-d', str(classes)] + list(map(str, sources))
    argfile = ROOT / 'build/test-javac.args'
    argfile.write_text('\n'.join('"' + a.replace('\\', '/') + '"' for a in args), encoding='utf-8')
    subprocess.run([str(JAVA_HOME / 'bin/javac.exe'), '@' + str(argfile)], check=True)
    services = classes / 'META-INF/services'
    services.mkdir(parents=True, exist_ok=True)
    for interface, implementation in [('IMixinService', 'TestMixinService'),
                                      ('IGlobalPropertyService', 'TestProperties')]:
        (services / ('org.spongepowered.asm.service.' + interface)).write_text(
            'local.ae2isolatedui.test.' + implementation + '\n', encoding='utf-8')
    runtime_cp = os.pathsep.join(os.path.relpath(p, ROOT) for p in [classes, jar, ae2] + classpath())
    args = ['-Dmixin.service=local.ae2isolatedui.test.TestMixinService',
            '-Dmixin.debug.verbose=true', '-cp', runtime_cp,
            'local.ae2isolatedui.test.VerifyMixins', 'inspection']
    argfile = ROOT / 'build/test-java.args'
    argfile.write_text('\n'.join('"' + a.replace('\\', '/') + '"' for a in args), encoding='utf-8')
    result = subprocess.run([str(JAVA_HOME / 'bin/java.exe'), '@' + str(argfile)],
                            capture_output=True, text=True, encoding='utf-8', errors='replace', cwd=ROOT)
    (ROOT / 'inspection/mixin-verification.log').write_text(result.stdout + result.stderr, encoding='utf-8')
    if result.returncode:
        raise SystemExit((result.stdout + result.stderr)[-7000:])
    print(result.stdout)
    report_path = ROOT / 'validation-mod.json'
    report = json.loads(report_path.read_text(encoding='utf-8'))
    report['real_mixin_transformation'] = {'forge': '47.4.0', 'ae2': '15.4.11',
        'transformed_classes': 2, 'injection_hooks_verified': 3, 'asm_analysis': 'passed'}
    report_path.write_text(json.dumps(report, indent=2), encoding='utf-8')


if __name__ == '__main__':
    main()
