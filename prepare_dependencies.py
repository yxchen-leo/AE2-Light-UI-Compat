"""Prepare pinned official build dependencies in .deps; never launch Minecraft."""
from concurrent.futures import ThreadPoolExecutor
import hashlib
import json
from pathlib import Path
import subprocess
import urllib.request

from build_mod import ROOT, FORGE_DIR, FORGE, EXTRA_LIB, AE2_JAR, JAVA_HOME


def download(record, target):
    algorithm = 'sha512' if 'sha512' in record else 'sha1'
    expected = record[algorithm]
    if target.is_file() and hashlib.new(algorithm, target.read_bytes()).hexdigest() == expected:
        return
    request = urllib.request.Request(record['url'], headers={'User-Agent': 'AE2-Light-UI-Compat/1.0'})
    with urllib.request.urlopen(request, timeout=90) as response:
        data = response.read()
    if hashlib.new(algorithm, data).hexdigest() != expected:
        raise RuntimeError('Dependency checksum mismatch: ' + target.name)
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_bytes(data)


def main():
    java = JAVA_HOME / 'bin/java.exe'
    if not java.is_file():
        raise SystemExit('Set JAVA_HOME or AE2_BUILD_JAVA_HOME to a Windows JDK 17+.')
    lock = json.loads((ROOT / 'dependencies.lock.json').read_bytes())
    deps = ROOT / '.deps'
    deps.mkdir(exist_ok=True)
    if not FORGE.is_file():
        installer = deps / 'forge-1.20.1-47.4.0-installer.jar'
        download(lock['forge_installer'], installer)
        FORGE_DIR.mkdir(parents=True, exist_ok=True)
        profiles = FORGE_DIR / 'launcher_profiles.json'
        if not profiles.exists():
            profiles.write_text('{"profiles": {}}\n', encoding='utf-8')
        print('Preparing official Forge runtime; log: .deps/forge-install.log', flush=True)
        with (deps / 'forge-install.log').open('w', encoding='utf-8') as log:
            subprocess.run([str(java), '-jar', str(installer), '--installClient', str(FORGE_DIR)],
                           cwd=deps, stdout=log, stderr=subprocess.STDOUT, check=True)
        if not FORGE.is_file():
            raise RuntimeError('Forge runtime missing; inspect .deps/forge-install.log')
    jobs = [(entry, EXTRA_LIB / entry['path']) for entry in lock['minecraft_libraries']]
    jobs.append((lock['ae2'], AE2_JAR))
    print(f'Checking/downloading {len(jobs)} pinned Minecraft/AE2 dependencies...', flush=True)
    with ThreadPoolExecutor(max_workers=6) as pool:
        list(pool.map(lambda args: download(*args), jobs))
    print('Dependencies ready. Run: python build_mod.py', flush=True)


if __name__ == '__main__':
    main()
